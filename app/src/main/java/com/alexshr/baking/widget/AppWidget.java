package com.alexshr.baking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.alexshr.baking.AppConstants;

import timber.log.Timber;

public class AppWidget extends AppWidgetProvider {

    public static void startUpdateService(Context context, int... widgetIds) {
        Timber.d("appWidgetIds=%s, length=%d", widgetIds, widgetIds.length);

        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.putExtra(AppConstants.WIDGET_IDS_KEY, widgetIds);
        ContextCompat.startForegroundService(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("appWidgetIds=%s", appWidgetIds);

        startUpdateService(context, appWidgetIds);
    }
}

