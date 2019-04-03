package com.openkey.okcsdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyLock {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("mac")
    @Expose
    private String mac;
    @SerializedName("_joinData")
    @Expose
    private JoinData joinData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public JoinData getJoinData() {
        return joinData;
    }

    public void setJoinData(JoinData joinData) {
        this.joinData = joinData;
    }

}