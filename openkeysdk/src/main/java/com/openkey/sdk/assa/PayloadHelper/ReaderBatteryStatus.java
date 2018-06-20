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

public enum ReaderBatteryStatus {

    Good(0x00),
    Warning(0x01),
    Critical(0x02),
    NotApplicable(-1);

    private final int value;

    private ReaderBatteryStatus(int value) {
        this.value = value;
    }

    public static ReaderBatteryStatus fromInt(int id) {
        ReaderBatteryStatus foundValue = NotApplicable;
        for (ReaderBatteryStatus type : ReaderBatteryStatus.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
