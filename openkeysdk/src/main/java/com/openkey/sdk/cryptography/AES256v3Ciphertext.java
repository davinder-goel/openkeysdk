/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.cryptography;

/**
 * Version 3 format.
 */
class AES256v3Ciphertext extends AES256Ciphertext {

    static final int EXPECTED_VERSION = 3;

    public AES256v3Ciphertext(byte[] data) throws InvalidDataException {
        super(data);
    }

    public AES256v3Ciphertext(byte[] encryptionSalt, byte[] hmacSalt, byte[] iv,
                              byte[] ciphertext) {
        super(encryptionSalt, hmacSalt, iv, ciphertext);
    }

    public AES256v3Ciphertext(byte[] iv, byte[] ciphertext) {
        super(iv, ciphertext);
    }

    @Override
    int getVersionNumber() {
        return EXPECTED_VERSION;
    }

}
