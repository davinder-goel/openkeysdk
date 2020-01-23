package com.openkey.sdk.okmodule;

import android.app.Application;
import android.util.Log;

import com.openkey.okmodule.callback.OKModuleCallBack;
import com.openkey.okmodule.ok_manager.OKModuleManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.util.ArrayList;

public class OKModule implements OKModuleCallBack {
    private Application mApplication;
    private OpenKeyCallBack openKeyCallBack;


    //-----------------------------------------------------------------------------------------------------------------|
    public OKModule(Application application, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mApplication = application;
        initialize();
    }


    //-----------------------------------------------------------------------------------------------------------------|

    /*
     * initialize  sdk for OKModule
     *
     * */
    private void initialize() {
        okModuleSDKInitialize();
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS, 0, mApplication);
        Log.e("mobileKeyStatusId", ":" + mobileKeyStatusId);
        Log.e("haveKey()", ":" + haveKey());
        if (haveKey() && mobileKeyStatusId == 3) {
            Log.e("Keystatus ", ":" + mobileKeyStatusId);
            Log.e("mobileKeyStatusId ", "haveKey:" + mobileKeyStatusId);
            openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
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

    private void okModuleSDKInitialize() {
        boolean environmentType = Utilities.getInstance().getValue(Constants.ENVIRONMENT_TYPE, false, mApplication);
        OKModuleManager.Companion.getInstance(mApplication).registerOKModuleCallback(this);
        OKModuleManager.Companion.getInstance(mApplication).OKInit(environmentType);
    }

    /**
     * if device has a key for okmodule
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        return key != null && key.length() > 0;

    }


    /* fetch roomlist from server*/
    public void fetchOkModuleRoomList() {
        String keyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        OKModuleManager.Companion.getInstance(mApplication).fetchKeys(keyToken);

    }


    /**
     * start OKModule scanning for open lock when scanning animation on going
     */
    public void startScanning(String roomNumber) {
        Log.e("OKModule startScanning", "true");

        try {
            OKModuleManager.Companion.getInstance(mApplication).scanDevices(roomNumber);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void scanResult(String msg) {

    }


    @Override
    public void openDoorSuccess(String msg) {
        Log.e("OpenSucess", "called");
        Api.logSDK(mApplication, 1);
        openKeyCallBack.stopScan(true, "");
    }

    @Override
    public void openDoorFailure(String msg) {
        openKeyCallBack.stopScan(false, "");

    }

    @Override
    public void fetchKeySuccess(ArrayList<String> roomList) {

        openKeyCallBack.getOKCandOkModuleMobileKeysResponse(roomList,false);

    }

    @Override
    public void fetchKeyFailure(String msg) {
//        openKeyCallBack.initializationFailure(msg);
    }


    @Override
    public void initializationFailure() {

    }

    @Override
    public void initializationSuccess() {

    }
}
