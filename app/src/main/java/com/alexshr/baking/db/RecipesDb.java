package com.alexshr.baking.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.alexshr.baking.data.Ingredient;
import com.alexshr.baking.data.Recipe;
import com.alexshr.baking.data.Step;

@Database(entities = {Recipe.class, Ingredient.class, Step.class}, version = 5)
public abstract class RecipesDb extends RoomDatabase {
    public abstract RecipesDao recipesDao();
}
