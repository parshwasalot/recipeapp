package com.example.recipeapp;

public class Recipe {
    private String title;
    private String description;
    private String videoUrl;
    private int imageResId; // Add this line

    public Recipe(String title, String description, String videoUrl, int imageResId) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.imageResId = imageResId; // Add this line
    }

    // Add getters for the new field
    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
