package com.openkey.sdk.api.model;

public class SdkLogRequest {

    public String action;
    public int success;

    public SdkLogRequest(String action, int success) {
        this.action = action;
        this.success = success;
    }
}