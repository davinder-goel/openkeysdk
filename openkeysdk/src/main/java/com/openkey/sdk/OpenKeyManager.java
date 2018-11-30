
package com.openkey.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.assa.ASSA;
import com.openkey.sdk.entrava.Entrava;
import com.openkey.sdk.enums.MANUFACTURER;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.Kaba;
import com.openkey.sdk.miwa.Miwa;
import com.openkey.sdk.okc.OKC;
import com.openkey.sdk.salto.Salto;
import com.openkey.sdk.singleton.GetBooking;

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
    private OKC okc;
    private OpenKeyCallBack mOpenKeyCallBack;

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * Access to this class can only be provided by this class
     * so object creation is limited to this only.
     * @param context
     */

    private OpenKeyManager(Context context) {
        this.context = context.getApplicationContext();
        Utilities.getInstance(context);
    }
    //-----------------------------------------------------------------------------------------------------------------|

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

            SessionResponse sessionResponse = Utilities.getInstance(context).getBookingFromLocal(context);
            if (sessionResponse != null)
                GetBooking.getInstance().setBooking(sessionResponse);
        }
        return instance;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * @param authToken
     * @param openKeyCallBack Call back for response purpose
     */
    public void authenticate(String authToken, OpenKeyCallBack openKeyCallBack,boolean environmentType) {

        //Set configuration
        setConfiguration(environmentType);

        if (authToken != null && authToken.length() > 0 && context != null)
            Api.getSession(context, authToken, openKeyCallBack);
        else
            openKeyCallBack.sessionFailure(Response.INVALID_AUTH_SIGNATURE,"");
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void setConfiguration(boolean environmentType)
    {
        if (environmentType)
            Utilities.getInstance().saveValue(Constants.BASE_URL,Constants.BASE_URL_LIVE,context);
        else
            Utilities.getInstance().saveValue(Constants.BASE_URL,Constants.BASE_URL_DEV,context);
    }

    public void getSession(String authToken,final Callback callback) {
        //Set configuration
       // setConfiguration();
        Api.getBooking(authToken,context, callback);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * Initialize SDK with unique number.
     * <p>
     * the unique identification number for setting up device with
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void initialize(@NonNull OpenKeyCallBack openKeyCallBack) {

        if (context == null) {
            Log.e("context", "null");
            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
            return;
        }

        final String manufacturerStr = Utilities.getInstance().getValue(Constants.MANUFACTURER,
                "", context);
        if (manufacturerStr.isEmpty()) {
            openKeyCallBack.initializationFailure(Response.BOOKING_NOT_FOUNT);
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

            case OKC:
                okc = new OKC(context, openKeyCallBack);
                break;
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * If the user is successfully authenticated
     * and initialization is also successful, can
     * get keys via this method
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void getKey(@NonNull final OpenKeyCallBack openKeyCallBack) {
        if (context == null && assa == null && salto == null && kaba == null && miwa == null && entrava == null) {
            openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
            return;
        }

        mOpenKeyCallBack = openKeyCallBack;

        //if context null then it returned callback with null context description
        if (context == null)
            openKeyCallBack.isKeyAvailable(false, Response.NULL_CONTEXT);

        openKeyCallBack.isKeyAvailable(true, Response.NULL_CONTEXT);

        //Getting key from server
        //Api.getMobileKey(context, getKeyCallback);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /*
     * Getting mobile key from server, If the key is issued from backend then start syncing
     * process
     * */
    private Callback getKeyCallback = new Callback() {
        @Override
        public void onResponse(Call call, retrofit2.Response response) {
            if (response.isSuccessful()) {
                startSync();
            } else {
                if (mOpenKeyCallBack != null)
                    mOpenKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            if (mOpenKeyCallBack != null)
                mOpenKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
        }
    };

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * If the user is successfully
     * get keys then start sync process via this method
     */
    private void startSync() {
        if (mOpenKeyCallBack == null || context == null)
            return;

        manufacturer = Utilities.getInstance().getManufacturer(context, mOpenKeyCallBack);
        switch (manufacturer) {
            case ASSA:
                if (assa.isSetupComplete()) {
                    assa.getKey();
                } else {
                    Log.e("getKey", "initializationFailure");
                    mOpenKeyCallBack.initializationFailure(Response.NOT_INITIALIZED);
                }
                break;

            case SALTO:
                updateKeyStatus(true);
                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                break;

            case KABA:
                kaba.synchronise();
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

            case OKC:
                //      okc.issueEntravaKey();
                break;
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     *  If device has a key available
     *
     * @param openKeyCallBack Call back for response purpose
     * @return boolean
     */
    public synchronized boolean isKeyAvailable(OpenKeyCallBack openKeyCallBack) {
        if (assa == null && salto == null && kaba == null && miwa == null && entrava == null) {
            Log.e("Started", "INITIALIZATION_FAILED");
            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
            initialize(openKeyCallBack);

        }
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

            case OKC:
                haveKey = okc.haveKey();
                break;
        }
        return haveKey;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * start scanning if passes the initial checks
     * and device have a key
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void startScanning(@NonNull OpenKeyCallBack openKeyCallBack) {

        if (context == null)
            openKeyCallBack.initializationFailure(Response.NULL_CONTEXT);

        if (isKeyAvailable(openKeyCallBack)) {
            manufacturer = Utilities.getInstance().getManufacturer(context, openKeyCallBack);
            switch (manufacturer) {
                case ASSA:
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

                case OKC:
                    okc.startScanning();
                    break;
            }
        } else {
            Log.e("Manager", "called");
            openKeyCallBack.stopScan(false, Response.NO_KEY_FOUND);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    /**
     * * This method is used to update the key status on server.
     * 1 identify the device have key
     * 0 identify the device have not key
     *
     * @param haveKey Device have key or not
     */
    public void updateKeyStatus(boolean haveKey)
    {
        if (haveKey)
            Api.setKeyStatus(context, Constants.KEY_DELIVERED);
        else
            Api.setKeyStatus(context, Constants.PENDING_KEY_SERVER_REQUEST);
    }
    //-----------------------------------------------------------------------------------------------------------------|

}
