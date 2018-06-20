/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.cryptography;

/**
 * An exception thrown when an error occurs encrypting or decrypting.
 */
public class CryptorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public CryptorException() {
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     */
    public CryptorException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public CryptorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     * @param cause   the cause of the exception
     */
    public CryptorException(String message, Throwable cause) {
        super(message, cause);
    }
}
