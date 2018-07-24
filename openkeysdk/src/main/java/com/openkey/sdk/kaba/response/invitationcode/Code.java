package com.openkey.sdk.kaba.response.invitationcode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Code {

    @SerializedName("prepareDirectWalletRegistrationResponse")
    @Expose
    private PrepareDirectWalletRegistrationResponse prepareDirectWalletRegistrationResponse;

    public PrepareDirectWalletRegistrationResponse getPrepareDirectWalletRegistrationResponse() {
        return prepareDirectWalletRegistrationResponse;
    }

    public void setPrepareDirectWalletRegistrationResponse(PrepareDirectWalletRegistrationResponse prepareDirectWalletRegistrationResponse) {
        this.prepareDirectWalletRegistrationResponse = prepareDirectWalletRegistrationResponse;
    }

}