package com.pay.util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;

import com.pay.payAssist.ToolKit;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
*@author star
*@version 创建时间：2019年3月30日下午6:24:14
*/
public class KeyPairGenUtil {
	 /** 指定加密算法为RSA */  
    private static final String ALGORITHM = "RSA";  
    /** 密钥长度，用来初始化 */  
    private static final int KEYSIZE = 1024;  
    /** 指定公钥存放文件 */  
    private static String PUBLIC_KEY_FILE = "PublicKey";  
    /** 指定私钥存放文件 */  
    private static String PRIVATE_KEY_FILE = "PrivateKey";  
  
    public static void main(String[] args) throws Exception {  
        //generateKeyPair();  
        genKeyPair();  
    }
  
    /** 
    * 生成密钥对 
    * @throws Exception 
    */  
    private static void generateKeyPair() throws Exception {  
  
        //     /** RSA算法要求有一个可信任的随机数源 */  
        //     SecureRandom secureRandom = new SecureRandom();  
        /** 为RSA算法创建一个KeyPairGenerator对象 */  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);  
  
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */  
        //     keyPairGenerator.initialize(KEYSIZE, secureRandom);  
        keyPairGenerator.initialize(KEYSIZE);  
  
        /** 生成密匙对 */  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
  
        /** 得到公钥 */  
        Key publicKey = keyPair.getPublic();  
  
        /** 得到私钥 */  
        Key privateKey = keyPair.getPrivate();  
  
        ObjectOutputStream oos1 = null;  
        ObjectOutputStream oos2 = null;  
        try {  
            /** 用对象流将生成的密钥写入文件 */  
            oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));  
            oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));  
            oos1.writeObject(publicKey);  
            oos2.writeObject(privateKey);  
        } catch (Exception e) {  
            throw e;  
        } finally {  
            /** 清空缓存，关闭文件输出流 */  
            oos1.close();  
            oos2.close();  
        }  
    }  
  
    public static Map<String,String> genKeyPair() throws NoSuchAlgorithmException {  
          
        /** RSA算法要求有一个可信任的随机数源 */  
        SecureRandom secureRandom = new SecureRandom();  
          
        /** 为RSA算法创建一个KeyPairGenerator对象 */  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);  
  
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */  
        keyPairGenerator.initialize(KEYSIZE, secureRandom);  
        //keyPairGenerator.initialize(KEYSIZE);  
  
        /** 生成密匙对 */  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
  
        /** 得到公钥 */  
        Key publicKey = keyPair.getPublic();  
  
        /** 得到私钥 */  
        Key privateKey = keyPair.getPrivate();  
  
        byte[] publicKeyBytes = publicKey.getEncoded();  
        byte[] privateKeyBytes = privateKey.getEncoded();  
        
        String publicKeyBase64 = Base64.encodeBase64String(publicKeyBytes);
        String privateKeyBase64 = Base64.encodeBase64String(privateKeyBytes);
        
        
//        Map<String, String> metaSignMap = new TreeMap<String, String>();
//			
//			try {
//				
//				byte[] dataStr = ToolKit.encryptByPublicKey("aaa".getBytes(ToolKit.CHARSET),
//						publicKeyBase64);
//				String param = new BASE64Encoder().encode(dataStr);
//				
//				byte[] result = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(param), privateKeyBase64);
//				String resultData = new String(result, ToolKit.CHARSET);
//				System.out.println(resultData);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}// 解密数据
//			
//        
        System.out.println("publicKeyBase64:"+publicKeyBase64);
        System.out.println("privateKeyBase64:"+privateKeyBase64);
        Map<String,String> map = new HashMap<String, String>();
        map.put("publicKey", publicKeyBase64);
        map.put("privateKey", privateKeyBase64);
        return map;
    }
}
