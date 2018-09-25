package com.openkey.sdk.miwa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.miwa.alv2core.ble.Alv2Service;
import com.miwa.alv2core.ble.Alv2ServiceStart;
import com.miwa.alv2core.ble.Utility;
import com.miwa.alv2core.data.Alv2Key;
import com.miwa.alv2core.data.Alv2KeyDb;
import com.miwa.alv2core.data.Alv2NotifyType;
import com.miwa.alv2core.data.Alv2ResultCode;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.singleton.GetBooking;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Miwa {

    /*
     * Miwa SDK
     * */
    private static final String SERIAL = "A8FEB2C4D3DB9165B591605E9E0F7A2C6B823C6E07350CC2C2C41DEEB8DAC02ADD3BCA372A523668FF290A357419FCB430A72049940A4560";
    //Recommended RF threshold
    private static final int ALV2_RSSI = -70;
    private static final int RDFL_RSSI = -65;
    private static final int TIMEOUT = 10000;
    private final String TAG = getClass().getSimpleName();
    private Alv2ServiceStart alv2;
    private Alv2Key selectedKey;
    private OpenKeyCallBack openKeyCallBack;
    private Context mContext;
    private boolean isDoorOpenedLogged;
    private BroadcastReceiver receiverMiwa = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleResult(intent);
        }
    };

    public Miwa(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        setUpMiwa();
    }

    /**
     * setup device for miwa lock
     */
    private void setUpMiwa() {
        try {
            Log.e("loadKeyData", "loadKeyData" + isDoorOpenedLogged);
            loadKeyData();

            alv2 = new Alv2ServiceStart(mContext, SERIAL);
            IntentFilter filter = new IntentFilter(Alv2Service.ACTION_NOTIFY);
            mContext.registerReceiver(receiverMiwa, filter);

            int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                    0, mContext);
            if (haveKey() && mobileKeyStatusId == 3) {
                Log.e("haveKey()", ":" + haveKey());
                openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
                return;
            }
            Api.setPeronalizationComplete(mContext,openKeyCallBack);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * loading key data from SQLite DB.
     */
    private void loadKeyData() {
        Alv2KeyDb db = new Alv2KeyDb(mContext);
        try {
            SQLiteDatabase sql = db.getReadableDatabase();
            List<Alv2Key> list = Alv2Key.list(sql);
            if (list != null && list.size() > 0) {
                selectedKey = list.get(0);
                Log.e("isDoorOpenedLogged", "loadKeyData" + isDoorOpenedLogged);
                isDoorOpenedLogged = true;
            }
        } finally {
            db.close();
        }
    }

    /**
     * if devive has a key for salto
     *
     */
    private boolean deviceHasMiwaKey() {
        final String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY,
                "",mContext);
        return (key.length() > 0 && selectedKey != null);
    }

    public void addKey() {

        String keys = Utilities.getInstance().getValue(Constants.MOBILE_KEY,
                "", mContext);

        //if the key is null then it will not proceed
        if (!(keys != null && keys.length() > 0))
            return;

        delAllKey();

//        String miwaKey = Utilities.getInstance().decodeMiwaKey(keys);
        String miwaKey = keys;

        Log.e("Key", ":" + keys);
        Log.e("Key", "length:" + keys.length());
        Log.e("Key", "basic:" + miwaKey.substring(0, 56));
        Log.e("Key", "ext1:" + miwaKey.substring(96, 192));
        Log.e("Key", "ext2:" + miwaKey.substring(192, 288));
        byte[] basic = Utility.hexToByte(miwaKey.substring(0, 56));
        byte[] ext1 = Utility.hexToByte(miwaKey.substring(96, 192));
        byte[] ext2 = Utility.hexToByte(miwaKey.substring(192, 288));

        Calendar cal = Calendar.getInstance();
        cal.clear();

        Alv2KeyDb db = new Alv2KeyDb(mContext);
        try {
            SQLiteDatabase sql = db.getWritableDatabase();
            Alv2Key key = new Alv2Key(basic, ext1, ext2);
            String roomNumber = GetBooking.getInstance().getBooking().getData().getHotelRoom().getTitle();
            key.setKeyName(roomNumber);
            key.setRoom(Integer.parseInt(roomNumber));
           /*
            cal.set(2016,0,1,0,0);
            key.setCi(cal.getTime());

            cal.set(2026,0,1,0,0);
            key.setCo(cal.getTime());*/

            long id = key.insert(sql);
            Log.d(TAG, "id=" + id);
        } finally {
            db.close();
        }
        Log.e("loadKeyData", "Alv2KeyDb" + isDoorOpenedLogged);
        loadKeyData();
    }

    /**
     * check if device have keys
     *
     * @return true/false
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        return !TextUtils.isEmpty(key);
    }



    public void startScanning()
    {
        if (deviceHasMiwaKey()) {
            Log.e("startScanning", "deviceHasMiwaKey" + isDoorOpenedLogged);
            isDoorOpenedLogged = true;
            Log.e("startScanning", "deviceHasMiwaKey");
            alv2.startAuth(selectedKey.get_id(), ALV2_RSSI, RDFL_RSSI, TIMEOUT);
        } else {
            Log.e("startScanning", "failed");
        }
    }

    /**
     * Handle Alv2Service result.
     *
     * @param intent sending or receiving result.
     */
    private void handleResult(Intent intent) {
        int type = intent.getIntExtra(Alv2Service.EXT_NOTIFY_TYPE, -1);
        Log.d(TAG, String.format(Locale.US, "ResultType=%d", type));

        int code = intent.getIntExtra(Alv2Service.EXT_RESULT, -1);
        Log.d(TAG, String.format(Locale.US, "ResultCode=%02x", code));

        switch (type) {
            case Alv2NotifyType.RECV_RES: {
                if (code == Alv2ResultCode.SUCCESS) {
                    long id = intent.getLongExtra(Alv2Service.EXT_KEY_ID, 0);
                    Log.d(TAG, String.format(Locale.US, "SUCCESS: id=%d", id));
                    Log.e("loadKeyData", "Alv2KeyDb RECV_RES" + isDoorOpenedLogged);
                    loadKeyData();
                }
                //showResult(code);
            }
            break;
            case Alv2NotifyType.AUTH_RES: {
                if (code == Alv2ResultCode.SUCCESS) {
                    openKeyCallBack.stopScan(true, com.openkey.sdk.Utilities.Response.LOCK_OPENED_SUCCESSFULLY);
                    if (isDoorOpenedLogged) {
                        Log.e("open", "SUCCESS");
                        isDoorOpenedLogged = false;
                        Api.logSDK(mContext, 1);
                    }
                } else {
                    openKeyCallBack.stopScan(false, com.openkey.sdk.Utilities.Response.LOCK_OPENING_FAILURE);
//                    Api.logSDK(mContext, 0);
                }
            }
            break;
        }
    }

    /**
     * delete all key data from DB.
     */
    private void delAllKey() {
        Alv2KeyDb db = new Alv2KeyDb(mContext);
        try {
            Alv2Key.deleteAll(db.getWritableDatabase());
        } finally {
            db.close();
        }
        Log.e("loadKeyData", "delAllKey" + isDoorOpenedLogged);
        loadKeyData();
    }

}
