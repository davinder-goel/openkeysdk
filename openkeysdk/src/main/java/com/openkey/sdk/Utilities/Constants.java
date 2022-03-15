package com.openkey.sdk.Utilities;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This will hold all the constants strings for the SDK
 */

public class Constants {

    // SharedPreference keys below
    // Manufacturer for identification purpose to use which SDK needs to be started
    public static final String UUID = "openkey_sdk_uuid";
    //    public static String UUID = "";
    public static final String MANUFACTURER = "manufacturer";
    public static final String AUTH_SIGNATURE = "auth_signature";
    public static final String BOOKING_ID = "booking_id";
    public final static String INVITATION_CODE = "invitation_code";
    public static final String MOBILE_KEY = "mobile_key";
    public static final String KABA_REGISTRATION_TOKEN = "kaba_registration";
    public static final String DRK_REGISTRATION_TOKEN = "drk_registration";
    public static final String OKC_KEY_TOKEN = "guest_sdk_okc_token";
    public static final String OKC_ROOM_LIST = "guest_sdk_okc_ROOM_LIST";

    // KABA requirements
    public static final String URL_KABA_BASE = "https://api.legicconnect.com";
    public static final String KABA_SERVER_URL = URL_KABA_BASE + "/connect";
    public static final String KABA_SERVER_USER_NAME = "WalletOpenKeyTechUser";
    public static final String KABA_SERVER_PASSWORD = "cG1RT04H5xw06lUPV40U";

    public static final String BOOKING = "booking";
    public static final String UNIQUE_NUMBER = "unique_number";
    public static final String MOBILE_KEY_STATUS = "mobile_key_status";
    public static final String BASE_URL = "base_url";
    public static final String TOKEN = "Token ";
    public static final String ENVIRONMENT_TYPE = "environment_type";
    public static final long SCANNING_TIME = 15000;
    public static boolean IS_SCANNING_STOPPED = false;

    // KEY_DELIVERED=0;
    // PENDING_KEY_SERVER_REQUEST=1;
    // KEY_SERVER_REQUESTED=2;
    public static final String KEY_DELIVERED = "KEY DELIVERED";
    public static final String PENDING_KEY_SERVER_REQUEST = "PENDING KEY SERVER REQUEST";
    public static final String KEY_SERVER_REQUESTED = "KEY SERVER REQUESTED";

    //LIVE BASE URL
    //  public static final String BASE_URL_LIVE = "https://developer.openkey.co/";
    // public static final String BASE_URL_LIVE = "https://betadeveloper.openkey.co/";
    //ASSA LIVE
    public static final String ASSA_LIVE_URL = "https://credential-services.sci.assaabloy.com/";
    public static final String ASSA_LIVE_TOKEN = "Basic b3BlbmtleS10bm50OmtoU1NDeVY3cjlGRzl2U1Q0cTVx";
    //ASSA DEV
    public static final String ASSA_DEV_URL = "https://demo.credential-services.sci.assaabloy.net";
    public static final String ASSA_DEV_TOKEN = "Basic b3BlbmtleS1kZW1vLXRubnQ6eFpKTGM1aHNTcTdKcDJ2d1FGMjU=";
    //DEV BASE URL
//    public static String BASE_URL_DEV = "https://connector.openkey.co/";
    //    public static  String BASE_URL_DEV = "https://partner.openkey.co/";
    public static String BASE_URL_DEV = "https://apidev.openkey.co/";
//    public static String BASE_URL_DEV = "https://apistage.openkey.co/";

    //    public static String BASE_URL_STAGE = "https://apistage.openkey.co/";
    //Live
    public static String BASE_URL_LIVE = "https://connector.openkey.co/";
//    public static String BASE_URL_LIVE = "https://developer.openkey.co/";

    public static boolean IS_60_SECONDS = false;
    public static boolean IS_SESSION_API_ALREADY_CALLED = false;

}
