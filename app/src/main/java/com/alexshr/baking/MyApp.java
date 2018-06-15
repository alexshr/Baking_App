package com.alexshr.baking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.annotation.NonNull;

import com.alexshr.baking.di.AppInjector;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class MyApp extends Application implements HasActivityInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(this).build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree() {
                @SuppressLint("DefaultLocale")
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format("%s: %s:%d (%s)",
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber(),
                            Thread.currentThread().getName());
                }
            });
            AppInjector.init(this);
        }
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }
}
