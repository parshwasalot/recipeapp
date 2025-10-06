package com.example.recipeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to display the application settings.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // This loads the FrameLayout container

        // Check if we're creating this for the first time
        // This prevents creating new fragments on screen rotation
        if (savedInstanceState == null) {
            // Place the SettingsFragment into the 'settings_container' FrameLayout
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

}