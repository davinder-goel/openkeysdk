package com.openkey.sdk.api.request;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback<T> implements Callback<T> {
    @SuppressWarnings("unused")
    private static final String TAG = "RetrofitCallback";
    private final Callback<T> mCallback;

    public RetrofitCallback(Callback<T> callback) {
        this.mCallback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        // Do application relavent custom operation like manupulating reponse etc.
        mCallback.onResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        // Handle error etc.
        mCallback.onFailure(call, t);
    }
}