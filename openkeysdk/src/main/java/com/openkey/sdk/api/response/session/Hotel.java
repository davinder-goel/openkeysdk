package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hotel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("app_id")
    @Expose
    private Integer appId;
    @SerializedName("lock_vendor_id")
    @Expose
    private Integer lockVendorId;
    @SerializedName("pms_type_id")
    @Expose
    private Integer pmsTypeId;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("check_in")
    @Expose
    private String checkIn;
    @SerializedName("check_out")
    @Expose
    private String checkOut;
    @SerializedName("late_check_out")
    @Expose
    private String lateCheckOut;
    @SerializedName("logo_url")
    @Expose
    private String logoUrl;
    @SerializedName("booking_url")
    @Expose
    private String bookingUrl;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("deleted")
    @Expose
    private Object deleted;
    @SerializedName("lock_vendor")
    @Expose
    private LockVendor lockVendor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getLockVendorId() {
        return lockVendorId;
    }

    public void setLockVendorId(Integer lockVendorId) {
        this.lockVendorId = lockVendorId;
    }

    public Integer getPmsTypeId() {
        return pmsTypeId;
    }

    public void setPmsTypeId(Integer pmsTypeId) {
        this.pmsTypeId = pmsTypeId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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

    public String getLateCheckOut() {
        return lateCheckOut;
    }

    public void setLateCheckOut(String lateCheckOut) {
        this.lateCheckOut = lateCheckOut;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
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

    public LockVendor getLockVendor() {
        return lockVendor;
    }

    public void setLockVendor(LockVendor lockVendor) {
        this.lockVendor = lockVendor;
    }

}