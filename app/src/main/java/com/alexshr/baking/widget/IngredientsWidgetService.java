package com.alexshr.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.alexshr.baking.AppConstants;
import com.alexshr.baking.R;
import com.alexshr.baking.api.RecipesRepository;
import com.alexshr.baking.data.Ingredient;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class IngredientsWidgetService extends RemoteViewsService {

    @Inject
    RecipesRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewFactory(getApplicationContext(), intent);
    }

    class RemoteViewFactory implements RemoteViewsFactory {
        private final Context context;
        private final Intent intent;
        private String recipeName = "widget error";
        private List<Ingredient> ingredients = new ArrayList<>();

        public RemoteViewFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
            Timber.d("creating ...");
        }

        @Override
        public RemoteViews getViewAt(int pos) {
            Ingredient ingr = ingredients.get(pos);

            RemoteViews remoteViews =
                    new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_item);

            remoteViews.setTextViewText(R.id.ingredientTv,
                    String.format("%s (%s%s)", ingr.getIngredientName(), ingr.getQuantity(), ingr.getMeasure()));

            return remoteViews;
        }

        @Override
        //sync loading from db
        public void onDataSetChanged() {
            int appWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
            int recipeId = Hawk.get(AppConstants.WIDGET_PREFIX + appWidgetId);

            recipeName = repository.getRecipeName(recipeId);
            ingredients = repository.getIngredients(recipeId);

            Timber.d("appWidgetId=%d, recipeId=%d, recipeName=%s, ingredients=%s", appWidgetId, recipeId, recipeName, ingredients);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
