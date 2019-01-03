package com.openkey.sdk.api.model;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This will hold the secret key provided via third party developer for authenticating the
 *         SDK on OpenKyeServer.
 */

public class Token {
    private String token;

    public Token(String secretKey) {
        this.token = secretKey;
    }
}
