package com.alexshr.baking.ui.recipe.fragments.recipe;

import android.arch.lifecycle.LiveData;

import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.data.Step;
import com.alexshr.baking.viewmodel.AbstractViewModel;

import java.util.List;

import javax.inject.Inject;

public class RecipeViewModel extends AbstractViewModel<List<Step>> {

    @Inject
    public RecipeViewModel(RecipesRepository repository) {
        super(repository);
    }

    //avoid to repeat the request after orientation change
    public LiveData<List<Step>> getStepsData(int recipeId) {
        if (getLiveData() == null) setLiveData(getRepository().getStepsData(recipeId));
        return getLiveData();
    }
}
