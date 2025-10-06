package com.example.recipeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Fragment that contains the UI for the user settings, defined in root_preferences.xml.
 * Implements a listener to apply theme changes immediately.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener { // Implement the listener

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // This line links the Java code to the XML file
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    // --- Lifecycle methods to register and unregister the listener ---

    @Override
    public void onResume() {
        super.onResume();
        // Register the listener to receive preference changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener to prevent memory leaks
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    // --- Core method to handle preference changes ---

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Check if the preference that changed is the theme toggle
        if (key.equals("theme_preference")) {
            // Get the new value of the switch
            boolean isDark = sharedPreferences.getBoolean(key, false);

            if (isDark) {
                // Set the theme to Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Set the theme to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Note: Calling setDefaultNightMode handles recreating the activity for you.
            // You do not need to call recreate() here.
        }

        // You could add logic for the "unit_system" here if needed later.
    }
}