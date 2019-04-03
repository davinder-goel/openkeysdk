package com.openkey.okcsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.openkey.okcsdk.model.Status;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.internal.platform.Platform;
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
        }
        return utilities;

    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), Platform.get().trustManager(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
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
        prefs = new SharedPreferencesEncryption(context);

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
        prefs = new SharedPreferencesEncryption(context);

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
        prefs = new SharedPreferencesEncryption(context);

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
        prefs = new SharedPreferencesEncryption(context);

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
        prefs = new SharedPreferencesEncryption(context);

        return prefs.getInt(key, defaultValue);
    }

    /**
     * Get retrofit Object for accessing web services.
     *
     * @return retrofit instance for web service calling
     */
    public Retrofit getRetrofit(Context context) {
        Retrofit retrofit;
        OkHttpClient.Builder httpClient = getNewHttpClient().newBuilder();

        String url = Constants.BASE_URL;

        if (context != null)
            url = Utilities.getInstance().getValue(Constants.BASE_URL, Constants.BASE_URL, context);

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
            if (!TextUtils.isEmpty(error.getData().getMessage())) {
                Log.e("", "Response message : " + error.getData().getMessage());
                return (String) error.getData().getMessage();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OkHttpClient getNewHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging);
        return enableTls12OnPreLollipop(client).build();
    }
}