/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.cryptography;

/**
 * An exception thrown when invalid data is encountered.
 */
class InvalidDataException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public InvalidDataException() {
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     */
    public InvalidDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     * @param cause   the cause of the exception
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
