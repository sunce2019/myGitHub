package com.pay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
*@author star
*@version 创建时间：2019年3月27日下午2:12:04
*/
public class Encryption {
	
	/**
	 * php 不设置编码使用
	 * @param OrderNo
	 * @param charset
	 * @return
	 */
	public static String md5_32(String OrderNo,String charset) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try {
            	if(!StringUtils.isBlank(charset)) {
            		md.update(URLEncoder.encode(OrderNo, "UTF-8").getBytes());
            	}else {
            		md.update(OrderNo.getBytes());
            	}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            byte b[] = md.digest();
 
            int i;
 
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
 
            re_md5 = buf.toString();
 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
	
	/**
	 * 可逆加密
	 * @param str
	 * @return
	 */
	public static String md5Hex(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bs = md5.digest(datas);
        String result = new String(new Hex().encode(bs));
        return result;
    }
	
	public static String sign(String text,String charset) {
    	try
    	{
	    	 MessageDigest md = MessageDigest.getInstance("MD5");
	         md.update(text.getBytes(charset));
	         byte b[] = md.digest();
	         int i;
	         StringBuffer buf = new StringBuffer("");
	         for (int offset = 0; offset < b.length; offset++) {
	          i = b[offset];
	          if (i < 0)
	           i += 256;
	          if (i < 16)
	           buf.append("0");
	          buf.append(Integer.toHexString(i));
         }
         return buf.toString();
        } catch (Exception e) {
         e.printStackTrace();
        }
    	return null;
    }
	
	public static String MD5Encoder(String s) {
        try {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = ((int) md[i]) & 0xff;
                if (val < 16){
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
	
	/***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
 
    }
 
    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){
 
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
 
    }
    
    public static void main(String[] args) {
    	String s = "orderid=1000001878524266&result=1&amount=47.75&systemorderid=W19041318175847749801802&completetime=20190413181956&key=2b0af24ea3f64dcfd3edfc883eb588cf";
    	//6b95e40404a1380e70350e6068442c83

    	System.out.println(md5_32(s,""));
    	System.out.println(md5_32(s,"UTF-8"));
    	System.out.println(sign(s,"UTF-8"));
	}
 

}
