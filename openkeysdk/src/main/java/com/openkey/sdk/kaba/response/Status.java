
package com.openkey.sdk.kaba.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2015 OpenKey. All Rights Reserved
 *
 * @author OpenKey Inc.
 */
public class Status {

    @SerializedName("code")
    @Expose
    private Long mCode;
    @Expose
    private String mDescription;

    public Long getCode() {
        return mCode;
    }

    public void setCode(Long code) {
        mCode = code;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

}
