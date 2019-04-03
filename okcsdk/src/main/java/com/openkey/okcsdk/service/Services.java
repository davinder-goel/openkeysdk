package com.openkey.okcsdk.service;

import com.openkey.okcsdk.model.FetchKeyResponse;
import com.openkey.okcsdk.model.Status;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * @author OpenKey Inc.
 * <p>
 * This will provide the methods to call a web service via retrofit 2.3.0,
 * with proper Api models and responses
 */
public interface Services {


    @Headers({"Accept: application/json",
            "Cache-Control: no-cache"})
    @GET("api/v1/mobile_keys")
    Call<FetchKeyResponse> fetchKeys(@Header("Authorization") String Authorization);

    //-----------------------------------------------------------------------------------------------------------------|
    @Headers({"Accept: application/json",
            "Cache-Control: no-cache"})
    @GET("api/v1/mobile_keys/key/{id}")
    Call<Status> sendOpenDoorData(@Header("Authorization") String Authorization, @Path("id") int roomId);
    //-----------------------------------------------------------------------------------------------------------------|

}
