package com.openkey.sdk.okc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.okc.ble.configs.OpenKeyConfig;

import java.util.List;
import java.util.Map;

import key.open.cn.blecontrollor.helper.BaseDeviceConfig;
import key.open.cn.blecontrollor.helper.BleCallBack;
import key.open.cn.blecontrollor.helper.BleHelper;

//

public class OKC implements BleCallBack {
    private Context mContext;
    private OpenKeyCallBack openKeyCallBack;

    //-----------------------------------------------------------------------------------------------------------------|
    public OKC(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        BleHelper.getInstance().init(mContext);

        initialize();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void initialize() {
        BleHelper.getInstance().initAfterPermission(mContext);
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
//                openKeyCallBack.initializationSuccess();
            } else {
                Log.e("Keystatus ", ":" + mobileKeyStatusId);
                openKeyCallBack.initializationSuccess();
            }
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

    /**
     * start IMGATE service for open lock when scanning animation on going
     */
    public void startScanning() {


//        if (GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava() != null)
//            mMacAddress = GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava();

        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
//        if (key != null && key.length() > 0)

        Log.e("token key", key + "");
        OpenKeyConfig.getIns().setScanType(BaseDeviceConfig.ScanType.ByMac);
        OpenKeyConfig.getIns().setMac(key);
        BleHelper.getInstance().setCallBack(this);

        BleHelper.getInstance().scanDevice(OpenKeyConfig.getIns());
    }

    private void sendToSpecificDevice() {
        try {
            BleHelper.getInstance().sendToSpecificDevice(OpenKeyConfig.getIns());
        } catch (Exception e) {
            Log.e("Exception okc", e.getLocalizedMessage() + "");
        }
    }

    //BLEs
    @Override
    public void find(BaseDeviceConfig baseDeviceConfig, BleDevice bleDevice) {
        sendToSpecificDevice();
        Toast.makeText(mContext, "find device", Toast.LENGTH_SHORT).show();
        Log.e("find mac", baseDeviceConfig.getMac()+"   called");
        Log.e("find bleDevice", bleDevice.getMac()+"   called");
    }

    @Override
    public void endScan(Map<BaseDeviceConfig, List<BleDevice>> map) {
        sendToSpecificDevice();
        Log.e("endScan", "called");
    }

    @Override
    public void connectFailed(BaseDeviceConfig baseDeviceConfig) {
        Log.e("connectFailed", "called");

    }

    @Override
    public void connectSuccess(BaseDeviceConfig baseDeviceConfig) {
        Log.e("connectSuccess", "called");

    }

    @Override
    public void disconnect(BaseDeviceConfig baseDeviceConfig) {
        Log.e("disconnect", "called");

    }

    @Override
    public void writeSuccess(BaseDeviceConfig baseDeviceConfig) {
        Log.e("writeSuccess", "called");

    }

    @Override
    public void writeFailed(BaseDeviceConfig baseDeviceConfig) {
        Log.e("writeFailed", "called");

    }

    @Override
    public void finishNotify(BaseDeviceConfig baseDeviceConfig, String s) {
        Log.e("finishNotify", "called");
        Toast.makeText(mContext, "electricity is : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleStatus(int i) {
        Log.e("bleStatus", "called");

    }

    @Override
    public void locationStatus(int i) {
        Log.e("locationStatus", "called");

    }

    @Override
    public void busy() {
        Log.e("busy", "called");

    }

    @Override
    public void disable() {
        Log.e("disable", "called");

    }
}