package com.pay.payAssist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author: daihw
 * @date: 2018/9/13
 * @time: 14:00
 * @description:  速汇宝签名验签加密工具类
 * 注，所有异常类型抛出自行捕获
 *
 **/
public class ApiUtil {
    private static final Logger logger = LoggerFactory.getLogger(ApiUtil.class);

    /**
     * 签名
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(String data, String privateKey) throws Exception {
        privateKey = privateKey.replaceAll("\n", "");
        return RsaUtils.sign(data, privateKey);
    }

    /**
     * 签名
     * @param context
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(Map context, String privateKey) throws Exception {
        SortedMap signParams = new TreeMap();
        signParams.putAll(context);
        return sign(JSON.toJSONString(signParams), privateKey);
    }

    /**
     * 验签
     * @param context
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(Map context, String sign, String publicKey) throws Exception{
        SortedMap signParams = new TreeMap();
        signParams.putAll(context);
        publicKey = publicKey.replaceAll("\n", "");
        sign = sign.replaceAll(" ", "+");
        return RsaUtils.verify(JSON.toJSONString(signParams), sign, publicKey);
    }

    /**
     * 验签
     * @param data
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String sign, String publicKey) throws Exception{
        publicKey = publicKey.replaceAll("\n", "");
        sign = sign.replaceAll(" ", "+");
        return RsaUtils.verify(data, sign, publicKey);
    }

    /**
     * 验签
     * @param decryptData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(String decryptData, String publicKey) throws Exception{
        JSONObject context = JSONObject.parseObject(decryptData);
        SortedMap signParams = new TreeMap();
        signParams.putAll(context.getJSONObject("businessContext"));
        return verify(JSON.toJSONString(signParams), context.getJSONObject("businessHead").getString("sign"), publicKey);
    }

    /**
     * 加密数据
     * @param encryptedData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String encryptedData, String publicKey) throws Exception {
        publicKey = publicKey.replaceAll("\n", "");
        return RsaUtils.encrypt(encryptedData, publicKey);
    }

    /**
     * 解密数据
     * @param encryptedData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String privateKey) throws Exception {
        privateKey = privateKey.replaceAll("\n", "");
        encryptedData = encryptedData.replaceAll(" ", "+");
        return RsaUtils.decrypt(encryptedData, privateKey);
    }

    /**
     * 签名、组装businessHead和businessContext、加密
     *
     * @param businessHead
     * @param businessContext
     * @param privateKey
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String signAndEncryptToContext(JSONObject businessHead, JSONObject businessContext, String privateKey, String publicKey) throws Exception {
        businessHead.put("sign",sign(businessContext, privateKey));
        JSONObject context = new JSONObject();
        context.put("businessHead", businessHead);
        context.put("businessContext", businessContext);
        return encrypt(context.toString(), publicKey);
    }
}
