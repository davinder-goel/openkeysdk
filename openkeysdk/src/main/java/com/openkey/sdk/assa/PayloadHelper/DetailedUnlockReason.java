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

public enum DetailedUnlockReason {

    // 0x01 - Door opened by guest key
    OpeningWithGuestCard(0x0043),
    OpeningWithGuestSuiteCard(0x0044),
    OpeningByGuestInCommonRoom(0x0045),
    OpeningWithFutureArrivalCard(0x0046),
    OpeningWithOneTimeCard(0x0048),
    OpeningByGuestInEntranceDoor(0x0049),

    // 0x10 - Door opened by staff key
    OpeningWithStaffCard(0x0040),

    // 0x20 - Door opened by special key
    StandOpenSet(0x0041),

    NotApplicable(-1);

    private final int value;

    private DetailedUnlockReason(int value) {
        this.value = value;
    }

    public static DetailedUnlockReason fromInt(int id) {
        DetailedUnlockReason foundValue = NotApplicable;
        for (DetailedUnlockReason type : DetailedUnlockReason.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
