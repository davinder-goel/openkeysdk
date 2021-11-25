package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotelCommonArea {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("pass_level")
    @Expose
    private Integer passLevel;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_auto")
    @Expose
    private Boolean isAuto;
    @SerializedName("entrava")
    @Expose
    private Object entrava;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("deleted")
    @Expose
    private Object deleted;
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

    public Integer getPassLevel() {
        return passLevel;
    }

    public void setPassLevel(Integer passLevel) {
        this.passLevel = passLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(Boolean isAuto) {
        this.isAuto = isAuto;
    }

    public Object getEntrava() {
        return entrava;
    }

    public void setEntrava(Object entrava) {
        this.entrava = entrava;
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

    public JoinData getJoinData() {
        return joinData;
    }

    public void setJoinData(JoinData joinData) {
        this.joinData = joinData;
    }
}