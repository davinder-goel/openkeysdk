package com.openkey.sdk.okc.ble.ciper;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import key.open.cn.blecontrollor.util.MD5;
import key.open.cn.blecontrollor.util.ToHexUtil;

/**
 * 门锁管理器
 */
public class Locker {

    private String macAddress;
    private String key, iv;

    public Locker() {
    }

    /**
     * 解密获取mac地址
     *
     * @param base64
     * @return
     */
    public static String generateTrueMacAddress(byte[] base64) {
        try {
            return new String(AesUtils.getIns("AES/CBC/NoPadding").decrypt(base64, "futurekey2018Goo", "2018GoLfuturekey"), 0, 12, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成后台加密字段的前置字段
     *
     * @param key
     * @return
     */
    public static String generateWebKeySecurity(String key) {
        try {
            return Base64.encodeToString(AesUtils.getIns("AES/CBC/PKCS5Padding").encrypt(key.getBytes("UTF-8"), "futurekey2018Goo", "2018GoLfuturekey"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算电量
     *
     * @param ele
     * @return
     */
    public static double electricityCal(String ele) {
        double p = -1;
        int hex10 = Integer.parseInt(ele, 16);

        double v = hex10 * 42.1875;
        if (v >= 6000)
            p = 1;
        else if (v <= 4000)
            p = 0;
        else
            p = (v - 4000) / 20 / 100;
        return p;
    }

    public void setMac(String mac) {
        this.macAddress = mac;
    }

    /**
     * 开门密文
     *
     * @return
     */
    public byte[] openLockData() {
        StringBuilder plaintText = new StringBuilder();
        plaintText.append("32");
        plaintText.append(Long.toHexString(System.currentTimeMillis() / 1000));
        plaintText.append(macAddress.toLowerCase());

        StringBuilder mac16 = new StringBuilder();
        if (macAddress.length() >= 16) {
            mac16.append(macAddress);
        } else {
            int times = 16 / macAddress.length() + 1;

            for (int i = 0; i < times; i++) {
                mac16.append(macAddress);
            }
        }

        key = mac16.substring(0, 16).toLowerCase();

        String md5 = MD5.encrypt(key);

        iv = md5.substring(0, 16);

        byte[] resArr = AesUtils.getIns("AES/CBC/PKCS5Padding").encrypt(plaintText.toString().getBytes(), key, iv);

        return resArr;
    }

    /**
     * 开门密文
     * 爆款锁
     *
     * @return
     */
    public byte[] openLockDataVpop(String phone, String pwd) {
        StringBuilder plaintText = new StringBuilder();
        plaintText.append("32");
        String timeStr = Long.toHexString(System.currentTimeMillis() / 1000);
        plaintText.append(timeStr.substring(0, 8));
        plaintText.append(phone);
        plaintText.append(pwd);

        StringBuilder mac16 = new StringBuilder();
        if (macAddress.length() >= 16) {
            mac16.append(macAddress);
        } else {
            int times = 16 / macAddress.length() + 1;

            for (int i = 0; i < times; i++) {
                mac16.append(macAddress);
            }
        }

        key = mac16.substring(0, 16).toLowerCase();

        String md5 = MD5.encrypt(key);

        iv = md5.substring(0, 16);

        byte[] resArr = AesUtils.getIns("AES/CBC/PKCS5Padding").encrypt(plaintText.toString().getBytes(), key, iv);
        String hex16resArr = ToHexUtil.byte2hex(resArr);

        String x = Integer.toHexString(hex16resArr.length() / 32);
        String len = x.length() == 1 ? "0" + x + "00" : x + "00";

        StringBuilder resLen = new StringBuilder();
        resLen.append(len);
        resLen.append(hex16resArr);
        return ToHexUtil.hex2byte(resLen.toString());
    }

    /**
     * 添加用户
     * 爆款锁
     *
     * @return
     */
    public byte[] addUserVpop(String oldPhone, String oldPwd, String phone, String pwd, String type) {
        StringBuilder plaintText = new StringBuilder();
        plaintText.append("19");
        String timeStr = Long.toHexString(System.currentTimeMillis() / 1000);
        plaintText.append(timeStr.substring(0, 8));
        plaintText.append(oldPhone);
        plaintText.append(oldPwd);
        plaintText.append(macAddress.toLowerCase());
        plaintText.append(type);
        plaintText.append(phone);
        plaintText.append(pwd);
        plaintText.append(macAddress.toLowerCase());
        for (int i = 0; i < 40; i++) {
            plaintText.append("0");
        }

        StringBuilder mac16 = new StringBuilder();
        if (macAddress.length() >= 16) {
            mac16.append(macAddress);
        } else {
            int times = 16 / macAddress.length() + 1;

            for (int i = 0; i < times; i++) {
                mac16.append(macAddress);
            }
        }

        key = mac16.substring(0, 16).toLowerCase();

        String md5 = MD5.encrypt(key);

        iv = md5.substring(0, 16);

        byte[] resArr = AesUtils.getIns("AES/CBC/PKCS5Padding").encrypt(plaintText.toString().getBytes(), key, iv);
        String hex16resArr = ToHexUtil.byte2hex(resArr);

        String x = Integer.toHexString(hex16resArr.length() / 32);
        String len = x.length() == 1 ? "0" + x + "00" : x + "00";

        StringBuilder resLen = new StringBuilder();
        resLen.append(len);
        resLen.append(hex16resArr);
        return ToHexUtil.hex2byte(resLen.toString());
    }

    /**
     * 开门回调明文
     *
     * @return
     */
    public String openLockDataBack(byte[] backData) {

        byte[] res = AesUtils.getIns("AES/CBC/PKCS5Padding").decrypt(backData, key, iv);

        //fixme 反编码后得到的String数据一般都是有意义数据，因此清除加解密时末尾填充的0
        if (res == null) {
            return null;
        }

        int meaningfulNumber = res.length;
        for (int i = res.length - 1; i >= 0; i--) {
            if (res[i] == 0) {
                meaningfulNumber--;
                continue;
            }
            break;
        }

        byte[] clean = Arrays.copyOf(res, meaningfulNumber);

        return new String(clean);
    }

    /**
     * 开门回调明文
     * （爆款）
     *
     * @return
     */
    public String openLockDataBackVPOP(byte[] backData) {
        if (backData == null) {
            return "-1";
        }

        String str16Pre = ToHexUtil.byte2hex(backData);
        String str16 = str16Pre.substring(4, str16Pre.length());

        byte[] dataPre = ToHexUtil.hex2byte(str16);
        if (dataPre.length % 16 != 0) {
            return "-1";
        }

        byte[] res = AesUtils.getIns("AES/CBC/PKCS5Padding").decrypt(dataPre, key, iv);

//        String resStr = ToHexUtil.byte2hex(res);
        String resStr = new String(res);

        return resStr;
    }

    /**
     * 添加用户回调数据
     * （爆款）
     *
     * @return
     */
    public String addUserDataBackVPOP(byte[] backData) {
        if (backData == null) {
            return "-1";
        }

        String str16Pre = ToHexUtil.byte2hex(backData);
        String str16 = str16Pre.substring(4, str16Pre.length());

        byte[] dataPre = ToHexUtil.hex2byte(str16);
        if (dataPre.length % 16 != 0) {
            return "-1";
        }

        byte[] res = AesUtils.getIns("AES/CBC/PKCS5Padding").decrypt(dataPre, key, iv);

        String resStr = new String(res);

        return resStr;
    }
}
