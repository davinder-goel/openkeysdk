/*
 * Copyright (c) dormakaba Holding AG 2018. All Rights Reserved.
 */

package com.openkey.sdk.kaba.util;

import android.content.Context;


/**
 * This class is used to parse an IDC Message returned from the BLE PLugin
 *
 * @author
 */
public class BLEDataHandler {
    private boolean accessGranted;
    private int errorCode;
    private int flags;
    private int systemType;
    private int lockModel;
    private String saflokRecordAddress;
    private String saflokPropertyNumber;
    private String saflokLockName;
    private int ilcoLockType;
    private String ilcoDoorIdNmbre;
    private String ilcoLockId;
    private String batteryStatus;
    private String batteryVoltage;
    private String messageString;
    private String interpretedMessageString;
    private SaflokLockError saflokLockError;

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
                    batteryStatus = (data[offset] == 1) ? "BATTERY_OK" : "BATTERY_LOW";
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

                case 3:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length3 = data[offset++];
                    systemType = data[offset];
                    lockModel = data[offset + 1];
                    messageString = messageString.concat(String.format("%02x", data[offset]));
                    messageString = messageString.concat(String.format("%02x\n", data[offset + 1]));
                    offset += length3;
                    break;

                case 4:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length4 = data[offset++];

                    if (systemType == 0) {
                        saflokRecordAddress = "Lock Record Address: 0x";
                        saflokRecordAddress = saflokRecordAddress.concat(String.format("%02X", data[offset]));
                        saflokRecordAddress = saflokRecordAddress.concat(String.format("%02X", data[offset + 1]));
                        saflokRecordAddress += "\n";

                        saflokPropertyNumber = "Property Number: 0x";
                        saflokPropertyNumber = saflokPropertyNumber.concat(String.format("%02X", data[offset + 2]));
                        saflokPropertyNumber = saflokPropertyNumber.concat(String.format("%02X", data[offset + 3]));
                        saflokPropertyNumber += "\n";

                        saflokLockName = "Lock Name: ";
                        saflokLockName += String.format("%c%c%c%c%c\n", (char) data[offset + 4], (char) data[offset + 5], (char) data[offset + 6], (char) data[offset + 7], (char) data[offset + 8]);

                    } else {
                        ilcoLockType = data[offset];

                        ilcoDoorIdNmbre = "Door Id Nmbre: 0x";
                        ilcoDoorIdNmbre = ilcoDoorIdNmbre.concat(String.format("%02X", data[offset + 1]));
                        ilcoDoorIdNmbre = ilcoDoorIdNmbre.concat(String.format("%02X", data[offset + 2]));
                        ilcoDoorIdNmbre = ilcoDoorIdNmbre.concat(String.format("%02X", data[offset + 3]));
                        ilcoDoorIdNmbre += "\n";

                        ilcoLockId = "Lock Id : 0x";
                        ilcoLockId = ilcoLockId.concat(String.format("%02X", data[offset + 4]));
                        ilcoLockId = ilcoLockId.concat(String.format("%02X", data[offset + 5]));
                        ilcoLockId = ilcoLockId.concat(String.format("%02X", data[offset + 6]));
                        ilcoLockId = ilcoLockId.concat(String.format("%02X", data[offset + 7]));
                        ilcoLockId = ilcoLockId.concat(String.format("%02X", data[offset + 8]));
                        ilcoDoorIdNmbre += "\n";
                    }

