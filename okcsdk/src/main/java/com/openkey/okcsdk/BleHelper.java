package com.openkey.okcsdk;


import com.clj.fastble.BleManager;

public class BleHelper {
    private static BleHelper mBleHelper;

    protected BleHelper() {
    }

    public static BleHelper getInstance() {
        if (mBleHelper == null) {
            mBleHelper = new BleHelper();
        }

        return mBleHelper;
    }

    /**
     * Bluetooth is available
     *
     * @return
     */
    public static boolean isBleOpend() {
        return BleManager.getInstance().isBlueEnable();
    }

    /**
     * Turn on Bluetooth
     *
     * @return
     */
    public static void enableBle() {
        BleManager.getInstance().enableBluetooth();
    }

}
