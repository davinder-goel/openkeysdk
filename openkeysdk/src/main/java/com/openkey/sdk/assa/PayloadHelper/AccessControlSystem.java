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

public enum AccessControlSystem {
    Visionline(0x01),
    SystemUnknown(-1);

    private final int value;

    private AccessControlSystem(int value) {
        this.value = value;
    }

    public static AccessControlSystem fromInt(int id) {
        AccessControlSystem foundValue = SystemUnknown;
        for (AccessControlSystem type : AccessControlSystem.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
