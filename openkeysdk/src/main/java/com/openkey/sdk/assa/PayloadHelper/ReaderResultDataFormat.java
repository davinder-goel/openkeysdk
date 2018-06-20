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

public enum ReaderResultDataFormat {

    Standard(0x01),
    Unknown(-1);

    private final int value;

    private ReaderResultDataFormat(int value) {
        this.value = value;
    }

    public static ReaderResultDataFormat fromInt(int id) {
        ReaderResultDataFormat foundValue = Unknown;
        for (ReaderResultDataFormat type : ReaderResultDataFormat.values()) {
            if (type.value == id) {
                foundValue = type;
                break;
            }
        }
        return foundValue;
    }
}
