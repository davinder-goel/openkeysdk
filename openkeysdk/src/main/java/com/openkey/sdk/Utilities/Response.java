package com.openkey.sdk.Utilities;

/**
 * Response constants for showing the Specific error occurred
 **/
public class Response {

    // if provided phone number is empty.
    public static final String NUMBER_NOT_VALID = "NUMBER NOT VALID";

    // if any failure occur while initialization
    public static final String INITIALIZATION_FAILED = "INITIALIZATION FAILED";

    // if getting key operation is failed
    public static final String FETCH_KEY_FAILED = "FAILED GETTING KEYS";

    // if getting key operation is success
    public static final String FETCH_KEY_SUCCESS = "SUCCESS GETTING KEYS";

    // If Unknown error occurred.
    public static final String UNKNOWN = "Unknown error occurred, please contact administrator";

    // IF device has not been setup
    public static final String NOT_INITIALIZED = "SDK NOT INITIALIZED";

    // IF device has failed authentication
    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION FAILED";

    // IF booking not exist
    public static final String BOOKING_NOT_FOUNT = "BOOKING NOT FOUND";

    // IF device has failed authentication
    public static final String AUTHENTICATION_SUCCESSFUL = "AUTHENTICATION SUCCESSFUL";

    // IF  salto key is not decrypted properly
    public static final String KEY_NOT_CORRECT = "INVALID KEY";

    // IF null context passed
    public static final String NULL_CONTEXT = "CONTEXT NULL";

    // if auth signature is not valid
    public static final String INVALID_AUTH_SIGNATURE = "AUTH SIGNATURE IS INVALID";

    // if device has no keys
    public static final String NO_KEY_FOUND = "NO KEY FOUND";

    // if lock opened successfully
    public static final String LOCK_OPENED_SUCCESSFULLY = "LOCK OPENED SUCCESSFULLY";

    // if lock opened successfully
    public static final String LOCK_OPENING_FAILURE = "LOCK OPENING FAILURE";
    public static final String TIME_OUT_LOCK_NOT_FOUND = "Timeout: Lock not found.";
    public static final String BT_PERMISSION_MISSING = "Bluetooth permission are missing";


}