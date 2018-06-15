package com.alexshr.baking.ui.recipe.fragments.ingredients;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.databinding.FragmentIngredientsBinding;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.recipe.RecipeActivityViewModel;

import javax.inject.Inject;

import static com.alexshr.baking.AppConstants.RECIPE_ID_KEY;

public class IngredientsFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentIngredientsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ingredients,
                container, false);

        IngredientsAdapter adapter = new IngredientsAdapter();

        binding.ingredientsRw.setAdapter(adapter);

        binding.ingredientsRw.addItemDecoration(new DividerItemDecoration(binding.ingredientsRw.getContext(), DividerItemDecoration.VERTICAL));

        RecipeActivityViewModel activityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RecipeActivityViewModel.class);

        IngredientsViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(IngredientsViewModel.class);

        viewModel.getIngredientsData(activityViewModel.getRecipeId()).observe(this, adapter::replace);

        return binding.getRoot();
    }

    public void reconfigure(int recipeId) {
        getArguments().putInt(RECIPE_ID_KEY, recipeId);
    }
}
