package com.jkqj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

/**
 * AES工具类
 *
 * @author xuweizhe@reta-inc.com
 * @date 2022/4/8
 */
@Slf4j
public final class AESUtils {

    private static Cipher PKCS5_CIPHER = null;
    private static Cipher PKCS7_CIPHER = null;
    static {
        Security.addProvider(new BouncyCastleProvider());

        try {
            PKCS5_CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding");
            PKCS7_CIPHER = Cipher.getInstance("AES/CBC/PKCS7Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String key, String iv, String data) {
        return encrypt(key, iv, data, PKCS5_CIPHER);
    }

    public static String decrypt(String key, String iv, String data) {
        return decrypt(key, iv, data, PKCS5_CIPHER);
    }

    public static String pkcs7Encrypt(String key, String iv, String data) {
        return encrypt(key, iv, data, PKCS7_CIPHER);
    }

    public static String pkcs7Decrypt(String key, String iv, String data) {
        return decrypt(key, iv, data, PKCS7_CIPHER);
    }

    private static String encrypt(String key, String iv, String data, Cipher cipher) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.warn("加密异常, data: {}, key: {}, iv: {}", data, key, iv, e);

            return "";
        }
    }

    public static String decrypt(String key, String iv, String data, Cipher cipher) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = Base64.getDecoder().decode(data);
            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("解密异常, data: {}, key: {}, iv: {}", data, key, iv, e);

            return "";
        }
    }

}
