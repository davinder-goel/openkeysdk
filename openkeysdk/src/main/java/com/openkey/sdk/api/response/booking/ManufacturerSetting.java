package com.openkey.sdk.api.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ManufacturerSetting {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}