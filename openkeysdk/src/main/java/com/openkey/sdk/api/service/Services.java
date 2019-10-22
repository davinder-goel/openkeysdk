package com.openkey.sdk.api.service;

import com.openkey.sdk.api.model.KeyStatusRequest;
import com.openkey.sdk.api.model.SdkLogRequest;
import com.openkey.sdk.api.response.Mobile_key_status.KeyStatusResp;
import com.openkey.sdk.api.response.invitation_code.InvitationCode;
import com.openkey.sdk.api.response.key_status.KeyStatusResponse;
import com.openkey.sdk.api.response.logaction.LogActionResponse;
import com.openkey.sdk.api.response.mobile_key_response.MobileKeyResponse;
import com.openkey.sdk.api.response.personlization.PersonlizationResponse;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.kaba.response.invitationcode.KabaToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author OpenKey Inc.
 * <p>
 * This will provide the methods to call a web service via retrofit 2.3.0,
 * with proper Api models and responses
 */
public interface Services {

    //V5
//    String key = "45144534-f181-4011-b142-5d53162a95c8";
//    String key = Utilities.getInstance().getValue(Constants.UUID,"",)
    String type = "application/vnd.assaabloy-com.credential-2.3+json";

    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk/v5/sessions")
    Call<SessionResponse> getSession(@Header("Authorization") String Authorization);
    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk/v5/sessions/initializePersonalization.json")
    Call<InvitationCode> initializePersonalization(@Header("Authorization") String Authorization);
    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk/v5/sessions/9/session_mobile_keys")
    Call<MobileKeyResponse> getMobileKey(@Header("Authorization") String Authorization);
    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk/v5/sessions/setPersonalization.json")
    Call<PersonlizationResponse> setPeronalizationComplete(@Header("Authorization") String Authorization);
    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk/v5/sessions/initializePersonalization.json")
    Call<KabaToken> initializePersonalizationForKaba(@Header("Authorization") String Authorization);
    //-----------------------------------------------------------------------------------------------------------------|

    @POST("sdk/v5/sessions/setMobileKeyStatus")
    Call<KeyStatusResponse> setKeyStatus(@Header("Authorization") String Authorization,
                                         @Body KeyStatusRequest keyStatusRequest);
    //-----------------------------------------------------------------------------------------------------------------|

    @GET("sdk_api/v1/sessions/{session_id}/mobile_keys/getStatus")
    Call<KeyStatusResp> getStatus(@Header("Authorization") String Authorization, @Path("session_id") String session_id);
    //-----------------------------------------------------------------------------------------------------------------|

    @POST("/sdk/v5/sessions/logAction")
    Call<LogActionResponse> logSDK(@Header("Authorization") String Authorization, @Body SdkLogRequest sdkLogRequest);
    //-----------------------------------------------------------------------------------------------------------------|
}