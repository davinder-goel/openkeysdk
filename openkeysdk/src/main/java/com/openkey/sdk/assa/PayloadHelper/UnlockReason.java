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

public enum UnlockReason {

    OpenedByGuestKey(0x01),
    OpenedByStaffKey(0x10),
    OpenedbySpecialKey(0x20),
    NotApplicable(1);

    private final int value;

    private UnlockReason(int value) {
        this.value = value;
    }

    public static UnlockReason fromInt(int id) {
        UnlockReason foundValue = NotApplicable;
        for (UnlockReason type : UnlockReason.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
