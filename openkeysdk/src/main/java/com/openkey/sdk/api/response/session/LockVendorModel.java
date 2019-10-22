package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LockVendorModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lock_vendor_id")
    @Expose
    private Integer lockVendorId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("stay_screen_phrase")
    @Expose
    private String stayScreenPhrase;
    @SerializedName("key_screen_phrase")
    @Expose
    private String keyScreenPhrase;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("lock_vendor")
    @Expose
    private LockVendor lockVendor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLockVendorId() {
        return lockVendorId;
    }

    public void setLockVendorId(Integer lockVendorId) {
        this.lockVendorId = lockVendorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getStayScreenPhrase() {
        return stayScreenPhrase;
    }

    public void setStayScreenPhrase(String stayScreenPhrase) {
        this.stayScreenPhrase = stayScreenPhrase;
    }

    public String getKeyScreenPhrase() {
        return keyScreenPhrase;
    }

    public void setKeyScreenPhrase(String keyScreenPhrase) {
        this.keyScreenPhrase = keyScreenPhrase;
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

    public LockVendor getLockVendor() {
        return lockVendor;
    }

    public void setLockVendor(LockVendor lockVendor) {
        this.lockVendor = lockVendor;
    }

}