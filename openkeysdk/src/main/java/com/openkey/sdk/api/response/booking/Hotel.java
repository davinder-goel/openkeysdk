package com.openkey.sdk.api.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hotel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotel_name")
    @Expose
    private String hotelName;
    @SerializedName("hotel_timezone_cn")
    @Expose
    private String hotelTimezoneCn;
    @SerializedName("manufacturer_setting")
    @Expose
    private ManufacturerSetting manufacturerSetting;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelTimezoneCn() {
        return hotelTimezoneCn;
    }

    public void setHotelTimezoneCn(String hotelTimezoneCn) {
        this.hotelTimezoneCn = hotelTimezoneCn;
    }

    public ManufacturerSetting getManufacturerSetting() {
        return manufacturerSetting;
    }

    public void setManufacturerSetting(ManufacturerSetting manufacturerSetting) {
        this.manufacturerSetting = manufacturerSetting;
    }

}