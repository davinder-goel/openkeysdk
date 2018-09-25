package com.openkey.sdk.api.response.Mobile_key_status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("key_issued")
    @Expose
    private Boolean keyIssued;

    public Boolean getKeyIssued() {
        return keyIssued;
    }

    public void setKeyIssued(Boolean keyIssued) {
        this.keyIssued = keyIssued;
    }

}