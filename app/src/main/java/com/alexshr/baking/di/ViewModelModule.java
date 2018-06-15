package com.alexshr.baking.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.alexshr.baking.ui.MainViewModel;
import com.alexshr.baking.ui.recipe.RecipeActivityViewModel;
import com.alexshr.baking.ui.recipe.fragments.ingredients.IngredientsViewModel;
import com.alexshr.baking.ui.recipe.fragments.recipe.RecipeViewModel;
import com.alexshr.baking.ui.recipe.fragments.step.StepViewModel;
import com.alexshr.baking.viewmodel.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

//creating Map<Class<? extends ViewModel>, Provider<ViewModel>> (multibinding)
//to insert to ViewModelFactory as constructor parameter (to single factory for many ViewModels)
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeActivityViewModel.class)
    abstract ViewModel bindRecipeActivityViewModel(RecipeActivityViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    abstract ViewModel bindRecipeViewModel(RecipeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(IngredientsViewModel.class)
    abstract ViewModel bindIngredientsViewModel(IngredientsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StepViewModel.class)
    abstract ViewModel bindStepViewModel(StepViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
