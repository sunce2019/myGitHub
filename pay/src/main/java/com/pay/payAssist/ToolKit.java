package com.pay.payAssist;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.pay.util.StringUtils;

public class ToolKit {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	// 支付公钥
	public static final String PAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQKu0SzMmA82PaiINBOltfYdpdMMiZDG7P9ROxHW4LbLVT/j1HmPMKbSlBkGoDo2wGYUaS99lRg3uAmYG356VsmvkDFmqmorA2Ntx+WtFOcDdxd/CCAuqGk8S65k4BHsXxKzWo2myWlXFbM29woO/Dk/o12+ErJ0wHkv4zaSbPtQIDAQAB";

	// 代付公钥
	public static final String REMIT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbjd90wlu0EpcD09/oqCksBX6ZxTgqZiv5nqf1TOoRU3pgXxmx0vCaeF+C5Kosn/FCFQW55yGYk+oZlDy3FApd2aFwzku+tV733pIaTEmK0K1qx62aE5EFc5yL1x0jj+HkxloqtKkNJqB1NBgcWsPF5YByX1e2cSe6K2yyUw5afwIDAQAB";
	// 商户私钥
	public static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIFx2KSGemoMJEmKdAHz3tz3ikMAeq3eIyyvRz85My0FKIDDwdU3ighGE2vyEkf2Zt7dkw3EbdEcfiU2xsWmmRSiAaqidUTzb+0brrIleoMZQ4ENMpJwjLdK1jM3dm+wB0+SXMc4QJfWkjOcy9rgkT7HJprKMf/8EUBr6hj03Zh7AgMBAAECgYB6hJT29Em9Qgy+hotSpc5U+v8kd7mmA1DrpGMdFji37d/uMFqwQsclGZ6cVxyyfCfV3YaoSzld6UgEVRpCTXMpV7TmbhiPikV9PtaCUVPkZmYNOPgxqCyjnZPe+AC3Mz+DUw8KcrQntMGaNv9B5NFbeQZ4ecr7NC2Vjf1n0OuqsQJBAMqM/v4HPVULN13cQ9/DY8yWxT6H3SCKeAnZoJxFxWTeP9gAOdisusEUnau1LskQxMg0PFrhFsLK06rWpfzo1ncCQQCjmkihcA/Pw9cP08NdPRIItXfOWOqESfFb+sNF0c3xbW1zWapUZirZnDIwV3ik1MYwCLCgte2xUa0c6kD7B1sdAkB24l7gNtuWyOiMXLYx5frzoCLRWuk9Kkjeby/HyWPcml4ap7dMJ8XNgg4xNDzror39TDuIk8jKOEYYZbgzQ25tAkAwJFVdFBHDDJj7clAZ3r+wyl2P3gBcnzmj3F2b7QoSEiZT/D2wCkRvtpPaP9Mjhe0v806ua/qe5C7xxEkM1XRNAkB6dOwfs5IXRTMwJnX0/e4tyMtgWjsUv1iAWMcLfcM92aHiu+mwvMmXV85tkAfi7M3IKGKz2lmojl/Q9UHjeCac";

	// public static int blockSize = 128;
	// 非对称密钥算法
	public static final String KEY_ALGORITHM = "RSA";
	public final static String CHARSET = "UTF-8";

