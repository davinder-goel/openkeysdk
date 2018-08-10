package com.openkey.sdk.entrava;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

import java.util.ArrayList;
//
import kr.co.chahoo.sdk.DoorLockSdk;
import kr.co.chahoo.sdk.IssueCallback;

public class Entrava {

    private Context mContext;
    private DoorLockSdk mDoorLockSdk;
    private PendingIntent mPendingIntent;
    private ArrayList<String> mReferenceIds;
    private OpenKeyCallBack openKeyCallBack;
    private final String TAG = getClass().getSimpleName();

    public Entrava(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initialize();
    }

    private void initialize()
    {
        Intent intent = new Intent(mContext, Entrava.class);
        mPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("mPendingIntent", mPendingIntent + " ");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(DoorLockSdk.ACTION_RESULT);

        DoorLockSdk.initialize(mContext);
        mDoorLockSdk = DoorLockSdk.getInstance(mContext);
        mReferenceIds = mDoorLockSdk.issued();
        ServiceResult mServiceResultReceiver = new ServiceResult(openKeyCallBack,mDoorLockSdk);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mServiceResultReceiver, mIntentFilter);

        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                0, mContext);
        if (haveKey() && mobileKeyStatusId == 3) {
            Log.e("haveKey()", ":" + haveKey());
            openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
            return;
        }
        Api.setPeronalizationComplete(mContext,openKeyCallBack);
    }

    /**
     * issue imgate doorlock key from imgate server
     * by passing access token to imgate server through imgate sdk
     */
    public void issueEntravaKey() {
        if (mReferenceIds.size() > 0) {
            mDoorLockSdk.cancel("String.valueOf(booking.getBookingId())");
            Log.e("Cancel Key", "Called");
        }

        String mImgateIssueKeyToken = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        if (mImgateIssueKeyToken != null && mImgateIssueKeyToken.length() > 0) {
            mDoorLockSdk.issue(mImgateIssueKeyToken, new IssueCallback() {
                @Override
                public void onResult(int result) {
                    Log.e("TAG", "onResult : " + result);
                    //IF the result is 0 then key issued successfully and update the status on server
                    if (result == 0) {
                        Api.setKeyStatus(mContext, Constants.KEY_DELIVERED);
                    } else {
                        Api.setKeyStatus(mContext, Constants.PENDING_KEY_SERVER_REQUEST);
                    }
                }
            });
        }
    }

    /**
     * start IMGATE service for open lock when scanning animation on going
     */
    public void startImGateScanningService() {
        mDoorLockSdk.start(mPendingIntent);

    }

    /**
     * if device has a key for Imgate
     *
     * @return
     */
    public boolean haveKey() {
        mReferenceIds = mDoorLockSdk.issued();
        if (mReferenceIds.size() > 0) {
            return true;
        }
        return false;
    }
}
