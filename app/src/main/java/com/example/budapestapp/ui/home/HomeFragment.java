package com.example.budapestapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Button;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.budapestapp.databinding.FragmentHomeBinding;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budapestapp.R;
import com.example.budapestapp.adapter.RouteAdapter;
import com.example.budapestapp.model.Route;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private List<Route> routeList = new ArrayList<>();
    private RouteAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
        }

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "ROUTE_REMINDER", "Járat emlékeztetők", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        RecyclerView recyclerView = root.findViewById(R.id.routeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RouteAdapter(routeList, new RouteAdapter.OnRouteClickListener() {
            @Override
            public void onRouteClick(Route route) {
                showEditDialog(route);
            }

            @Override
            public void onRouteLongClick(Route route) {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Járat törlése")
                        .setMessage("Biztosan törölni szeretnéd ezt a járatot?")
                        .setPositiveButton("Igen", (dialog, which) -> {
                            FirebaseDatabase.getInstance()
                                    .getReference("routes")
                                    .child(route.getId())
                                    .removeValue();
                        })
                        .setNegativeButton("Mégsem", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        Spinner typeSpinner = root.findViewById(R.id.typeSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            Arrays.asList("Összes", "busz", "villamos", "metró")
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                loadRoutesByType(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadRoutesByType("Összes");
            }
        });

        EditText departureEdit = root.findViewById(R.id.departureFilterEditText);
        EditText destinationEdit = root.findViewById(R.id.destinationFilterEditText);
        Button filterButton = root.findViewById(R.id.filterButton);

        filterButton.setOnClickListener(v -> {
            String selectedType = typeSpinner.getSelectedItem().toString();
            String from = departureEdit.getText().toString().trim();
            String to = destinationEdit.getText().toString().trim();
            loadRoutesByFilters(selectedType, from, to);
        });

        ComponentName componentName = new ComponentName(requireContext(), com.example.budapestapp.service.RouteRefreshJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showEditDialog(Route route) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.edit_route, null);

        EditText editLine = dialogView.findViewById(R.id.editLineNumber);
        EditText editType = dialogView.findViewById(R.id.editType);
        EditText editDeparture = dialogView.findViewById(R.id.editDeparture);
        EditText editDestination = dialogView.findViewById(R.id.editDestination);
        EditText editDepTime = dialogView.findViewById(R.id.editDepartureTime);
        EditText editArrTime = dialogView.findViewById(R.id.editArrivalTime);

        editLine.setText(route.getLineNumber());
        editType.setText(route.getType());
        editDeparture.setText(route.getDepartureStation());
        editDestination.setText(route.getDestinationStation());
        editDepTime.setText(route.getDepartureTime());
        editArrTime.setText(route.getArrivalTime());

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Járat szerkesztése")
                .setView(dialogView)
                .setPositiveButton("Mentés", (dialog, which) -> {

                    String updatedLine = editLine.getText().toString().trim();
                    String updatedType = editType.getText().toString().trim();
                    String updatedDep = editDeparture.getText().toString().trim();
                    String updatedDest = editDestination.getText().toString().trim();
                    String updatedDepTime = editDepTime.getText().toString().trim();
                    String updatedArrTime = editArrTime.getText().toString().trim();

                    Route updatedRoute = new Route(
                            route.getId(),
                            updatedType,
                            updatedLine,
                            updatedDep,
                            updatedDest,
                            updatedDepTime,
                            updatedArrTime
                    );

                    FirebaseDatabase.getInstance()
                            .getReference("routes")
                            .child(route.getId())
                            .setValue(updatedRoute);
                })
                .setNegativeButton("Mégsem", null)
                .show();
    }

    private void loadRoutesByType(String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("routes");

        Query query;
        if (type.equals("Összes")) {
            query = ref;
        } else {
            query = ref.orderByChild("type").equalTo(type);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Route route = ds.getValue(Route.class);
                    if (route != null) {
                        routeList.add(route);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Hiba történt a lekérdezés során.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRoutesByFilters(String type, String from, String to) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("routes");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                routeList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Route route = ds.getValue(Route.class);
                    if (route == null) continue;

                    boolean typeMatch = type.equals("Összes") || route.getType().equalsIgnoreCase(type);
                    boolean fromMatch = from.isEmpty() || route.getDepartureStation().equalsIgnoreCase(from);
                    boolean toMatch = to.isEmpty() || route.getDestinationStation().equalsIgnoreCase(to);

                    if (typeMatch && fromMatch && toMatch) {
                        routeList.add(route);
                    }
                }
                if (!routeList.isEmpty()) {
                    if (routeList.size() == 1) {
                        showRouteReminderNotification("Sikeresen találtunk 1 járatot!");
                    } else {
                        showRouteReminderNotification("Sikeresen találtunk " + routeList.size() + " járatot!");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Hiba történt a lekérdezés során.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRouteReminderNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "ROUTE_REMINDER")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Járat értesítő")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(getContext()).notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Értesítések engedélyezve!", Toast.LENGTH_SHORT).show();
        }
    }
}