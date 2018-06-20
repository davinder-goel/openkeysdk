/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.cryptography;


/**
 * A format compatible with version 2.
 */
class AES256v2Ciphertext extends AES256Ciphertext {

    static final int EXPECTED_VERSION = 2;

    AES256v2Ciphertext(byte[] encryptionSalt, byte[] hmacSalt, byte[] iv,
                       byte[] ciphertext) {
        super(encryptionSalt, hmacSalt, iv, ciphertext);
    }

    AES256v2Ciphertext(byte[] iv, byte[] ciphertext) {
        super(iv, ciphertext);
    }

    AES256v2Ciphertext(byte[] data) throws InvalidDataException {
        super(data);
    }

    @Override
    int getVersionNumber() {
        return EXPECTED_VERSION;
    }
}
