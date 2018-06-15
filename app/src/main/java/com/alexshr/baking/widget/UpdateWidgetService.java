package com.alexshr.baking.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.alexshr.baking.AppConstants;
import com.alexshr.baking.R;
import com.alexshr.baking.api.RecipesRepository;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.alexshr.baking.AppConstants.WIDGET_PREFIX;

//request api and update every widget
public class UpdateWidgetService extends Service {

    @Inject
    RecipesRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);

        /*using foreground service in Android 8.0
        https://stackoverflow.com/a/45047542/2886841
        https://stackoverflow.com/a/46449975/2886841*/
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "Baking app";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Baking app",
                    NotificationManager.IMPORTANCE_NONE);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Baking recipes was checking for update")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        int[] widgetIds = intent.getIntArrayExtra(AppConstants.WIDGET_IDS_KEY);

        repository.getApiObservable()
                .doOnError(Timber::e)
                .doFinally(() -> {
                    for (int id : widgetIds) {
                        updateAppWidget(manager, id);
                    }
                    stopSelf(startId);
                })
                .subscribe();

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateAppWidget(AppWidgetManager manager, int widgetId) {

        if (!Hawk.contains(WIDGET_PREFIX + widgetId)) {
            //https://stackoverflow.com/questions/9144262/android-widget-show-configuration-activity-before-widget-is-added-to-the-screen/12236443#12236443
            Timber.w("It seems widget id=%s hasn't been configurated yet (api bug)", widgetId);
            return;
        }

        int recipeId = Hawk.get(WIDGET_PREFIX + widgetId);
        String recipeName = repository.getRecipeName(recipeId);
        Timber.d("widgetId=%d, recipeId=%d, recipeName=%s", widgetId, recipeId, recipeName);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.recipeTv, recipeName);

        //region ================= setup list filling =================
        Intent intent = new Intent(this, IngredientsWidgetService.class);
        //using data to provide unique intent
        intent.setData(Uri.fromParts("content", String.valueOf(widgetId), null));
        views.setRemoteAdapter(R.id.ingredientsLv, intent);
        //endregion

        manager.updateAppWidget(widgetId, views);
    }
}
