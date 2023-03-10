package com.openkey.sdk.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openkey.sdk.api.response.Status;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.cryptography.EncryptedSharedPref;
import com.openkey.sdk.enums.MANUFACTURER;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.singleton.GetGson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author OpenKey Inc.
 * <p>
 * This class will provide all the necessary  utility methods.
 */

public class Utilities {

    private static SharedPreferences prefs;
    private static Utilities utilities;
    private Toast msg;

    public static Utilities getInstance(Context... contexts) {
        if (utilities == null) {
            utilities = new Utilities();

            if (contexts != null && contexts.length > 0)
                prefs = new EncryptedSharedPref(contexts[0].getApplicationContext());
        }
        return utilities;

    }

    public boolean isOnline(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;

    }

    /**
     * Clear values of shared preference.
     *
     * @param context the context
     *                If user logout then clear all the saved values from the
     *                shared preference file
     */
    public void clearValueOfKey(Context context, String key) {
        if (context == null) return;

        SharedPreferences.Editor saveValue = prefs.edit();
        saveValue.remove(key).apply();
    }

    /*
     * Decode key into base64
     * */
    public String decodeMiwaKey(String miwaKey) {
        byte[] valueDecoded = new byte[0];
        try {
            valueDecoded = Base64.decode(miwaKey.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodingException", ":" + e.getMessage());
        }
        return new String(valueDecoded);
    }

    public void saveRoomList(ArrayList<String> data, Context context) {
        if (context == null) return;

        Gson gson = new Gson();
        SharedPreferences.Editor saveValue = prefs.edit();
        String jsonData = gson.toJson(data);
        saveValue.putString(Constants.ALL_ROOMS, jsonData);
        saveValue.apply();
    }

    public ArrayList<String> getRoomList(Context context) {
        if (context == null) return null;
        Gson gson = new Gson();
        String jsonText = prefs.getString(Constants.ALL_ROOMS, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> data = gson.fromJson(jsonText, type);
        return data;
    }

    /**
     * Save value to shared preference.
     *
     * @param key     On which key you want to save the value.
     * @param value   The value which needs to be saved.
     * @param context the context
     *                To save the value to a preference file on the specified key.
     */
    public void saveValue(String key, String value, Context context) {
        if (context == null) return;

        SharedPreferences.Editor saveValue = prefs.edit();
        saveValue.putString(key, value);
        saveValue.apply();
    }

    /**
     * Gets value from shared preference.
     *
     * @param key          The key from you want to get the value.
     * @param defaultValue Default value, if nothing is found on that key.
     * @param context      the context
     * @return the value from shared preference
     * To get the value from a preference file on the specified
     * key.
     */
    public String getValue(String key, String defaultValue, Context context) {
        if (context == null) return defaultValue;

        return prefs.getString(key, defaultValue);
    }

    /**
     * for vibration
     */
    @SuppressLint("MissingPermission")
    public void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    /**
     * Show toast.
     *
     * @param context the context
     * @param toast   String value which needs to shown in the toast.
     *                if you want to print a toast just call this method and pass
     *                what you want to be shown.
     */
    public Toast showToast(Context context, String toast) {
        if (context != null && msg == null || msg.getView().getWindowVisibility() != View.VISIBLE) {
            msg = Toast.makeText(context, toast, Toast.LENGTH_LONG);
            msg.setGravity(Gravity.CENTER, 0, 0);
            msg.show();
        }


        return msg;
    }

    /**
     * retrofit for KABA
     */
    public Retrofit getRetrofitForKaba() {

        Retrofit retrofit;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-type", "application/json")
                        .addHeader("Authorization", "Basic UHJlYXV0aE9wZW5LZXlUZWNoVXNlcjpmOEtDY3VNUkpLOGRNUDMwYWtNcg==")
                        .build();
                return chain.proceed(request);
            }
        });


