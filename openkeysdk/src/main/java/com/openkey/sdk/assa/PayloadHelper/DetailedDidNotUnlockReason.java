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

public enum DetailedDidNotUnlockReason {

    // 0x40 - Key is overridden
    GuestCardOverridden(0x0010),
    GuestCardOverridden_v2(0x0011),

    // 0x41 - Key is not valid yet
    CardNotValidYet(0x0012),

    // 0x42 - Key has expired
    CardHasExpired(0x0142),

    // 0x43 - Key is cancelled
    CardCancelled(0x0017),

    // 0x44 - Key group is blocked
    CardUsergroupBlocked(0x0018),

    // 0x45 - Key has no access to this room
    NoAccessToThisRoom(0x0140),

    // 0x46 - Key has no access to this hotel
    WrongSystemNumberOnCard(0x0105),

    // 0x47 - Key not valid at this time of day
    NotValidAtThisTime(0x0016),
    CardOnlyValidDuringOpeningTime(0x0020),

    // 0x48 - Key blocked because of deadbolt
    DoorUnitDeadBolted(0x0021),
    DoorUnitDeadBoltedGuest(0x0022),
    DoorUnitDeadBoltedFamily(0x0023),

    // 0x49 - Key blocked because of room privacy settings
    NotValidDueToPrivacyStatus(0x0026),
    NotValidDueToPrivacyStatusSilt(0x0027),
    NotValidDutToDoNotDisturbStatus(0x002b),

    // 0x4a - Key blocked because of door battery level
    AccessDeniedDueToBatteryAlarm(0x0035),

    // 0x4b - Key blocked by anti passback function
    NotValidDueToAntiPassback(0x0015),

    // 0x4c - Key refused because door is not locked
    NotValidDueToDoorNotClosed(0x0024),
    NotValidDueToOpenStatus(0x0025),

    // 0x4d - Key refused due to PIN code
    WrongPIN(0x0013),
    WrongPIN_v2(0x0019),

    // 0x4e - Key blocked by access rules
    CommandNotValidForThisLock(0x001a),
    CounterValueTooLow(0x0014),
    NotValidDueToPassageRevoked(0x0028),

    NotApplicable(-1);

    private final int value;

    DetailedDidNotUnlockReason(int value) {
        this.value = value;
    }

    public static DetailedDidNotUnlockReason fromInt(int id) {
        DetailedDidNotUnlockReason foundValue = NotApplicable;
        for (DetailedDidNotUnlockReason type : DetailedDidNotUnlockReason.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
