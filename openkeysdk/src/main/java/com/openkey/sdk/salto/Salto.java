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
import android.widget.Toast;

import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Response;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.saltosystems.justinmobile.sdk.ble.IJustinBleResultAndDiscoverCallback;
import com.saltosystems.justinmobile.sdk.ble.JustinBle;
import com.saltosystems.justinmobile.sdk.common.OpResult;
import com.saltosystems.justinmobile.sdk.exceptions.JustinException;
import com.saltosystems.justinmobile.sdk.model.MobileKey;

import io.sentry.Sentry;
//import com.saltosystems.justinkey.sdk.ble.IMasterDeviceManagerApi;
//import com.saltosystems.justinkey.sdk.ble.IMasterDeviceManagerResultAndDiscoverCallback;
//import com.saltosystems.justinkey.sdk.ble.MasterDeviceManagerApi;
//import com.saltosystems.justinkey.sdk.exceptions.SaltoException;
//import com.saltosystems.justinkey.sdk.model.SaltoAccessKey;

/**
 * @author OpenKey Inc.
 * <p>
 * This will handle all things about SALTO lock manufacturer
 */

public final class Salto {

    private final String SECRET_KEY = "Op3nk3y4.0";
    private OpenKeyCallBack openKeyCallBack;
    private Context mContext;
    //    private MasterDeviceManagerApi api;
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

//        final String encryptedKey = Utilities.getInstance().getValue(Constants.MOBILE_KEY, "", mContext);
//        class BackgroundDecryptor extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected String doInBackground(String... strings) {
//                JNCryptor cryptor = new AES256JNCryptor();
//                final byte[] encodedKey = Base64.decode(strings[0], Base64.DEFAULT);
//                try {
//                    byte[] cipherText = cryptor.decryptData(encodedKey, SECRET_KEY.toCharArray());
//                    return new String(cipherText);
//                } catch (CryptorException e) {
//                    e.printStackTrace();
//                }
//                return "";
//            }
//
//            @Override
//            protected void onPostExecute(String decryptedKey) {
//                super.onPostExecute(decryptedKey);
//                openLock(decryptedKey);
//            }
//        }
//        new BackgroundDecryptor().execute(encryptedKey);
    }

    /**
     * Open Salto Lock
     */
    private void openLock(String decryptedKey) {
        Toast.makeText(mContext, "SALTO opening process started", Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(decryptedKey)) {
            try {
                if (api == null) {
                    api = new JustinBle(mContext);
                }
                MobileKey virtualkey = new MobileKey(decryptedKey);
                api.startLockOpening(virtualkey, new IJustinBleResultAndDiscoverCallback() {
                    @Override
                    public void onPeripheralFound() {

                    }

                    @Override
                    public void onSuccess(int opResult) {
                        if (!Constants.IS_SCANNING_STOPPED) {
                            OpenKeyManager.getInstance().removeTimeoutHandler();
                            Constants.IS_SCANNING_STOPPED = true;
                            if (opResult == OpResult.AUTH_SUCCESS_ACCESS_GRANTED) {
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
//                                openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
                                openKeyCallBack.stopScan(false, opResult + "");
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
//                            Log.e("timeout", "salto scanning");
//                            openKeyCallBack.stopScan(false, Response.TIME_OUT_LOCK_NOT_FOUND);
                            openKeyCallBack.stopScan(false, e.getErrorCode() + "::" + e.getMessage());
//                        Api.logSDK(mContext, 0);

                        }
                    }
                });

            } catch (JustinException e) {
                if (!Constants.IS_SCANNING_STOPPED) {
                    Constants.IS_SCANNING_STOPPED = true;
                    OpenKeyManager.getInstance().removeTimeoutHandler();
                    Sentry.captureException(e);
//                    openKeyCallBack.stopScan(false, Response.LOCK_OPENING_FAILURE);
                    openKeyCallBack.stopScan(false, e.getErrorCode() + "::" + e.getMessage());
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
//                openKeyCallBack.stopScan(false, Response.KEY_NOT_CORRECT);
                openKeyCallBack.stopScan(false, "Salto Key:-" + decryptedKey + "---Not Valid");
            }
//            Api.logSDK(mContext, 0);
        }
    }
}
