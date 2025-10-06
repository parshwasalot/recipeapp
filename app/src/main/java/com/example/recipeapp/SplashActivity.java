package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the transition to the next screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start the LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Finish the SplashActivity so the user can't go back to it
            finish();
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
        