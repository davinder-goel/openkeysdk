package com.openkey.sdk.api.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("guest_id")
    @Expose
    private Integer guestId;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("parent_session_id")
    @Expose
    private Integer parentSessionId;
    @SerializedName("room_type_id")
    @Expose
    private Integer roomTypeId;
    @SerializedName("check_in")
    @Expose
    private String checkIn;
    @SerializedName("check_out")
    @Expose
    private String checkOut;
    @SerializedName("key_issued")
    @Expose
    private Integer keyIssued;
    @SerializedName("checkin_status")
    @Expose
    private String checkinStatus;
    @SerializedName("guest_check_in_time")
    @Expose
    private String guestCheckInTime;
    @SerializedName("session_code")
    @Expose
    private Integer sessionCode;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("key_issued_time")
    @Expose
    private Integer keyIssuedTime;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("booking_id")
    @Expose
    private Integer bookingId;
    @SerializedName("parent_booking_id")
    @Expose
    private Integer parentBookingId;
    @SerializedName("booking_code")
    @Expose
    private Integer bookingCode;
    @SerializedName("recp_id")
    @Expose
    private Integer recpId;
    @SerializedName("is_updated")
    @Expose
    private Boolean isUpdated;
    @SerializedName("is_key_pending")
    @Expose
    private Boolean isKeyPending;
    @SerializedName("hotel")
    @Expose
    private Hotel hotel;
    @SerializedName("common_areas")
    @Expose
    private List<Object> commonAreas = null;
    @SerializedName("rooms")
    @Expose
    private List<Room> rooms = null;

    @SerializedName("guest")
    @Expose
    private Guest guest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
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

    public Integer getParentSessionId() {
        return parentSessionId;
    }

    public void setParentSessionId(Integer parentSessionId) {
        this.parentSessionId = parentSessionId;
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

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getKeyIssued() {
        return keyIssued;
    }

    public void setKeyIssued(Integer keyIssued) {
        this.keyIssued = keyIssued;
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

    public Integer getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(Integer sessionCode) {
        this.sessionCode = sessionCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getKeyIssuedTime() {
        return keyIssuedTime;
    }

    public void setKeyIssuedTime(Integer keyIssuedTime) {
        this.keyIssuedTime = keyIssuedTime;
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

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getParentBookingId() {
        return parentBookingId;
    }

    public void setParentBookingId(Integer parentBookingId) {
        this.parentBookingId = parentBookingId;
    }

    public Integer getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(Integer bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Integer getRecpId() {
        return recpId;
    }

    public void setRecpId(Integer recpId) {
        this.recpId = recpId;
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

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public List<Object> getCommonAreas() {
        return commonAreas;
    }

    public void setCommonAreas(List<Object> commonAreas) {
        this.commonAreas = commonAreas;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

}