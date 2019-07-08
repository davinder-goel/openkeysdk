package com.openkeysdk.security;

import android.content.Context;
import android.util.Log;


public class KeyStoreEncryptionDecryptionHelper {

    private static final String LOG_TAG = KeyStoreEncryptionDecryptionHelper.class.getSimpleName();

    private KeyStoreEncryptionDecryption keyStoreEncryptionDecryption = null;

    public KeyStoreEncryptionDecryptionHelper(Context context) {
//        if (android.os.Build.VERSION.SDK_INT < 18) {
//            keyStoreEncryptionDecryption = new KeyStoreEncryptionDecryptionTargetSDK16();
//        } else {
        keyStoreEncryptionDecryption = new KeyStoreEncryptionDecryptionTargetSDK18();
//        }
//
        boolean isInitialized = false;

        try {
            isInitialized = keyStoreEncryptionDecryption.init(context);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "PasswordStorage initialisation error:" + ex.getMessage(), ex);
        }

        if (!isInitialized && keyStoreEncryptionDecryption instanceof KeyStoreEncryptionDecryptionTargetSDK18) {
            keyStoreEncryptionDecryption = new KeyStoreEncryptionDecryptionTargetSDK16();
            keyStoreEncryptionDecryption.init(context);
        }
    }

    public void setData(String key, byte[] data) {
        keyStoreEncryptionDecryption.saveData(key, data);
    }

    public byte[] getData(String key) {
        return keyStoreEncryptionDecryption.getData(key);
    }

    public void remove(String key) {
        keyStoreEncryptionDecryption.remove(key);
    }

}