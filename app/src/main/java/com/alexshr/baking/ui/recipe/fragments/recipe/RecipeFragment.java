package com.alexshr.baking.ui.recipe.fragments.recipe;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.databinding.FragmentRecipeBinding;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.recipe.RecipeActivityViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.baking.AppConstants.STEP_INGREDIENTS;

public class RecipeFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    RecipeActivityViewModel activityViewModel;

    private RecipeViewModel viewModel;
    private FragmentRecipeBinding binding;
    private StepsAdapter stepsAdapter;

    public RecipeFragment() {
        Timber.d("RecipeFragment created");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe,
                container, false);

        binding.setFragment(this);

        activityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RecipeActivityViewModel.class);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeViewModel.class);

        refreshIngredientsSelection();

        stepsAdapter = new StepsAdapter(this::onStepClick, activityViewModel.getStepId());

        binding.stepsRv.setAdapter(stepsAdapter);

        viewModel.getStepsData(activityViewModel.getRecipeId()).observe(this, stepsAdapter::replace);

        return binding.getRoot();
    }

    private void refreshIngredientsSelection() {
        boolean state = activityViewModel.getStepId() == STEP_INGREDIENTS;
        Timber.d("state=%s", state);
        binding.ingredientsCv.setPressed(state);
    }

    public void onIngredientsClick() {
        onStepClick(STEP_INGREDIENTS);
    }

    private void onStepClick(int position) {
        Timber.d("position=%d", position);
        activityViewModel.onDetailOpen(position);
        stepsAdapter.selectItem(position);
        refreshIngredientsSelection();
    }
}