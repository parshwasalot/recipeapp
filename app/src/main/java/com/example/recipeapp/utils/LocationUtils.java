package com.example.recipeapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class LocationUtils {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationUtils(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public Task<Location> getLastLocation() {
        if (hasLocationPermission()) {
            return fusedLocationClient.getLastLocation();
        }
        return null;
    }

    public static int getLocationPermissionRequestCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }

    public String[] getRequiredPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }
}