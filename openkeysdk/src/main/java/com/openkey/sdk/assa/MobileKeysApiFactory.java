/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.assa;

import com.assaabloy.mobilekeys.api.MobileKeys;
import com.assaabloy.mobilekeys.api.ReaderConnectionController;
import com.assaabloy.mobilekeys.api.ble.ScanConfiguration;

interface MobileKeysApiFactory {
    /**
     * Get the a mobile keys api instance
     */
    MobileKeys getMobileKeys();

    /**
     * Get the a reader connection controller instance
     */
    ReaderConnectionController getReaderConnectionController();

    /**
     * Get the scan configuration instance
     */
    ScanConfiguration getScanConfiguration();

}
