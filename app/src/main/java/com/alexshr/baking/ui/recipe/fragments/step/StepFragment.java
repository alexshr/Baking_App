package com.alexshr.baking.ui.recipe.fragments.step;

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
import android.view.ViewGroup.LayoutParams;

import com.alexshr.baking.R;
import com.alexshr.baking.binding.StepFragmentDataBindingComponent;
import com.alexshr.baking.databinding.FragmentStepBinding;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.recipe.RecipeActivityViewModel;
import com.google.android.exoplayer2.Player;

import javax.inject.Inject;

import timber.log.Timber;

import static com.alexshr.baking.AppConstants.PLAY_POSITION_KEY;
import static com.alexshr.baking.AppConstants.PLAY_READY_KEY;

public class StepFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    StepFragmentDataBindingComponent dataBindingComponent;

    private boolean isPlayWhenReady;
    private long playPosition;

    private boolean isRotating;

    public FragmentStepBinding binding;

    private StepViewModel viewModel;

    public StepFragment() {
        Timber.d("StepFragment created");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle restoredState) {

        isRotating = false;

        if (restoredState != null) {
            playPosition = restoredState.getLong(PLAY_POSITION_KEY);
            isPlayWhenReady = restoredState.getBoolean(PLAY_READY_KEY);
        }
        Timber.d("onCreateView playPosition=%s,isPlayWhenReady=%s", playPosition, isPlayWhenReady);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step,
                container, false, dataBindingComponent);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StepViewModel.class);

        RecipeActivityViewModel activityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RecipeActivityViewModel.class);

        int recipeId = activityViewModel.getRecipeId();
        int stepId = activityViewModel.getStepId();

        viewModel.getStepData(recipeId, stepId).observe(this, step -> {
            binding.setStep(step);
            /*if (!activityViewModel.isDual() && step.getVideoUrl() != null && getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
                setupFullScreenVideoMode();
            }*/
        });

        return binding.getRoot();
    }

    public void setupFullScreenVideoMode() {
        Timber.d("setupFullScreenVideoMode");
        binding.playerView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        binding.playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAY_POSITION_KEY, playPosition);
        outState.putBoolean(PLAY_READY_KEY, isPlayWhenReady);
        isRotating = true;
        Timber.d("save playPosition=%s, isPlayWhenReady=%s", playPosition, isPlayWhenReady);
    }

    public void setPlayer(Player exoPlayer) {
        binding.playerView.setPlayer(exoPlayer);
    }

    public boolean isPlayWhenReady() {
        return isPlayWhenReady;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        isPlayWhenReady = playWhenReady;
    }

    public long getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(long playPosition) {
        this.playPosition = playPosition;
    }

    public boolean isRotating() {
        return isRotating;
    }
}

