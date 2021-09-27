package com.openkey.sdk.assa;

import android.app.Application;
import android.app.Notification;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.assaabloy.mobilekeys.api.ApiConfiguration;
import com.assaabloy.mobilekeys.api.MobileKey;
import com.assaabloy.mobilekeys.api.MobileKeys;
import com.assaabloy.mobilekeys.api.MobileKeysApi;
import com.assaabloy.mobilekeys.api.MobileKeysCallback;
import com.assaabloy.mobilekeys.api.MobileKeysException;
import com.assaabloy.mobilekeys.api.ReaderConnectionController;
import com.assaabloy.mobilekeys.api.ble.OpeningResult;
import com.assaabloy.mobilekeys.api.ble.OpeningStatus;
import com.assaabloy.mobilekeys.api.ble.OpeningTrigger;
import com.assaabloy.mobilekeys.api.ble.OpeningTriggerMediator;
import com.assaabloy.mobilekeys.api.ble.OpeningType;
import com.assaabloy.mobilekeys.api.ble.Reader;
import com.assaabloy.mobilekeys.api.ble.ReaderConnectionCallback;
import com.assaabloy.mobilekeys.api.ble.ReaderConnectionListener;
import com.assaabloy.mobilekeys.api.ble.RssiSensitivity;
import com.assaabloy.mobilekeys.api.ble.ScanConfiguration;
import com.assaabloy.mobilekeys.api.ble.ScanMode;
import com.assaabloy.mobilekeys.api.ble.SeamlessOpeningTrigger;
import com.assaabloy.mobilekeys.api.ble.TapOpeningTrigger;
import com.openkey.sdk.BuildConfig;
import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.OpenkeyLog;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.invitation_code.InvitationCode;
import com.openkey.sdk.assa.PayloadHelper.ByteArrayHelper;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.openkey.sdk.Utilities.Constants.SCANNING_TIME;


public final class ASSA implements MobileKeysApiFactory, ReaderConnectionListener {

    private final String TAG = getClass().getSimpleName();

    //ASSA NEW openKeyCallBack IMPLEMENTATION 6.5.1
    private OpenKeyCallBack openKeyCallBack;
    private MobileKeysApi mobileKeysFactory;
    private Application mContext;
    private ReaderConnectionCallback readerConnectionCallback;
    private Handler mHandlerStopScanning;
    private boolean isLoginActionFired;

    //-----------------------------------------------------------------------------------------------------------------|
    private Callback<InvitationCode> invitationCodeCallback = new Callback<InvitationCode>() {
        @Override
        public void onResponse(@NonNull Call<InvitationCode> call, retrofit2.Response<InvitationCode> response) {
            if (response.isSuccessful()) {

                if (response.body() != null) {
                    InvitationCode responseModel = response.body();
                    //If the invitation code is null then call back return initialization failed
                    if (!(responseModel.getData() != null && responseModel.getData().getCode() != null
                            && responseModel.getData().getCode().getInvitationCode() != null)) {
                        openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                    } else {
                        String invitationCode = responseModel.getData().getCode().getInvitationCode();
                        OpenkeyLog.e("InvitationCode" + ":" + invitationCode);
                        Utilities.getInstance().saveValue(Constants.INVITATION_CODE,
                                invitationCode, mContext);
                        startSetup();
                    }


                }

            } else {
                // Show the message  from the error body if response is not successful
                Utilities.getInstance().handleApiError(response.errorBody(), mContext);
                openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
            }
        }

        @Override
        public void onFailure(Call<InvitationCode> call, Throwable t) {
            openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

        }
    };

    //-----------------------------------------------------------------------------------------------------------------|
    //Callback run after 12 seconds to stop scanning if its already started
    private Runnable scanningStopCallBack = this::stopScanning;

