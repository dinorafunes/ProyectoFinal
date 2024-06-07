package com.ugb.controlesbasicos20;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class ClassGps {
    private final AppCompatActivity activity;
    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private ClassLocationCallback classLocationCallback;

    public ClassGps(AppCompatActivity activity, ClassLocationCallback classLocationCallback) {
        this.activity = activity;
        this.registry = activity.getActivityResultRegistry();
        this.classLocationCallback = classLocationCallback;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        registerLauncher();
    }

    public void registerLauncher() {
        locationPermissionRequest = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        //
                        getLastLocation();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        //
                        getLastLocation();
                    } else {
                        //
                    }
                });
    }

    public void startGpsLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                classLocationCallback.onLocationResult(latitude, longitude);
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            //////
        }
    }
}
