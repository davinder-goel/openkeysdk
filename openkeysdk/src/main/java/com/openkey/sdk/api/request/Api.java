package com.openkey.sdk.api.request;

/*
 *
 *  Copyright 2018 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

import android.content.Context;
import android.util.Log;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.model.SdkLogRequest;
import com.openkey.sdk.api.response.Mobile_key_status.KeyStatusResp;
import com.openkey.sdk.api.response.Status;
import com.openkey.sdk.api.response.mobile_key_response.MobileKeyResponse;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.api.service.Services;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;
import static com.openkey.sdk.Utilities.Constants.TOKEN;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This class will hold all the api calls made from the SDK
 */

public class Api {

    /**
     * Token the user(Third party developer) to use the SDK. This will
     * call OpenKey server to authenticate, response will be provided via @{@link OpenKeyCallBack}
     *
     * @param authSignature   Secret key that is provided by OpenKey to the user
     * @param openKeyCallBack Call back for  response
     */
    public static void authenticate(final Context context, final String authSignature,
                                    final OpenKeyCallBack openKeyCallBack) {
        // Get the retrofit instance
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        services.authenticateGuest(TOKEN + authSignature).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, retrofit2.Response<SessionResponse>
                    response) {
                if (response.isSuccessful()) {
                    Utilities.getInstance().saveValue(Constants.AUTH_SIGNATURE, authSignature, context);


                    SessionResponse bookingResponse = response.body();

                    if (bookingResponse != null && bookingResponse.getData() != null) {
                        Log.e("onResponse", ":1:" + bookingResponse.getData().getHotel().getLockVendor().getTitle());
                        if (bookingResponse.getData().getHotel() != null &&
                                bookingResponse.getData().getHotel().getLockVendor() != null &&
                                bookingResponse.getData().getHotel().getLockVendor().getTitle() != null)
                        {

                            String manufacturer = bookingResponse.getData().getHotel().getLockVendor().getTitle().toUpperCase();
                            Utilities.getInstance().saveValue(Constants.MANUFACTURER, manufacturer, context);
                        }

                        if (bookingResponse.getData().getGuest() != null &&
                                    bookingResponse.getData().getGuest().getPhone()!=null)
                            {
                                // save it locally
                                String phoneNumber=bookingResponse.getData().getGuest().getPhone();
                                Utilities.getInstance().saveValue(Constants.UNIQUE_NUMBER,phoneNumber, context);
                            }

                    }
//                    Log.e("onResponse",":"+response.body().toString());
//                    // save user if authenticated successfully
//                    Utilities.getInstance().saveValue(Constants.IS_AUTHENTICATED, true, context);
//
//                    if (response.body()!=null)
//                    {
//                        SessionResponse bookingResponse=response.body();
//                        if (bookingResponse!=null&&bookingResponse.getData()!=null)
//                        {
//                            GetBooking.getInstance().setBooking(response.body());
//                            Utilities.getInstance().saveBookingToLocal(context, response.body());
//
//                            // get the booking_id returned from the server and save it locally
//                            String bookingID=""+bookingResponse.getData().getBookingId();
//                            Utilities.getInstance().saveValue(Constants.BOOKING_ID,bookingID, context);
//
//
//                            // get the phone_number returned from the server
//                            if (bookingResponse.getData().getGuest()!=null&&
//                                    bookingResponse.getData().getGuest().getPhone()!=null)
//                            {
//                                // save it locally
//                                String phoneNumber=bookingResponse.getData().getGuest().getPhone();
//                                Utilities.getInstance().saveValue(Constants.UNIQUE_NUMBER,phoneNumber, context);
//                            }
//
//                             /*
//                             * get the manufacturer returned from the server
//                             * */
//
//                            Utilities.getInstance().saveValue(Constants.MANUFACTURER, "KABA", context);
//
//                            if (bookingResponse.getData().getHotel()!=null&&
//                                    bookingResponse.getData().getHotel().getManufacturerSetting()!=null
//                                    &&bookingResponse.getData().getHotel().getManufacturerSetting().getManufacturer()!=null)
//                            {
//                                String manufacturer = bookingResponse.getData().getHotel()
//                                        .getManufacturerSetting().getManufacturer();
//
//                                // save it locally
//                                Utilities.getInstance().saveValue(Constants.MANUFACTURER, manufacturer, context);
//                            }
//                        }
//
//                    }
//
//                    Utilities.getInstance().saveValue(Constants.AUTH_SIGNATURE, authSignature, context);
                    openKeyCallBack.authenticated(true, Response.AUTHENTICATION_SUCCESSFUL);
                } else {
                    // get the error message from the response and return it to the callback
                    openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                Log.e("onFailure", ":" + t.getMessage());

                openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);
            }
        });
    }


    /**
     * Token the user(Third party developer) to use the SDK. This will
     * call OpenKey server to get booking, response will be provided via @{@link OpenKeyCallBack}
     *
     * @param openKeyCallBack Call back for  response
     */
    public static void getSession(final Context context, final String token, final OpenKeyCallBack openKeyCallBack) {

        if (!(token != null && token.length() > 0))
            openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);

        // Get the retrofit instance
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        services.getSession(TOKEN + token).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, retrofit2.Response<SessionResponse>
                    response) {
                if (response.isSuccessful()) {
                    openKeyCallBack.session(response.body());
                } else {
                    // get the error message from the response and return it to the callback
                    openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                Log.e("onFailure", ":" + t.getMessage());

                openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);
            }
        });
    }


    /*
    * Getting the key from server
    * */
    @SuppressWarnings("unchecked")
    public static void getMobileKey(final Context context, final Callback callback) {
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);
        services.getMobileKey(TOKEN + tokenStr).enqueue(new Callback<MobileKeyResponse>() {
            @Override
            public void onResponse(Call<MobileKeyResponse> call, retrofit2.Response<MobileKeyResponse> response) {
                Utilities.getInstance().saveValue(Constants.MOBILE_KEY, "", context);
                Log.e("onResponse", ":");
//
//                    /*
//                    * This is used only in case of salto
//                    * */
//                if (response.isSuccessful()) {
//                    BinaryKey binaryKey=response.body();
//                    if (binaryKey!=null && binaryKey.getData()!=null)
//                    {
//                        final String key = binaryKey.getData().getData().getKey();
//                        Utilities.getInstance().saveValue(Constants.MOBILE_KEY, key, context);
//                    }
//                } else {
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        String message = jObjError.optString("message");
//                        if (message != null && message.length() > 0) {
//                            Utilities.getInstance().clearValueOfKey(context, Constants.BOOKING);
//                            if (message.equals("Unauthorized"))
//                                Utilities.getInstance().saveValue(Constants.IS_AUTHENTICATED, false, context);
//                        }
//                    } catch (Exception e) {
//                        Log.e("Exception",":"+e.getMessage());
//                    }
//                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MobileKeyResponse> call, Throwable t) {
                Log.e("onFailure", ":" + t.getMessage());

                Utilities.getInstance().saveValue(Constants.MOBILE_KEY, "", context);
                callback.onFailure(call, t);
            }
        });
    }


    /*
    * Getting the key status either key issued from backend or not
    * */
    public static void getKeyStatus(final Context context, final Callback callback) {
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);
        final String bookingId = Utilities.getInstance().getValue(Constants.BOOKING_ID, "", context);
        services.getStatus(TOKEN + tokenStr, bookingId).enqueue(new Callback<KeyStatusResp>() {
            @Override
            public void onResponse(Call<KeyStatusResp> call, retrofit2.Response<KeyStatusResp> response) {
                Log.e("onResponse","onResponse");
            }

            @Override
            public void onFailure(Call<KeyStatusResp> call, Throwable t) {
                callback.onFailure(call, t);
                Log.e("onFailure","onFailure"+t.getMessage());
            }
        });
    }

    /**
     * Gets date time.
     *
     * @param format Type of the format of date or time
     * @return Get current date and time of the device, according to the format
     * @description Return string contains date if this format yyyy/MM/dd is
     * passed or time if this HH:mm:ss is passed and return both
     * with combined format yyyy/MM/dd HH:mm:ss
     */
    public static String getDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String Time = null;
        Date date = new Date();
        Time = dateFormat.format(date);
        return Time;
    }


    /**
     * @param context application's context
     */
    public static void logSDK(final Context context, boolean isDoorOpened) {

        String timeStamp = getDateTime("yyyy:MM:dd HH:mm:ss");

        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);
        services.logSDK("Bearer " + tokenStr, new SdkLogRequest("Bearer " + tokenStr, "door-open-attempt", isDoorOpened,
                timeStamp, "no reason")).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, retrofit2.Response<Status> response) {
                Log.e("OnResponse", "Lock Opened Successfully");
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                Log.e("onFailure", "Lock Opened Failed");
            }
        });
    }



    /**
     * Update the status on server that Peronalization(Device is ready to get key from server) has been completed
     */
    public static void setPeronalizationComplete(final Context mContext, final OpenKeyCallBack openKeyCallBack) {
        Services services = Utilities.getInstance().getRetrofit(mContext).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", mContext);
        services.setPeronalizationComplete(TOKEN + tokenStr).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, retrofit2.Response<Status> response) {
                if (response.isSuccessful()) {
                    // tell user, startSetup is success
                    openKeyCallBack.initializationSuccess();
                    Utilities.getInstance().saveValue(Constants.IS_PERSONLIZATION_STATUS_UPDATED, true, mContext);
                    Log.e(TAG, "Personalization Status updated on server");
                } else {
                    // tell user, startSetup is success
                    openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                    Log.e(TAG, "Personalization failed");
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                Log.e(TAG, "Personalization Status failed to update on server");
            }
        });
    }


    /**
     *
     */
    public static void setInitializePersonalization(final Context mContext, final Callback callback) {
        Services services = Utilities.getInstance().getRetrofit(mContext).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", mContext);
        services.initializePersonalization(TOKEN + tokenStr).enqueue(new RetrofitCallback(callback));
    }




    /*
    * Update status on server once device get key.
    * */
    public static void setKeyStatus(final Context context,int status) {
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);
        final String bookingId = Utilities.getInstance().getValue(Constants.BOOKING_ID, "", context);
//        services.setKeyStatus(TOKEN + tokenStr,bookingId, new KeyStatusRequest(status)).enqueue(new Callback<KeyStatusResponse>() {
//            @Override
//            public void onResponse(Call<KeyStatusResponse> call, retrofit2.Response<KeyStatusResponse> response) {
//                Log.e("onResponse","onResponse");
//                Utilities.getInstance().saveValue(Constants.IS_KEY_STATUS_UPDATED,true,context);
//            }
//
//            @Override
//            public void onFailure(Call<KeyStatusResponse> call, Throwable t) {
//                Log.e("onFailure","onFailure"+t.getMessage());
//                Utilities.getInstance().saveValue(Constants.IS_KEY_STATUS_UPDATED,false,context);
//            }
//        });
    }
}
