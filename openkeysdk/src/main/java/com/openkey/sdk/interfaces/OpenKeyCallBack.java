package com.openkey.sdk.interfaces;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This interface will provide callback methods to user(Third Party Developer)
 *         for success or fai.ure of every operation of the SDK.
 */

public interface OpenKeyCallBack {

    /**
     * Provide web service response to user if user
     * is authorized to use SDK
     *
     * @param isAuthenticated return true/false depend upon response
     * @param description     description of the response
     */
    void authenticated(boolean isAuthenticated, String description);

    /**
     * If setting up device has been successful
     */
    void initializationSuccess();

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


}
