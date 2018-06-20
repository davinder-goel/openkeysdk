package com.openkey.sdk.api.service;

import com.openkey.sdk.BuildConfig;
import com.openkey.sdk.api.model.CreateEndPoint;
import com.openkey.sdk.api.model.KeyStatusRequest;
import com.openkey.sdk.api.model.SdkLogRequest;
import com.openkey.sdk.api.response.EndPointResponse;
import com.openkey.sdk.api.response.Status;
import com.openkey.sdk.api.response.booking.BookingResponse;
import com.openkey.sdk.api.response.Mobile_key_status.KeyStatusResp;
import com.openkey.sdk.api.response.key_status.KeyStatusResponse;
import com.openkey.sdk.api.response.salto_key.BinaryKey;
import com.openkey.sdk.kaba.response.KabaTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This will provide the methods to call a web service via retrofit 2.3.0,
 *         with proper Api models and responses
 */
public interface Services {

    //Initialize  the header type for assa, these contain the demo credential's
    String type = "application/vnd.assaabloy-com.credential-2.3+json";


    //DEMO
    String credentials = "Basic b3BlbmtleS1kZW1vLXRubnQ6eFpKTGM1aHNTcTdKcDJ2d1FGMjU=";

    //LIVE
  //  String credentials = "Basic b3BlbmtleS10bm50OmtoU1NDeVY3cjlGRzl2U1Q0cTVx";


    @Headers({"Accept: " + "application/json"})
    @GET("sdk_api/v1/sessions")
    Call<BookingResponse> authenticateGuest(@Header("Authorization") String Authorization);

    @Headers({"Accept: " + "application/json"})
    @GET("sdk_api/v1/sessions/{session_id}/mobile_keys/getStatus")
    Call<KeyStatusResp> getStatus(@Header("Authorization") String Authorization, @Path("session_id") String session_id);


    @Headers({"Accept: " + "application/json"})
    @GET("sdk_api/v1/sessions/{session_id}/mobile_keys/getMobileKey")
    Call<BinaryKey> getMobileKey(@Header("Authorization") String Authorization, @Path("session_id") String session_id);

    @Headers({"Accept: " + "application/json"})
    @POST("sdk_api/v1/sessions/setPersonalization.json")
    Call<Status> setPeronalizationComplete(@Header("Authorization") String Authorization);

    @Headers({"Accept: " + "application/json"})
    @POST("/sdk_api/v1/sessions/{session_id}/mobile_keys/sdkLog")
    Call<Status> logSDK(@Header("Authorization") String Authorization, @Body SdkLogRequest sdkLogRequest);

    /**
     * ASSA WEB SERVICES
     */
    @Headers({"Accept: " + type, "Content-Type: " + type})
    @POST("/endpoint/invitation")
    Call<EndPointResponse> getInvitationCode(@Header("Authorization")String Authorization,@Body CreateEndPoint createEndPoint);

    @Headers({"Accept: " + type, "Content-Type: " + type})
    @DELETE("/endpoint/{id}")
    Call<Void> deleteEndPoint(@Header("Authorization")String Authorization,@Path("id") String id);


    /**
     * Get token from the KABA serverPrepareDirectWalletRegistrationRequest
     * Token
     *
     * @param token
     * @return
     */
    @POST("/connect/WalletServer/PrepareDirectWalletRegistration")
    Call<KabaTokenResponse> getRegistrationToken(@Body com.openkey.sdk.kaba.model.Token token);

    @Headers({"Accept: " + "application/json"})
    @POST("/sdk_api/v1/sessions/{session_id}/mobile_keys/setKeyStatus")
    Call<KeyStatusResponse> setKeyStatus(@Header("Authorization") String Authorization,
                                         @Path("session_id") String session_id,
                                         @Body KeyStatusRequest keyStatusRequest);
}
