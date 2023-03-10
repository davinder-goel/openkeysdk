package com.openkey.sdk;

import static com.openkey.sdk.enums.MANUFACTURER.DRK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.assa.ASSA;
import com.openkey.sdk.drk.DRKModule;
import com.openkey.sdk.entrava.Entrava;
import com.openkey.sdk.enums.EnvironmentType;
import com.openkey.sdk.enums.MANUFACTURER;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.Kaba;
import com.openkey.sdk.miwa.Miwa;
import com.openkey.sdk.okc.OKC;
import com.openkey.sdk.okmobilekey.OKMobileKey;
import com.openkey.sdk.okmodule.OKModule;
import com.openkey.sdk.salto.Salto;
import com.openkey.sdk.singleton.GetBooking;

import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author OpenKey Inc.
 * <p>
 * This class is responsible for the  SDK task management and Exception handling.
 * This class is build upon the Singleton pattern to prevent the multiple Object
 * creation.
 */
public final class OpenKeyManager {

    @SuppressLint("StaticFieldLeak")
    private static volatile OpenKeyManager instance;
    private static Application mContext;
    private MANUFACTURER manufacturer;
    private ASSA assa;
    private Salto salto;
    private Kaba kaba;
    private Entrava entrava;
    private Miwa miwa;
    private OKModule okModule;
    private OKC okc;
    private OKMobileKey okMobileKey;
    private DRKModule drkModule;
    private Handler handler;
    private Handler keyTimeoutHandler;
    private OpenKeyCallBack mOpenKeyCallBack;

    private boolean mEnvironmentType;
    private Runnable runnableTimeOut = new Runnable() {
        @Override
        public void run() {
            if (!Constants.IS_SCANNING_STOPPED && mOpenKeyCallBack != null) {
                Sentry.configureScope(scope -> {
                    scope.setTag("timeout", manufacturer.toString() + "");
                    Sentry.captureMessage("timeout->" + manufacturer.toString());
                });
                Constants.IS_SCANNING_STOPPED = true;
//                Log.e("IS_SCANNING_STOPPED", Constants.IS_SCANNING_STOPPED + "  timeout");
                mOpenKeyCallBack.stopScan(false, Response.TIME_OUT_LOCK_NOT_FOUND);
            }
        }
    };

    private Runnable runnableTimeOutKeyDownload = new Runnable() {
        @Override
        public void run() {

        }
    };
    //-----------------------------------------------------------------------------------------------------------------|
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

    /**
     * to implement key download failure callback after 60 seconds
     */
    void keyDownloadTimeout() {
        if (keyTimeoutHandler == null) {
            keyTimeoutHandler = new Handler();
        }
        keyTimeoutHandler.postDelayed(runnableTimeOutKeyDownload, 60 * 1000);
    }

