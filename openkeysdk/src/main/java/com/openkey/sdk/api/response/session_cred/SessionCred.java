package com.openkey.sdk.api.response.session_cred;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionCred {

    @SerializedName("kabaSdkParams")
    @Expose
    private KabaSdkParams kabaSdkParams;

    public KabaSdkParams getKabaSdkParams() {
        return kabaSdkParams;
    }

    public void setKabaSdkParams(KabaSdkParams kabaSdkParams) {
        this.kabaSdkParams = kabaSdkParams;
    }

}