package com.openkey.okcsdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("property_id")
    @Expose
    private Integer propertyId;
    @SerializedName("property_locks")
    @Expose
    private ArrayList<PropertyLock> propertyLocks = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public List<PropertyLock> getPropertyLocks() {
        return propertyLocks;
    }

    public void setPropertyLocks(ArrayList<PropertyLock> propertyLocks) {
        this.propertyLocks = propertyLocks;
    }

}