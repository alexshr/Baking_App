package com.alexshr.baking.api;

import android.arch.lifecycle.LiveData;

import com.alexshr.baking.data.Ingredient;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.data.Step;
import com.alexshr.baking.db.RecipesDao;
import com.alexshr.baking.rx.RestCallTransformer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class RecipesRepository {
    private ApiService apiService;
    private RecipesDao dao;
    private RestCallTransformer<List<Recipe>> transformer;

    @Inject
    public RecipesRepository(ApiService apiService, RestCallTransformer<List<Recipe>> transformer, RecipesDao dao) {
        this.apiService = apiService;
        this.dao = dao;
        this.transformer = transformer;
    }

    public Observable<List<Recipe>> getApiObservable() {
        return apiService.getRecipesObservable()
                .compose(transformer)
                .doOnNext(dao::insertAll);
    }

    public LiveData<List<Step>> getStepsData(int recipeId) {
        return dao.getSteps(recipeId);
    }

    public LiveData<Integer> getStepsAmount(int recipeId) {
        return dao.getStepsAmount(recipeId);
    }

    public LiveData<Step> getStepData(int recipeId, int stepId) {
        return dao.getStep(recipeId, stepId);
    }

    public LiveData<List<Ingredient>> getIngredientsData(int recipeId) {
        return dao.getIngredients(recipeId);
    }

    public LiveData<List<Recipe>> getRecipesData() {
        return dao.getRecipes();
    }

    public LiveData<Recipe> getRecipeData(int recipeId) {
        return dao.getRecipe(recipeId);
    }

    //region ================= sync operation for widget service =================

    public String getRecipeName(int recipeId) {
        return dao.getRecipeSync(recipeId).getName();
    }

    public List<Ingredient> getIngredients(int recipeId) {
        return dao.getIngredientsSync(recipeId);
    }

    //endregion
}
