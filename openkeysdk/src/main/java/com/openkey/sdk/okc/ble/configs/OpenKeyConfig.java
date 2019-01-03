package com.openkey.sdk.okc.ble.configs;

import android.text.TextUtils;

import com.openkey.sdk.okc.ble.ciper.Locker;

import key.open.cn.blecontrollor.helper.BaseDeviceConfig;

public class OpenKeyConfig extends BaseDeviceConfig {

    private static OpenKeyConfig ins;
    private ScanType scanType = ScanType.ByName;
    private String webKey; //后台生成的加密字符串
    private Locker locker;
    private String mac;

    public static OpenKeyConfig getIns() {
        if (ins == null) {
            ins = new OpenKeyConfig();
        }
        return ins;
    }

    @Override
    public String getTag() {
        return "OpenKeyConfig";
    }

    @Override
    public String getUUID_SERVICE() {
        return "C6A80200-E7F4-369C-FD01-E37D865BB2F6";
    }

    @Override
    public String getUUID_CHARACTERISTIC() {
        return "C6A80201-E7F4-369C-FD01-E37D865BB2F6";
    }

    @Override
    public String getUUID_INDICAT() {
        return "C6A80202-E7F4-369C-FD01-E37D865BB2F6";
    }

    @Override
    public ScanType scanBy() {
        return scanType;
    }

    public void setScanType(ScanType scanType) {
        this.scanType = scanType;
    }

    @Override
    public ScanPriority scanPriority() {
        return ScanPriority.Normal1;
    }

    @Override
    public String getName() {
        return "openkey";
    }

    @Override
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
        if (locker == null) {
            locker = new Locker();
        }
        locker.setMac(mac);
    }

    @Override
    public int getSplitNum() {
        return 20;
    }

    @Override
    public byte[] generateData() {
        return locker.openLockData();
    }

    public String getWebKey() {
        return webKey;
    }

    public void setWebKey(String webKey) {
        this.webKey = webKey;
    }

    @Override
    public String analysisBackData(byte[] res) {
        String str = locker.openLockDataBack(res);
        if (!TextUtils.isEmpty(str)) {
            return String.valueOf(Locker.electricityCal(str.substring(4, 6)));
        }
        return "-1";
    }
}
