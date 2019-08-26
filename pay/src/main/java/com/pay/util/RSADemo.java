package com.pay.util;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSADemo {
  public static final String KEY_ALGORITHM = "RSA";

  /**
   * 公钥加密
   *
   * @param data
   *          待加密数据
   * @param key
   *          密钥
   * @return byte[] 加密数据
   */
  public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {

    // 实例化密钥工厂
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    // 初始化公钥
    // 密钥材料转换
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
    // 产生公钥
    PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

    // 数据加密
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    return cipher.doFinal(data);
  }

  /**
   * 公钥解密
   *
   * @param data
   *          待解密数据
   * @param key
   *          密钥
   * @return byte[] 解密数据
   */
  public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {

    // 实例化密钥工厂
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    // 初始化公钥
    // 密钥材料转换
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
    // 产生公钥
    PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
    // 数据解密
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.DECRYPT_MODE, pubKey);
    return cipher.doFinal(data);
  }

  public static void main(String[] args) throws Exception {
    // 公钥
    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw+cz2iJ1XgimMyLp19eTZAe72d63E+On7PG4gM9z66jtCrblALtocGBaf9d9i/r2QQ7f2RllMscs1Nol15LbHj8L+4g2Sj2FJSmnbR2byiV/R6qt1DP6tTZKKfmV2PzLaHv63McvqYE3c3IwO0iJGM1bRAzUlk5cTIBViwYmfwC1Zg5zYWOSaRkZhtazABg0IkkdUHqGw5th0REgFNNuKFwWXQ97l5jvHJDabptfALDrolL8NllDN0+uXoxVeDeQ4Kh8hOWBzgfOs4QDF2u9amwnr+ulG9KfH6z7J7MnOnRPEAh/C55N6c+X/ERYsh+fi8An2U7w5Dd/DRe+0uFgZQIDAQAB";
    String str = "account=xx&merchantNo=xx&type=xx&amount=xx&key=xx";
    byte[] decoded = Base64.getDecoder().decode(publicKey);
    //公钥加密传送数据到第三方
    byte[] encryptByPublicKey = encryptByPublicKey(str.getBytes(), decoded);
    String encodeToString = Base64.getEncoder().encodeToString(encryptByPublicKey);
    System.out.println("加密后数据：" + encodeToString);
    // 进行参数编码
    String encode = URLEncoder.encode(encodeToString, "UTF-8");
    System.out.println("编码后数据：" + encode);
    
    //公钥解密接收第三方传送过来的数据
    String strReceive = "Xfy+DUkvwbroDHLUew8mZfvSb3Q7lw2skcTHcStVQADmAmdvuwGAz3j4n47WNHuPj+YlRBu5YfiPA8ErN9094LduPGLfvlznNpn09/TMZUNIzgRbiI06Nx/uKV80bOwNsKS6p653C9GFZYVyaJbKGMW46UCvb3LZZ3tIn8KMIN/Z05vauxFyQoY/8Ljpv0C2f/twnNBzikwGAmdF2zBt9oUTIKSNyFrfyw1PDdmMBLFY657uVigt6wqp8OJFW86wRyCGrEfsisVfQdDgFj3IOg+wR4L+2T+bB+mTk4amVjAzxwfP5diu0VJA79wkF8BVSsLT5TP0swzYWV862xysPQ==";
    byte[] decryptKey = decryptByPublicKey(Base64.getDecoder().decode(strReceive), decoded);
    System.out.println("解密后数据：" + new String(decryptKey));
  }

}
