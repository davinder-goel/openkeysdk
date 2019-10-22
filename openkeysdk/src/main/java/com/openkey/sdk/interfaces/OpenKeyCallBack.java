package com.openkey.sdk.interfaces;

import com.openkey.sdk.api.response.session.SessionResponse;

import java.util.ArrayList;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This interface will provide callback methods to user(Third Party Developer)
 *         for success or fai.ure of every operation of the SDK.
 */

public interface OpenKeyCallBack {

    /**
     * @param sessionResponse Provide booking response to user if user is authorized
     *                        to use SDK
     */
    void sessionResponse(SessionResponse sessionResponse);

    /**
     * If setting up device has been successful
     */
    void initializationSuccess();

    void sessionFailure(String errorDescription, String errorCode);

    /**
     * If setting up the device has been failed
     * due to any error this method will called
     * with the description of the error
     *
     * @param errorDescription Describe the error
     */
    void initializationFailure(String errorDescription);

    /**
     * this  will let us know if the lock is opened or not.
     *
     * @param isLockOpened whether lock is opened or not
     */
    void stopScan(boolean isLockOpened, String description);

    /**
     * When a key is available the we need to mark a status on server that
     * we have a key
     *
     * @param haveKey is Key available
     */
    void isKeyAvailable(boolean haveKey, String description);

    /**
     * when we receive multiple rooms in a booking
     * main for common areas like GYM, BAR etc
     *
     * @param availableRooms is Keys available
     */
    void getOKCandOkModuleMobileKeysResponse(ArrayList<String> availableRooms);
}
