/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.cryptography;

import java.io.IOException;

/**
 * Thrown when a stream fails HMAC validation.
 */
public class StreamIntegrityException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public StreamIntegrityException() {
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     */
    public StreamIntegrityException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception.
     *
     * @param cause the cause of the exception
     */
    public StreamIntegrityException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception.
     *
     * @param message error message
     * @param cause   the cause of the exception
     */
    public StreamIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

}