	/**
	 * 获取响应报文
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private static String getResponseBodyAsString(InputStream in) throws Exception {
		try {
			BufferedInputStream buf = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			StringBuffer data = new StringBuffer();
			int readDataLen;
			while ((readDataLen = buf.read(buffer)) != -1) {
				data.append(new String(buffer, 0, readDataLen, CHARSET));
			}
			return data.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		}
	}

	/**
	 * 提交请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String request(String url, String params) throws Exception {
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(30 * 1000);
		conn.setReadTimeout(30 * 1000);
		// conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
		// conn.setRequestProperty("Accept","*/*");
		conn.setRequestProperty("Charset", CHARSET);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(params.toString().getBytes(CHARSET));
		outStream.flush();
		outStream.close();
		return getResponseBodyAsString(conn.getInputStream());
	}

	public static String getPostResponse(String url, Map<String, String> parmMap) throws Exception {
		{
			String result = "";
			PostMethod post = new PostMethod(url);
			ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
			Protocol.registerProtocol("https", new Protocol("https", fcty, 443));

			HttpClient client = new HttpClient();
			Iterator it = parmMap.entrySet().iterator();
			NameValuePair[] param = new NameValuePair[parmMap.size()];
			int i = 0;
			while (it.hasNext()) {
				Map.Entry parmEntry = (Map.Entry) it.next();
				param[i++] = new NameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue());
			}

			post.setRequestBody(param);
			try {
				int statusCode = client.executeMethod(post);

				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header locationHeader = post.getResponseHeader("location");
					String location = "";
					if (locationHeader != null) {
						location = locationHeader.getValue();
						result = ToolKit.getPostResponse(location, parmMap);// 用跳转后的页面重新请求�??
					}
				} else if (statusCode == HttpStatus.SC_OK) {
					result = post.getResponseBodyAsString();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				post.releaseConnection();
			}
			return result;
		}
	}

	/**
	 * 提交请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String requestJson(String strURL, String params) throws Exception {
		BufferedReader reader = null;
		URL url = new URL(strURL);// 创建连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod("POST"); // 设置请求方式
		// connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
		connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
		connection.connect();
		// 一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
		out.append(params);
		out.flush();
		out.close();
		// 读取响应
		reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String line;
		String res = "";
		while ((line = reader.readLine()) != null) {
			res += line;
		}
		reader.close();

		return res; // 自定义错误信息
	}

	/**
	 * MD5加密
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	public final static String MD5(String s, String encoding) {
		try {
			byte[] btInput = s.getBytes(encoding);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 转换成Json格式
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToJsonobj(Map<String, Object> map) {
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		StringBuffer json = new StringBuffer();
		json.append("{");
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			json.append("\"").append(key).append("\"");
			json.append(":");
			json.append("\"").append(value).append("\"");
			if (it.hasNext()) {
				json.append(",");
			}
		}
		json.append("}");
		return json.toString();
	}

	public static String mapToJson(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		StringBuffer json = new StringBuffer();
		json.append("{");
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			json.append("\"").append(key).append("\"");
			json.append(":");
			json.append("\"").append(value).append("\"");
			if (it.hasNext()) {
				json.append(",");
			}
		}
		json.append("}");
		return json.toString();
	}

	/**
	 * 生成随机字符
	 * 
	 * @param num
	 * @return
	 */
	public static String randomStr(int num) {
		char[] randomMetaData = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
				'5', '6', '7', '8', '9' };
		Random random = new Random();
		String tNonceStr = "";
		for (int i = 0; i < num; i++) {
			tNonceStr += (randomMetaData[random.nextInt(randomMetaData.length - 1)]);
		}
		return tNonceStr;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data待加密数据
	 * @param key       密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
		byte[] key = Base64.getDecoder().decode(publicKey);
		// 实例化密钥工厂
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 密钥材料转换
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
			// 产生公钥
			PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
			// 数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			int blockSize = cipher.getOutputSize(data.length) - 11;
			return doFinal(data, cipher, blockSize);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data 待解密数据
	 * @param key  密钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String privateKeyValue) {
		byte[] key = Base64.getDecoder().decode(privateKeyValue);
		try {
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成私钥
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int blockSize = cipher.getOutputSize(data.length);
			return doFinal(data, cipher, blockSize);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密解密共用核心代码，分段加密解密
	 * 
	 * @param decryptData
	 * @param cipher
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static byte[] doFinal(byte[] decryptData, Cipher cipher, int blockSize)
			throws IllegalBlockSizeException, BadPaddingException, IOException {
		int offSet = 0;
		byte[] cache = null;
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		while (decryptData.length - offSet > 0) {
			if (decryptData.length - offSet > blockSize) {
				cache = cipher.doFinal(decryptData, offSet, blockSize);
			} else {
				cache = cipher.doFinal(decryptData, offSet, decryptData.length - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * blockSize;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	public static String sendGet(String url, String param) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			throw new Exception();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * DES加密
	 * 
	 * @param value   要加密的字符串
	 * @param orderNo 订单号（加密盐）
	 * @return
	 */
	public static String desEncrypt(String value, String orderNo) {
		String result = "";
		try {
			value = java.net.URLEncoder.encode(value, "utf-8");
			orderNo = trancateRight(orderNo, 8);
			result = toHexString(encrypt(value, orderNo)).toUpperCase();
		} catch (Exception ex) {
			logger.error("des encrypt is error: ", ex);
			return "";
		}
		return result;
	}

	private static byte[] encrypt(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("ASCII"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("ASCII"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return cipher.doFinal(message.getBytes("UTF-8"));
	}

	private static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}
		return hexString.toString();
	}

	/**
	 * 去掉字符串右边的字符
	 * 
	 * @param value 字符串
	 * @param len   去掉字符的长度
	 * @return
	 */
	public static String trancateRight(String value, int len) {
		if (value == null || len < 1) {
			return "";
		}

		if (value.length() < len) {
			return value;
		}

		return StringUtils.substring(value, 0, len);
	}

}
