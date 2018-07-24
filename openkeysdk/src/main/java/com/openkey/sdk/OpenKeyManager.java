
/*
 *
 *  Copyright 2018 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.assa.ASSA;
import com.openkey.sdk.entrava.Entrava;
import com.openkey.sdk.enums.MANUFACTURER;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.Kaba;
import com.openkey.sdk.miwa.Miwa;
import com.openkey.sdk.salto.Salto;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This class is responsible for the  SDK task management and Exception handling.
 *         This class is build upon the Singleton pattern to prevent the multiple Object
 *         creation.
 */
public final class OpenKeyManager {


    @SuppressLint("StaticFieldLeak")
    private static volatile OpenKeyManager instance;
    private Context context;
    private MANUFACTURER manufacturer;
    private ASSA assa;
    private Salto salto;
    private Kaba kaba;
    private Entrava entrava;
    private Miwa miwa;
    private OpenKeyCallBack mOpenKeyCallBack;


    /*
     * Getting mobile key from server, If the key is issued from backend then start syncing
     * process
     * */
    private Callback getKeyCallback = new Callback() {
        @Override
        public void onResponse(Call call, retrofit2.Response response) {
            //   if (response.isSuccessful()) {
            if (true) {
                startSync();
            } else {
                mOpenKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            if (mOpenKeyCallBack != null)
                mOpenKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
        }
    };

    /**
     * Access to this class can only be provided by this class
     * so object creation is limited to this only.
     * @param context
     */

    private OpenKeyManager(Context context) {
        this.context = context.getApplicationContext();
        Utilities.getInstance(context);
    }

    /**
     * This will return the instance of this class and provide a
     * context to this class.
     *
     * @param context {@link Context } of the host application
     * @return Instance of the {@link OpenKeyManager} class
     */
    public static synchronized OpenKeyManager getInstance(Context context)
            throws NullPointerException {
        if (instance == null) {
            if (context == null) {
                throw new NullPointerException(Response.NULL_CONTEXT);
            }
            instance = new OpenKeyManager(context);
        }
        return instance;
    }

    /**
     * Authorize the user(Third party developer) to use the SDK. This will
     * call OpenKey server to authenticate, response will be provided via @{@link OpenKeyCallBack}
     *
     * @param authSignature   Secret key that is provided by OpenKey to the user(Third party developer)
     * @param openKeyCallBack Call back for  response
     * @param isLiveEnvironment Check either authenciate with dev or live url
     */
    public synchronized void authenticate(@NonNull String authSignature,
                                          @NonNull OpenKeyCallBack openKeyCallBack,
                                          boolean isLiveEnvironment) {
        if (checkContext()) {
            openKeyCallBack.initializationFailure(Response.NULL_CONTEXT);
            return;
        }

        //Set configuration
        setConfiguration(isLiveEnvironment);

        // if key is empty or small then return callback with error
        if (TextUtils.isEmpty(authSignature)) {
            openKeyCallBack.authenticated(false, Response.INVALID_AUTH_SIGNATURE);
            return;
        }


        //If the user is already authenticated then it will return callback with authenticate successful
        boolean isAuthenticated = Utilities.getInstance().getValue(Constants.IS_AUTHENTICATED, false, this.context);
        if (isAuthenticated) {
            openKeyCallBack.authenticated(true, Response.AUTHENTICATION_SUCCESSFUL);
        } else {
            Api.authenticate(context, authSignature, openKeyCallBack);
        }
    }

    /**
     * Set configuration for app either it will run on dev or aws server.
     *
     * @param isLiveEnvironment specification for environment.
     */

    private void setConfiguration(boolean isLiveEnvironment) {
        if (isLiveEnvironment) {
            Utilities.getInstance().saveValue(Constants.IS_LIVE_ENVIRONMENT, true, context);
//            Utilities.getInstance().saveValue(Constants.ASSA_TOKEN,Constants.ASSA_LIVE_TOKEN,context);
//            Utilities.getInstance().saveValue(Constants.ASSA_BASE_URL,Constants.ASSA_LIVE_URL,context);
            Utilities.getInstance().saveValue(Constants.BASE_URL, Constants.BASE_URL_LIVE, context);
        } else {
            Utilities.getInstance().saveValue(Constants.IS_LIVE_ENVIRONMENT, false, context);
            // Utilities.getInstance().saveValue(Constants.ASSA_TOKEN,Constants.ASSA_DEV_TOKEN,context);
            Utilities.getInstance().saveValue(Constants.BASE_URL, Constants.BASE_URL_DEV, context);
            //   Utilities.getInstance().saveValue(Constants.ASSA_BASE_URL,Constants.ASSA_DEV_URL,context);
        }
    }

    /**
     * Initialize SDK with unique number.
     * <p>
     * the unique identification number for setting up device with
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void initialize(@NonNull OpenKeyCallBack openKeyCallBack) {

        if (context == null)
            return;

        final String manufacturerStr = Utilities.getInstance().getValue(Constants.MANUFACTURER,
                "", context);
        Log.e("manufacturerStr", ":" + manufacturerStr);
        if (manufacturerStr.isEmpty()) {
            openKeyCallBack.authenticated(false, Response.UNKNOWN);
            return;
        }

        manufacturer = Utilities.getInstance().getManufacturer(context, openKeyCallBack);
        switch (manufacturer) {
            case ASSA:
                assa = new ASSA(context, openKeyCallBack);
                break;

            case SALTO:
                salto = new Salto(context, openKeyCallBack);
                break;

            case KABA:
                kaba = new Kaba(context, openKeyCallBack);
                break;

            case MIWA:
                miwa = new Miwa(context, openKeyCallBack);
                break;

            case ENTRAVA:
            case ENTRAVATOUCH:
                entrava = new Entrava(context, openKeyCallBack);
                break;
        }
    }

    /**
     * If the user is successfully authenticated
     * and initialization is also successful, can
     * get keys via this method
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void getKey(@NonNull final OpenKeyCallBack openKeyCallBack) {
        if (assa == null && salto == null && kaba == null && miwa==null&& entrava==null) {
            openKeyCallBack.isKeyAvailable(false, Response.INITIALIZATION_FAILED);
            return;
        }
        mOpenKeyCallBack = openKeyCallBack;
        Api.getMobileKey(context, getKeyCallback);
    }

    /**
     * If the user is successfully
     * get keys then start sync process via this method
     */
    private void startSync() {
        if (mOpenKeyCallBack == null)
            return;

        manufacturer = Utilities.getInstance().getManufacturer(context, mOpenKeyCallBack);
        Log.e("COMEPELTE", "manufacturer" + manufacturer);


        switch (manufacturer) {
            case ASSA:
                if (assa.isSetupComplete()) {
                    Log.e("COMEPELTE", "GET KEY");
                    assa.getKey();
                } else {
                    mOpenKeyCallBack.initializationFailure(Response.NOT_INITIALIZED);
                }
                break;

            case SALTO:
                updateKeyStatus(true);
                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                break;

            case KABA:
                kaba.getKabaKey();
                break;

            case MIWA:
                updateKeyStatus(true);
                miwa.addKey();
                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                break;

            case ENTRAVA:
            case ENTRAVATOUCH:
                entrava.issueEntravaKey();
                break;
        }
    }

    /**
     * @param authToken
     * @param openKeyCallBack Call back for response purpose
     */
    public void getSession(String authToken, OpenKeyCallBack openKeyCallBack) {
        Api.getSession(context, authToken, openKeyCallBack);
    }

    /**
     *  If device has a key available
     *
     * @param openKeyCallBack Call back for response purpose
     * @return boolean
     */
    public synchronized boolean isKeyAvailable(OpenKeyCallBack openKeyCallBack) {
        if (assa == null && salto == null && kaba == null && miwa==null&& entrava==null) return false;
        boolean haveKey = false;
        manufacturer = Utilities.getInstance().getManufacturer(context, openKeyCallBack);
        switch (manufacturer) {
            case ASSA:
                haveKey = assa.haveKey();
                break;

            case SALTO:
                haveKey = salto.haveKey();
                break;

            case KABA:
                haveKey = kaba.haveKey();
                break;

            case MIWA:
                haveKey = miwa.haveKey();
                break;

            case ENTRAVA:
            case ENTRAVATOUCH:
                haveKey = entrava.haveKey();
                break;
        }
        return haveKey;
    }

    /**
     * start scanning if passes the initial checks
     * and device have a key
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void startScanning(@NonNull OpenKeyCallBack openKeyCallBack) {

        Log.e("Assa", " startScanning");

        // if (!performInitialCheck(openKeyCallBack)) return;
        //checkKeyStatus();
        if (isKeyAvailable(openKeyCallBack)) {
            Log.e("Assa", " isKeyAvailable");

            manufacturer = Utilities.getInstance().getManufacturer(context, openKeyCallBack);
            Log.e("Assa", " manufacturer" + manufacturer);

            switch (manufacturer) {
                case ASSA:
                    Log.e("Assa", " ASSA");

                    if (assa.isSetupComplete()) {
                        assa.startScanning();
                    } else {
                        openKeyCallBack.stopScan(false, Response.NOT_INITIALIZED);
                    }
                    break;
                case SALTO:
                    salto.startScanning();
                    break;

                case KABA:
                    kaba.startScanning();
                    break;

                case MIWA:
                    miwa.startScanning();
                    break;

                case ENTRAVA:
                case ENTRAVATOUCH:
                    entrava.startImGateScanningService();
                    break;
            }
        } else {
            openKeyCallBack.stopScan(false, Response.NO_KEY_FOUND);
        }
    }

    private void scanningStart()
    {
        if (isKeyAvailable(mOpenKeyCallBack)) {
            manufacturer = Utilities.getInstance().getManufacturer(context, mOpenKeyCallBack);
            switch (manufacturer) {
                case ASSA:
                    if (assa.isSetupComplete()) {
                        assa.startScanning();
                    } else {
                        mOpenKeyCallBack.stopScan(false, Response.NOT_INITIALIZED);
                    }
                    break;
                case SALTO:
                    salto.startScanning();
                    break;

                case KABA:
                    kaba.startScanning();
                    break;

                case MIWA:
                    break;

                case ENTRAVATOUCH:
                case ENTRAVA:
                    break;
            }
        } else {
            mOpenKeyCallBack.stopScan(false, Response.NO_KEY_FOUND);
        }
    }

    /**
     * * This method is used to update the key status on server.
     * 1 identify the device have key
     * 0 identify the device have not key
     *
     * @param haveKey Device have key or not
     */
    public void updateKeyStatus(boolean haveKey)
    {
        //If the key status is already updated on server then it returns.
        boolean isKeyStatusUpdated=Utilities.getInstance().getValue(Constants.IS_KEY_STATUS_UPDATED,false,context);
        if (isKeyStatusUpdated)
            return;

        if (haveKey)
        {
            Api.setKeyStatus(context,1);
        }
        else
        {
            Api.setKeyStatus(context,0);
        }
    }

    /**
     * To check context every time ,if its reference goes to null
     * throw a {@link NullPointerException}
     */
    private synchronized boolean checkContext() {
        // if context is null throw a NullPointerException
        return context == null;
    }

    /**
     * Check user is authorize to use SDK,
     * if not the throw exception
     */
    private synchronized boolean checkAuthorization() {
        return Utilities.getInstance(context).getValue(Constants.IS_AUTHENTICATED, false, context);
    }

    /**
     * Check all things before providing a call to method
     * exp:
     * (context!=null, isAuthenticated==true)
     */
    private boolean performInitialCheck(OpenKeyCallBack openKeyCallBack) {
        return checkContext();
        /*if (checkContext()) {
            openKeyCallBack.initializationFailure(Response.NULL_CONTEXT);
            return false;
        }
        if (!checkAuthorization()) {
            openKeyCallBack.authenticated(false, Response.AUTHENTICATION_FAILED);
            return false;
        }
        return false;*/
    }


}
