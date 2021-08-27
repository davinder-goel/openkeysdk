package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("parent_session_id")
    @Expose
    private Integer parentSessionId;
    @SerializedName("guest_id")
    @Expose
    private Integer guestId;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("hotel_room_id")
    @Expose
    private Integer hotelRoomId;
    @SerializedName("mobile_key_status_id")
    @Expose
    private Integer mobileKeyStatusId;
    @SerializedName("host_user_id")
    @Expose
    private Integer hostUserId;
    @SerializedName("is_personalized")
    @Expose
    private Boolean isPersonalized;
    @SerializedName("check_in")
    @Expose
    private String checkIn;
    @SerializedName("check_out")
    @Expose
    private String checkOut;
    @SerializedName("last_door_opened")
    @Expose
    private Object lastDoorOpened;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;

    @SerializedName("guest")
    @Expose
    private Guest guest;

    @SerializedName("hotel")
    @Expose
    private Hotel hotel;

    @SerializedName("additional_rooms")
    @Expose
    private ArrayList<AdditionalRoom> additionalRooms = null;

    @SerializedName("hotel_room")
    @Expose
    private HotelRoom hotelRoom;
    @SerializedName("mobile_key_status")
    @Expose
    private MobileKeyStatus mobileKeyStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentSessionId() {
        return parentSessionId;
    }

    public void setParentSessionId(Integer parentSessionId) {
        this.parentSessionId = parentSessionId;
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

    public Integer getHotelRoomId() {
        return hotelRoomId;
    }

    public void setHotelRoomId(Integer hotelRoomId) {
        this.hotelRoomId = hotelRoomId;
    }

    public Integer getMobileKeyStatusId() {
        return mobileKeyStatusId;
    }

    public void setMobileKeyStatusId(Integer mobileKeyStatusId) {
        this.mobileKeyStatusId = mobileKeyStatusId;
    }


    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }


    public Integer getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Integer hostUserId) {
        this.hostUserId = hostUserId;
    }

    public Boolean getIsPersonalized() {
        return isPersonalized;
    }

    public void setIsPersonalized(Boolean isPersonalized) {
        this.isPersonalized = isPersonalized;
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

    public Object getLastDoorOpened() {
        return lastDoorOpened;
    }

    public void setLastDoorOpened(Object lastDoorOpened) {
        this.lastDoorOpened = lastDoorOpened;
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

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public HotelRoom getHotelRoom() {
        return hotelRoom;
    }

    public void setHotelRoom(HotelRoom hotelRoom) {
        this.hotelRoom = hotelRoom;
    }

    public MobileKeyStatus getMobileKeyStatus() {
        return mobileKeyStatus;
    }

    public void setMobileKeyStatus(MobileKeyStatus mobileKeyStatus) {
        this.mobileKeyStatus = mobileKeyStatus;
    }

    public Boolean getPersonalized() {
        return isPersonalized;
    }

    public void setPersonalized(Boolean personalized) {
        isPersonalized = personalized;
    }

    public ArrayList<AdditionalRoom> getAdditionalRooms() {
        return additionalRooms;
    }

    public void setAdditionalRooms(ArrayList<AdditionalRoom> additionalRooms) {
        this.additionalRooms = additionalRooms;
    }
}