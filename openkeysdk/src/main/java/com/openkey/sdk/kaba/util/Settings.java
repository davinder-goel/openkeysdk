// -----------------------------------------------------------------------------
// Copyright© 2017 LEGIC® Identsystems AG, CH-8623 Wetzikon
// Confidential. All rights reserved!
// -----------------------------------------------------------------------------
package com.openkey.sdk.kaba.util;

import com.legic.mobile.sdk.api.types.LcConfirmationMethod;

public class Settings {
    //Configuration parameters
    // LEGIC Connect environment
    public static final String serverUrl = "https://api.legicconnect.com/public";

    // the LC Mobile App ID is the ID generated in LEGIC Connect for your mobile App
    public static final long mobileAppId = 10008;

    // the mobile app tech user and the according password are generated in LEGIC Connect when you add a new wallet.
    // the password is only displayed once, so make sure to save this password for later use in your mobile app.


    // the LC Confirmation methos is for registration method, Possible values are SMS, EMAIL, NONE (custom device ID)
    public static final LcConfirmationMethod lcConfirmationMethod = LcConfirmationMethod.NONE;

    //Used for local storage of data
    public static final String MY_PREFS_NAME = "OpenkeySDKAppPrefs";
}



