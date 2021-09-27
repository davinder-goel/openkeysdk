package com.openkey.sdk.drk;

import android.app.Application;
import android.util.Log;

import com.openkey.okdrksdk.callbackmodule.OKDrkCallBack;
import com.openkey.okdrksdk.enums.ResultReturn;
import com.openkey.okdrksdk.ok_manager.DrkManager;
import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.DRKToken;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class DRKModule implements OKDrkCallBack {
    private Application mApplication;
    private OpenKeyCallBack callBack;
    private String state;
    private Callback<DRKToken> drkToken = new Callback<DRKToken>() {
        @Override
        public void onResponse(Call<DRKToken> call, retrofit2.Response<DRKToken> response) {
            if (response.isSuccessful()) {
                DRKToken token = response.body();
                if (token != null && token.getData() != null && token.getData().getCode() != null) {
                    startDrkProcessing(token.getData().getCode());
                } else {
                    // Show the message  from the error body if response is not successful
                    callBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                }
            }

        }

        @Override
        public void onFailure(Call<DRKToken> call, Throwable t) {
            callBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

        }
    };

    public DRKModule(Application application, OpenKeyCallBack OpenKeyCallBack) {
        this.callBack = OpenKeyCallBack;
        this.mApplication = application;
        initialize();
    }


    @Override
    public void initializeResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
            state = "personalizationCheck";
            DrkManager.Companion.getInstance(mApplication).isPersonalized();
        } else {
            callBack.initializationFailure("Initialization failure");
        }
    }

    @Override
    public void personalizationResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null) {
            if (resultReturn.getSuccess() != null && resultReturn.getSuccess()) {
                startDrk();
            } else {
                if (state.equals("personalizationCheck")) {
                    state = "personalizing";
                    getDrkRegistrationId();
                } else {
                    callBack.initializationFailure(resultReturn.getError().name());
                }
            }
        } else {
            callBack.initializationFailure("Drk personalization error");
        }
    }

    /**
     * if device has a key for okmodule
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        return key != null && key.length() > 0;
    }

    public void fetchKeys() {
        DrkManager.Companion.getInstance(mApplication).sync();
    }

    public void syncResult(@Nullable ResultReturn resultReturn) {
        if (resultReturn != null && resultReturn.getSuccess() != null &&
                resultReturn.getSuccess() &&
                resultReturn.getData() != null) {
            Utilities.getInstance().saveValue(Constants.MOBILE_KEY, 1, mApplication);
            callBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
            int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS, 0, mApplication);
            if (mobileKeyStatusId != 3) {
                Api.setKeyStatus(mApplication, Constants.KEY_DELIVERED);
            }
            DrkManager.Companion.getInstance(mApplication).fetchRoomList();
        } else {
            callBack.isKeyAvailable(false, "Key is available");
        }
    }

    @Override
    public void fetchResults(@Nullable ResultReturn resultReturn) {
        Log.e("Call fecth key response", resultReturn + "");
        Log.e("Call fecth key response", resultReturn.getDrkRoomList().size() + "");
        if (resultReturn != null && resultReturn.getSuccess() != null &&
                resultReturn.getSuccess() &&
                resultReturn.getDrkRoomList() != null) {
            callBack.getOKCandOkModuleMobileKeysResponse(resultReturn.getDrkRoomList(), false);
        } else {
            Log.e("fecth key response else", "called");
            callBack.getOKCandOkModuleMobileKeysResponse(new ArrayList<String>(), false);
        }
    }

    public void open(String roomTitle) {
        DrkManager.Companion.getInstance(mApplication).findSubModule(roomTitle, null);
//        DrkManager.Companion.getInstance(mApplication).open(roomTitle);
    }

    //
    public void open(String roomTitle, String subModule) {
        DrkManager.Companion.getInstance(mApplication).findSubModule(roomTitle, subModule);
    }

    @Override
    public void openResult(@Nullable ResultReturn resultReturn) {
        if (!Constants.IS_SCANNING_STOPPED) {
            OpenKeyManager.getInstance().removeTimeoutHandler();

            if (resultReturn != null &&
                    resultReturn.getSuccess() != null &&
                    resultReturn.getSuccess()
            ) {
                Constants.IS_SCANNING_STOPPED = true;
                if (resultReturn.isDoorOpened() != null && resultReturn.isDoorOpened()) {
                    callBack.stopScan(true, "Door Opened");
                    Api.logSDK(mApplication, 1);
                } else {
//                    if (resultReturn.getMessage() != null && resultReturn.getMessage().equals("no lock found")) {
//                        callBack.stopScan(false, "Timeout: Lock not found");
//                    } else {
                    callBack.stopScan(false, "MODULE COULD NOT BE OPENED");
//                    }
                }
            } else {
                Log.e("openResult", "OKSDK DrkModule 157");
//                if (resultReturn.getMessage() != null && resultReturn.getMessage().equals("no lock found")) {
//                    callBack.stopScan(false, "Timeout: Lock not found");
//                } else {
//                callBack.stopScan(false, "MODULE COULD NOT BE OPENED");
//                }
            }
        }
    }

    /*
     * initialize  sdk for OKModule
     *
     * */
    private void initialize() {
        String uuid = Utilities.getInstance().getValue(Constants.UUID, "", mApplication);
        boolean environmentType = Utilities.getInstance().getValue(Constants.ENVIRONMENT_TYPE, false, mApplication);
        DrkManager.Companion.getInstance(mApplication).registerCallback(this);
        DrkManager.Companion.getInstance(mApplication).initialize(environmentType, uuid);
    }

    private void startDrk() {
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS, 0, mApplication);
        if (haveKey() && mobileKeyStatusId == 3) {
            fetchKeys();
        } else {
            if (mobileKeyStatusId == 1) {
                Api.setPeronalizationComplete(mApplication, callBack);
            } else {
                callBack.initializationSuccess();
            }
        }
    }

    private void getDrkRegistrationId() {
        Api.setInitializePersonalizationForDRK(mApplication, drkToken, callBack);
    }

    private void startDrkProcessing(String mDrkRegId) {
        if (mDrkRegId.length() > 0) {
            DrkManager.Companion.getInstance(mApplication).personalize(mDrkRegId);
        } else {
            callBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }

    @Override
    public void deleteDRKResult(@Nullable ResultReturn resultReturn) {
        Log.e("Delete DRK", resultReturn.getMessage() + "");
    }

    @Override
    public void fetchSubModuleResults(@Nullable ResultReturn resultReturn) {
        Log.e("okSDK: fetch sub Rooms", resultReturn.getMessage() + "::" + resultReturn.getDrkSubModuleList().size());
        OpenKeyManager.getInstance().removeTimeoutHandler();
        callBack.fetchDrkSubModules(resultReturn.getDrkSubModuleList());
        Constants.IS_SCANNING_STOPPED = true;
    }
}