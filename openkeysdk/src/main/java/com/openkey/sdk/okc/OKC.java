package com.openkey.sdk.okc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
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

    private String mMacAddress = "";

    //-----------------------------------------------------------------------------------------------------------------|
    public OKC(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
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
                // Api.setPeronalizationComplete(mContext, openKeyCallBack);
                openKeyCallBack.initializationSuccess();
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
        OpenKeyConfig.getIns().setScanType(BaseDeviceConfig.ScanType.ByMac);

//        if (GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava() != null)
//            mMacAddress = GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava();

        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "asdfghe", mContext);
        if (key != null && key.length() > 0)

            OpenKeyConfig.getIns().setMac(key);
        BleHelper.getInstance().scanDevice(OpenKeyConfig.getIns());
        BleHelper.getInstance().sendToSpecificDevice(OpenKeyConfig.getIns());
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        BleHelper.getInstance().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BleHelper.getInstance().pause();
    }

    @Override
    protected void onDestroy() {
        BleHelper.getInstance().destroy(MainActivity.this);
        super.onDestroy();
    }
*/

    //BLEs
    @Override
    public void find(BaseDeviceConfig baseDeviceConfig, BleDevice bleDevice) {
        Toast.makeText(mContext, "find device", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endScan(Map<BaseDeviceConfig, List<BleDevice>> map) {

    }

    @Override
    public void connectFailed(BaseDeviceConfig baseDeviceConfig) {

    }

    @Override
    public void connectSuccess(BaseDeviceConfig baseDeviceConfig) {

    }

    @Override
    public void disconnect(BaseDeviceConfig baseDeviceConfig) {

    }

    @Override
    public void writeSuccess(BaseDeviceConfig baseDeviceConfig) {

    }

    @Override
    public void writeFailed(BaseDeviceConfig baseDeviceConfig) {

    }

    @Override
    public void finishNotify(BaseDeviceConfig baseDeviceConfig, String s) {
        Toast.makeText(mContext, "electricity is : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleStatus(int i) {

    }

    @Override
    public void locationStatus(int i) {

    }

    @Override
    public void busy() {

    }

    @Override
    public void disable() {

    }
}