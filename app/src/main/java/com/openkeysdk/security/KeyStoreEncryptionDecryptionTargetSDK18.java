package com.openkeysdk.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.x500.X500Principal;

public class KeyStoreEncryptionDecryptionTargetSDK18 implements KeyStoreEncryptionDecryption {
    private static final String LOG_TAG = KeyStoreEncryptionDecryptionTargetSDK18.class.getSimpleName();

    private static final String KEY_ALGORITHM_RSA = "RSA";
    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private KeyStore mKeyStore;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private SharedPreferences preferences;
    private String alias;

    @SuppressWarnings("deprecation")
    @SuppressLint({"NewApi", "TrulyRandom"})
    @Override
    public boolean init(Context context) {
        preferences = context.getSharedPreferences("openkeysdkpref", Context.MODE_PRIVATE);
        alias = "OpenKeySdk";

        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            //Use null to load Keystore with default parameters.
            mKeyStore.load(null);

            // Check if Private and Public already keys exists. If so we don't need to generate them again
            privateKey = (PrivateKey) mKeyStore.getKey(alias, null);
            if (privateKey != null && mKeyStore.getCertificate(alias) != null) {
                publicKey = mKeyStore.getCertificate(alias).getPublicKey();
                if (publicKey != null) {
                    // All keys are available.
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 10);

        // Specify the parameters object which will be passed to KeyPairGenerator
        AlgorithmParameterSpec spec;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            spec = new android.security.KeyPairGeneratorSpec.Builder(context)
                    // Alias - is a key for your KeyPair, to obtain it from Keystore in future.
                    .setAlias(alias)
                    // The subject used for the self-signed certificate of the generated pair
                    .setSubject(new X500Principal("CN=" + alias))
                    // The serial number used for the self-signed certificate of the generated pair.
                    .setSerialNumber(BigInteger.valueOf(1337))
                    // Date range of validity for the generated pair.
                    .setStartDate(start.getTime()).setEndDate(end.getTime())
                    .build();
            // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
            // and the KeyStore. This example uses the AndroidKeyStore.
            KeyPairGenerator kpGenerator;
            try {
                kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
                kpGenerator.initialize(spec);
                // Generate private/public keys
                kpGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
                try {
                    if (mKeyStore != null)
                        mKeyStore.deleteEntry(alias);
                } catch (Exception e1) {
                    // Just ignore any errors here
                }
            }
//        } else {
//            spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
//                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
//                    .build();
        }

//        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
//        // and the KeyStore. This example uses the AndroidKeyStore.
//        KeyPairGenerator kpGenerator;
//        try {
//            kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
//            kpGenerator.initialize(spec);
//            // Generate private/public keys
//            kpGenerator.generateKeyPair();
//        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
//            try {
//                if (mKeyStore != null)
//                    mKeyStore.deleteEntry(alias);
//            } catch (Exception e1) {
//                // Just ignore any errors here
//            }
//        }

        // Check if device support Hardware-backed keystore
//        try {
//            boolean isHardwareBackedKeystoreSupported;
//            if (android.os.Build.VERSION.SDK_INT < 23) {
//                isHardwareBackedKeystoreSupported = KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA);
//            } else {
////                PrivateKey privateKey = (PrivateKey) mKeyStore.getKey(alias, null);
////                KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA);
////                KeyFactory keyFactory = KeyFactory.getInstance(privateKey.getAlgorithm(), "AndroidKeyStore");
////                KeyInfo keyInfo = keyFactory.getKeySpec(privateKey, KeyInfo.class);
////                isHardwareBackedKeystoreSupported = keyInfo.isInsideSecureHardware();
//            }
////            Log.e("Hardware-Backed Keystore Supported: ", isHardwareBackedKeystoreSupported + "");
//        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | InvalidKeySpecException | NoSuchProviderException e) {
//            e.printStackTrace();
//        }

        return true;
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        KeyGenerator keyGenerator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);


            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
        }

        return keyGenerator.generateKey();
    }


    private SecretKey getSecretKeyDec(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) mKeyStore.getEntry(alias, null)).getSecretKey();
    }

    @Override
    public void saveData(String key, byte[] data) {
//            KeyStore ks = null;
        try {
//                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
//
//                ks.load(null);
//                if (ks.getCertificate(alias) == null) return;
//
//                PublicKey publicKey = ks.getCertificate(alias).getPublicKey();


//            if (publicKey == null) {
//                Log.d(LOG_TAG, "Error: Public key was not found in Keystore");
//                return;
//            }

            String value = encrypt(publicKey, data);

            Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
                | InvalidKeySpecException e) {
            try {
                if (mKeyStore != null)
                    mKeyStore.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
    }

    @Override
    public byte[] getData(String key) {
//            KeyStore ks = null;
        try {
//                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
//                ks.load(null);
//                PrivateKey privateKey = null;
//                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
//                    privateKey = (PrivateKey) ks.getKey(alias, null);
//                }
            return decrypt(privateKey, preferences.getString(key, ""));
        } catch (NoSuchAlgorithmException
                | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            try {
                if (mKeyStore != null)
                    mKeyStore.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
        return null;
    }

    @Override
    public void remove(String key) {
        Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    @SuppressLint("TrulyRandom")
    private String encrypt(PublicKey encryptionKey, byte[] data) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, InvalidKeySpecException {
        Cipher cipher = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            try {
                cipher = Cipher.getInstance(AES_GCM_NOPADDING);
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        } else {
            if (encryptionKey == null) {
                Log.d(LOG_TAG, "Error: Public key was not found in Keystore");
            } else {
                cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
                cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            }
        }
        byte[] encrypted = new byte[0];
        encrypted = cipher.doFinal(data);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    private byte[] decrypt(PrivateKey decryptionKey, String encryptedData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (encryptedData == null)
            return null;
        byte[] encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT);

        Cipher cipher = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                cipher = Cipher.getInstance(AES_GCM_NOPADDING);

                final GCMParameterSpec spec = new GCMParameterSpec(128, cipher.getIV());
                cipher.init(Cipher.DECRYPT_MODE, getSecretKeyDec(alias), spec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        } else {
            cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        }

//            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
//            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        return cipher.doFinal(encryptedBuffer);
    }
}