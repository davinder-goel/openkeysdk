package com.openkey.sdk.api.response.session;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotelPmsBooking {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotel_id")
    @Expose
    private Integer hotelId;
    @SerializedName("session_id")
    @Expose
    private Integer sessionId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("booking_code")
    @Expose
    private String bookingCode;
    @SerializedName("check_in")
    @Expose
    private String checkIn;
    @SerializedName("check_out")
    @Expose
    private String checkOut;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("room")
    @Expose
    private String room;
    @SerializedName("is_email_sent")
    @Expose
    private Boolean isEmailSent;
    @SerializedName("is_email_read")
    @Expose
    private Boolean isEmailRead;
    @SerializedName("is_webcheckin_complete")
    @Expose
    private Boolean isWebcheckinComplete;
    @SerializedName("is_std")
    @Expose
    private Boolean isStd;
    @SerializedName("eta")
    @Expose
    private String eta;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("deleted")
    @Expose
    private Object deleted;
    @SerializedName("sendgrid_id")
    @Expose
    private Object sendgridId;
    @SerializedName("pms_guest_identifier")
    @Expose
    private Object pmsGuestIdentifier;

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

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Boolean getIsEmailSent() {
        return isEmailSent;
    }

    public void setIsEmailSent(Boolean isEmailSent) {
        this.isEmailSent = isEmailSent;
    }

    public Boolean getIsEmailRead() {
        return isEmailRead;
    }

    public void setIsEmailRead(Boolean isEmailRead) {
        this.isEmailRead = isEmailRead;
    }

    public Boolean getIsWebcheckinComplete() {
        return isWebcheckinComplete;
    }

    public void setIsWebcheckinComplete(Boolean isWebcheckinComplete) {
        this.isWebcheckinComplete = isWebcheckinComplete;
    }

    public Boolean getIsStd() {
        return isStd;
    }

    public void setIsStd(Boolean isStd) {
        this.isStd = isStd;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
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

    public Object getSendgridId() {
        return sendgridId;
    }

    public void setSendgridId(Object sendgridId) {
        this.sendgridId = sendgridId;
    }

    public Object getPmsGuestIdentifier() {
        return pmsGuestIdentifier;
    }

    public void setPmsGuestIdentifier(Object pmsGuestIdentifier) {
        this.pmsGuestIdentifier = pmsGuestIdentifier;
    }

}