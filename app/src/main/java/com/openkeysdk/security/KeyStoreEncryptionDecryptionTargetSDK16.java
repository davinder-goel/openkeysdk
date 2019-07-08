package com.openkeysdk.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class KeyStoreEncryptionDecryptionTargetSDK16 implements KeyStoreEncryptionDecryption {
    private SharedPreferences preferences;

    @Override
    public boolean init(Context context) {
        preferences = context.getSharedPreferences("keystoreDemo", Context.MODE_PRIVATE);
        return true;
    }

    @Override
    public void saveData(String key, byte[] data) {
        if (data == null)
            return;
        Editor editor = preferences.edit();
        editor.putString(key, Base64.encodeToString(data, Base64.DEFAULT));
        editor.apply();
    }

    @Override
    public byte[] getData(String key) {
        String res = preferences.getString(key, null);
        if (res == null)
            return null;
        return Base64.decode(res, Base64.DEFAULT);
    }

    @Override
    public void remove(String key) {
        Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}