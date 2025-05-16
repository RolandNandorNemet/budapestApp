package com.example.budapestapp.ui.dashboard;

import com.example.budapestapp.R;
import com.example.budapestapp.model.Route;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.budapestapp.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText typeEditText = root.findViewById(R.id.typeEditText);
        EditText lineNumberEditText = root.findViewById(R.id.lineNumberEditText);
        EditText departureEditText = root.findViewById(R.id.departureEditText);
        EditText destinationEditText = root.findViewById(R.id.destinationEditText);
        EditText departureTimeEditText = root.findViewById(R.id.departureTimeEditText);
        EditText arrivalTimeEditText = root.findViewById(R.id.arrivalTimeEditText);
        Button saveButton = root.findViewById(R.id.saveButton);

        DatabaseReference databaseRoutes = FirebaseDatabase.getInstance().getReference("routes");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeEditText.getText().toString().trim();
                String lineNumber = lineNumberEditText.getText().toString().trim();
                String departureStation = departureEditText.getText().toString().trim();
                String destinationStation = destinationEditText.getText().toString().trim();
                String departureTime = departureTimeEditText.getText().toString().trim();
                String arrivalTime = arrivalTimeEditText.getText().toString().trim();

                if (type.isEmpty() || lineNumber.isEmpty() || departureStation.isEmpty()
                    || destinationStation.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty()) {
                    Toast.makeText(getContext(), "Kérlek, tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = databaseRoutes.push().getKey();
                Route route = new Route(id, type, lineNumber, departureStation, destinationStation, departureTime, arrivalTime);

                databaseRoutes.child(id).setValue(route)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Mentés sikeres!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}