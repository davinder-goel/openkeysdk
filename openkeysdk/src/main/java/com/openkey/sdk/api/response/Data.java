/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("isAuthorized")
    @Expose
    private Boolean isAuthorized;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;

    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
