package com.alexshr.baking.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.alexshr.baking.api.RecipesRepository;

import timber.log.Timber;

public abstract class AbstractViewModel<R> extends ViewModel {

    private RecipesRepository repository;

    private LiveData<R> liveData;
    private MutableLiveData<Throwable> errorData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressData = new MutableLiveData<>();

    public AbstractViewModel(RecipesRepository repository) {
        this.repository = repository;
        progressData.postValue(false);
    }

    public void requestApi() {
        progressData.postValue(true);
        repository.getApiObservable()
                .doOnError(errorData::postValue)
                .doFinally(() -> progressData.postValue(false))
                .subscribe();
    }

    public MutableLiveData<Throwable> getErrorData() {
        return errorData;
    }

    public MutableLiveData<Boolean> getProgressData() {
        return progressData;
    }

    protected LiveData<R> getLiveData() {
        return liveData;
    }

    protected void setLiveData(LiveData<R> data) {
        this.liveData = data;
        data.observeForever(obj -> Timber.d("test data observer: %s", data.getValue()));
    }

    public RecipesRepository getRepository() {
        return repository;
    }
}
