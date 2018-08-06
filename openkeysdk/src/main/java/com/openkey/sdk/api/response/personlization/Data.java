package com.openkey.sdk.api.response.personlization;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("is_personalized")
    @Expose
    private Boolean isPersonalized;
    @SerializedName("key_issued")
    @Expose
    private Boolean keyIssued;

    public Boolean getIsPersonalized() {
        return isPersonalized;
    }

    public void setIsPersonalized(Boolean isPersonalized) {
        this.isPersonalized = isPersonalized;
    }

    public Boolean getKeyIssued() {
        return keyIssued;
    }

    public void setKeyIssued(Boolean keyIssued) {
        this.keyIssued = keyIssued;
    }

}