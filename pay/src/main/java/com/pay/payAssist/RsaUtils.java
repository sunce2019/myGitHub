package com.pay.payAssist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author: daihw
 * @date: 2018/9/10
 * @time: 11:48
 * @description: RSA工具类
 * 全部不以try/catch处理异常，抛出异常方便捕获
 * 使用的是JDK 1.8 的 Base64工具类
 **/
public class RsaUtils {
    private static final Logger logger = LoggerFactory.getLogger(RsaUtils.class);

    public static final String RSA_ALGORITHM = "RSA";

    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    public static final String CHARSET = "UTF-8";

    public static final int KEY_SIZE = 1024;

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 还原公钥
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) throws Exception{
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }

    /**
     * 还原私钥 PCKS8
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) throws Exception{
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
        return privateKey;
    }

    /**
     * 加密
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        PublicKey pubKey = restorePublicKey(Base64.getDecoder().decode(publicKey));
        byte[] encryptedData = encrypt(data.getBytes(CHARSET), pubKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 加密
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        //加密模式
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 解密
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String privateKey) throws Exception {
        PrivateKey priKey = restorePrivateKey(Base64.getDecoder().decode(privateKey));
        byte[] decryptedData = decrypt(Base64.getDecoder().decode(data.getBytes(CHARSET)), priKey);
        return new String(decryptedData, CHARSET);
    }


    /**
     * 解密
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 签名 MD5
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(String data, String privateKey) throws Exception{
        return sign(data, privateKey, SIGNATURE_ALGORITHM);
    }

    /**
     * RSA签名
     * @param data
     * @param privateKey
     * @param signAlgorithm
     * @return
     * @throws Exception
     */
    public static String sign(String data, String privateKey, String signAlgorithm) throws Exception{
        PrivateKey priKey = restorePrivateKey(Base64.getDecoder().decode(privateKey));
        byte[] sign = sign(data.getBytes(CHARSET), priKey, signAlgorithm);
        return Base64.getEncoder().encodeToString(sign);
    }

    /**
     * RSA签名
     * @param data
     * @param privateKey
     * @param signAlgorithm
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey, String signAlgorithm) throws Exception{
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }


    /**
     * RSA验签
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String sign, String publicKey) throws Exception{
        return verify(data, sign, publicKey, SIGNATURE_ALGORITHM);
    }

    /**
     * RSA验签
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String sign, String publicKey, String signAlgorithm) throws Exception{
        PublicKey pubKey = restorePublicKey(Base64.getDecoder().decode(publicKey));
        return verify(data.getBytes(CHARSET), Base64.getDecoder().decode(sign), pubKey, signAlgorithm);
    }

    /**
     * RSA验签
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey, String signAlgorithm) throws Exception{
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }


    /**
     * 生成密钥对
     * @return
     * @throws Exception
     */
    public static KeyPair generateKeyPair() {
        return generateKeyPair(KEY_SIZE);
    }

    /**
     * 生成密钥对
     * 获取公私钥使用
     *      keyPair.gePublic();
     *      keyPair.getPrivate();
     * @param keySize 密钥长度
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair(int keySize) {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 获取私钥
     * @param keyPair
     * @return
     */
    public static String getPrivateKey(KeyPair keyPair) {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 获取公钥
     * @param keyPair
     * @return
     */
    public static String getPublicKey(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

}
