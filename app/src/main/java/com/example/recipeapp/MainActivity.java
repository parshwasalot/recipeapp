package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Load the default fragment (HomeFragment) when the activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Show HomeFragment
                    openFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    // Launch NearbyGroceriesActivity
                    Intent intent = new Intent(MainActivity.this, NearbyGroceriesActivity.class);
                    startActivity(intent);
                    return false; // Don't select the item, just launch the activity
                } else if (itemId == R.id.navigation_profile) {
                    // Show ProfileFragment
                    openFragment(new ProfileFragment());
                    return true;
                }
                return false;
            };

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // --- KEEPING THE TOP MENU FOR SETTINGS ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorites) {
            Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
            startActivity(favoritesIntent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        // Note: The filter action is now part of HomeFragment and will not be triggered here.
        // You can remove it from main_menu.xml if you want.
        return super.onOptionsItemSelected(item);
    }
}
