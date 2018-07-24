/*
 * *
 *  * Copyright 2015 OpenKey. All Rights Reserved
 *  *
 *  * @author OpenKey Inc.
 *
 */

package com.openkey.sdk.Utilities;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This will hold all the constants strings for the SDK
 */

public class Constants {

    // SharedPreference keys below
    public static final String IS_AUTHENTICATED = "is_authenticated";
    // Manufacturer for identification purpose to use which SDK needs to be started
    public static final String MANUFACTURER = "manufacturer";
    public static final String AUTH_SIGNATURE = "auth_signature";
    public static final String BOOKING_ID = "booking_id";
    public final static String INVITATION_CODE = "invitation_code";
    //public static final String SALTO_BINARY_KEY = "salto_binary_key";
    public static final String MOBILE_KEY = "mobile_key";
    public static final String IS_PERSONLIZATION_STATUS_UPDATED = "is_personlization_status_updated";

    public static final String KABA_REGISTRATION_TOKEN = "kaba_registration";
    // KABA requirements
    public static final String URL_KABA_BASE = "https://api.legicconnect.com";
    public static final String KABA_SERVER_URL = URL_KABA_BASE + "/connect";
    public static final String KABA_SERVER_USER_NAME = "WalletOpenKeyTechUser";
    public static final String KABA_SERVER_PASSWORD = "cG1RT04H5xw06lUPV40U";

    public static final String BEARER = "Bearer ";
    public static final String BOOKING = "booking";
    public static final String IS_KEY_STATUS_UPDATED = "is_key_status_updated";
    public static final String UNIQUE_NUMBER = "unique_number";
    public static final String IS_LIVE_ENVIRONMENT = "is_live_envirnonment";
    public static final String ASSA_TOKEN = "assa_token";
    public static final String ASSA_BASE_URL = "assa_base_url";
    public static final String BASE_URL = "base_url";
    public static final String TOKEN = "Token ";

    //LIVE BASE URL
     public static final String BASE_URL_LIVE = "https://developer.openkey.co/";
    // public static final String BASE_URL_LIVE = "https://betadeveloper.openkey.co/";

     //DEV BASE URL
     //public static  String BASE_URL_DEV = "https://partner.openkey.co/";
     public static String BASE_URL_DEV = "https://apidev.openkey.co/";
/*
    https://apidev.openkey.co/sdk/v5
*/
//     //ASSA LIVE
//     public static final String ASSA_LIVE_URL = "https://credential-services.sci.assaabloy.com/";
//     public static final String ASSA_LIVE_TOKEN = "Basic b3BlbmtleS10bm50OmtoU1NDeVY3cjlGRzl2U1Q0cTVx";
//
//     //ASSA DEV
//     public static final String ASSA_DEV_URL = "https://demo.credential-services.sci.assaabloy.net";
//     public static final String ASSA_DEV_TOKEN = "Basic b3BlbmtleS1kZW1vLXRubnQ6eFpKTGM1aHNTcTdKcDJ2d1FGMjU=";


}
