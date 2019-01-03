//package com.openkey.sdk.okc;
//
//import android.Manifest;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.Toast;
//
//import com.clj.fastble.data.BleDevice;
//import com.openkey.sdk.R;
//
//import java.util.List;
//import java.util.Map;
//
//import cn.openkey.com.openkeysdkdemo.ble.configs.OpenKeyConfig;
//import key.open.cn.blecontrollor.helper.BaseDeviceConfig;
//import key.open.cn.blecontrollor.helper.BleCallBack;
//import key.open.cn.blecontrollor.helper.BleHelper;
//
///**
// * some kind of smart phones need their GPS to be on
// * otherwise ble won't work will
// * in these cases you need to let advise users to open gps and explain it
// */
//public class MainActivity extends AppCompatActivity implements BleCallBack {
//
//    String mMacAddress="A408EA1A52A5";
//
//   // String mMacAddress="f3e6255e798d";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        new PermissionHelper().requestPermission(this, new PermissionHelper.PermissionCallBack() {
//            @Override
//            public void permissionGranted() {
//                if(!BleHelper.isBleOpend()){
//                    BleHelper.enableBle(MainActivity.this, 1 /*request_code*/);
//                }
//
//                BleHelper.getInstance().initAfterPermission(MainActivity.this);
//            }
//
//            @Override
//            public void permissionRefused() {
//
//            }
//
//        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH);
//
//
//        findViewById(R.id.scan).setOnClickListener(view -> {
//            OpenKeyConfig.getIns().setScanType(BaseDeviceConfig.ScanType.ByMac);
//            OpenKeyConfig.getIns().setMac(mMacAddress);
//
//            BleHelper.getInstance().scanDevice(OpenKeyConfig.getIns());
//        });
//
//        findViewById(R.id.open).setOnClickListener(view -> {
//            BleHelper.getInstance().sendToSpecificDevice(OpenKeyConfig.getIns());
//        });
//
//        BleHelper.getInstance().init(MainActivity.this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        BleHelper.getInstance().resume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        BleHelper.getInstance().pause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        BleHelper.getInstance().destroy(MainActivity.this);
//        super.onDestroy();
//    }
//
//    //BLEs
//    @Override
//    public void find(BaseDeviceConfig baseDeviceConfig, BleDevice bleDevice) {
//        Toast.makeText(MainActivity.this, "find device", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void endScan(Map<BaseDeviceConfig, List<BleDevice>> map) {
//
//    }
//
//    @Override
//    public void connectFailed(BaseDeviceConfig baseDeviceConfig) {
//
//    }
//
//    @Override
//    public void connectSuccess(BaseDeviceConfig baseDeviceConfig) {
//
//    }
//
//    @Override
//    public void disconnect(BaseDeviceConfig baseDeviceConfig) {
//
//    }
//
//    @Override
//    public void writeSuccess(BaseDeviceConfig baseDeviceConfig) {
//
//    }
//
//    @Override
//    public void writeFailed(BaseDeviceConfig baseDeviceConfig) {
//
//    }
//
//    @Override
//    public void finishNotify(BaseDeviceConfig baseDeviceConfig, String s) {
//        Toast.makeText(MainActivity.this, "electricity is : "+ s , Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void bleStatus(int i) {
//
//    }
//
//    @Override
//    public void locationStatus(int i) {
//
//    }
//
//    @Override
//    public void busy() {
//
//    }
//
//    @Override
//    public void disable() {
//
//    }
//}
