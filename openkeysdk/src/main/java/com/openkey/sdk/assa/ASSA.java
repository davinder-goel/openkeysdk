/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.assa;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private Context mContext;
    private ReaderConnectionCallback readerConnectionCallback;
    private Handler mHandlerStopScanning;

    public ASSA(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initializeMobileKeysApi();
        startSetup();
    }

    private Callback<InvitationCode> invitationCodeCallback = new Callback<InvitationCode>() {
        @Override
        public void onResponse(@NonNull Call<InvitationCode> call, retrofit2.Response<InvitationCode> response) {
            if (response.isSuccessful()) {

                if (response.body() != null) {

//                    String codeKey = response.body().toString();
//
//                    if (!(codeKey != null && codeKey.length() > 0))
//                    {
//                        openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
//                        return;
//                    }
//
//
//                    AssaInvitationCode responseModel = GetGson.getInstance()
//                            .fromJson(codeKey,
//                                    AssaInvitationCode.class);
                    InvitationCode responseModel = response.body();
                    //If the invitation code is null then call back return with initialization failed
                    if (!(responseModel != null && responseModel.getData() != null && responseModel.getData().getCode() != null
                            && responseModel.getData().getCode().getInvitationCode() != null)) {
                        openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                    } else {
                        String invitationCode = responseModel.getData().getCode().getInvitationCode();
                        Log.e("InvitationCode", ":" + invitationCode);

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
    //Callback run after 12 seconds to stop scanning if its already started
    private Runnable scanningStopCallBack = new Runnable() {
        @Override
        public void run() {
            stopScanning();
        }
    };

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
                    .setApplicationId(BuildConfig.APPLICATION_ID)
                    .setApplicationDescription(BuildConfig.VERSION_NAME)
                    .build();
            scanConfiguration.setScanMode(ScanMode.OPTIMIZE_PERFORMANCE);
            scanConfiguration.setRssiSensitivity(RssiSensitivity.NORMAL);
            mobileKeysFactory = MobileKeysApi.getInstance();
            if (!mobileKeysFactory.isInitialized()) {
                Log.e(TAG, "isInitialized:");
                mobileKeysFactory.initialize(mContext, apiConfiguration, scanConfiguration);
            }

            if (readerConnectionCallback == null) {
                Log.e(TAG, "ReaderConnectionCallback : Registered");
                readerConnectionCallback = new ReaderConnectionCallback(mContext);
                readerConnectionCallback.registerReceiver(this);
            }
        }
    }

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
                Log.e("haveKey()", ":" + haveKey());
                openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                return;
            }

            Api.setPeronalizationComplete(mContext,openKeyCallBack);
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

    /**
     * To get the current startSetup status
     *
     * @return true if startSetup is completed false otherwise
     */
    public boolean isSetupComplete() {
        try {
            Log.e("ASSA", " :SetupCompleted " + getMobileKeys().isEndpointSetupComplete());
            return getMobileKeys().isEndpointSetupComplete();
        } catch (MobileKeysException e) {

            Log.e("MobileKeysException", ":" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public MobileKeys getMobileKeys() {
        return mobileKeysFactory.getMobileKeys();
    }

    @Override
    public ReaderConnectionController getReaderConnectionController() {
        return mobileKeysFactory.getReaderConnectionController();
    }

    @Override
    public ScanConfiguration getScanConfiguration() {
        return getReaderConnectionController().getScanConfiguration();
    }

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
                Log.e("personalize", "failed");
                // tell user , startSetup is failure
                openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
            }
        }, personalizationCode);
    }





    /* *//**
     * this method is used to get Invitation Code
     *
     * @param endPointId phone Number that uses as a Endpoint ID
     *//*
    private synchronized void createEndPoint(final String endPointId) {
        Services services = Utilities.getInstance().getRetrofitForASSA(mContext).create(Services.class);
        final CreateEndPoint createEndPoint = new CreateEndPoint(endPointId);
        String authorization=Utilities.getInstance().getValue(Constants.ASSA_TOKEN,Constants.ASSA_DEV_TOKEN,mContext);
        services.getInvitationCode(authorization,createEndPoint).enqueue(new Callback<EndPointResponse>() {
            @Override
            public void onResponse(Call<EndPointResponse> call, retrofit2.Response<EndPointResponse> response) {
                if (response.isSuccessful()) {

                    if (response.body()!=null)
                    {
                        EndPointResponse endPointResponse=response.body();

                        if (endPointResponse!=null&&endPointResponse.getInvitationCode()!=null
                                &&endPointResponse.getInvitationCode().length()>0)
                        {
                            Utilities.getInstance().saveValue(Constants.INVITATION_CODE,
                                    endPointResponse.getInvitationCode(), mContext);
                            Log.e("createEndPoint", "startSetup");
                            // if endpoint created successfully then start personalizing
                            startSetup();
                        }
                    }

                } else {
                    Log.e("createEndPoint", "deleteEndPoint");
                    // if endpoint already startSetup on other device then delete it and request again for new one
                    deleteEndPoint(endPointId);
                }
            }

            @Override
            public void onFailure(Call<EndPointResponse> call, Throwable t) {
                Log.e("createEndPoint", "onFailure" + t.getMessage());
                openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
            }
        });
    }
*/

    /**
     * Delete end point, if endpoint is already exist
     *//*
    private synchronized void deleteEndPoint(final String endpointID) {
        Services services = Utilities.getInstance().getRetrofitForASSA(mContext).create(Services.class);
        String authorization=Utilities.getInstance().getValue(Constants.ASSA_TOKEN,Constants.ASSA_DEV_TOKEN,mContext);
        services.deleteEndPoint(authorization,endpointID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    // after deleting , create new
                    createEndPoint(endpointID);
                } else {
                    openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                openKeyCallBack.initializationFailure(Response.INITIALIZATION_FAILED);
            }
        });
    }*/


    @Override
    public void onReaderConnectionOpened(Reader reader, OpeningType openingType) {
    }

    @Override
    public void onReaderConnectionFailed(Reader reader, OpeningType openingType, OpeningStatus openingStatus) {
        responseCallBack(false);
    }

    @Override
    public synchronized void onReaderConnectionClosed(Reader reader, OpeningResult openingResult) {
        Log.e(TAG, "onReaderConnectionClosed");

        final boolean isReaderOpened = openingResult.getOpeningStatus().equals(OpeningStatus.SUCCESS);
        final boolean isLockOpened = isLockOpened(openingResult);
        responseCallBack(isLockOpened);

        if (reader != null && isReaderOpened) {
            if (isLockOpened) {
                // if lock opened successfully then let user know
                // save door opened log on server
                Log.e(TAG, "Lock Opened Successfully");
                Api.logSDK(mContext, true);
            }
        } else {
            Log.e(TAG, "Reader Opening Failed");
            Api.logSDK(mContext, false);
        }

    }

    /**
     * send response via callback and remove callback
     * for unwanted stopping of scanning
     *
     * @param isOpened is lock opened or not
     */
    private void responseCallBack(boolean isOpened) {
        if (isOpened) {
            openKeyCallBack.stopScan(true, Response.LOCK_OPENED_SUCCESSFULLY);
        } else {
            openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
        }
        if (mHandlerStopScanning != null) {
            mHandlerStopScanning.removeCallbacks(scanningStopCallBack);
        }
    }

    /**
     * It will start scanning service and look for the bluetooth
     * reader(Locks) and communicate with them if found one
     */
    public void startScanning() {
        getReaderConnectionController().startScanning();
        mHandlerStopScanning = new Handler();
        mHandlerStopScanning.postDelayed(scanningStopCallBack, SCANNING_TIME);
    }

    /**
     * Stop looking for locks
     */
    private void stopScanning() {
        responseCallBack(false);
        getReaderConnectionController().stopScanning();
    }

    /**
     * check if device have keys
     *
     * @return true/false
     */
    public boolean haveKey() {
        boolean haveKey = false;
        try {
            List<MobileKey> keys = getMobileKeys().listMobileKeys();
            Log.e(TAG, "Keys found : " + keys.size());
            if (keys.size() > 0) {
                final String cardNumber = keys.get(0).getCardNumber();
                int lockServiceCode = Integer.valueOf(cardNumber.split("-")[0]);

                // change the lock service code for scanning so that scanning can
                // be performed for the lock that can opened by the existing key
                getScanConfiguration().setLockServiceCodes(lockServiceCode);
                haveKey = true;
            } else { haveKey = false;  }
        } catch (MobileKeysException e) {
            Log.e("MobileKeysException", "initializationFailure: " + e.getMessage());

            openKeyCallBack.initializationFailure(Response.UNKNOWN);
        }
        return haveKey;
    }


    /**
     * Check if lock has been opened or not
     *
     * @param result lock opening result
     * @return true if lock opened,false otherwise
     */
    private boolean isLockOpened(OpeningResult result) {
        byte[] payload = result.getStatusPayload();
        if (ByteArrayHelper.containsData(payload)) {
            if (ByteArrayHelper.didUnlock(payload)) {
                return true;
            }
        }
        return false;
    }




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
                OpenKeyManager.getInstance(mContext).updateKeyStatus(haveKey);
                 if (haveKey) {
                    openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                } else {
                    openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
                }
            }

            @Override
            public void handleMobileKeysTransactionFailed(MobileKeysException e) {
                OpenKeyManager.getInstance(mContext).updateKeyStatus(false);
                Log.e("handleMobileKeysTransactionFailed", "initializationFailure: " + e.getMessage());
                openKeyCallBack.initializationFailure(Response.FETCH_KEY_FAILED);
            }
        });
    }

}
