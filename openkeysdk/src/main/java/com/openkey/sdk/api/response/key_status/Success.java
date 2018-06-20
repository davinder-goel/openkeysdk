package com.openkey.sdk.api.response.key_status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Success {

    @SerializedName("booking_id")
    @Expose
    private Integer bookingId;
    @SerializedName("guest_id")
    @Expose
    private Integer guestId;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("parent_booking_id")
    @Expose
    private Integer parentBookingId;
    @SerializedName("room_type_id")
    @Expose
    private Integer roomTypeId;
    @SerializedName("check_in")
    @Expose
    private String checkIn;
    @SerializedName("key_issued")
    @Expose
    private Integer keyIssued;
    @SerializedName("check_out")
    @Expose
    private String checkOut;
    @SerializedName("checkin_status")
    @Expose
    private String checkinStatus;
    @SerializedName("guest_check_in_time")
    @Expose
    private String guestCheckInTime;
    @SerializedName("booking_code")
    @Expose
    private Integer bookingCode;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("recp_id")
    @Expose
    private Integer recpId;
    @SerializedName("key_issued_time")
    @Expose
    private Integer keyIssuedTime;
    @SerializedName("is_updated")
    @Expose
    private Boolean isUpdated;
    @SerializedName("is_key_pending")
    @Expose
    private Boolean isKeyPending;
    @SerializedName("key_status")
    @Expose
    private Integer keyStatus;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getParentBookingId() {
        return parentBookingId;
    }

    public void setParentBookingId(Integer parentBookingId) {
        this.parentBookingId = parentBookingId;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public Integer getKeyIssued() {
        return keyIssued;
    }

    public void setKeyIssued(Integer keyIssued) {
        this.keyIssued = keyIssued;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getCheckinStatus() {
        return checkinStatus;
    }

    public void setCheckinStatus(String checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    public String getGuestCheckInTime() {
        return guestCheckInTime;
    }

    public void setGuestCheckInTime(String guestCheckInTime) {
        this.guestCheckInTime = guestCheckInTime;
    }

    public Integer getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(Integer bookingCode) {
        this.bookingCode = bookingCode;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRecpId() {
        return recpId;
    }

    public void setRecpId(Integer recpId) {
        this.recpId = recpId;
    }

    public Integer getKeyIssuedTime() {
        return keyIssuedTime;
    }

    public void setKeyIssuedTime(Integer keyIssuedTime) {
        this.keyIssuedTime = keyIssuedTime;
    }

    public Boolean getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(Boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public Boolean getIsKeyPending() {
        return isKeyPending;
    }

    public void setIsKeyPending(Boolean isKeyPending) {
        this.isKeyPending = isKeyPending;
    }

    public Integer getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(Integer keyStatus) {
        this.keyStatus = keyStatus;
    }

}