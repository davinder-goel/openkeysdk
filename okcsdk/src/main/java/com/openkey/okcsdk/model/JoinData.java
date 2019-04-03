package com.openkey.okcsdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mobile_key_id")
    @Expose
    private Integer mobileKeyId;
    @SerializedName("property_lock_id")
    @Expose
    private Integer propertyLockId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMobileKeyId() {
        return mobileKeyId;
    }

    public void setMobileKeyId(Integer mobileKeyId) {
        this.mobileKeyId = mobileKeyId;
    }

    public Integer getPropertyLockId() {
        return propertyLockId;
    }

    public void setPropertyLockId(Integer propertyLockId) {
        this.propertyLockId = propertyLockId;
    }

}