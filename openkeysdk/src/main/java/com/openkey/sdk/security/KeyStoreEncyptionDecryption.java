//package com.openkey.sdk.security;
//
//import android.content.Context;
//import android.security.KeyPairGeneratorSpec;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
//import java.security.InvalidAlgorithmParameterException;
//import java.security.InvalidKeyException;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.UnrecoverableEntryException;
//import java.security.cert.CertificateException;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.util.Calendar;
//
//import javax.crypto.Cipher;
//import javax.crypto.CipherInputStream;
//import javax.crypto.CipherOutputStream;
//import javax.crypto.NoSuchPaddingException;
//import javax.security.auth.x500.X500Principal;
//
//public class KeyStoreEncyptionDecryption {
//    String TAG = "KeyStoreEncyptionDecryption";
//    RSAPublicKey publicKey;
//    RSAPrivateKey privateKey;
//    String encryptedDataFilePath;
//    private Context context;
//
//    KeyStoreEncyptionDecryption(Context context) {
//        this.context = context;
//    }
//
//    void keyStore(String alias) {
//        try {
//            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//
////            String alias = "key3";
//
//            int nBefore = keyStore.size();
//
//            // Create the keys if necessary
//            if (!keyStore.containsAlias(alias)) {
//
//                Calendar notBefore = Calendar.getInstance();
//                Calendar notAfter = Calendar.getInstance();
//                notAfter.add(Calendar.YEAR, 1);
//                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
//                        .setAlias(alias)
//                        .setKeyType("RSA")
//                        .setKeySize(2048)
//                        .setSubject(new X500Principal("CN=test"))
//                        .setSerialNumber(BigInteger.ONE)
//                        .setStartDate(notBefore.getTime())
//                        .setEndDate(notAfter.getTime())
//                        .build();
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(spec);
//
//                KeyPair keyPair = generator.generateKeyPair();
//            }
//            int nAfter = keyStore.size();
//            Log.e("Before = ", nBefore + " After = " + nAfter);
//
//            // Retrieve the keys
//            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
//            privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
//            publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
//
//            Log.e("private key = ", privateKey.toString());
//            Log.e("public key = ", publicKey.toString());
//
//            // Encrypt the text
//            String plainText = "This text is supposed to be a secret!";
//            String dataDirectory = context.getApplicationInfo().dataDir;
//            String filesDirectory = context.getFilesDir().getAbsolutePath();
//            encryptedDataFilePath = filesDirectory + File.separator + "keep_yer_secrets_here";
//
//            Log.e("plainText = ", plainText);
//            Log.e("dataDirectory = ", dataDirectory);
//            Log.e("filesDirectory = ", filesDirectory);
//            Log.e("encryptedDataFilePath", encryptedDataFilePath);
////            encryptValue(plainText);
//
////            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
////            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
////
////            Cipher outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
////            outCipher.init(Cipher.DECRYPT_MODE, privateKey);
////
////            CipherOutputStream cipherOutputStream =
////                    new CipherOutputStream(
////                            new FileOutputStream(encryptedDataFilePath), inCipher);
////            cipherOutputStream.write(plainText.getBytes("UTF-8"));
////            cipherOutputStream.close();
//
////            CipherInputStream cipherInputStream =
////                    new CipherInputStream(new FileInputStream(encryptedDataFilePath),
////                            outCipher);
////            byte[] roundTrippedBytes = new byte[1000]; // TODO: dynamically resize as we get more data
////
////            int index = 0;
////            int nextByte;
////            while ((nextByte = cipherInputStream.read()) != -1) {
////                roundTrippedBytes[index] = (byte) nextByte;
////                index++;
////            }
////            String roundTrippedString = new String(roundTrippedBytes, 0, index, "UTF-8");
////            Log.e(TAG, "round tripped string = " + roundTrippedString);
//
//        } catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableEntryException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        }
//    }
//
//    String decryptVlaue() {
//        String roundTrippedString = "";
//        Cipher outCipher = null;
//        try {
//            outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//            outCipher.init(Cipher.DECRYPT_MODE, privateKey);
//
//            CipherInputStream cipherInputStream =
//                    new CipherInputStream(new FileInputStream(encryptedDataFilePath),
//                            outCipher);
//
//            byte[] roundTrippedBytes = new byte[1000]; // TODO: dynamically resize as we get more data
//
//            int index = 0;
//            int nextByte;
//            while ((nextByte = cipherInputStream.read()) != -1) {
//                roundTrippedBytes[index] = (byte) nextByte;
//                index++;
//            }
//            roundTrippedString = new String(roundTrippedBytes, 0, index, "UTF-8");
//            Log.e(TAG, "round tripped string = " + roundTrippedString);
//
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return roundTrippedString;
//    }
//
//    void encryptValue(String value) {
//        Cipher inCipher = null;
//        try {
//            inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
//        CipherOutputStream cipherOutputStream =
//                null;
//        try {
//            cipherOutputStream = new CipherOutputStream(
//                    new FileOutputStream(encryptedDataFilePath), inCipher);
//            cipherOutputStream.write(value.getBytes("UTF-8"));
//            cipherOutputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
