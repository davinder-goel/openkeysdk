package com.openkey.sdk.kaba.model;

/**
 * Copyright 2015 OpenKey. All Rights Reserved
 *
 * @author OpenKey Inc.
 */
public class PrepareDirectWalletRegistrationRequest {

    public String publicSEId;
    public String walletId;

    public PrepareDirectWalletRegistrationRequest(String publicSEId, String walletId) {
        this.publicSEId = publicSEId;
        this.walletId = walletId;
    }
}