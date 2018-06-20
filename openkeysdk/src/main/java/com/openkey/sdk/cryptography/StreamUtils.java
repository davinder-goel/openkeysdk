/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */
package com.openkey.sdk.cryptography;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Methods for operating on streams.
 */
class StreamUtils {

    /**
     * Attempts to fill the buffer by reading as many bytes as available. The
     * returned number indicates how many bytes were read, which may be smaller
     * than the buffer size if EOF was reached.
     *
     * @param in     input stream
     * @param buffer buffer to fill
     * @return the number of bytes read
     * @throws IOException
     */
    static int readAllBytes(InputStream in, byte[] buffer) throws IOException {
        int index = 0;

        while (index < buffer.length) {
            int read = in.read(buffer, index, buffer.length - index);
            if (read == -1) {
                return index;
            }
            index += read;
        }

        return index;
    }

    /**
     * Fills the buffer from the input stream. Throws exception if EOF occurs
     * before buffer is filled.
     *
     * @param in     the input stream
     * @param buffer the buffer to fill
     * @throws IOException
     */
    static void readAllBytesOrFail(InputStream in, byte[] buffer)
            throws IOException {
        int read = readAllBytes(in, buffer);
        if (read != buffer.length) {
            throw new EOFException(String.format(
                    "Expected %d bytes but read %d bytes.", buffer.length, read));
        }
    }

}
