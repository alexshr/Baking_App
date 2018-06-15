package com.alexshr.baking.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alexshr.baking.AppConstants;
import com.alexshr.baking.R;
import com.alexshr.baking.databinding.ActivityMainBinding;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.recipe.RecipeActivity;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    MenuItem progressBar;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        RecipesAdapter rvAdapter = new RecipesAdapter(this::onRecipeClick);
        binding.recipes.setAdapter(rvAdapter);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        viewModel.getRecipes().observe(this, rvAdapter::replace);

        viewModel.requestApi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        progressBar = menu.findItem(R.id.progress);
        viewModel.getProgressData().observe(this, progressBar::setVisible);
        return true;
    }

    private void onRecipeClick(int recipeId) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(AppConstants.RECIPE_ID_KEY, recipeId);
        startActivity(intent);
    }
}
