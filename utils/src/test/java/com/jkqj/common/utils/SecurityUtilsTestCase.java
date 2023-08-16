package com.jkqj.common.utils;

import org.junit.Test;

import java.io.File;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SecurityUtilsTestCase {

    @Test
    public void testAes() {
        String key = SecurityUtils.genAESKey();

        String data = SecurityUtils.aesEncryptString(key, "wenbo13900088k");

        String raw = SecurityUtils.aesDecryptString(key, data);

        System.out.println(raw);
    }

    @Test
    public void testRsa() {
        String root = "/Users/hexiufeng/ssl/";

        RSAPublicKey publicKey = SecurityUtils.readPemPublicKey(new File(root + "1.public_key.pem"));
        RSAPrivateKey privateKey = SecurityUtils.readPemPrivateKey(new File(root + "1.private_key.pem"));

        String encryptData = SecurityUtils.rsaEncryptString(publicKey, "hello xxxxooo");

        String raw = SecurityUtils.rsaDecryptString(privateKey, encryptData);

        System.out.println(raw);

    }
    @Test
    public void testRsa1() {
        String root = "/Users/hexiufeng/ssl/";

        RSAPublicKey publicKey = SecurityUtils.readPemPublicKey(new File(root + "1.public_key.pem"));
        RSAPrivateKey privateKey = SecurityUtils.readPemPrivateKey(new File(root + "1.private_key.pem"));

        String encryptData = SecurityUtils.rsaEncryptString(privateKey, "hello xxxxooo");

        String raw = SecurityUtils.rsaDecryptString(publicKey, encryptData);

        System.out.println(raw);

    }
}
