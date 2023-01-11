/*
 *
 *  Copyright 2015 OpenKey. All Rights Reserved
 *
 *  @author OpenKey Inc.
 *
 */

package com.openkey.sdk.salto;

import android.content.Context;
import android.text.TextUtils;

import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.saltosystems.justinmobile.sdk.ble.IJustinBleMobileKeyRetriever;
import com.saltosystems.justinmobile.sdk.ble.IJustinBleResultAndDiscoverCallbacks;
import com.saltosystems.justinmobile.sdk.ble.JustinBle;
import com.saltosystems.justinmobile.sdk.common.LockOpeningParams;
import com.saltosystems.justinmobile.sdk.common.OpResult;
import com.saltosystems.justinmobile.sdk.common.OpeningMode;
import com.saltosystems.justinmobile.sdk.exceptions.JustinException;
import com.saltosystems.justinmobile.sdk.model.MobileKey;
import com.saltosystems.justinmobile.sdk.model.Result;

import io.sentry.Sentry;

/**
 * @author OpenKey Inc.
 * <p>
 * This will handle all things about SALTO lock manufacturer
 */

public final class Salto {

    private OpenKeyCallBack openKeyCallBack;
    private Context mContext;
    private JustinBle api;


    public Salto(Context mContext, OpenKeyCallBack openKeyCallBack) {
        this.mContext = mContext;
        this.openKeyCallBack = openKeyCallBack;
        startSetup();
    }


    /**
     * Salto require no initial setup, just return the success
     */
    private void startSetup() {
        int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                0, mContext);
        if (haveKey() && mobileKeyStatusId == 3) {
            openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
        } else {
            if (mobileKeyStatusId == 1) {
                /**
                 * Update the status on server that Registration Complete has been completed on Kaba server
                 */
                Api.setPeronalizationComplete(mContext, openKeyCallBack);
            } else {
                openKeyCallBack.initializationSuccess();
            }
        }
    }

    /**
     * check if device have keys
     *
     * @return true/false
     */
    public boolean haveKey() {
        String key = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        return !TextUtils.isEmpty(key);
    }

    /**
     * Start decrypting key
     */
    public void startScanning() {
        decryptSaltoKey();
    }

    /**
     * Decrypt SALTO key which was encrypted by the server, in background
     * after decrypting ,pass the key to SDK for openeing locks
     */
    private void decryptSaltoKey() {
        // Get encrypted key from shared preference
        final String encryptedKey = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
        openLock(encryptedKey);
    }

    private void openLock(String decryptedKey) {
        if (!TextUtils.isEmpty(decryptedKey)) {
            try {
                MobileKey mobileKey = new MobileKey(decryptedKey);
                JustinBle.getInstance(mContext).startOpening(
                        new IJustinBleMobileKeyRetriever() {
                            @Override
                            public MobileKey retrieve() {
                                return mobileKey;
                            }
                        },
                        new IJustinBleResultAndDiscoverCallbacks() {
                            @Override
                            public void onPeripheralFound() {
                            }

                            @Override
                            public void onSuccess(Result result) {

                                if (!Constants.IS_SCANNING_STOPPED) {
                                    OpenKeyManager.getInstance().removeTimeoutHandler();
                                    Constants.IS_SCANNING_STOPPED = true;
                                    if (result.getOpResult() == OpResult.AUTH_SUCCESS_ACCESS_GRANTED) {
                                        Sentry.configureScope(scope -> {
                                            scope.setTag("openingStatus", "SALTO Lock open success");
                                            Sentry.captureMessage("openingStatus->SALTO Lock open success");

                                        });
                                        openKeyCallBack.stopScan(true, Response.LOCK_OPENED_SUCCESSFULLY);
                                        Api.logSDK(mContext, 1);
                                    } else {
                                        Sentry.configureScope(scope -> {
                                            scope.setTag("openingStatus", "SALTO Lock opening failure");
                                            Sentry.captureMessage("openingStatus->SALTO Lock opening failure");

                                        });
                                        openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
//                            Api.logSDK(mContext, 0);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(JustinException e) {
                                e.printStackTrace();
                                if (!Constants.IS_SCANNING_STOPPED && e.getErrorCode() == 410) {
                                    Constants.IS_SCANNING_STOPPED = true;
                                    OpenKeyManager.getInstance().removeTimeoutHandler();
                                    Sentry.configureScope(scope -> {
                                        scope.setTag("stopScan", "SALTO Timeout");
                                        Sentry.captureMessage("stopScan->SALTO Timeout");

                                    });
                                    openKeyCallBack.stopScan(false, Response.TIME_OUT_LOCK_NOT_FOUND);
//                        Api.logSDK(mContext, 0);
                                }
                            }
                        }, new LockOpeningParams.Builder()
                                .setOpeningMode(OpeningMode.STANDARD_MODE)
                                .build()
                );
            } catch (JustinException e) {
                if (!Constants.IS_SCANNING_STOPPED) {
                    Constants.IS_SCANNING_STOPPED = true;
                    OpenKeyManager.getInstance().removeTimeoutHandler();
                    Sentry.captureException(e);
                    openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
                }
                //                Api.logSDK(mContext, 0);
                e.printStackTrace();
            }
        } else {
            // if key is not decrypted by any  reason then show key error
            if (!Constants.IS_SCANNING_STOPPED) {
                Constants.IS_SCANNING_STOPPED = true;
                OpenKeyManager.getInstance().removeTimeoutHandler();
                Sentry.configureScope(scope -> {
                    scope.setTag("stopScan", "SALTO key not correct");
                    Sentry.captureMessage("stopScan->SALTO key not correct");

                });
                openKeyCallBack.stopScan(false, Response.KEY_NOT_CORRECT);
            }
//            Api.logSDK(mContext, 0);
        }
    }
}
