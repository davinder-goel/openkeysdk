package com.openkey.sdk.kaba.util;


import com.openkey.sdk.R;

public enum SaflokLockError {

    // Saflok Errors
    Error113(113, R.string.error_113),
    Error117(117, R.string.error_117),

    Any_UnknownError(255, R.string.error_255);

    private final int code;
    private final int description;

    SaflokLockError(int code, int description) {
        this.code = code;
        this.description = description;
    }

    public static SaflokLockError fromCode(int code) {
        for (SaflokLockError error : values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return Any_UnknownError;
    }

    public int getCode() {
        return code;
    }

    public int getDescription() {
        return description;
    }
}