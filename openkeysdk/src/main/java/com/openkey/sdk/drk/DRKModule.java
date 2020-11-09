package com.openkey.sdk.drk;

import android.app.Application;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.openkey.okdrksdk.callbackmodule.OKDrkCallBack;
import com.openkey.okdrksdk.enums.ResultReturn;
import com.openkey.okdrksdk.ok_manager.DrkManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.DRKToken;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class DRKModule implements OKDrkCallBack {
    private Application mApplication;
    private OpenKeyCallBack openKeyCallBack;
    private CountDownTimer mCountDownTimer;
    private Boolean isRunning = false;
    private Handler mHandlerOkMobileKey = null;
    private String mDrkRegId;

    //    private Runnable runnableOkMobileKey = this::fetchOkMobileKeyRoomList;
    private String roomTitle;
    private Callback<DRKToken> drkToken = new Callback<DRKToken>() {
        @Override
        public void onResponse(Call<DRKToken> call, retrofit2.Response<DRKToken> response) {
            if (response.isSuccessful()) {
                DRKToken token = response.body();
                if (token != null && token.getData() != null && token.getData().getCode() != null) {
                    Utilities.getInstance().saveValue(Constants.DRK_REGISTRATION_TOKEN, token.getData().getCode(), mApplication);
//                    DrkManager.Companion.getInstance(mApplication).personalize(token.getData().getCode());
                    startDrkProcessing();
                } else {
                    // Show the message  from the error body if response is not successful
                    openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                }
            }

        }

        @Override
        public void onFailure(Call<DRKToken> call, Throwable t) {
            openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

        }
    };
//
//    public void countTimer() {
//        mCountDownTimer = new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.e("timer", "tick");
//                isRunning = true;
//            }
//
//            public void onFinish() {
//                Log.e("timer", "finish");
//                removeAllCallBack();
//                isRunning = false;
//                fetchOkMobileKeyRoomList();
//                mCountDownTimer.cancel();
//            }
//
//        };
//    }

    //-----------------------------------------------------------------------------------------------------------------|

    //-----------------------------------------------------------------------------------------------------------------|
    public DRKModule(Application application, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mApplication = application;
        initialize();
//        countTimer();
    }

    /*
     * initialize  sdk for OKModule
     *
     * */
    private void initialize() {
        drkSDKInitialize();
    }


    private void startDrkProcessing() {
        mDrkRegId = Utilities.getInstance().getValue(Constants.DRK_REGISTRATION_TOKEN, "", mApplication);
        if (mDrkRegId.length() > 0) {
//            startDrk();
            DrkManager.Companion.getInstance(mApplication).personalize(mDrkRegId);
        } else {
            getDrkRegistrationId();
        }
    }

    private void getDrkRegistrationId() {
        Api.setInitializePersonalizationForDRK(mApplication, drkToken, openKeyCallBack);
    }

    private void startDrk() {
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS, 0, mApplication);
        Log.e("OkmobileKeyStatusId", ":" + mobileKeyStatusId);
        Log.e("haveKey()", ":" + haveKey());
        if (haveKey() && mobileKeyStatusId == 3) {
            Log.e("Keystatus ", ":" + mobileKeyStatusId);
            Log.e("OkmobileKeyStatusId ", "haveKey:" + mobileKeyStatusId);
            openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
        } else {
            if (mobileKeyStatusId == 1) {
                Log.e("Keystatus ", ":" + mobileKeyStatusId);
                Log.e("mobileKeyStatusId", "is: " + haveKey());
                Api.setPeronalizationComplete(mApplication, openKeyCallBack);
            } else {
                Log.e("Keystatus ", ":" + mobileKeyStatusId);
                openKeyCallBack.initializationSuccess();
            }
        }
    }

    private void drkSDKInitialize() {
        String uuid = Utilities.getInstance().getValue(Constants.UUID, "", mApplication);
        boolean environmentType = Utilities.getInstance().getValue(Constants.ENVIRONMENT_TYPE, false, mApplication);
        DrkManager.Companion.getInstance(mApplication).registerDRKModuleCallback(this);
        DrkManager.Companion.getInstance(mApplication).DRKInit(environmentType, uuid);
    }

    /**
     * if device has a key for okmodule
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        return key != null && key.length() > 0;

    }

    /* fetch roomlist from server*/
    public void fetchDrkRoomList() {
//        removeAllCallBack();
//        String keyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        DrkManager.Companion.getInstance(mApplication).syncDevices();
//        openKeyCallBack.getOKCandOkModuleMobileKeysResponse(null, true);

    }

