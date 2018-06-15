package com.alexshr.baking.di;

import com.alexshr.baking.ui.recipe.fragments.ingredients.IngredientsFragment;
import com.alexshr.baking.ui.recipe.fragments.navigation.NavigationFragment;
import com.alexshr.baking.ui.recipe.fragments.recipe.RecipeFragment;
import com.alexshr.baking.ui.recipe.fragments.step.StepFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract RecipeFragment contributeRecipeDetailFragment();

    @ContributesAndroidInjector
    abstract StepFragment contributeStepFragment();

    @ContributesAndroidInjector
    abstract IngredientsFragment contributeIngredientsFragment();

    @ContributesAndroidInjector
    abstract NavigationFragment contributeNavigationFragment();
}
