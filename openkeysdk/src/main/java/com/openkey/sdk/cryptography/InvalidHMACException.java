/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.cryptography;

/**
 * An {@code InvalidHMACException} is thrown when the HMAC value is incorrect,
 * indicating that the data is corrupted.
 */
public class InvalidHMACException extends CryptorException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public InvalidHMACException() {
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     */
    public InvalidHMACException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public InvalidHMACException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     * @param cause   the cause of the exception
     */
    public InvalidHMACException(String message, Throwable cause) {
        super(message, cause);
    }

}
