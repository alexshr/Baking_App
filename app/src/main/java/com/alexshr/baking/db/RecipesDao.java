package com.alexshr.baking.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.alexshr.baking.data.Ingredient;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.data.Step;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@Dao
public abstract class RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRecipes(List<Recipe> recipes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertIngredients(List<Ingredient> ingredients);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSteps(List<Step> steps);

    @Query("SELECT * FROM recipes ORDER BY id")
    public abstract LiveData<List<Recipe>> getRecipes();

    @Query("SELECT * FROM recipes WHERE id=:recipeId")
    public abstract LiveData<Recipe> getRecipe(int recipeId);

    @Query("SELECT * FROM ingredients WHERE recipeId=:recipeId ORDER BY position")
    public abstract LiveData<List<Ingredient>> getIngredients(int recipeId);

    @Query("SELECT * FROM steps WHERE recipeId=:recipeId ORDER BY position")
    public abstract LiveData<List<Step>> getSteps(int recipeId);

    @Query("SELECT * FROM steps WHERE recipeId=:recipeId AND position=:position")
    public abstract LiveData<Step> getStep(int recipeId, int position);

    @Query("DELETE FROM recipes")
    public abstract void deleteRecipes();

    @Query("SELECT count(*) FROM steps WHERE recipeId=:recipeId ORDER BY position")
    public abstract LiveData<Integer> getStepsAmount(int recipeId);

    //region ================= for widget service =================
    @Query("SELECT * FROM recipes WHERE id=:recipeId")
    public abstract Recipe getRecipeSync(int recipeId);

    @Query("SELECT * FROM ingredients WHERE recipeId=:recipeId ORDER BY position")
    public abstract List<Ingredient> getIngredientsSync(int recipeId);
    //endregion

    @Transaction
    public void insertAll(List<Recipe> recipes) {
        try {
            Timber.d("insertAll starting; size=%d", recipes.size());
            List<Ingredient> ingredients = new ArrayList<>();
            List<Step> steps = new ArrayList<>();

            for (Recipe recipe : recipes) {
                int i = 0;
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ingredient.setRecipeId(recipe.getId());
                    ingredient.setPosition(i++);
                    ingredients.add(ingredient);
                }
                i = 0;
                for (Step step : recipe.getSteps()) {
                    step.setRecipeId(recipe.getId());
                    step.setPosition(i++);
                    steps.add(step);
                }
            }
            //deleteRecipes();
            insertRecipes(recipes);
            insertIngredients(ingredients);
            insertSteps(steps);
        } catch (Exception e) {
            Timber.e(e);
            throw e;
        }
    }
}
