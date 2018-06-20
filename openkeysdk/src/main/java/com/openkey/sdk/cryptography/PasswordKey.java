/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.cryptography;

import javax.crypto.SecretKey;

/**
 * <p>Stores a secret key alongside the salt that was used during the key
 * derivation. Storing and reusing a {@code PasswordKey} object can improve
 * performance.</p>
 * <p>
 * <p>Create instances of this class using {@link JNCryptor#getPasswordKey(char[])}.</p>
 *
 * @since 1.2.0
 */
public class PasswordKey {
    private final SecretKey key;
    private final byte[] salt;

    PasswordKey(SecretKey key, byte[] salt) {
        this.key = key;
        this.salt = salt;
    }


    SecretKey getKey() {
        return key;
    }

    byte[] getSalt() {
        return salt;
    }
}