                    messageString = messageString.concat(String.format("%02x", data[offset]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 1]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 2]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 3]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 4]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 5]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 6]));
                    messageString = messageString.concat(String.format("%02x", data[offset + 7]));
                    messageString = messageString.concat(String.format("%02x\n", data[offset + 8]));
                    offset += length4;
                    break;

                default:
                    messageString = messageString.concat(String.format("%02x ", data[offset]));
                    int length = data[offset++];
                    int index = 0;

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
    public String getBatteryStatus() {
        return batteryStatus;
    }

    /**
     * @param batteryStatus the batteryStatus to set
     */
    public void setBatteryStatus(String batteryStatus) {
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
     * @return the lock identification info
     */
    public String getLockIdentificationInfo() {
        if (systemType == 0) {
            // Saflok
            return saflokRecordAddress + saflokPropertyNumber + saflokLockName;
        } else if (systemType == 1) {
            // Ilco
            return "Lock Type: " + ilcoLockType + "\n" + ilcoDoorIdNmbre + ilcoLockId;
        } else {
            // Unknown System
            return "Unknown Lock Identification info\n";
        }
    }

    /**
     * @return the lock model
     * TODO Change this to enums.
     */
    public String getLockModel() {
        if (systemType == 0) {
            // Saflok
            if ((lockModel == 0) || (lockModel == 1) || (lockModel == 4) || (lockModel == 7) || (lockModel == 8) || (lockModel == 9) || (lockModel == 12) || (lockModel == 15)) {
                return "Lock Model MT2\n";
            }

            if ((lockModel == 2) || (lockModel == 10) || (lockModel == 11)) {
                return "Lock Model MCC\n";
            }

            if ((lockModel == 3) || (lockModel == 5) || (lockModel == 6) || (lockModel == 13) || (lockModel == 14)) {
                return "Lock Model RCU/ECU\n";
            }

            if (((lockModel >= 16) && (lockModel <= 31))) {
                return "Lock Model RT\n";
            }

            if ((lockModel == 32) || (lockModel == 33) || (lockModel == 36) || ((lockModel >= 38) && (lockModel <= 47))) {
                return "Lock Model MT4\n";
            }

            if ((lockModel == 34)) {
                return "MCC4\n";
            }

            if ((lockModel == 35) || (lockModel == 37)) {
                return "RCU4/ECU4\n";
            }

            if (((lockModel >= 48) && (lockModel <= 63))) {
                return "Confidant\n";
            }

            if (((lockModel >= 64) && (lockModel <= 78))) {
                return "Secure Wall Reader\n";
            }

            if ((lockModel == 79)) {
                return "Keyscan Reader\n";
            }

            if ((lockModel == 96) || (lockModel == 97)) {
                return "Bambino\n";
            }

            return "Unknown Lock Model\n";
        } else if (systemType == 1) {
            // Ilco
            if (lockModel == 0) {
                return "Unknown Lock Model\n";
            }

            if (lockModel == 1) {
                return "Lock Model 760\n";
            }

            if (lockModel == 2) {
                return "Lock Model 710-II/730/720-II/71M\n";
            }

            if (lockModel == 3) {
                return "Lock Model RAC\n";
            }

            if (lockModel == 4) {
                return "Lock Model SOL710\n";
            }

            if (lockModel == 5) {
                return "Lock Model 700-II\n";
            }

            if (lockModel == 6) {
                return "Lock Model 790\n";
            }

            if (lockModel == 7) {
                return "Lock Model 790M\n";
            }

            if (lockModel == 8) {
                return "Lock Model Confidant\n";
            }

            if (lockModel == 9) {
                return "Lock Model Q90\n";
            }

            if (lockModel == 10) {
                return "Lock Model 790 Plus\n";
            }

            if (lockModel == 11) {
                return "Lock Model Confidant Plus\n";
            }

            return "Unknown Lock Model\n";
        } else {
            // Unknown System
            return "Unknown Lock Model\n";
        }
    }

    /**
     * @return the messageString
     */
    public String getMessageString() {
        return messageString;
    }

    /**
     * @return the interpreted messageString
     */
    public String getInterpretedMessageString(Context context) {
        interpretedMessageString = "";

        if (accessGranted) {
            interpretedMessageString += "Access is Granted\n";
        } else {
            interpretedMessageString = interpretedMessageString.concat(String.format("Access is not granted\nError code: %d\n", errorCode));

            // Only Saflok locks have detailed error descriptions at this point) add Ilco if exists
            saflokLockError = SaflokLockError.fromCode(errorCode);
            interpretedMessageString += "Error description: " + context.getString(saflokLockError.getDescription());
        }

        interpretedMessageString += ((flags & 0x01) == 1) ? "First Access\n" : "Not First Access\n";
        interpretedMessageString += (((flags & 0x02) >> 1) == 1) ? "Motor Status Locked\n" : "Motor Status Unlocked\n";
        interpretedMessageString += (((flags & 0x04) >> 2) == 1) ? "Door Ajar Switch On\n" : "Door Ajar Switch Off\n";
        interpretedMessageString += (((flags & 0x08) >> 3) == 1) ? "Deadbolt Switch On\n" : "Deadbolt Switch Off\n";
        interpretedMessageString += batteryStatus + "\n";
        interpretedMessageString += "Battery Voltage: " + batteryVoltage + "mV\n";
        interpretedMessageString += (systemType == 1) ? "Ilco lock system\n" : "Saflok lock system\n";
        interpretedMessageString += getLockModel();
        interpretedMessageString += getLockIdentificationInfo();

        return interpretedMessageString;
    }

}
