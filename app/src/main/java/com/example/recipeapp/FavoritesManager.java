package com.example.recipeapp;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesManager {
    private static final String TAG = "FavoritesManager";
    private static final String FAVORITES_COLLECTION = "favorites";
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FavoritesManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public interface OnFavoriteCheckListener {
        void onResult(boolean isFavorite);
    }

    public interface OnFavoriteActionListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnFavoritesLoadListener {
        void onFavoritesLoaded(List<String> recipeIds);
        void onFailure(Exception e);
    }

    // Check if a recipe is favorited by the current user
    public void isFavorite(String recipeId, OnFavoriteCheckListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            listener.onResult(false);
            return;
        }

        db.collection(FAVORITES_COLLECTION)
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("recipeId", recipeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listener.onResult(!queryDocumentSnapshots.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking favorite status", e);
                    listener.onResult(false);
                });
    }

    // Add a recipe to favorites
    public void addFavorite(String recipeId, OnFavoriteActionListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            listener.onFailure(new Exception("User not logged in"));
            return;
        }

        // First check if already favorited
        isFavorite(recipeId, isFavorite -> {
            if (isFavorite) {
                listener.onSuccess();
                return;
            }

            // Create favorite document
            Map<String, Object> favorite = new HashMap<>();
            favorite.put("userId", user.getUid());
            favorite.put("recipeId", recipeId);
            favorite.put("createdAt", Timestamp.now());
            favorite.put("updatedAt", Timestamp.now());

            db.collection(FAVORITES_COLLECTION)
                    .add(favorite)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Favorite added with ID: " + documentReference.getId());
                        listener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding favorite", e);
                        listener.onFailure(e);
                    });
        });
    }

    // Remove a recipe from favorites
    public void removeFavorite(String recipeId, OnFavoriteActionListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            listener.onFailure(new Exception("User not logged in"));
            return;
        }

        db.collection(FAVORITES_COLLECTION)
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("recipeId", recipeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onSuccess();
                        return;
                    }

                    // Delete all matching documents (should be only one)
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Favorite removed");
                                    listener.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error removing favorite", e);
                                    listener.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error finding favorite to remove", e);
                    listener.onFailure(e);
                });
    }

    // Toggle favorite status
    public void toggleFavorite(String recipeId, OnFavoriteActionListener listener) {
        isFavorite(recipeId, isFavorite -> {
            if (isFavorite) {
                removeFavorite(recipeId, listener);
            } else {
                addFavorite(recipeId, listener);
            }
        });
    }

    // Get all favorite recipe IDs for current user
    public void getUserFavorites(OnFavoritesLoadListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            listener.onFailure(new Exception("User not logged in"));
            return;
        }

        db.collection(FAVORITES_COLLECTION)
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> recipeIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String recipeId = document.getString("recipeId");
                        if (recipeId != null) {
                            recipeIds.add(recipeId);
                        }
                    }
                    Log.d(TAG, "Loaded " + recipeIds.size() + " favorites");
                    listener.onFavoritesLoaded(recipeIds);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading favorites", e);
                    listener.onFailure(e);
                });
    }
}
