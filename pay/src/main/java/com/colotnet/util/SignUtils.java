package com.colotnet.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import com.pay.util.RSAUtil;

public class SignUtils {

    static String BASE_PATH = "D:\\Downloads\\1000119";

    public static String signData(List<BasicNameValuePair> nvps, String path) throws Exception {
        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (BasicNameValuePair pair : nvps) {
            if (StringUtils.isNotBlank(pair.getValue())) {
                tempMap.put(pair.getName(), pair.getValue());
            }
        }
        StringBuffer buf = new StringBuffer();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append((String) tempMap.get(key)).append("&");
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        String signData = RSAUtil.signByPrivate(signatureStr, RSAUtil.readFile(BASE_PATH + path, "UTF-8"), "UTF-8");
        System.out.println("请求数据：" + signatureStr);
        System.out.println("请求数据：" + "&signature=" + signData);
        return signData;
    }

    public static String signData(Map<String, String> nvps, String path) throws Exception {
        TreeMap<String, String> tempMap = new TreeMap<String, String>(nvps);
        StringBuffer buf = new StringBuffer();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append((String) tempMap.get(key)).append("&");
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        String signData = RSAUtil.signByPrivate(signatureStr, RSAUtil.readFile(path, "UTF-8"), "UTF-8");
        System.out.println("请求数据：" + signatureStr);
        System.out.println("请求数据：" + "&signature=" + signData);
        return signData;
    }

    public static boolean verferSignData(String str, String path) {
        System.out.println("响应数据：" + str);
        String data[] = str.split("&");
        StringBuffer buf = new StringBuffer();
        String signature = "";

        for (int i = 0; i < data.length; i++) {
            String tmp[] = data[i].split("=", 2);
            if ("signature".equals(tmp[0])) {
                signature = tmp[1];
            } else {
                buf.append(tmp[0]).append("=").append(tmp[1]).append("&");
            }
        }
        String signatureStr = buf.substring(0, buf.length() - 1);
        System.out.println("验签数据：" + signatureStr);
        return RSAUtil.verifyByKeyPath(signatureStr, signature, BASE_PATH + path, "UTF-8");
    }

}
