package com.openkey.sdk.singleton;


import com.openkey.sdk.api.response.booking.BookingResponse;

/**
 * @author OpenKey Inc.
 *         <p>
 *         Singleton class so that booking can be accessed from any where in the app
 */

public class GetBooking {

    private static GetBooking mInstance;

    // response from server
    private BookingResponse mBookingResponse;


    private GetBooking() {
    }

    public static GetBooking getInstance() {
        if (mInstance == null) {
            mInstance = new GetBooking();
        }

        return mInstance;
    }


    public BookingResponse getBooking() {
        return mBookingResponse;
    }

    public void setBooking(BookingResponse booking) {
        mBookingResponse = booking;
    }

}
