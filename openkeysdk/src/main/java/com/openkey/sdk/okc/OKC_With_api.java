package com.openkey.sdk.okc;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.okc.ble.configs.OpenKeyConfig;
import com.openkey.sdk.singleton.GetBooking;

import java.util.List;
import java.util.Map;

import key.open.cn.blecontrollor.helper.BaseDeviceConfig;
import key.open.cn.blecontrollor.helper.BleCallBack;
import key.open.cn.blecontrollor.helper.BleHelper;

//

public class OKC_With_api implements BleCallBack {
    private Context mContext;
    private OpenKeyCallBack openKeyCallBack;

    private String mMacAddress = "";

    //-----------------------------------------------------------------------------------------------------------------|
    public OKC_With_api(Context mContext, OpenKeyCallBack OpenKeyCallBack) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        initialize();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void initialize() {

        FragmentActivity fragmentActivity = (FragmentActivity) mContext;
        new PermissionHelper().requestPermission(fragmentActivity, new PermissionHelper.PermissionCallBack() {
            @Override
            public void permissionGranted() {
                if (!BleHelper.isBleOpend()) {
                    BleHelper.enableBle(fragmentActivity, 1 /*request_code*/);
                }

                BleHelper.getInstance().initAfterPermission(mContext);
            }

            @Override
            public void permissionRefused() {

            }

        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH);
        openKeyCallBack.isKeyAvailable(true, Response.FETCH_KEY_SUCCESS);
    }

    /**
     * if device has a key for Imgate
     *
     * @return
     */
    public boolean haveKey() {
        return true;
    }


    /**
     * start IMGATE service for open lock when scanning animation on going
     */
    public void startScanning() {
        OpenKeyConfig.getIns().setScanType(BaseDeviceConfig.ScanType.ByMac);

        if (GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava() != null)
            mMacAddress = GetBooking.getInstance().getBooking().getData().getHotelRoom().getEntrava();

        OpenKeyConfig.getIns().setMac(mMacAddress);
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