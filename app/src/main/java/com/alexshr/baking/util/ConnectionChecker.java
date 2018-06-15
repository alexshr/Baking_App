package com.alexshr.baking.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class ConnectionChecker {

    private Context context;

    @Inject
    public ConnectionChecker(Application context) {
        this.context = context;
    }

    public Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public Observable<Boolean> getConnectionObservable() {
        return Observable.just(isConnected());
    }
}
