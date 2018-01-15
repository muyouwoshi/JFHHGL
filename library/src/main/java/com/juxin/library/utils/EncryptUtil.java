package com.juxin.library.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 字符串加密工具类
 */
public final class EncryptUtil {

    /**
     * 对字符串进行md5加密
     *
     * @param source 需要加密的字符串
     * @return 加密之后的字符串
     */
    public static String md5(String source) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(source.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        return StringUtils.hexString(hash);
    }

    /**
     * 对字符串进行sha1加密
     *
     * @param source 需要加密的字符串
     * @return 加密之后的字符串
     */
    public static String sha1(String source) {
        try {
            byte[] hash;
            try {
                hash = MessageDigest.getInstance("SHA-1").digest(source.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Huh, SHA-1 should be supported?", e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Huh, UTF-8 should be supported?", e);
            }

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * DES加密
     *
     * @param encryptString 需要加密的字符串
     * @param encryptKey    加密key
     * @return 加密之后输出的字符串
     * @throws Exception 抛出exception
     */
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(encryptKey.getBytes());
        DESKeySpec desKey = new DESKeySpec(encryptKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    /**
     * DES解密
     *
     * @param decryptString 需要解密的字符串
     * @param decryptKey    解密key
     * @return 解密之后输出的字符串
     * @throws Exception 抛出exception
     */
    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(decryptKey.getBytes());
        byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
        DESKeySpec desKey = new DESKeySpec(decryptKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }

    /**
     * DES-PCK加密
     *
     * @param encryptString 需要加密的字符串
     * @param encryptKey    加密key
     * @return 加密之后输出的字符串
     * @throws Exception 抛出exception
     */
    public static String encryptDESPCK(String encryptString, String encryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(encryptKey.getBytes());
        DESKeySpec desKey = new DESKeySpec(encryptKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
    }

    /**
     * DES-PCK解密
     *
     * @param decryptString 需要解密的字符串
     * @param decryptKey    解密key
     * @return 解密之后输出的字符串
     * @throws Exception 抛出exception
     */
    public static String decryptDESPCK(String decryptString, String decryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(decryptKey.getBytes());
        byte[] byteMi = Base64.decode(decryptString, Base64.NO_WRAP);
        DESKeySpec desKey = new DESKeySpec(decryptKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }

    /**
     * 获取字符串的hashCode
     *
     * @param source 原字符串
     * @return hashCode
     */
    public static int getHashCode(String source) {
        if (source == null) return 0;
        return source.hashCode();
    }
}
