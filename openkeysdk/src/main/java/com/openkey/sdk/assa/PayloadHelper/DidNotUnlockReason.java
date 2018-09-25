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

public enum DidNotUnlockReason {

    KeyIsOverridden(0x40),
    KeyIsNotValidYet(0x41),
    KeyHasExpired(0x42),
    KeyIsCancelled(0x43),
    KeyGroupIsBlocked(0x44),
    KeyHasNoAccessToThisRoom(0x45),
    KeyHasNoAccesstoThisFacility(0x46),
    KeyNotValidAtthisTimeOfDay(0x47),
    KeyBlockedBecauseOfDeadBolt(0x48),
    KeyBlockedBecauseOfRoomPrivacySetting(0x49),
    KeyBlockedBecauseOfDoorBatteryLevel(0x4a),
    KeyBlockedByAntiPassbackFunction(0x4b),
    KeyRefusedBecauseDoorIsNotLocked(0x4c),
    KeyRefusedDurToPINCode(0x4d),
    KeyBlockedByAccessRules(0x4e),
    NotApplicable(-1);

    private final int value;

    private DidNotUnlockReason(int value) {
        this.value = value;
    }

    public static DidNotUnlockReason fromInt(int id) {
        DidNotUnlockReason foundValue = NotApplicable;
        for (DidNotUnlockReason type : DidNotUnlockReason.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
