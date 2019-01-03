package com.openkey.sdk.okc.ble.ciper;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import key.open.cn.blecontrollor.util.ToHexUtil;

/**
 * AES加密解密算法
 */
public class AesUtils {
    // /** 算法/模式/填充 **/
    private static final String defaultCipherMode = "AES/CBC/PKCS5Padding"; //默认加密方式

    private static Map<String, AesUtils> ins = new HashMap<>();

    private String ciperMode;

    private AesUtils(String mode) {
        this.ciperMode = mode;
    }

    public static AesUtils getIns() {
        return getIns(defaultCipherMode);
    }

    public static AesUtils getIns(String mode) {
        AesUtils tmp = ins.get(mode);
        if (tmp == null) {
            tmp = new AesUtils(mode);
            ins.put(mode, tmp);
        }
        return tmp;
    }

    // /** 创建密钥 **/
    private SecretKeySpec createKey(String key) {
        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    private IvParameterSpec createIV(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(password);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new IvParameterSpec(data);
    }

    // /** 加密字节数据 **/
    public byte[] encrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(ciperMode);
            if (iv != null) {
                cipher.init(Cipher.ENCRYPT_MODE, key, createIV(iv));
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 加密(结果为16进制字符串) **/
    public String encrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password, iv);
        String result = ToHexUtil.byte2hex(data);
        return result;
    }

    // /** 解密字节数组 **/
    public byte[] decrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(ciperMode);
            if (iv != null) {
                cipher.init(Cipher.DECRYPT_MODE, key, createIV(iv));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 解密 **/
    public String decrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            data = ToHexUtil.hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password, iv);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}