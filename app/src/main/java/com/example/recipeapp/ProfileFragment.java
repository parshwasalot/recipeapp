package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView usernameTextView, emailTextView;
    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameTextView = view.findViewById(R.id.profile_username);
        emailTextView = view.findViewById(R.id.profile_email);
        Button logoutButton = view.findViewById(R.id.logout_button);

        loadUserProfile();

        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        usernameTextView.setText("Username: " + document.getString("username"));
                        emailTextView.setText("Email: " + document.getString("email"));
                    } else {
                        Log.d(TAG, "No such document");
                        emailTextView.setText("Email: " + user.getEmail());
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear back stack so the user cannot navigate back to the main activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
