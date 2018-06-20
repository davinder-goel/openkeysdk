package com.openkey.sdk.api.model;

public class SdkLogRequest {

    public String token;
    public String action;
    public Boolean result;
    public String timestamp;
    public String reason;

    /**
     * @param timestamp
     * @param result
     * @param reason
     * @param token
     * @param action
     */
    public SdkLogRequest(String token, String action, Boolean result,
                         String timestamp, String reason) {
        this.token = token;
        this.action = action;
        this.result = result;
        this.timestamp = timestamp;
        this.reason = reason;
    }
}