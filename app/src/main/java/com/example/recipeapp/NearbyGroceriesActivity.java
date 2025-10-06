package com.example.recipeapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.recipeapp.utils.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Import the required Places SDK classes
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

// Import OkHttp and JSON classes for new Places API
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NearbyGroceriesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationUtils locationUtils;

    // Add a PlacesClient member
    private PlacesClient placesClient;
    
    // Add OkHttpClient for API requests
    private OkHttpClient httpClient;
    
    // Your Google Maps API key
    private static final String MAPS_API_KEY = "AIzaSyAngBD2Z_mqn4dB3qmwv-3igqr7mgb1fvY";
    
    // New Places API endpoint
    private static final String PLACES_API_URL = "https://places.googleapis.com/v1/places:searchNearby";

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "NearbyGroceries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_groceries);

        // --- Initialize Places SDK ---
        if (!Places.isInitialized()) {
            // Fetch the API key from strings.xml for better security
            String apiKey = MAPS_API_KEY;
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);
        // --- End of Places SDK initialization ---
        
        // Initialize OkHttp client
        httpClient = new OkHttpClient();

        locationUtils = new LocationUtils(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton fabMyLocation = findViewById(R.id.fab_my_location);
        fabMyLocation.setOnClickListener(v -> checkLocationPermissionAndGetLocation());

        // Request location permissions if not granted
        if (!locationUtils.hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    locationUtils.getRequiredPermissions(),
                    LocationUtils.getLocationPermissionRequestCode());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (locationUtils.hasLocationPermission()) {
            enableMyLocation();
            getCurrentLocationAndSearch();
        }
    }

    private void enableMyLocation() {
        // This check is redundant with the one in onMapReady but is safe to keep.
        if (mMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getCurrentLocationAndSearch() {
        if (locationUtils.hasLocationPermission()) {
            locationUtils.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                    // Call the new method to search for groceries
                    searchNearbyGroceries();
                } else {
                    Toast.makeText(this, "Could not get current location. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get location: " + e.getMessage());
            });
        }
    }

    /**
     * Uses the new Places API to find nearby grocery stores via HTTP request.
     */
    private void searchNearbyGroceries() {
        if (mMap == null) {
            return;
        }

        // Get current location first
        if (!locationUtils.hasLocationPermission()) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        locationUtils.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                searchNearbyGroceriesWithLocation(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Makes HTTP request to new Places API to search for nearby grocery stores.
     */
    private void searchNearbyGroceriesWithLocation(double latitude, double longitude) {
        // Build the JSON request body
        JsonObject requestBody = new JsonObject();
        
        // Add included types (grocery stores)
        JsonArray includedTypes = new JsonArray();
        includedTypes.add("grocery_store");
        includedTypes.add("supermarket");
        requestBody.add("includedTypes", includedTypes);
        
        // Add location restriction (circle with 5000 meter radius)
        JsonObject locationRestriction = new JsonObject();
        JsonObject circle = new JsonObject();
        JsonObject center = new JsonObject();
        center.addProperty("latitude", latitude);
        center.addProperty("longitude", longitude);
        circle.add("center", center);
        circle.addProperty("radius", 5000.0); // 5km radius
        locationRestriction.add("circle", circle);
        requestBody.add("locationRestriction", locationRestriction);
        
        // Set max result count
        requestBody.addProperty("maxResultCount", 20);

        // Create the request
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        
        Request request = new Request.Builder()
                .url(PLACES_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", MAPS_API_KEY)
                .addHeader("X-Goog-FieldMask", "places.displayName,places.location")
                .post(body)
                .build();

        // Make async HTTP call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Failed to fetch nearby groceries: " + e.getMessage());
                    Toast.makeText(NearbyGroceriesActivity.this, 
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Places API Response: " + responseBody);
                    
                    try {
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                        
                        if (jsonResponse.has("places")) {
                            JsonArray places = jsonResponse.getAsJsonArray("places");
                            
                            runOnUiThread(() -> {
                                mMap.clear(); // Clear old markers
                                
                                for (int i = 0; i < places.size(); i++) {
                                    JsonObject place = places.get(i).getAsJsonObject();
                                    
                                    // Get place name
                                    String name = "";
                                    if (place.has("displayName")) {
                                        JsonObject displayName = place.getAsJsonObject("displayName");
                                        if (displayName.has("text")) {
                                            name = displayName.get("text").getAsString();
                                        }
                                    }
                                    
                                    // Get location
                                    if (place.has("location")) {
                                        JsonObject locationObj = place.getAsJsonObject("location");
                                        double lat = locationObj.get("latitude").getAsDouble();
                                        double lng = locationObj.get("longitude").getAsDouble();
                                        
                                        LatLng placeLatLng = new LatLng(lat, lng);
                                        mMap.addMarker(new MarkerOptions()
                                                .position(placeLatLng)
                                                .title(name));
                                        
                                        Log.i(TAG, String.format("Found grocery: %s at (%f, %f)", name, lat, lng));
                                    }
                                }
                                
                                Toast.makeText(NearbyGroceriesActivity.this, 
                                        "Found " + places.size() + " grocery stores", 
                                        Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(NearbyGroceriesActivity.this, 
                                        "No grocery stores found nearby", 
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            Toast.makeText(NearbyGroceriesActivity.this, 
                                    "Error parsing results", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        String errorMsg = "API Error: " + response.code();
                        try {
                            if (response.body() != null) {
                                errorMsg += " - " + response.body().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, errorMsg);
                        Toast.makeText(NearbyGroceriesActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }


    private void checkLocationPermissionAndGetLocation() {
        if (locationUtils.hasLocationPermission()) {
            getCurrentLocationAndSearch();
        } else {
            // Request permissions if not already granted.
            ActivityCompat.requestPermissions(this,
                    locationUtils.getRequiredPermissions(),
                    LocationUtils.getLocationPermissionRequestCode());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationUtils.getLocationPermissionRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, now we can enable location and search.
                enableMyLocation();
                getCurrentLocationAndSearch();
            } else {
                // Permission was denied.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
