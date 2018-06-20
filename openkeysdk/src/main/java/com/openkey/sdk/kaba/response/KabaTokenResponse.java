
package com.openkey.sdk.kaba.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2015 OpenKey. All Rights Reserved
 *
 * @author OpenKey Inc.
 */
public class KabaTokenResponse {

    @SerializedName("prepareDirectWalletRegistrationResponse")
    @Expose
    private PrepareDirectWalletRegistrationResponse mPrepareDirectWalletRegistrationResponse;

    public PrepareDirectWalletRegistrationResponse getPrepareDirectWalletRegistrationResponse() {
        return mPrepareDirectWalletRegistrationResponse;
    }

    public void setPrepareDirectWalletRegistrationResponse(PrepareDirectWalletRegistrationResponse prepareDirectWalletRegistrationResponse) {
        mPrepareDirectWalletRegistrationResponse = prepareDirectWalletRegistrationResponse;
    }

}
