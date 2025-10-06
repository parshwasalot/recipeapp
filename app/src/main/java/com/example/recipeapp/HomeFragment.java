package com.example.recipeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private List<Recipe> filteredRecipeList;
    private View rootView; // To anchor the Snackbar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout that contains the RecyclerView
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView); // Find RecyclerView in the fragment's view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use getContext() in fragments

        initializeRecipeList();
        showFilterDialog(); // Show filter dialog on start

        return rootView;
    }

    private void initializeRecipeList() {
        recipeList = new ArrayList<>();
        recipeList.add(new Recipe("Vegetable Biryani", "LUNCH/DINNER : A fragrant rice dish cooked with mixed vegetables and spices.", "https://www.youtube.com/watch?v=SZByh7nXp1U", R.drawable.vegetable_biryani));
        recipeList.add(new Recipe("Paneer Tikka", "LUNCH/DINNER : Grilled cubes of marinated paneer with spices and vegetables.", "https://www.youtube.com/watch?v=Nru8aadeN0I", R.drawable.paneer_tikka));
        recipeList.add(new Recipe("Chana Masala", "LUNCH/DINNER : A spicy chickpea curry made with onions, tomatoes, and spices.", "https://www.youtube.com/watch?v=B8CT6-D2gOM", R.drawable.chana_masala));
        recipeList.add(new Recipe("Aloo Gobi", "LUNCH/DINNER : A dry curry made with potatoes and cauliflower, flavored with spices.", "https://www.youtube.com/watch?v=Bvk2tr3joLA", R.drawable.aloo_gobi));
        recipeList.add(new Recipe("Palak Paneer", "LUNCH/DINNER : Cottage cheese cooked in a creamy spinach sauce.", "https://www.youtube.com/watch?v=5lVLxEr_qgM", R.drawable.palak_paneer));
        recipeList.add(new Recipe("Dal Tadka", "LUNCH/DINNER : Lentils cooked with spices and tempered with ghee.", "https://www.youtube.com/watch?v=8c_scYUN5uc", R.drawable.dal_tadka));
        recipeList.add(new Recipe("Baigan Bharta", "LUNCH/DINNER : Smoked eggplant mashed with spices", "https://www.youtube.com/watch?v=exp-Fo1H57U", R.drawable.baigan_bharta));
        recipeList.add(new Recipe("Naan", "BREADS : Soft, leavened bread traditionally baked in a tandoor.", "https://www.youtube.com/watch?v=PTYYOH8VjYM", R.drawable.naan));
        recipeList.add(new Recipe("Roti", "BREADS : Unleavened flatbread made from whole wheat flour.", "https://www.youtube.com/watch?v=1vQ34bs_dTw", R.drawable.roti));
        recipeList.add(new Recipe("Falafel", "SNACK/LIGHT DINNER : Deep-fried balls made from ground chickpeas, herbs, and spices.", "https://www.youtube.com/watch?v=tFOSxnsMCDw", R.drawable.falafel));
        recipeList.add(new Recipe("Tiramisu", "DESSERT : An Italian coffee-flavored dessert made with cheese.", "https://www.youtube.com/watch?v=O2g5uW2qotw", R.drawable.tiramisu));
        recipeList.add(new Recipe("White Sauce Pasta", "LUNCH/DINNER : Pasta tossed with fresh vegetables, olive oil in a white sauce.", "https://www.youtube.com/watch?v=l_HOohJZL0U", R.drawable.white_sauce_pasta));
        recipeList.add(new Recipe("Spring Rolls", "SNACK/LIGHT DINNER :  Crispy rolls filled with vegetables, served with a dipping sauce.", "https://www.youtube.com/watch?v=wNB05Zc4TqA", R.drawable.spring_rolls));
        recipeList.add(new Recipe("Chow Mein", "SNACK/LIGHT DINNER : Stir-fried noodles with vegetables and soy sauce.", "https://www.youtube.com/watch?v=j5o7RUtyaRw", R.drawable.chow_mein));
        recipeList.add(new Recipe("Chocolate Cake", "DESSERT : A dessert with a buttery chocolate cream and soft spongy inside", "https://www.youtube.com/watch?v=1EXDuvL9uU0", R.drawable.chocolate_cake));
        recipeList.add(new Recipe("Gulab Jamun", "DESSERT : Sweet, deep-fried dough balls soaked in syrup.", "https://www.youtube.com/watch?v=QFvd7u_YjVk", R.drawable.gulab_jamun));
        recipeList.add(new Recipe("Samosa", "Deep-fried pastry filled with spiced potatoes and peas.", "https://www.youtube.com/watch?v=HCyalu9KMIs", R.drawable.samosa));
        recipeList.add(new Recipe("Pav Bhaji", "SNACK : A spicy vegetable mash served with buttered bread rolls.", "https://www.youtube.com/watch?v=Gbuse4WX01I", R.drawable.pav_bhaji));
        recipeList.add(new Recipe("Risotto", "LUNCH/DINNER : A creamy Italian rice dish cooked with broth and cheese.", "https://www.youtube.com/watch?v=hTjaYRXW8nQ", R.drawable.risotto));
        recipeList.add(new Recipe("Fried Rice", "LUNCH/DINNER : Stir-fried rice with vegetables, soy sauce, and spices.", "https://www.youtube.com/watch?v=hoZccEa0Pqo", R.drawable.fried_rice));
        recipeList.add(new Recipe("Chocolate Mousse", "DESSERT : A rich, creamy dessert made with chocolate and whipped cream.", "https://www.youtube.com/watch?v=HIZjiP7K65o", R.drawable.chocolate_mousse));
        recipeList.add(new Recipe("Khichdi", "LUNCH/DINNER : A comforting dish made with rice and lentils, seasoned with spices.", "https://www.youtube.com/watch?v=_JUcqjCKhHc", R.drawable.khichdi));
        recipeList.add(new Recipe("Kathiyawadi Thali", "LUNCH/DINNER : A platter featuring a variety of Kathiyawadi dishes like baingan bharta, dal, and bhakri.", "https://www.youtube.com/watch?v=sqlAuNuBaL0", R.drawable.kathiyawadi_thali));
        recipeList.add(new Recipe("Vegetable Salad", "BREAKFAST/LUNCH : A fresh mix of seasonal vegetables, served with lemon dressing.", "https://www.youtube.com/watch?v=ptub0dCVK9k", R.drawable.vegetable_salad));
        recipeList.add(new Recipe("Chaas (Buttermilk)", "DRINK : A refreshing drink made from yogurt, seasoned with spices and herbs.", "https://www.youtube.com/watch?v=82DyooLMuLE", R.drawable.chaas));
        recipeList.add(new Recipe("Masala Chai", "DRINK : A spiced tea made with black tea, milk, and various spices.", "https://www.youtube.com/watch?v=2KI-PGM7PYQ", R.drawable.masala_chai));
        recipeList.add(new Recipe("Dhokla", "BREAKFAST/SNACK : A steamed savory cake made from fermented rice and chickpea flour.", "https://www.youtube.com/watch?v=w_2eb9uaXns", R.drawable.dhokla));
        recipeList.add(new Recipe("Upma", "BREAKFAST : A savory semolina dish cooked with vegetables and spices.", "https://www.youtube.com/watch?v=HOzhXHHcito", R.drawable.upma));
        recipeList.add(new Recipe("Paneer Bhurji", "LUNCH/DINNER : Scrambled paneer cooked with spices, onions, and tomatoes.", "https://www.youtube.com/watch?v=MUzjIceBV70", R.drawable.paneer_bhurji));
        recipeList.add(new Recipe("Vegetable Soup", "SNACK/DINNER : A healthy and warm soup made with seasonal vegetables.", "https://www.youtube.com/watch?v=0TEsadsR35I", R.drawable.vegetable_soup));
        recipeList.add(new Recipe("Bhel Puri", "SNACK : A savory snack made with puffed rice, vegetables, and tangy tamarind chutney.", "https://www.youtube.com/watch?v=c-hjrGHQyAs", R.drawable.bhel_puri));
        recipeList.add(new Recipe("Virgin Mojito", "DRINK : A refreshing non-alcoholic drink made with mint leaves, lime, and soda.", "https://www.youtube.com/watch?v=Dl4RHpoyBhc", R.drawable.virgin_mojito));
        recipeList.add(new Recipe("Pani Puri", "SNACK : Crispy hollow puris filled with spicy water and chickpeas.", "https://www.youtube.com/watch?v=zkbDNyV9G3w", R.drawable.pani_puri));
        recipeList.add(new Recipe("Veg Pizza", "DINNER : A baked flatbread topped with tomato sauce, cheese, and various vegetables.", "https://www.youtube.com/watch?v=kSb62MGJSI4", R.drawable.veg_pizza));
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("What are you looking for? (Meal type or ingredient)");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString().toLowerCase();
            filterRecipes(userInput);
        });

        builder.setNegativeButton("Show All", (dialog, which) -> {
            filteredRecipeList = new ArrayList<>(recipeList);
            updateRecyclerView();
            showReapplyFilterSnackbar();
        });

        builder.show();
    }

    private void filterRecipes(String filter) {
        filteredRecipeList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getTitle().toLowerCase().contains(filter) ||
                    recipe.getDescription().toLowerCase().contains(filter)) {
                filteredRecipeList.add(recipe);
            }
        }
        updateRecyclerView();
        showClearFilterSnackbar();
    }

    private void updateRecyclerView() {
        recipeAdapter = new RecipeAdapter(getContext(), filteredRecipeList, this);
        recyclerView.setAdapter(recipeAdapter);
        Log.d("HomeFragment", "Filtered recipe list size: " + filteredRecipeList.size());
    }

    private void showClearFilterSnackbar() {
        Snackbar.make(rootView, "Filters applied", Snackbar.LENGTH_INDEFINITE)
                .setAction("Clear Filters", v -> {
                    filteredRecipeList = new ArrayList<>(recipeList);
                    updateRecyclerView();
                    Snackbar.make(rootView, "Filters cleared", Snackbar.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showReapplyFilterSnackbar() {
        Snackbar.make(rootView, "Showing all recipes", Snackbar.LENGTH_INDEFINITE)
                .setAction("Apply Filter", v -> showFilterDialog())
                .show();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("title", recipe.getTitle());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("videoUrl", recipe.getVideoUrl());
        intent.putExtra("imageResId", recipe.getImageResId());
        startActivity(intent);
    }
}
