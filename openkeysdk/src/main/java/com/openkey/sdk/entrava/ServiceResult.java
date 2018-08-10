package com.openkey.sdk.entrava;

import android.util.Log;

import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import kr.co.chahoo.sdk.DoorLockSdk;
import kr.co.chahoo.sdk.ResultCode;
import kr.co.chahoo.sdk.ResultReceiver;

class ServiceResult extends ResultReceiver {

    private OpenKeyCallBack openKeyCallBack;

    private DoorLockSdk doorLockSdk;
    public ServiceResult(OpenKeyCallBack openKeyCallBack,DoorLockSdk doorLockSdk)
    {
     this.doorLockSdk=doorLockSdk;
     this.openKeyCallBack=openKeyCallBack;
    }

        @Override
        protected void onResult(int result, int battery, String issueId) {
            super.onResult(result, battery, issueId);
            doorLockSdk.stop();
            Log.e(" Receiver result", ":" + result);
            Log.e(" Receiver issuedId", ":" + issueId);
            if (result == ResultCode.SUCCESS) {
                openKeyCallBack.stopScan(true,Response.LOCK_OPENED_SUCCESSFULLY);
                Log.e("Entrava Lock: ", "LOCK_OPENED_SUCCESSFULLY");
            } else {
                Log.e("Entrava Lock: ", "LOCK_OPENING_FAILURE");
                openKeyCallBack.stopScan(true, Response.LOCK_OPENING_FAILURE);

            }
        }
    }