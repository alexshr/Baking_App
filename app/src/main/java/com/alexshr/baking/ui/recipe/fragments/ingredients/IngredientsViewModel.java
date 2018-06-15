package com.alexshr.baking.ui.recipe.fragments.ingredients;

import android.arch.lifecycle.LiveData;

import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.data.Ingredient;
import com.alexshr.baking.viewmodel.AbstractViewModel;

import java.util.List;

import javax.inject.Inject;

public class IngredientsViewModel extends AbstractViewModel<List<Ingredient>> {

    @Inject
    public IngredientsViewModel(RecipesRepository repository) {
        super(repository);
    }

    public LiveData<List<Ingredient>> getIngredientsData(int recipeId) {
        if (getLiveData() == null) setLiveData(getRepository().getIngredientsData(recipeId));
        return getLiveData();
    }
}