    //-----------------------------------------------------------------------------------------------------------------|
    public ASSA(Application mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initializeMobileKeysApi();
        startSetup();
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * Configure and initialize the ASSA SDK
     */
    private void initializeMobileKeysApi() {
        if (mobileKeysFactory == null) {
            // 1 is testing lock service code , but we never use it to open locks
            // we always get service code from the keys.
            final int LOCK_SERVICE_CODE = 1;

            ScanConfiguration scanConfiguration = new ScanConfiguration.Builder(new OpeningTrigger[]{
                    new TapOpeningTrigger(mContext),
                    new OpeningTriggerMediator(),
                    new SeamlessOpeningTrigger()}, LOCK_SERVICE_CODE).build();

            ApiConfiguration apiConfiguration = new ApiConfiguration.Builder()
                    .setApplicationId(BuildConfig.LIBRARY_PACKAGE_NAME)
                    .setApplicationDescription("1.0")
                    .build();
            scanConfiguration.setScanMode(ScanMode.OPTIMIZE_PERFORMANCE);
            scanConfiguration.setRssiSensitivity(RssiSensitivity.NORMAL);
            mobileKeysFactory = MobileKeysApi.getInstance();
            if (!mobileKeysFactory.isInitialized()) {
                OpenkeyLog.e("isInitialized:");
                mobileKeysFactory.initialize(mContext, apiConfiguration, scanConfiguration);
            }

            if (readerConnectionCallback == null) {
                OpenkeyLog.e("ReaderConnectionCallback : Registered");
                readerConnectionCallback = new ReaderConnectionCallback(mContext);
                readerConnectionCallback.registerReceiver(this);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|


    /**
     * This will handle all the process of setting up the device, it will return
     * immediately if already startSetup  completed
     */
    private void startSetup() {
        // if already personalized then return success
        if (isSetupComplete()) {
            int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                    0, mContext);
            if (haveKey() && mobileKeyStatusId == 3) {
                OpenkeyLog.e("haveKey()" + ":" + haveKey());
                openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
            } else {
                if (mobileKeyStatusId == 1) {
                    Api.setPeronalizationComplete(mContext, openKeyCallBack);
                } else {
                    openKeyCallBack.initializationSuccess();
                }
            }

        } else {
            String invitationCode = Utilities.getInstance().getValue(Constants.INVITATION_CODE, "", mContext);
            if (invitationCode.length() > 0) {
                // personalised the device for ASSA
                personalize(invitationCode);
            } else {
                // Generate a endpoint on ASSA server for personalizing the device with
                //the returned code.
                Api.setInitializePersonalization(mContext, invitationCodeCallback, openKeyCallBack);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * To get the current startSetup status
     *
     * @return true if startSetup is completed false otherwise
     */
    public boolean isSetupComplete() {
        try {
            OpenkeyLog.e("ASSA" + " :SetupCompleted " + getMobileKeys().isEndpointSetupComplete());
            return getMobileKeys().isEndpointSetupComplete();
        } catch (MobileKeysException e) {

            OpenkeyLog.e("MobileKeysException" + ":" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public MobileKeys getMobileKeys() {
        return mobileKeysFactory.getMobileKeys();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public ReaderConnectionController getReaderConnectionController() {
        return mobileKeysFactory.getReaderConnectionController();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public ScanConfiguration getScanConfiguration() {
        return getReaderConnectionController().getScanConfiguration();
    }

    //-----------------------------------------------------------------------------------------------------------------|


    /**
     * Personalize the device for ASSA
     *
     * @param personalizationCode invitation code that is returned by the createEndPoint
     */
    private void personalize(String personalizationCode) {
        getMobileKeys().endpointSetup(new MobileKeysCallback() {
            @Override
            public void handleMobileKeysTransactionCompleted() {
                Api.setPeronalizationComplete(mContext, openKeyCallBack);
            }

            @Override
            public void handleMobileKeysTransactionFailed(MobileKeysException e) {
                OpenkeyLog.e("personalize" + "failed");
                Utilities.getInstance().saveValue(Constants.INVITATION_CODE, "", mContext);
                startSetup();
            }
        }, personalizationCode);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void onReaderConnectionOpened(Reader reader, OpeningType openingType) {
    }

    //-----------------------------------------------------------------------------------------------------------------|


    @Override
    public void onReaderConnectionFailed(Reader reader, OpeningType openingType, OpeningStatus openingStatus) {
        responseCallBack(false, openingStatus.name() + "", false);
        OpenkeyLog.e("onReaderConnectionFailed");

    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public synchronized void onReaderConnectionClosed(Reader reader, OpeningResult openingResult) {
        OpenkeyLog.e("onReaderConnectionClosed");

        final boolean isReaderOpened = openingResult.getOpeningStatus().equals(OpeningStatus.SUCCESS);
        final boolean isLockOpened = isLockOpened(openingResult);
        byte[] payload = openingResult.getStatusPayload();
        boolean isV1Board = false;
        if (isReaderOpened && !ByteArrayHelper.containsData(payload)) {
            isV1Board = true;
        }
        responseCallBack(isLockOpened, openingResult.getOpeningStatus().name(), isV1Board);
        OpenkeyLog.e("ASSA isLockOpened" + isLockOpened + "");
        OpenkeyLog.e("ASSA isReaderOpened" + isReaderOpened + "");
        OpenkeyLog.e("ASSA openingResult" + openingResult + "");

        if (reader != null && isReaderOpened) {
            if (isLockOpened) {
                // if lock opened successfully then let user know
                // save door opened log on server
                OpenkeyLog.e("Lock Opened Successfully");
                if (isLoginActionFired) {
                    isLoginActionFired = false;
                    Api.logSDK(mContext, 1);
                }

            }
        } else {
            OpenkeyLog.e("Reader Opening Failed");
        }
        Utilities.getInstance().vibrate(mContext);

    }

    //-----------------------------------------------------------------------------------------------------------------|


    /**
     * send response via callback and remove callback
     * for unwanted stopping of scanning
     *
     * @param isOpened is lock opened or not
     */
    private void responseCallBack(boolean isOpened, String description, boolean isV1Board) {
        if (!Constants.IS_SCANNING_STOPPED) {
            OpenKeyManager.getInstance().removeTimeoutHandler();
            Constants.IS_SCANNING_STOPPED = true;
//            if (isV1Board) {
//                openKeyCallBack.stopScan(false, "Timeout");
//            } else {
            if (isOpened) {
                openKeyCallBack.stopScan(true, Response.LOCK_OPENED_SUCCESSFULLY);
            } else {
                if (isV1Board) {
                    openKeyCallBack.stopScan(false, Response.TIME_OUT_LOCK_NOT_FOUND);
                } else {
                    openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
//                    openKeyCallBack.stopScan(false, description);
                }
            }
        }
        ReaderConnectionController controller = MobileKeysApi.getInstance().getReaderConnectionController();
        controller.stopScanning();
        controller.disableHce();

        if (mHandlerStopScanning != null) {
            mHandlerStopScanning.removeCallbacks(scanningStopCallBack);
        }

    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * It will start scanning service and look for the bluetooth
     * reader(Locks) and communicate with them if found one
     */
    public void startScanning() {
        isLoginActionFired = true;
        OpenkeyLog.d("Starting BLE service and enabling HCE");
        ReaderConnectionController controller = mobileKeysFactory.getReaderConnectionController();
        controller.enableHce();
        Notification notification = UnlockNotification.create(mContext);
        controller.startForegroundScanning(notification);
        mHandlerStopScanning = new Handler();
        mHandlerStopScanning.postDelayed(scanningStopCallBack, SCANNING_TIME);
    }

    public void breakBleConnection() {
        ReaderConnectionController controller = mobileKeysFactory.getReaderConnectionController();
        controller.stopScanning();
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * Stop looking for locks
     */
    private void stopScanning() {
        ReaderConnectionController controller = MobileKeysApi.getInstance().getReaderConnectionController();
        controller.stopScanning();
        controller.disableHce();
        OpenkeyLog.e("stopScanning" + ":called");
        responseCallBack(false, Response.LOCK_OPENING_FAILURE, false);
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * check if device have keys
     *
     * @return true/false
     */
    public boolean haveKey() {
        boolean haveKey = false;
        try {
            List<MobileKey> keys = getMobileKeys().listMobileKeys();
            OpenkeyLog.e("Keys found : " + keys.size());
            if (keys.size() > 0) {
                final String cardNumber = keys.get(0).getCardNumber();
                int lockServiceCode = Integer.valueOf(cardNumber.split("-")[0]);

                // change the lock service code for scanning so that scanning can
                // be performed for the lock that can opened by the existing key
                getScanConfiguration().setLockServiceCodes(lockServiceCode);
                haveKey = true;
            }
        } catch (MobileKeysException e) {
            OpenkeyLog.e("MobileKeysException" + e.getMessage());
        }
        return haveKey;
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * Check if lock has been opened or not
     *
     * @param result lock opening result
     * @return true if lock opened,false otherwise
     */
    private boolean isLockOpened(OpeningResult result) {
        byte[] payload = result.getStatusPayload();
        OpenkeyLog.e("ASSA payload" + payload);
        OpenkeyLog.e("ASSA getOpeningStatus" + result.getOpeningStatus() + " ");
        OpenkeyLog.e("ASSA opening type" + result.getOpeningType() + " ");

        if (ByteArrayHelper.containsData(payload)) {
            OpenkeyLog.e("ASSA containsData" + "containsData ");
            if (ByteArrayHelper.didUnlock(payload)) {
                OpenkeyLog.e("ASSA didUnlock" + "didUnlock ");
                return true;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------|


    /**
     * Get key from the endpoint if key has been issued from the server
     */
    public void getKey() {
        getMobileKeys().endpointUpdate(new MobileKeysCallback() {
            @Override
            public void handleMobileKeysTransactionCompleted() {
                if (mContext == null)
                    openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);

                boolean haveKey = haveKey();
                OpenKeyManager.getInstance().updateKeyStatus(haveKey);
                if (haveKey) {
                    openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                } else {
                    openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
                }
            }

            @Override
            public void handleMobileKeysTransactionFailed(MobileKeysException e) {
                OpenKeyManager.getInstance().updateKeyStatus(false);
                OpenkeyLog.e("handleMobileKeysTransactionFailed" + ": " + e.getMessage());
                openKeyCallBack.initializationFailure(Response.FETCH_KEY_FAILED);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------|

}