        httpClient.addInterceptor(logging);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.URL_KABA_BASE)
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    /**
     * Save value to shared preference.
     *
     * @param key     On which key you want to save the value.
     * @param value   The value which needs to be saved.
     * @param context the context
     *                To save the value to a saved preference file on the
     *                specified key.
     */
    public void saveValue(String key, boolean value, Context context) {
        if (context == null) return;

        SharedPreferences.Editor saveValue = prefs.edit();
        saveValue.putBoolean(key, value);
        saveValue.apply();
    }

    /**
     * Gets value from shared preference.
     *
     * @param key          The key from you want to get the value.
     * @param defaultValue Default value, if nothing is found on that key.
     * @param context      the context
     * @return the value from shared preference
     * To get the value from a saved preference file on the
     * specified key.
     */
    public boolean getValue(String key, boolean defaultValue, Context context) {
        return context != null && prefs.getBoolean(key, defaultValue);
    }

    /**
     * Save value to shared preference.
     *
     * @param key     On which key you want to save the value.
     * @param value   The value which needs to be saved.
     * @param context the context
     *                To save the value to a saved preference file on the
     *                specified key.
     */
    public void saveValue(String key, int value, Context context) {
        if (context == null) return;

        SharedPreferences.Editor saveValue = prefs.edit();
        saveValue.putInt(key, value);
        saveValue.apply();
    }

    /**
     * Gets value from shared preference.
     *
     * @param key          The key from you want to get the value.
     * @param defaultValue Default value, if nothing is found on that key.
     * @param context      the context
     * @return the value from shared preference
     * @description To get the value from a preference file on the specified
     * key.
     */
    public int getValue(String key, int defaultValue, Context context) {
        if (context == null) return 0;

        return prefs.getInt(key, defaultValue);
    }

    /**
     * Get retrofit Object for accessing web services.
     *
     * @return retrofit instance for web service calling
     */
    public Retrofit getRetrofit(Context context) {
        Retrofit retrofit;
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        // set your desired log level
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = getNewHttpClient(context).newBuilder();

//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.readTimeout(30, TimeUnit.SECONDS);
//        httpClient.connectTimeout(30, TimeUnit.SECONDS);


        String url = Constants.BASE_URL_DEV;

        if (context != null)
            url = Utilities.getInstance().getValue(Constants.BASE_URL, Constants.BASE_URL_DEV, context);

        // add logging as last interceptor
//        httpClient.addInterceptor(logging);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    /**
     * Show toast of message if api response code is not 200
     *
     * @param responseBody Get message from the response body
     */
    public String handleApiError(ResponseBody responseBody, Context context) {
        Converter<ResponseBody, Status> errorConverter = getRetrofit(context)
                .responseBodyConverter(Status.class, new Annotation[0]);
        try {
            Status error = errorConverter.convert(responseBody);
            if (!TextUtils.isEmpty(error.getMessage())) {
                Log.e("", "Response message : " + error.getMessage());
                return error.getMessage().toLowerCase();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OkHttpClient getNewHttpClient(Context context) {
        String UUID = Utilities.getInstance().getValue(Constants.UUID, "", context);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging);

        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request request = chain.request().newBuilder()
                        .header("x-openkey-app", UUID)
                        .header("Accept", "application/json")
                        .header("Cache-Control", "no-cache")
//                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
//        return enableTls12OnPreLollipop(client).build();
        return client.build();
    }

    /**
     * @return return retrofit instance
     * <p>
     * retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
     * .baseUrl("https://demo.credential-services.sci.assaabloy.net")
     * .client(httpClient.build())
     * .build();
     * <p>
     * return retrofit;
     */
//    public Retrofit getRetrofitForASSA(Context context) {
//        Retrofit retrofit;
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        // set your desired log level
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(logging);
//
//        String url=Constants.ASSA_DEV_URL;
//
//        if (context!=null)
//        {
//           url=Utilities.getInstance().getValue(Constants.ASSA_BASE_URL,Constants.ASSA_DEV_URL,context);
//        }
//
//        retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(url)
//                .client(httpClient.build())
//                .build();
//        return retrofit;
//    }

    /**
     * Save the booking to shared preference
     *
     * @param booking {@link com.openkey.sdk.api.response.booking.BookingResponse}
     */
    public void saveBookingToLocal(Context context, SessionResponse booking) {
        Gson gson = new Gson();
        String bookingString = gson.toJson(booking);
        saveValue(Constants.BOOKING, bookingString, context);
    }


    /**
     * Get booking from the saved shared preference
     */
    public SessionResponse getBookingFromLocal(Context context) {
        if (context != null) {
            String bookingString = getValue(Constants.BOOKING, "", context);
            if (!TextUtils.isEmpty(bookingString)) {
                Gson gson = GetGson.getInstance();
                return gson.fromJson(bookingString, SessionResponse.class);
            }
        }
        return null;
    }

    public MANUFACTURER getManufacturer(Context context, OpenKeyCallBack openKeyCallBack) {
        final String manufacturerStr = Utilities.getInstance().getValue(Constants.MANUFACTURER, "", context);
        if (TextUtils.isEmpty(manufacturerStr)) {
            openKeyCallBack.initializationFailure(Response.UNKNOWN);
            throw new IllegalStateException(Response.UNKNOWN);
        }
        return MANUFACTURER.valueOf(manufacturerStr);
    }
}