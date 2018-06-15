package com.alexshr.baking.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.alexshr.baking.AppConstants;
import com.alexshr.baking.R;
import com.alexshr.baking.di.Injectable;
import com.alexshr.baking.ui.MainViewModel;
import com.alexshr.baking.ui.RecipesAdapter;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import timber.log.Timber;

public class WidgetConfigActivity extends AppCompatActivity implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    Intent result = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("");

        //setResult(RESULT_CANCELED,result);

        //ActivityWidgetConfigBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_widget_config);
        setContentView(R.layout.activity_widget_config);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Timber.d("extras not null ...");
            appWidgetId = extras
                    .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Timber.e("invalid widgetId, extras=%s", extras);
            finish();
            return;
        }

        RecipesAdapter rvAdapter = new RecipesAdapter(this::onRecipeClick);

        RecyclerView rv = findViewById(R.id.recipesRv);
        rv.setAdapter(rvAdapter);

        MainViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, rvAdapter::replace);
        viewModel.requestApi();
    }

    private void onRecipeClick(int recipeId) {

        Hawk.put(AppConstants.WIDGET_PREFIX + appWidgetId, recipeId);

        Timber.d("before start update service; recipeId=%d, appWidgetId=%d", recipeId, appWidgetId);
        AppWidget.startUpdateService(this, appWidgetId);

        setResult(RESULT_OK, result);
        finish();
    }
}
