package com.alexshr.baking.rx;

import com.alexshr.baking.AppConfig;
import com.alexshr.baking.api.ApiException;
import com.alexshr.baking.util.ConnectionChecker;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

import static com.alexshr.baking.AppConfig.ETAG_HEADER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RestCallTransformer<R> implements ObservableTransformer<Response<R>, R> {

    ConnectionChecker conChecker;

    @Inject
    public RestCallTransformer(ConnectionChecker conChecker) {
        this.conChecker = conChecker;
    }

    @Override
    public ObservableSource<R> apply(Observable<Response<R>> responseObservable) {
        return conChecker.getConnectionObservable()
                .filter(isAvailable -> isAvailable)
                .flatMap(isAvailable -> getApiResponseObservable(responseObservable))
                .subscribeOn(Schedulers.io());
    }

    private Observable<R> getApiResponseObservable(Observable<Response<R>> responseObservable) {
        return responseObservable
                .doOnNext(this::saveETag)
                .flatMap(response -> {
                    switch (response.code()) {
                        case 200:
                        case 201:
                        case 202:
                        case 204:
                            return Observable.just(response.body());
                        case 304:
                            return Observable.empty();
                        default:
                            return Observable.error(ApiException.parseError(response));
                    }
                })
                .retryWhen(errorObservable -> errorObservable
                        .zipWith(Observable.range(1, AppConfig.RETRY_COUNT + 1), (e, attempt) -> {
                            Timber.e("network request error after attempt %d; max count fo retry %d", attempt, AppConfig.RETRY_COUNT);
                            if (attempt == AppConfig.RETRY_COUNT + 1)
                                throw new RuntimeException(e);
                            else
                                return attempt;
                        })
                        .flatMap(attempt -> {
                            long delay = getExponentialDelayInMs(attempt, AppConfig.RETRY_INITIAL_DELAY);
                            Timber.d("delay %d ms; after attempt %d; max count fo retry %d", delay, attempt, AppConfig.RETRY_COUNT);
                            return Observable.timer(delay, MILLISECONDS);
                        })
                );
    }

    private long getExponentialDelayInMs(int runCount, long initialBackOffInMs) {
        return initialBackOffInMs * (long) Math.pow(2.0D, (double) Math.max(0, runCount - 1));
    }

    private void saveETag(Response<R> res) {
        String eTag = res.headers().get(ETAG_HEADER);
        if (eTag != null) Hawk.put(ETAG_HEADER, eTag);
    }
}
