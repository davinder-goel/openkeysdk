package com.openkey.sdk.api.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinData {

    @SerializedName("booking_d_id")
    @Expose
    private Integer bookingDId;
    @SerializedName("booking_id")
    @Expose
    private Integer bookingId;
    @SerializedName("room_id")
    @Expose
    private Integer roomId;
    @SerializedName("guest_id")
    @Expose
    private Integer guestId;
    @SerializedName("delegated_guest_id")
    @Expose
    private Integer delegatedGuestId;
    @SerializedName("access_level")
    @Expose
    private String accessLevel;
    @SerializedName("key_issued")
    @Expose
    private String keyIssued;
    @SerializedName("delegated_key")
    @Expose
    private String delegatedKey;

    public Integer getBookingDId() {
        return bookingDId;
    }

    public void setBookingDId(Integer bookingDId) {
        this.bookingDId = bookingDId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public Integer getDelegatedGuestId() {
        return delegatedGuestId;
    }

    public void setDelegatedGuestId(Integer delegatedGuestId) {
        this.delegatedGuestId = delegatedGuestId;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getKeyIssued() {
        return keyIssued;
    }

    public void setKeyIssued(String keyIssued) {
        this.keyIssued = keyIssued;
    }

    public String getDelegatedKey() {
        return delegatedKey;
    }

    public void setDelegatedKey(String delegatedKey) {
        this.delegatedKey = delegatedKey;
    }

}