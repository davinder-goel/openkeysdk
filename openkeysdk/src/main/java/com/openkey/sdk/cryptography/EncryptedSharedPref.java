package com.openkey.sdk.cryptography;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EncryptedSharedPref implements SharedPreferences {
    private static final String fileName = "SecretPreferencesSDK";
    private static SharedPreferences mSharedPref;

    public EncryptedSharedPref(Context context) {
        if (mSharedPref == null) {
            try {
                KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
                String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
                mSharedPref = EncryptedSharedPreferences.create(
                        fileName,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
//
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public Map<String, ?> getAll() {
        final Map<String, ?> encryptedMap = mSharedPref.getAll();
        final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
        for (Map.Entry<String, ?> entry : encryptedMap.entrySet()) {
            try {
                decryptedMap.put(entry.getKey(),
                        entry.getValue().toString());
            } catch (Exception e) {
                // Ignore unencrypted key/value pairs
            }
        }
        return decryptedMap;
    }

    @Override
    public String getString(String key, String defaultValue) {
        final String encryptedValue =
                mSharedPref.getString(key, null);
        return (encryptedValue != null) ? encryptedValue : defaultValue;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultValues) {
        final Set<String> encryptedSet =
                mSharedPref.getStringSet(key, null);
        if (encryptedSet == null) {
            return defaultValues;
        }
        final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
        for (String encryptedValue : encryptedSet) {
            decryptedSet.add(encryptedValue);
        }
        return decryptedSet;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return mSharedPref.getInt(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return mSharedPref.getLong(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return mSharedPref.getFloat(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPref.getBoolean(key, defaultValue);
    }

    @Override
    public boolean contains(String key) {
        return mSharedPref.contains(key);
    }

    @Override
    public Editor edit() {
        return new Editor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPref.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Wrapper for Android's {@link SharedPreferences.Editor}.
     * <p>
     * Used for modifying values in a {@link EncryptedSharedPref} object. All changes you make in an
     * editor are batched, and not copied back to the original {@link EncryptedSharedPref} until you
     * call {@link #commit()} or {@link #apply()}.
     */
    public static class Editor implements SharedPreferences.Editor {
        private SharedPreferences.Editor mEditor;

        /**
         * Constructor.
         */
        private Editor() {
            mEditor = mSharedPref.edit();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            mEditor.putString(key, value);
            return this;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            final Set<String> encryptedValues = new HashSet<String>(values.size());
            for (String value : values) {
                encryptedValues.add(value);
            }
            mEditor.putStringSet(key, encryptedValues);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            mEditor.putInt(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            mEditor.putLong(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            mEditor.putFloat(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            mEditor.putBoolean(key, value);
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            mEditor.remove(key);
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public void apply() {
            mEditor.apply();
        }
    }
}