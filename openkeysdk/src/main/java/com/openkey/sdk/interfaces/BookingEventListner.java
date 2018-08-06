package com.openkey.sdk.interfaces;

import com.openkey.sdk.api.response.session.SessionResponse;

public interface BookingEventListner {
    void getBooking(boolean isBookingFound, SessionResponse sessionResponse);
}
