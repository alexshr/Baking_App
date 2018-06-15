package com.alexshr.baking.ui.recipe;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.alexshr.baking.R;
import com.alexshr.baking.di.Injectable;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements Injectable, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    RecipeActivityViewModel viewModel;

    com.alexshr.baking.databinding.ActivityDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeActivityViewModel.class);

        viewModel.onActivityCreate(this, savedInstanceState);

        viewModel.getActionBarTitleData().observe(this, getSupportActionBar()::setTitle);
        viewModel.getActionBarSubtitleData().observe(this, getSupportActionBar()::setSubtitle);
        viewModel.getErrorData().observe(this, this::showError);
    }

    @Override
    //"back" instead of "up"
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPressed();
        super.onBackPressed();
    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void showError(Throwable error) {
        Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(viewModel.fillState(outState));
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //viewModel.onActivityDestroy();
        Timber.d(this + "");
    }
}
