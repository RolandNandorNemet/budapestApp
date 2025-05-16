package com.example.budapestapp.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RouteRefreshJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        FirebaseDatabase.getInstance().getReference("routes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            count++;
                        }
                        Log.d("RouteJob", "Frissítés kész, összesen: " + count + " járat.");
                        jobFinished(params, false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("RouteJob", "Frissítés sikertelen: " + error.getMessage());
                        jobFinished(params, true);
                    }
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}