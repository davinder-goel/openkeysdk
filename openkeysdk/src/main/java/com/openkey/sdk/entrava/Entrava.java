package com.openkey.sdk.entrava;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.util.ArrayList;

import kr.co.chahoo.sdk.DoorLockSdk;
import kr.co.chahoo.sdk.IssueCallback;
import kr.co.chahoo.sdk.ResultCode;
import kr.co.chahoo.sdk.ResultReceiver;

//

public class Entrava {
    private Context mContext;
    private DoorLockSdk mDoorLockSdk;
    private PendingIntent mPendingIntent;
    private ArrayList<String> mReferenceIds;
    private OpenKeyCallBack openKeyCallBack;
    private ServiceResult mServiceResultReceiver;
    private boolean isLogActionFired;
    //-----------------------------------------------------------------------------------------------------------------|

    public Entrava(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initialize();
    }
    //-----------------------------------------------------------------------------------------------------------------|
    private void initialize() {
        Intent intent = new Intent(mContext, Entrava.class);
        mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("mPendingIntent", mPendingIntent + " ");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(DoorLockSdk.ACTION_RESULT);

        DoorLockSdk.initialize(mContext);
        mDoorLockSdk = DoorLockSdk.getInstance(mContext);
        mReferenceIds = mDoorLockSdk.issued();
        mServiceResultReceiver = new ServiceResult(openKeyCallBack);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mServiceResultReceiver, mIntentFilter);

        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                0, mContext);
        Log.e("initialize", "mReferenceIds:" + mReferenceIds);
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
    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * issue imgate doorlock key from imgate server
     * by passing access token to imgate server through imgate sdk
     */
    public void issueEntravaKey() {
        mReferenceIds = mDoorLockSdk.issued();

        if (mReferenceIds.size() > 0) {
            Log.e("mReferenceIds", "size:" + mReferenceIds.toString());
//            String bookingId = String.valueOf(GetBooking.getInstance().getBooking().getData().getId());
//            Log.e("mReferenceIds", "cancel:" + mDoorLockSdk.cancel(bookingId));
            for (int i = 0; i < mReferenceIds.size(); i++) {
                mDoorLockSdk.cancel(mReferenceIds.get(i));
            }
            mReferenceIds = mDoorLockSdk.issued();
        }

        final String mImgateIssueKeyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        if (mImgateIssueKeyToken != null && mImgateIssueKeyToken.length() > 0) {
            Log.e("TAG", "mImgateIssueKeyToken : " + mImgateIssueKeyToken);

            int issueRes = mDoorLockSdk.issue(mImgateIssueKeyToken, new IssueCallback() {


                @Override
                public void onResult(int result) {
                    Log.e("TAG", "onResult : " + result);
                    //IF the result is 0 then key issued successfully and update the status on server
                    if (result == 0) {
                        mReferenceIds = mDoorLockSdk.issued();
                        openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
                        Api.setKeyStatus(mContext, Constants.KEY_DELIVERED);
                    } else {
                        openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
                        Api.setKeyStatus(mContext, Constants.PENDING_KEY_SERVER_REQUEST);
                    }
                }
            });

            if (issueRes == 10) {

                if (mReferenceIds.size() > 0) {
                    for (int i = 0; i < mReferenceIds.size(); i++) {
                        mDoorLockSdk.cancel(mReferenceIds.get(i));
                    }
                }
                Utilities.getInstance().clearValueOfKey(mContext, Constants.MOBILE_KEY);
                mDoorLockSdk.stop();
                openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
            }
            Log.e("TAG", "issueRes : " + issueRes);
        } else {
            Log.e("TAG", "mImgateIssueKeyToken :isKeyAvailable " + mImgateIssueKeyToken);

            openKeyCallBack.isKeyAvailable(false, Response.FETCH_KEY_FAILED);
        }
    }
    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * start IMGATE service for open lock when scanning animation on going
     */
    public void startImGateScanningService() {
        isLogActionFired = true;
        mDoorLockSdk.start(mPendingIntent);
    }
    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * if device has a key for Imgate
     *
     * @return
     */
    public boolean haveKey() {
        mReferenceIds = mDoorLockSdk.issued();
        if (mReferenceIds != null && mReferenceIds.size() > 0) {
            return true;
        }

        return false;
    }
    //-----------------------------------------------------------------------------------------------------------------|

    class ServiceResult extends ResultReceiver {

        private OpenKeyCallBack openKeyCallBack;

        public ServiceResult(OpenKeyCallBack openKeyCallBack) {
            this.openKeyCallBack = openKeyCallBack;
        }

        @Override
        protected void onResult(int result, int battery, String issueId) {
            super.onResult(result, battery, issueId);
            mDoorLockSdk.stop();
            Log.e(" Receiver result", ":" + result);
            Log.e(" Receiver issuedId", ":" + issueId);
            if (result == ResultCode.SUCCESS) {
                openKeyCallBack.stopScan(true, Response.LOCK_OPENED_SUCCESSFULLY);
                Log.e("Entrava Lock: ", "LOCK_OPENED_SUCCESSFULLY");
                if (isLogActionFired) {
                    isLogActionFired = false;
                    Api.logSDK(mContext, 1);
                }
            } else {
                Log.e("Entrava Lock: ", "LOCK_OPENING_FAILURE");
                openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
//                Api.logSDK(mContext, 0);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|

}