package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdditionalRoom {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("entrava")
    @Expose
    private String entrava;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("pms_title")
    @Expose
    private String pmsTitle;
    @SerializedName("battery_level")
    @Expose
    private Float batteryLevel;
    @SerializedName("is_suite")
    @Expose
    private Boolean isSuite;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("deleted")
    @Expose
    private Object deleted;
    @SerializedName("guests_limit")
    @Expose
    private Integer guestsLimit;
    @SerializedName("_joinData")
    @Expose
    private JoinData joinData;

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

    public String getEntrava() {
        return entrava;
    }

    public void setEntrava(String entrava) {
        this.entrava = entrava;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPmsTitle() {
        return pmsTitle;
    }

    public void setPmsTitle(String pmsTitle) {
        this.pmsTitle = pmsTitle;
    }

    public Float getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Boolean getIsSuite() {
        return isSuite;
    }

    public void setIsSuite(Boolean isSuite) {
        this.isSuite = isSuite;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Object getDeleted() {
        return deleted;
    }

    public void setDeleted(Object deleted) {
        this.deleted = deleted;
    }

    public Integer getGuestsLimit() {
        return guestsLimit;
    }

    public void setGuestsLimit(Integer guestsLimit) {
        this.guestsLimit = guestsLimit;
    }

    public JoinData getJoinData() {
        return joinData;
    }

    public void setJoinData(JoinData joinData) {
        this.joinData = joinData;
    }

}