//
//    public void removeAllCallBack() {
//        if (mCountDownTimer != null) {
//            mCountDownTimer.cancel();
//        }
//        if (runnableOkMobileKey != null && mHandlerOkMobileKey != null) {
//            mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
//        }
//    }


    /**
     * start OKModule scanning for open lock when scanning animation on going
     */
    public void startScanning(String title) {
        try {
//            if (title.equalsIgnoreCase("RemoveCallback")) {
//                Log.e("startScanning", "remove call");
////                removeAllCallBack();
//            } else {
//                Log.e("startScanning", "connect devices");
            connectDevice(title);
//            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public void connectDevice(String roomTitle) {
//        if (mCountDownTimer != null && isRunning) {
//            mCountDownTimer.cancel();
//        }
//        if (mHandlerOkMobileKey != null && runnableOkMobileKey != null) {
//            mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
//        }
//
        this.roomTitle = roomTitle;

        DrkManager.Companion.getInstance(mApplication).scanDevices(roomTitle);
    }


    @Override
    public void fetchDeviceDRKResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null && resultReturn.getSuccess()) {

            openKeyCallBack.getOKCandOkModuleMobileKeysResponse(resultReturn.getDrkRoomList(), true);
//            if (mCountDownTimer != null) {
//                mCountDownTimer.cancel();
//
//            } else {
//                Log.e("timerrumming", "false");
//            }
//            if (resultReturn.getSuccess()) {
//                Log.e("device found", "found");
//                Log.e("Timerstart", "15s");
//
//
//                if (mHandlerOkMobileKey == null) {
//                    mHandlerOkMobileKey = new Handler();
//                } else {
//
//                    mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
//                }
////                SCANNING_TIME_OKMOBILEKEY = 15000L;
//
//            }
        } else {
            openKeyCallBack.initializationFailure("error in fetch key");
        }
    }

    @Override
    public void initializeResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
            Api.setInitializePersonalizationForDRK(mApplication, drkToken, openKeyCallBack);
        } else {
            openKeyCallBack.initializationFailure("Initialization failure");
        }
    }

    @Override
    public void openResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
            Api.logSDK(mApplication, 1);
            openKeyCallBack.stopScan(true, "");
        } else {
            if (Utilities.getInstance(mApplication).isOnline(mApplication)) {
                openKeyCallBack.stopScan(false, "");
            } else {
                Toast.makeText(mApplication, "Network connection failed, Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void personalizationResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null) {
            if (resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
                startDrk();
            } else {
                openKeyCallBack.initializationFailure(resultReturn.getError().name());
            }
            openKeyCallBack.initializationFailure("Drk personalization error");
        } else {
            openKeyCallBack.initializationFailure("Drk personalization error");
        }
    }

    @Override
    public void scanningResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
            DrkManager.Companion.getInstance(mApplication).connectDevices(roomTitle);
        }
    }

    @Override
    public void syncResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null &&
                resultReturn.getSuccess() &&
                resultReturn.getSyncData() != null && resultReturn.getSyncData().getData() != null) {
            for (int i = 0; i < resultReturn.getSyncData().getData().size() - 1; i++) {
                DrkManager.Companion.getInstance(mApplication).fetchKeys(resultReturn.getSyncData().getData().get(i).getId());
            }
        } else {
            Toast.makeText(mApplication, "Something went wrong, please try after some time.", Toast.LENGTH_SHORT).show();
        }
    }
}
