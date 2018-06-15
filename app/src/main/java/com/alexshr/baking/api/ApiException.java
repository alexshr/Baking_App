package com.alexshr.baking.api;

import java.io.IOException;

import retrofit2.Response;
import timber.log.Timber;

public class ApiException extends Exception {
    private int mCode;
    private String message;

    public static ApiException parseError(Response<?> response) {
        try {
            ApiException apiExeption = new ApiException();
            apiExeption.mCode = response.code();
            apiExeption.message = "http error: response code=" + apiExeption.mCode;
            if (response.errorBody() != null)
                apiExeption.message += "; " + response.errorBody().string();

            Timber.e(apiExeption);
            return apiExeption;
        } catch (IOException e) {
            Timber.e(e);
            return new ApiException();
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String sb = "ApiExeption{" + "message=" + getMessage() +
                ", mCode=" + mCode +
                '}';
        return sb;
    }
}
