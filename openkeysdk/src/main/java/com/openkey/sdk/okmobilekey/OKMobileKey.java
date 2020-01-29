package com.openkey.sdk.okmobilekey;

import android.app.Application;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.openkey.okmobilekeysdk.callbackmodule.OKMobileKeyCallBack;
import com.openkey.okmobilekeysdk.ok_manager.OKMobileKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class OKMobileKey implements OKMobileKeyCallBack {
    private Application mApplication;
    private OpenKeyCallBack openKeyCallBack;
    private CountDownTimer mCountDownTimer;
    private Boolean isRunning= false;
    private Long SCANNING_TIME_OKMOBILEKEY = 15000L;
    private Handler mHandlerOkMobileKey = null;
    private Runnable runnableOkMobileKey = this::fetchOkMobileKeyRoomList;



    //-----------------------------------------------------------------------------------------------------------------|
    public OKMobileKey(Application application, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mApplication = application;
        initialize();
        countTimer();
    }

    public void countTimer() {
        mCountDownTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Log.e("timer","tick");
                    isRunning=true;
                }

                public void onFinish() {
                    Log.e("timer","finish");
                    removeAllCallBack();
                    isRunning=false;
                    fetchOkMobileKeyRoomList();
                    mCountDownTimer.cancel();
                }

            };
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /*
     * initialize  sdk for OKModule
     *
     * */
    private void initialize() {
        okMobileKeySDKInitialize();
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS, 0, mApplication);
        Log.e("mobileKeyStatusId", ":" + mobileKeyStatusId);
        Log.e("haveKey()", ":" + haveKey());
        if (haveKey() && mobileKeyStatusId == 3) {
            Log.e("Keystatus ", ":" + mobileKeyStatusId);
            Log.e("mobileKeyStatusId ", "haveKey:" + mobileKeyStatusId);
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

    private void okMobileKeySDKInitialize() {
        boolean environmentType = Utilities.getInstance().getValue(Constants.ENVIRONMENT_TYPE, false, mApplication);
        OKMobileKeyManager.Companion.getInstance(mApplication).registerOKMobileKeyModuleCallback(this);
        OKMobileKeyManager.Companion.getInstance(mApplication).OKInit(environmentType);
    }

    /**
     * if device has a key for okmodule
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        return key != null && key.length() > 0;

    }


    /* fetch roomlist from server*/
    public void fetchOkMobileKeyRoomList() {
        removeAllCallBack();
        String keyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mApplication);
        OKMobileKeyManager.Companion.getInstance(mApplication).fetchKeys(keyToken);
        openKeyCallBack.getOKCandOkModuleMobileKeysResponse(null,true);

    }



    public void removeAllCallBack(){
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
        if(runnableOkMobileKey!=null && mHandlerOkMobileKey!=null){
            mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
        }
    }


    /**
     * start OKModule scanning for open lock when scanning animation on going
     */
    public void startScanning(String title) {


        try {
            OKMobileKeyManager.Companion.getInstance(mApplication).scanDevices(title);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public void connectDevice(String roomTitle) {
        if (mCountDownTimer != null && isRunning)
        {
            mCountDownTimer.cancel();
        }
        if (mHandlerOkMobileKey != null && runnableOkMobileKey != null) {
                    mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
                }

        OKMobileKeyManager.Companion.getInstance(mApplication).connectDevices(roomTitle);
    }

    @Override
    public void scanResult(String msg) {

    }


    @Override
    public void openDoorSuccess(String msg) {
        Api.logSDK(mApplication, 1);
        openKeyCallBack.stopScan(true, "");

        removeAllCallBack();

        mCountDownTimer.start();
    }

    @Override
    public void openDoorFailure(String msg) {
        if(Utilities.getInstance(mApplication).isOnline(mApplication)) {
            openKeyCallBack.stopScan(false, "");
        }
        else{
            Toast.makeText(mApplication,"Network connection failed, Please check your network connection.",Toast.LENGTH_SHORT).show();
        }


        if(mCountDownTimer!=null)
        {
            mCountDownTimer.cancel();
        }


        if (mHandlerOkMobileKey == null) {
            mHandlerOkMobileKey =new Handler();
        }
        else{
            mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
        }
        Log.e("Timerstart", "15s");
        SCANNING_TIME_OKMOBILEKEY = 2000L;
        mHandlerOkMobileKey.postDelayed(runnableOkMobileKey, SCANNING_TIME_OKMOBILEKEY);
    }


    @Override
    public void fetchKeyFailure(String msg) {
//        openKeyCallBack.initializationFailure(msg);
        Toast.makeText(mApplication,"Network connection failed, Please check your network connection.",Toast.LENGTH_SHORT).show();
    }





    @Override
    public void initializationFailure() {

    }

    @Override
    public void initializationSuccess() {

    }

    @Override
    public void fetchKeySuccess(@Nullable ArrayList<String> arrayList, boolean b) {
        openKeyCallBack.getOKCandOkModuleMobileKeysResponse(arrayList,b);
        if(mCountDownTimer!=null)
        {
            mCountDownTimer.cancel();

        }
        else{
            Log.e("timerrumming","false");
        }
        if(b){
            Log.e("device found", "found");
            Log.e("Timerstart", "15s");



            if (mHandlerOkMobileKey == null) {
                mHandlerOkMobileKey =   new Handler();
            }
            else{

                mHandlerOkMobileKey.removeCallbacks(runnableOkMobileKey);
            }
            SCANNING_TIME_OKMOBILEKEY = 15000L;
            mHandlerOkMobileKey.postDelayed(runnableOkMobileKey, SCANNING_TIME_OKMOBILEKEY);
        }

    }
}
