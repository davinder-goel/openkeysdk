package com.openkey.sdk.okc;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openkey.okcsdk.BleHelper;
import com.openkey.okcsdk.OKCManager;
import com.openkey.okcsdk.callbacks.OkcManagerCallback;
import com.openkey.okcsdk.model.FetchKeyResponse;
import com.openkey.okcsdk.model.PropertyLock;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.lang.reflect.Type;
import java.util.ArrayList;

//

public class OKC implements OkcManagerCallback {
    ArrayList<PropertyLock> mRoomList;
    private Application mContext;
    private OpenKeyCallBack openKeyCallBack;
    private Gson gson;

    //-----------------------------------------------------------------------------------------------------------------|
    public OKC(Application mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initialize();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void initialize() {
        okcSDKInitialize();
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                0, mContext);
        Log.e("mobileKeyStatusId", ":" + mobileKeyStatusId);
        Log.e("haveKey()", ":" + haveKey());
        if (haveKey() && mobileKeyStatusId == 3) {
            Log.e("Keystatus ", ":" + mobileKeyStatusId);
            Log.e("mobileKeyStatusId ", "haveKey:" + mobileKeyStatusId);
            openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
        } else {
            if (mobileKeyStatusId == 1) {
                /**
                 * Update the status on server that Registration Complete has been completed on Kaba server
                 */
                Log.e("Keystatus ", ":" + mobileKeyStatusId);
                Log.e("mobileKeyStatusId", "is: " + haveKey());
                Api.setPeronalizationComplete(mContext, openKeyCallBack);
            } else {
                Log.e("Keystatus ", ":" + mobileKeyStatusId);
                openKeyCallBack.initializationSuccess();
            }
        }
    }

    public void okcSDKInitialize() {
        OKCManager.getInstance(mContext).registerOkcCallBack(this);
        OKCManager.getInstance(mContext).OkcInit();
        checkPermission();
    }

    private void checkPermission() {
        if (!BleHelper.isBleOpend()) {
            BleHelper.enableBle();
        }
    }

    /**
     * if device has a key for Imgate
     *
     * @return
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        if (key != null && key.length() > 0)
            return true;

        return false;
    }

    public void fetchOkcRoomList() {
        String keyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        OKCManager.getInstance(mContext).fetchKeys(keyToken);

    }

    private Gson createGsonObj() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * start IMGATE service for open lock when scanning animation on going
     */
    public void startScanning() {
        Log.e("OKC startScanning", "true");
        //Retrieve the values
        String jsonText = Utilities.getInstance().getValue(Constants.OKC_ROOM_LIST, "", mContext);
        Type type = new TypeToken<ArrayList<PropertyLock>>() {
        }.getType();

        mRoomList = createGsonObj().fromJson(jsonText, type);
        if (Utilities.getInstance().isOnline(mContext)) {
            if (mRoomList != null && mRoomList.size() > 0) {
                Log.e("OKC mRoomList", "true");

//            for (int i=0; i<mRoomList.size();i++){
                OKCManager.getInstance(mContext).scanMyDevice(mRoomList.get(0).getMac());
//
            } else {
                Log.e("OKC mRoomList", "FALSE");

                openKeyCallBack.initializationFailure("Your Device not Synchronized please click on Fetch Keys");
            }
        }
    }

    @Override
    public void scanResult(String msg) {

    }

    @Override
    public void initilizationSuccess() {
//        openKeyCallBack.initializationSuccess();
    }

    @Override
    public void initilizationFailure() {
    }

    @Override
    public void openDoorSuccess(String msg) {
        Log.e("OpenSucess", "called");
        Api.logSDK(mContext, 1);
        openKeyCallBack.stopScan(true, "");
    }

    @Override
    public void openDoorFailure(String msg) {
        openKeyCallBack.stopScan(false, "");

    }

    @Override
    public void fetchKeySuccess(FetchKeyResponse response) {
        //Set the values

        if (mRoomList == null) {
            mRoomList = new ArrayList<>();
        } else {
            mRoomList.clear();
        }

        if (response != null) {
            Log.e("RoomList Size", response.getData().getPropertyLocks().size() + "");
            mRoomList.addAll(response.getData().getPropertyLocks());
            String jsonText = createGsonObj().toJson(mRoomList);
            Utilities.getInstance().saveValue(Constants.OKC_ROOM_LIST, jsonText, mContext);
        }
    }

    @Override
    public void fetchKeyFailure(String msg) {
//        openKeyCallBack.initializationFailure(msg);
    }
}