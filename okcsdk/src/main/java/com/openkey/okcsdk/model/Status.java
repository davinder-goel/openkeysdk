package com.openkey.okcsdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {
    @SerializedName("success")
    @Expose
    private Boolean success;


    @SerializedName("data")
    @Expose
    private StatusData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public StatusData getData() {
        return data;
    }

    public void setData(StatusData data) {
        this.data = data;
    }

}