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
import com.openkey.sdk.api.model.KeyStatusRequest;
import com.openkey.sdk.api.model.SdkLogRequest;
import com.openkey.sdk.api.response.Mobile_key_status.KeyStatusResp;
import com.openkey.sdk.api.response.key_status.KeyStatusResponse;
import com.openkey.sdk.api.response.logaction.LogActionResponse;
import com.openkey.sdk.api.response.mobile_key_response.MobileKeyResponse;
import com.openkey.sdk.api.response.personlization.PersonlizationResponse;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.api.service.Services;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.singleton.GetBooking;

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
     * call OpenKey server to get booking, response will be provided via @{@link OpenKeyCallBack}
     *
     * @param openKeyCallBack Call back for  response
     */
    public static void getSession(final Context context, final String token,
                                  final OpenKeyCallBack openKeyCallBack) {

        // Get the retrofit instance
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        services.getSession(TOKEN + token).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, retrofit2.Response<SessionResponse>
                    response) {
                if (response.isSuccessful()) {
                    Utilities.getInstance().saveValue(Constants.AUTH_SIGNATURE, token, context);
                    saveData(response.body(), context);
                    openKeyCallBack.session(response.body());
                } else {
                    // get the error message from the response and return it to the callback
                    openKeyCallBack.sessionFailure(Response.BOOKING_NOT_FOUNT, response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                openKeyCallBack.sessionFailure(Response.UNKNOWN, "");
            }
        });
    }

    private static void saveData(SessionResponse bookingResponse, Context context) {
        if (bookingResponse != null && bookingResponse.getData() != null) {
            Utilities.getInstance(context).saveBookingToLocal(context, bookingResponse);
            GetBooking.getInstance().setBooking(bookingResponse);
            //Saved manufacturer in locally
            if (bookingResponse.getData().getHotel() != null &&
                    bookingResponse.getData().getHotel().getLockVendor() != null &&
                    bookingResponse.getData().getHotel().getLockVendor().getTitle() != null) {
                String manufacturer = bookingResponse.getData().getHotel().getLockVendor().getTitle().toUpperCase();
                Utilities.getInstance().saveValue(Constants.MANUFACTURER, manufacturer, context);
            }

            if (bookingResponse.getData().getGuest() != null &&
                    bookingResponse.getData().getGuest().getPhone() != null) {
                // save it locally
                String phoneNumber = bookingResponse.getData().getGuest().getPhone();
                phoneNumber = phoneNumber.replace("+", "");
                Utilities.getInstance().saveValue(Constants.UNIQUE_NUMBER, phoneNumber, context);
            }

            if (bookingResponse.getData().getMobileKeyStatus() != null)
                Utilities.getInstance().saveValue(Constants.MOBILE_KEY_STATUS,
                        bookingResponse.getData().getMobileKeyStatusId(), context);

        }
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

                if (response != null && response.body() != null && response.body().getData() != null
                        && response.body().getData().size() > 0 &&
                        response.body().getData().get(0).getMobileKey() != null) {
                    String key = response.body().getData().get(0).getMobileKey();
                    Utilities.getInstance().saveValue(Constants.MOBILE_KEY, key, context);
                } else {
                    Utilities.getInstance().saveValue(Constants.MOBILE_KEY, "", context);
                }

                Log.e("onResponse", ":MobileKey");
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MobileKeyResponse> call, Throwable t) {
                Log.e("onFailure", ":MobileKey: " + t.getMessage());

                Utilities.getInstance().saveValue(Constants.MOBILE_KEY, "", context);
                callback.onFailure(call, t);
            }
        });
    }


    /*
    * Getting the key status either key issued from backend or not
    * */
    @SuppressWarnings("unchecked")
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
    public static void logSDK(final Context context, int isDoorOpened) {

        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);

        services.logSDK(TOKEN + tokenStr, new SdkLogRequest("door-opened",
                isDoorOpened)).enqueue(new Callback<LogActionResponse>() {
            @Override
            public void onResponse(Call<LogActionResponse> call, retrofit2.Response<LogActionResponse> response) {
                Log.e("OnResponse", "Lock Opened Successfully");
            }

            @Override
            public void onFailure(Call<LogActionResponse> call, Throwable t) {
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
        services.setPeronalizationComplete(TOKEN + tokenStr).enqueue(new Callback<PersonlizationResponse>() {
            @Override
            public void onResponse(Call<PersonlizationResponse> call, retrofit2.Response<PersonlizationResponse> response) {
                if (response.isSuccessful()) {

                    PersonlizationResponse personlizationResponse = response.body();
                    if (personlizationResponse != null && personlizationResponse.getData() != null
                            && personlizationResponse.getData().getKeyIssued())
                        openKeyCallBack.initializationSuccess();
                    else
                        openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);

                    Log.e(TAG, "Personalization Status updated on server");
                } else if (response.code() == 403) {
                    openKeyCallBack.initializationFailure("403");
                } else {
                    // tell user, startSetup is success
                    openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                    Log.e(TAG, "Personalization failed");
                }
            }

            @Override
            public void onFailure(Call<PersonlizationResponse> call, Throwable t) {
                openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                Log.e(TAG, "Personalization Status failed to update on server");
            }
        });
    }

    /**
     * @param context
     * @param callback
     */
    @SuppressWarnings("unchecked")
    public static void setInitializePersonalizationForKaba(final Context context, final Callback callback
            , OpenKeyCallBack openKeyCallBack) {
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);

        if (context == null || tokenStr == null && openKeyCallBack != null)
            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);

        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        services.initializePersonalizationForKaba(TOKEN + tokenStr).enqueue(new RetrofitCallback(callback));
    }


    /**
     *
     * @param context
     * @param callback
     */
    @SuppressWarnings("unchecked")
    public static void setInitializePersonalization(final Context context, final Callback callback
            , OpenKeyCallBack openKeyCallBack) {
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);

        if (context == null || tokenStr == null)
            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);

        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        services.initializePersonalization(TOKEN + tokenStr).enqueue(new RetrofitCallback(callback));
    }


    /**
     *
     * @param context
     * @param callback
     */
    @SuppressWarnings("unchecked")
    public static void getBooking(final Context context, String tokenStr, final Callback callback) {
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        //  services.getSession(TOKEN + tokenStr).enqueue(new RetrofitCallback(callback));
        services.getSession(TOKEN + tokenStr).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, retrofit2.Response<SessionResponse> response) {
                if (response.isSuccessful())
                    saveData(response.body(), context);

                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * Update status on server once device get key.
     *
     * @param context
     * @param status
     */

    public static void setKeyStatus(final Context context, String status) {
        Services services = Utilities.getInstance().getRetrofit(context).create(Services.class);
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", context);
        services.setKeyStatus(TOKEN + tokenStr, new KeyStatusRequest(status)).enqueue(new Callback<KeyStatusResponse>() {
            @Override
            public void onResponse(Call<KeyStatusResponse> call, retrofit2.Response<KeyStatusResponse> response) {
                Log.e("onResponse", "onResponse");
            }

            @Override
            public void onFailure(Call<KeyStatusResponse> call, Throwable t) {
                Log.e("onFailure", "onFailure" + t.getMessage());
            }
        });
    }
}
