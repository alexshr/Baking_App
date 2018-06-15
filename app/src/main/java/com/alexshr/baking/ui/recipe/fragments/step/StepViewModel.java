package com.alexshr.baking.ui.recipe.fragments.step;

import android.arch.lifecycle.LiveData;

import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.data.Step;
import com.alexshr.baking.viewmodel.AbstractViewModel;

import javax.inject.Inject;

public class StepViewModel extends AbstractViewModel<Step> {

    @Inject
    public StepViewModel(RecipesRepository repository) {
        super(repository);
    }

    public LiveData<Step> getStepData(int recipeId, int stepId) {
        if (getLiveData() == null) setLiveData(getRepository().getStepData(recipeId, stepId));
        return getLiveData();
    }
}
