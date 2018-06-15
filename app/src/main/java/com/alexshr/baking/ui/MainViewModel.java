package com.alexshr.baking.ui;

import android.arch.lifecycle.LiveData;

import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.viewmodel.AbstractViewModel;

import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends AbstractViewModel<List<Recipe>> {

    @Inject
    public MainViewModel(RecipesRepository repository) {
        super(repository);
    }

    public LiveData<List<Recipe>> getRecipes() {
        if (getLiveData() == null) setLiveData(getRepository().getRecipesData());
        return getLiveData();
    }
}
