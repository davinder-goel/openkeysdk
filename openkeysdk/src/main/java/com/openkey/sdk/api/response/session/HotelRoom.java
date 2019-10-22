package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotelRoom {

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

    public String getEntrava() {
        return entrava;
    }

    public void setEntrava(String entrava) {
        this.entrava = entrava;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getSuite() {
        return isSuite;
    }

    public void setSuite(Boolean suite) {
        isSuite = suite;
    }

    public Integer getGuestsLimit() {
        return guestsLimit;
    }

    public void setGuestsLimit(Integer guestsLimit) {
        this.guestsLimit = guestsLimit;
    }
}