    void removeDownloadTimeoutHandler() {
        if (keyTimeoutHandler != null) {
            keyTimeoutHandler.removeCallbacks(runnableTimeOutKeyDownload);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * This will return the instance of this class and provide a
     * mContext to this class.
     *
     * @return Instance of the {@link OpenKeyManager} class
     */
    public static synchronized OpenKeyManager getInstance() {
        if (instance == null) {
            instance = new OpenKeyManager();
        }
        return instance;
    }

    /**
     * Access to this class can only be provided by this class
     * so object creation is limited to this only.
     *
     * @param context
     */
    public void init(Application context, String UUID) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException(Response.NULL_CONTEXT);
        }
        Sentry.configureScope(scope -> {
            scope.setTag("uuid", UUID);
            Sentry.captureMessage("UUID->" + UUID);
        });
        handler = new Handler();
        mContext = context;
        Utilities.getInstance(mContext);
        Utilities.getInstance().saveValue(Constants.UUID, UUID, mContext);
        SessionResponse sessionResponse = Utilities.getInstance().getBookingFromLocal(mContext);
        if (sessionResponse != null) {
            GetBooking.getInstance().setBooking(sessionResponse);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * @param authToken
     * @param openKeyCallBack Call back for response purpose
     */
    public void authenticate(String authToken, OpenKeyCallBack openKeyCallBack, EnvironmentType environmentType) {
        Constants.IS_SESSION_API_ALREADY_CALLED = false;
        //Set configuration
        setConfiguration(environmentType);
//        Log.e("Callback OKManager", openKeyCallBack + "");
        if (authToken != null && authToken.length() > 0 && mContext != null) {
            Api.getSession(mContext, authToken, openKeyCallBack);
        } else {
            openKeyCallBack.sessionFailure(Response.INVALID_AUTH_SIGNATURE, "");
        }
    }

    //- ----------------------------------------------------------------------------------------------------------------|
    private void setConfiguration(EnvironmentType environmentType) {
        if (mContext != null) {
//            Log.e("OK ENV", environmentType.name() + "");
            Utilities.getInstance().saveValue(Constants.ENVIRONMENT_TYPE, environmentType.name(), mContext);
            if (environmentType.equals(EnvironmentType.LIVE)) {
                Utilities.getInstance().saveValue(Constants.BASE_URL, Constants.BASE_URL_LIVE, mContext);
            } else if (environmentType.equals(EnvironmentType.STAGE)) {
                Utilities.getInstance().saveValue(Constants.BASE_URL, Constants.BASE_URL_STAGE, mContext);
            } else {
                Utilities.getInstance().saveValue(Constants.BASE_URL, Constants.BASE_URL_DEV, mContext);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|

    public void getSession(String authToken, final Callback callback) {
        //Set configuration
        if (mContext != null && authToken != null) Api.getBooking(authToken, mContext, callback);
    }


    private boolean hasBTPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            }
        }
        return true;
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
        if (mContext == null) {
            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED + " Context=null");
            return;
        }
        Log.e("Initialization Start", "true");
        if (!hasBTPermissions()) {
            openKeyCallBack.initializationFailure(Response.BT_PERMISSION_MISSING);
            return;
        }
        if (!Utilities.getInstance().isOnline(mContext)) {
            Log.d("Exception", "No internet connection found.");
            return;
        }
        final String tokenStr = Utilities.getInstance().getValue(Constants.AUTH_SIGNATURE, "", mContext);

        final String manufacturerStr = Utilities.getInstance().getValue(Constants.MANUFACTURER, "", mContext);
        if (manufacturerStr.isEmpty()) {
            openKeyCallBack.initializationFailure(Response.BOOKING_NOT_FOUNT);
            return;
        }
        manufacturer = Utilities.getInstance().getManufacturer(mContext, openKeyCallBack);

        if (tokenStr != null && tokenStr.length() > 0) {
            getSession(tokenStr, new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
                    switch (manufacturer) {
                        case ASSA:
                            assa = new ASSA(mContext, openKeyCallBack);
                            break;

                        case SALTO:
                            salto = new Salto(mContext, openKeyCallBack);
                            break;

                        case KABA:
                            kaba = new Kaba(mContext, openKeyCallBack);
                            break;

                        case MIWA:
                            miwa = new Miwa(mContext, openKeyCallBack);
                            break;

                        case MODULE:
                            okModule = new OKModule(mContext, openKeyCallBack);
                            break;

                        case OKMOBILEKEY:
                            if (tokenStr != null && tokenStr.length() > 0) {
                                Api.getSession(mContext, tokenStr, null);
                            }
                            okMobileKey = new OKMobileKey(mContext, openKeyCallBack);
                            break;

                        case OKC:
                            okc = new OKC(mContext, openKeyCallBack);
                            break;

                        case DRK:
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                if (tokenStr != null && tokenStr.length() > 0) {
//                            Log.e("OK Session Api Call DRK",openKeyCallBack+"");
                            Api.getSession(mContext, tokenStr, null); //comment because session api getting call 2 time and callback getting set as null
//                                }
                            drkModule = new DRKModule(mContext, openKeyCallBack);
//                            } else {
//                                mOpenKeyCallBack.initializationFailure("Unsupported Android version, V3 will only support API level 23 and 23+ versions.");
//                            }
                            break;

                        case ENTRAVA:
                        case ENTRAVATOUCH:
                            entrava = new Entrava(mContext, openKeyCallBack);
                            break;
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    openKeyCallBack.initializationFailure(Response.BOOKING_NOT_FOUNT);
                }
            });
        }

    }

    /**
     * Initialize SDK with unique number.
     * <p>
     * the unique identification number for setting up device with
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void initObject(@NonNull OpenKeyCallBack openKeyCallBack) {
        if (mContext == null) {
            return;
        }

        final String manufacturerStr = Utilities.getInstance().getValue(Constants.MANUFACTURER, "", mContext);
        if (manufacturerStr.isEmpty()) {
            return;
        }
        manufacturer = Utilities.getInstance().getManufacturer(mContext, openKeyCallBack);
        switch (manufacturer) {
            case ASSA:
                assa = new ASSA(mContext, openKeyCallBack);
                break;

            case SALTO:
                salto = new Salto(mContext, openKeyCallBack);
                break;

            case KABA:
                kaba = new Kaba(mContext, openKeyCallBack);
                break;

            case DRK:
                drkModule = new DRKModule(mContext, openKeyCallBack);
                break;

            case ENTRAVA:
            case ENTRAVATOUCH:
                entrava = new Entrava(mContext, openKeyCallBack);
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
        if (mContext == null && assa == null && salto == null && kaba == null && miwa == null && entrava == null && okModule == null && okMobileKey == null && okc == null && drkModule == null) {
            openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
            return;
        }

        mOpenKeyCallBack = openKeyCallBack;

        //if mContext null then it returned callback with null mContext description
        if (mContext == null) openKeyCallBack.isKeyAvailable(false, Response.NULL_CONTEXT);


        //Getting key from server
        Api.getMobileKey(mContext, getKeyCallback);
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * If the user is successfully
     * get keys then start sync process via this method
     */
    private void startSync() {
        if (mOpenKeyCallBack == null || mContext == null) return;

        manufacturer = Utilities.getInstance().getManufacturer(mContext, mOpenKeyCallBack);
        switch (manufacturer) {
            case ASSA:
                if (assa.isSetupComplete()) {
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
                okc.fetchOkcRoomList();
                updateKeyStatus(true);
                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                break;

            case MODULE:

                updateKeyStatus(true);
                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                okModule.fetchOkModuleRoomList();
                break;

            case OKMOBILEKEY:

//                SessionResponse sessionResponse = GetBooking.getInstance().getBooking();
//                if (sessionResponse != null && sessionResponse.getData().getMobileKeyStatusId() == 2) {
                updateKeyStatus(true);
//                }

                okMobileKey.fetchOkMobileKeyRoomList();

                mOpenKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                break;

            case DRK:
                drkModule.fetchKeys();
                break;
        }
    }


    public void startOkMobileScanning() {
        okMobileKey.fetchOkMobileKeyRoomList();
    }


  /*  public void  removeCallBack() {
        okMobileKey.removeAllCallBack();
    }

    public void connectOkMobileKey(String roomTitle) {
        okMobileKey.connectDevice(roomTitle);
    }*/

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * If device has a key available
     *
     * @param openKeyCallBack Call back for response purpose
     * @return boolean
     */
    public synchronized boolean isKeyAvailable(OpenKeyCallBack openKeyCallBack) {
        boolean haveKey = false;
        if (assa == null && salto == null && kaba == null && drkModule == null && entrava == null) {
            initObject(openKeyCallBack);
//            Log.e("Started", "INITIALIZATION_FAILED");
//            openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
//        initialize(openKeyCallBack);
//            return haveKey;
        }

        manufacturer = Utilities.getInstance().getManufacturer(mContext, openKeyCallBack);
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
                if (entrava == null) {
                    haveKey = false;
                } else {
                    haveKey = entrava.haveKey();
                }
                break;

            case OKC:
                haveKey = okc.haveKey();
                break;

            case MODULE:
                haveKey = okModule.haveKey();
                break;

            case OKMOBILEKEY:
                haveKey = okMobileKey.haveKey();
                break;

            case DRK:
                if (drkModule == null) {
                    initialize(openKeyCallBack);
                    haveKey = false;
                } else {
                    haveKey = drkModule.haveKey();
                }
                break;
        }
        return haveKey;
    }

    //-----------------------------------------------------------------------------------------------------------------|

    private void timeOut(int time) {
//        if (handler == null) {
        handler = new Handler();
//        }
        handler.postDelayed(runnableTimeOut, time * 1000);
    }

    /**
     * start scanning if passes the initial checks
     * and device have a key
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void startScanning(@NonNull OpenKeyCallBack openKeyCallBack, String
            roomNumber) {
        manufacturer = Utilities.getInstance().getManufacturer(mContext, openKeyCallBack);
        Constants.IS_SCANNING_STOPPED = false;
//        Log.e("IS_SCANNING_STOPPED", Constants.IS_SCANNING_STOPPED + "  startScaning");
        if (mContext == null) {
            Log.e("Context", "null");
            openKeyCallBack.initializationFailure(Response.NULL_CONTEXT);
        }
//        Log.e("OKMGR", "Start Scanning");

        if (isKeyAvailable(openKeyCallBack)) {
            if (manufacturer.equals(DRK)) {
                timeOut(15);
            } else {
                timeOut(15);
            }
            switch (manufacturer) {
                case OKC:
                    okc.startScanning(roomNumber);
                    break;

                case MODULE:
                    okModule.startScanning(roomNumber);
                    break;

                case OKMOBILEKEY:
                    okMobileKey.startScanning(roomNumber);
                    break;

                case DRK:
//                    Log.e("OKMGR", "OPENING " + roomNumber);
                    drkModule.open(roomNumber);
                    break;

                case ASSA:
                    if (assa.isSetupComplete()) {
                        assa.startScanning();
                    } else {
                        if (!Constants.IS_SCANNING_STOPPED) {
                            Constants.IS_SCANNING_STOPPED = true;
                            removeTimeoutHandler();
                            Sentry.configureScope(scope -> {
                                scope.setTag("stopScan", "ASSA endpoints not generated");
                                Sentry.captureMessage("stopScan->ASSA endpoints not generated");
                            });
                            openKeyCallBack.stopScan(false, Response.NOT_INITIALIZED);
//                            openKeyCallBack.stopScan(false, "ASSA end-points not generated");
                        }
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
//            Log.e("startScanning", "key not available");
            if (!Constants.IS_SCANNING_STOPPED) {
                Constants.IS_SCANNING_STOPPED = true;
                removeTimeoutHandler();
                Sentry.configureScope(scope -> {
                    scope.setTag("stopScan", "Key Not Found");
                    Sentry.captureMessage("stopScan->Key not found");
                });
                openKeyCallBack.stopScan(false, Response.NO_KEY_FOUND);
            }
        }
    }

    /**
     * start scanning if passes the initial checks
     * and device have a key
     *
     * @param openKeyCallBack Call back for response purpose
     */
    public synchronized void startScanning(@NonNull OpenKeyCallBack openKeyCallBack, String
            roomNumber, String subModule) {
        manufacturer = Utilities.getInstance().getManufacturer(mContext, openKeyCallBack);
        Constants.IS_SCANNING_STOPPED = false;
        Log.e("IS_SCANNING_STOPPED", Constants.IS_SCANNING_STOPPED + "  startScaning");
        if (mContext == null) {
            Log.e("Context", "null");
            openKeyCallBack.initializationFailure(Response.NULL_CONTEXT);
        }
//        Log.e("OKMGR", "Start Scanning");

        if (isKeyAvailable(openKeyCallBack)) {
            timeOut(15);
            switch (manufacturer) {
                case DRK:
//                    Log.e("OKMGR", "OPENING " + roomNumber);
                    drkModule.open(roomNumber, subModule);
//                    drkModule.open(roomNumber);
                    break;
            }
        } else {
//            Log.e("startScanning", "key not available");
            if (!Constants.IS_SCANNING_STOPPED) {
                Constants.IS_SCANNING_STOPPED = true;
                removeTimeoutHandler();
                Sentry.configureScope(scope -> {
                    scope.setTag("stopScan", "Key Not Found");
                    Sentry.captureMessage("stopScan->Key not found");

                });
                openKeyCallBack.stopScan(false, Response.NO_KEY_FOUND);
            }
        }
    }

    public void removeTimeoutHandler() {
//        Log.e("removeTimeoutHandler", "called");
        if (handler != null) {
            handler.removeCallbacks(runnableTimeOut);
        }
    }

    public boolean haveEmptyModules(String roomTitle) {
        if (drkModule != null) {
            return drkModule.haveEmptyModules(roomTitle);
        }
        return false;
    }
    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * * This method is used to update the key status on server.
     * 1 identify the device have key
     * 0 identify the device have not key
     *
     * @param haveKey Device have key or not
     */
    public void updateKeyStatus(boolean haveKey) {

        if (haveKey) Api.setKeyStatus(mContext, Constants.KEY_DELIVERED);
        else Api.setKeyStatus(mContext, Constants.PENDING_KEY_SERVER_REQUEST);
    }
    //-----------------------------------------------------------------------------------------------------------------|

}