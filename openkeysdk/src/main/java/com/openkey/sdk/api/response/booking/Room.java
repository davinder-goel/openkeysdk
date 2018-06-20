package com.openkey.sdk.api.response.booking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("entrava_hotel_id")
    @Expose
    private Integer entravaHotelId;
    @SerializedName("room_type_id")
    @Expose
    private Integer roomTypeId;
    @SerializedName("entrava_room_id")
    @Expose
    private String entravaRoomId;
    @SerializedName("entrava_is_public")
    @Expose
    private Integer entravaIsPublic;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("room_no")
    @Expose
    private String roomNo;
    @SerializedName("lock_key")
    @Expose
    private String lockKey;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("technology")
    @Expose
    private String technology;
    @SerializedName("master_tag_id")
    @Expose
    private String masterTagId;
    @SerializedName("lock_id")
    @Expose
    private String lockId;
    @SerializedName("is_suite")
    @Expose
    private Boolean isSuite;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("created_to")
    @Expose
    private String createdTo;
    @SerializedName("modified_by")
    @Expose
    private Integer modifiedBy;
    @SerializedName("modified_to")
    @Expose
    private String modifiedTo;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("_joinData")
    @Expose
    private JoinData joinData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getEntravaHotelId() {
        return entravaHotelId;
    }

    public void setEntravaHotelId(Integer entravaHotelId) {
        this.entravaHotelId = entravaHotelId;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getEntravaRoomId() {
        return entravaRoomId;
    }

    public void setEntravaRoomId(String entravaRoomId) {
        this.entravaRoomId = entravaRoomId;
    }

    public Integer getEntravaIsPublic() {
        return entravaIsPublic;
    }

    public void setEntravaIsPublic(Integer entravaIsPublic) {
        this.entravaIsPublic = entravaIsPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getMasterTagId() {
        return masterTagId;
    }

    public void setMasterTagId(String masterTagId) {
        this.masterTagId = masterTagId;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Boolean getIsSuite() {
        return isSuite;
    }

    public void setIsSuite(Boolean isSuite) {
        this.isSuite = isSuite;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(String createdTo) {
        this.createdTo = createdTo;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedTo() {
        return modifiedTo;
    }

    public void setModifiedTo(String modifiedTo) {
        this.modifiedTo = modifiedTo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public JoinData getJoinData() {
        return joinData;
    }

    public void setJoinData(JoinData joinData) {
        this.joinData = joinData;
    }

}