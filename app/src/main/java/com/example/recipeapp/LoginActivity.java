package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_button);
        TextView goToSignup = findViewById(R.id.go_to_signup);

        loginButton.setOnClickListener(v -> loginUser());
        goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to MainActivity
            navigateToMainActivity();
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to the MainActivity
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed. Check your credentials.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Clear the back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish LoginActivity
    }
}
