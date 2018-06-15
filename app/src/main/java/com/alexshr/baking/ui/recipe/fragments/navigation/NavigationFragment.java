package com.alexshr.baking.ui.recipe.fragments.navigation;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexshr.baking.R;
import com.alexshr.baking.databinding.FragmentNavigationBinding;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.recipe.RecipeActivityViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.alexshr.baking.AppConstants.STEP_NONE;

public class NavigationFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public final ObservableField<Integer> fragmentVisibility = new ObservableField<Integer>();
    public final ObservableField<Integer> nextVisibility = new ObservableField<Integer>();
    public final ObservableField<Integer> prevVisibility = new ObservableField<Integer>();

    private RecipeActivityViewModel activityViewModel;

    public NavigationFragment() {
        Timber.d("NavigationFragment created");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentNavigationBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation,
                container, false);

        activityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RecipeActivityViewModel.class);

        activityViewModel.getStepIdData().observe(this, stepId -> fragmentVisibility.set(stepId != STEP_NONE ? VISIBLE : GONE));

        activityViewModel.getHasNextData().observe(this, hasNext -> nextVisibility.set(hasNext ? VISIBLE : INVISIBLE));

        activityViewModel.getHasPreviousData().observe(this, hasPrev -> prevVisibility.set(hasPrev ? VISIBLE : INVISIBLE));

        binding.setControl(this);

        return binding.getRoot();
    }

    public void onNextClicked() {
        activityViewModel.stepNext();
    }

    public void onPrevClicked() {
        activityViewModel.stepPrev();
    }
}
