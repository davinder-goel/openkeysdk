/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.assa.PayloadHelper;

/**
 * Created by Onboarding on 2016-09-30.
 * Based on BLE End Node Protocol V2.1 Draft 4
 */

public class ByteArrayHelper {
    public static Boolean containsData(byte[] payload) {
        if (payload == null) {
            return false;
        }
        return payload.length > 0;
    }

    public static Boolean didUnlock(byte[] payload) {
        if (!ByteArrayHelper.containsData(payload)) {
            return false;
        }

        String result = ByteArrayHelper.partialResult(payload, 1, 1);
        int unlockStatus = Integer.parseInt(result);
        if (unlockStatus > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static UnlockReason UnlockReason(byte[] payload) {

        UnlockReason result = UnlockReason.NotApplicable;
        String resultString = ByteArrayHelper.partialResult(payload, 2, 1);
        if (resultString.length() > 0) {

            int resultAsValue = Integer.parseInt(resultString, 16);
            result = UnlockReason.fromInt(resultAsValue);
        }
        return result;
    }

    public static DidNotUnlockReason DidNotUnlockReason(byte[] payload) {

        DidNotUnlockReason result = DidNotUnlockReason.NotApplicable;
        String resultString = ByteArrayHelper.partialResult(payload, 2, 1);
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            result = DidNotUnlockReason.fromInt(resultAsValue);
        }
        return result;
    }

    public static DetailedUnlockReason DetailedUnlockReason(byte[] payload) {

        DetailedUnlockReason result = DetailedUnlockReason.NotApplicable;
        String resultString = ByteArrayHelper.partialResult(payload, 9, 2);
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            result = DetailedUnlockReason.fromInt(resultAsValue);
        }
        return result;
    }

    public static DetailedDidNotUnlockReason DetailedDidNotUnlockReason(byte[] payload) {

        DetailedDidNotUnlockReason result = DetailedDidNotUnlockReason.NotApplicable;
        String resultString = ByteArrayHelper.partialResult(payload, 9, 2);
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            result = DetailedDidNotUnlockReason.fromInt(resultAsValue);
        }
        return result;
    }

    public static String DoorId(byte[] payload) {

        String resultString = ByteArrayHelper.partialResult(payload, 3, 4);
        String doorIdResult = "";
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            doorIdResult = String.format("%d", resultAsValue);
        }
        return doorIdResult;
    }

    public static ReaderBatteryStatus ReaderBatteryStatus(byte[] payload) {
        ReaderBatteryStatus result = ReaderBatteryStatus.NotApplicable;
        String resultString = ByteArrayHelper.partialResult(payload, 7, 1);
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            result = ReaderBatteryStatus.fromInt(resultAsValue);
        }
        return result;
    }

    public static AccessControlSystem AccessControlSystem(byte[] payload) {
        AccessControlSystem result = AccessControlSystem.SystemUnknown;
        String resultString = ByteArrayHelper.partialResult(payload, 8, 1);
        if (resultString.length() > 0) {
            int resultAsValue = Integer.parseInt(resultString, 16);
            result = AccessControlSystem.fromInt(resultAsValue);
        }
        return result;
    }

    public static String FirmwareVersionLCU(byte[] payload) {
        String result = ByteArrayHelper.partialResult(payload, 11, 4);
        String resMajor = result.substring(0, 2);
        String resMinor = result.substring(2, 4);
        String resRevision = result.substring(4, 6);
        String resBuild = result.substring(6, 8);

        String stringResult = String.format("%s.%s.%s (%s)", resMajor, resMinor, resRevision, resBuild);
        return stringResult;
    }

    public static String FirmwareVersionBLE(byte[] payload) {
        String result = ByteArrayHelper.partialResult(payload, 15, 2);
        String resMajor = result.substring(0, 2);
        String resMinor = result.substring(2, 4);
        String stringResult = String.format("%s.%s", resMajor, resMinor);
        return stringResult;
    }

    public static String RFU(byte[] payload) {
        return "";
    }

    private static String partialResult(byte[] payload, int location, int length) {
        String dataAsString = ByteArrayHelper.openingStatusPayloadAsString(payload);

        int formatStart = 0;
        int formatLength = 2;

        int useLocation = location * 2;
        int useLength = length * 2;

        ReaderResultDataFormat dataFormat = ReaderResultDataFormat.Unknown;
        String result = "";

        if (dataAsString.length() >= useLocation + useLength) {
            String dataFormatString = dataAsString.substring(formatStart, formatStart + formatLength);

            if (dataFormatString.length() > 0) {
                int resultAsValue = Integer.parseInt(dataFormatString);
                dataFormat = ReaderResultDataFormat.fromInt(resultAsValue);
            }
            if (dataFormat == ReaderResultDataFormat.Standard) {
                result = dataAsString.substring(useLocation, useLocation + useLength);
            }
        }
        return result;
    }

    private static String openingStatusPayloadAsString(byte[] payload) {
        String result = "";
        for (int i = 0; i < payload.length; i++) {
            String thisThing = String.format("%02x", (int) payload[i]);
            result = result.concat(thisThing);
        }
        return result;
    }
}
