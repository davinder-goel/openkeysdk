package com.openkey.sdk.kaba.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrepareCustomRegistrationResponse {

    @SerializedName("status")
    @Expose
    private com.openkey.sdk.kaba.response.invitationcode.Status status;
    @SerializedName("token")
    @Expose
    private String token;

    public com.openkey.sdk.kaba.response.invitationcode.Status getStatus() {
        return status;
    }

    public void setStatus(com.openkey.sdk.kaba.response.invitationcode.Status status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}