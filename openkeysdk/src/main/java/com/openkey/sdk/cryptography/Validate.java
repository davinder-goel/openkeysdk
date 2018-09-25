/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.cryptography;

/**
 * A basic validation class very similar to Apache Commons Lang.
 */
class Validate {

    private Validate() {
    }

    static void isTrue(boolean test, String msg, Object... args) {
        if (!test) {
            throw new IllegalArgumentException(String.format(msg, args));
        }
    }

    static void notNull(Object object, String msg, Object... args) {
        if (object == null) {
            throw new NullPointerException(String.format(msg, args));
        }
    }

    /**
     * Tests object is not null and is of correct length.
     *
     * @param object
     * @param length
     * @param name
     */
    static void isCorrectLength(byte[] object, int length, String name) {
        Validate.notNull(object, "%s cannot be null.", name);

        Validate.isTrue(object.length == length,
                "%s should be %d bytes, found %d bytes.", name, length, object.length);
    }

}
