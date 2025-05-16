package com.example.budapestapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budapestapp.R;
import com.example.budapestapp.model.Route;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routeList;

    public RouteAdapter(List<Route> routeList) {
        this.routeList = routeList;
    }

    private OnRouteClickListener listener;

    public RouteAdapter(List<Route> routeList, OnRouteClickListener listener) {
        this.routeList = routeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.lineTextView.setText(route.getLineNumber() + " - " + route.getType());
        holder.stationsTextView.setText(route.getDepartureStation() + " → " + route.getDestinationStation());
        holder.timesTextView.setText(route.getDepartureTime() + " → " + route.getArrivalTime());
        holder.itemView.setOnClickListener(v -> listener.onRouteClick(route));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onRouteLongClick(route);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }


    public interface OnRouteClickListener {
        void onRouteClick(Route route);
        void onRouteLongClick(Route route);
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView lineTextView, stationsTextView, timesTextView;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            lineTextView = itemView.findViewById(R.id.lineTextView);
            stationsTextView = itemView.findViewById(R.id.stationsTextView);
            timesTextView = itemView.findViewById(R.id.timesTextView);
        }
    }
}