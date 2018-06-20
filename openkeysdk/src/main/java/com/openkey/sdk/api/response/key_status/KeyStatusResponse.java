package com.openkey.sdk.api.response.key_status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeyStatusResponse {

    @SerializedName("success")
    @Expose
    private Success success;

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

}