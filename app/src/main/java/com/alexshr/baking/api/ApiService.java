package com.alexshr.baking.api;

import com.alexshr.baking.data.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface ApiService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Observable<Response<List<Recipe>>> getRecipesObservable();
}
