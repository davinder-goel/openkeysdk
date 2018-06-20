
package com.openkey.sdk.kaba.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2015 OpenKey. All Rights Reserved
 *
 * @author OpenKey Inc.
 */
public class PrepareDirectWalletRegistrationResponse {

    @SerializedName("status")
    @Expose
    private Status mStatus;
    @SerializedName("token")
    @Expose
    private String mToken;

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

}
