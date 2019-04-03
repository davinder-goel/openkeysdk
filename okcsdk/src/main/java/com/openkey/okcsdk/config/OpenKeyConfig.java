package com.openkey.okcsdk.config;

public class OpenKeyConfig {

    private static OpenKeyConfig ins;
    protected Object device;

    public static OpenKeyConfig getIns() {
        if (ins == null) {
            ins = new OpenKeyConfig();
        }
        return ins;
    }

    //get scanned devices
    public Object getDevice() {
        return device;
    }

    //set device after scanning
    public void setDevice(Object device) {
        this.device = device;
    }
}