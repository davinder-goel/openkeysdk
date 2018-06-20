/*
 * Copyright (c) Helixion Ltd, 2014. All Rights Reserved.
 */

package com.openkey.sdk.kaba;

import com.idconnect.sdk.ble.BLEBatteryStatus;

/**
 * This class is used to parse an IDC Message returned from the BLE PLugin
 */
public class BLEDataHandler {
    BLEBatteryStatus batteryStatus;
    String batteryVoltage;
    String messageString;
    private boolean accessGranted;
    private int errorCode;
    private int flags;

    public BLEDataHandler(byte[] data) {
        parseTlv(data);
    }

    public void parseTlv(byte[] data) {
        int offset = 0;
        int totalLength = data.length;
        messageString = "";

        do {
            messageString = messageString.concat(String.format("%02x ", data[offset]));

            switch (data[offset++]) {
                case 0:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length0 = data[offset++];
                    accessGranted = (data[offset] == 0);
                    errorCode = data[offset];
                    flags = data[offset + 1];
                    messageString = messageString.concat(String.format("%02x", data[offset]));
                    messageString = messageString.concat(String.format("%02x\n", data[offset + 1]));
                    offset += length0;
                    break;
                case 1:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length1 = data[offset++];
                    batteryStatus = (data[offset] == 1) ? BLEBatteryStatus.BLEBATTERY_OK : BLEBatteryStatus.BLEBATTERY_LOW;
                    messageString = messageString.concat(String.format("%02x\n", data[offset]));
                    offset += length1;
                    break;
                case 2:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length2 = data[offset++];
                    int voltage = data[offset] << 8;
                    voltage += data[offset + 1];
                    batteryVoltage = String.valueOf((long) voltage);
                    messageString = messageString.concat(String.format("%02x", data[offset]));
                    messageString = messageString.concat(String.format("%02x\n", data[offset + 1]));
                    offset += length2;
                    break;

                default:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length = data[offset++];
                    int index;

                    for (index = 0; index < length; index++) {
                        messageString = messageString.concat(String.format("%02x", data[offset + index]));
                    }

                    messageString = messageString.concat("\n");

                    offset += length;
                    break;
            }
        }
        while (offset < totalLength);
    }

    /**
     * @return the accessGranted
     */
    public boolean isAccessGranted() {
        return accessGranted;
    }

    /**
     * @param accessGranted the accessGranted to set
     */
    public void setAccessGranted(boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * @return the batteryStatus
     */
    public BLEBatteryStatus getBatteryStatus() {
        return batteryStatus;
    }

    /**
     * @param batteryStatus the batteryStatus to set
     */
    public void setBatteryStatus(BLEBatteryStatus batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    /**
     * @return the batteryVoltage
     */
    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    /**
     * @param batteryVoltage the batteryVoltage to set
     */
    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    /**
     * @return the messageString
     */
    public String getMessageString() {
        return messageString;
    }
}