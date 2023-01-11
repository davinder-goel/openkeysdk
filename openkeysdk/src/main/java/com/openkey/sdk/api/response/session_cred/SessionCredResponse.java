package com.openkey.sdk.api.response.session_cred;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionCredResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private SessionCred data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public SessionCred getData() {
        return data;
    }

    public void setData(SessionCred data) {
        this.data = data;
    }

}