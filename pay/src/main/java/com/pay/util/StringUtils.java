package com.pay.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class StringUtils extends org.apache.commons.lang.StringUtils {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static final DecimalFormat df = new DecimalFormat("#.00");
	public static final String PATH = "C:\\platform";
	/**
	 * 无需登录 排除api
	 */
	public static final String[] ALLOw = new String[] { "showParam.do", "testUeditor.do", "login.do",
			"discountsInfo.do", "collectSynAuto.do", "discountsPlan.do", "addDiscounts.do", "discountsSpeldLoad.do",
			"updateDiscounts.do", "discounts.do", "discountsList.do", "uploadImg.do", "getVerifyCode.do",
			"activeDetailInfo.do", "pageActiveList.do", "ueditor", "getCardNo.do", "testApi.do", "imgApi.do",
			"testysf.do", "uploadYSF.do", "req.do", "callBack", "success.do", "error.do", "queryOrder.do", "alipay",
			"alipayToBank", "showBank", "alipay", "favicon.ico", "fcApi.do", "test.do","sms/jxMessage.do","sms/getRegx.do","sms/getMoBan.do" };

	/**
	 * 权限排除功能。需要登录的功能，所有角色都能访问的api
	 */
	public static final String[] ALLOFILTER = new String[] { "manualCallBackGame2.do", "checkGoogle.do",
			"groupsDetails.do", "updateDetails.do", "wechatDetails.do", "cloudDetails.do", "bankDetails.do",
			"userDetails.do", "customerDetails.do", "businessDetails.do", "loadBank.do", "auto.do", "getAuto.do",
			"/hcpay/ueditor/l", "ueditor.do", "activeModelLoad.do", "getVerifyCode.do", "autoModelLoad.do",
			"getLoginNames.do", "groupsLists.do", "menuList.do", "index.do", "getOrderDate.do", "getDate.do",
			"getUsersMenu.do", "getPayTypeApi.do"};

	public static String getThisDate() {
		return SDF.format(new Date());
	}

	public static int unAbs(int a) {
		return (a > 0) ? -a : a;
	}

	public static boolean lognNamePasswordCheck(String s) {
		String regex = "^[0-9A-Za-z]{3,8}$";
		return s.matches(regex);
	}

	public static JsonConfig getJsonConfig(String[] s) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); // 自动为我排除circle。
		jsonConfig.setExcludes(s); // 设置转换中忽略的属性
		return jsonConfig;
	}

	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(unAbs(30));
	}

	public static Double getRandom(int min, int max) {
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return new Double(s);

	}

	public static Date afterHH(int num, Date date) {
		long curren = date.getTime();
		curren += num * 60 * 60 * 1000;
		Date data1 = new Date(curren);
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		System.out.println(sdf.format(data1));
		return new Date(curren);
	}

	public static String getCode() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddss");
		return sdf.format(new Date()) + getRandomNumber(3);
	}

	public static int getRandomNumber(int n) {
		int temp = 0;
		int min = (int) Math.pow(10, n - 1);
		int max = (int) Math.pow(10, n);
		Random rand = new Random();
		while (true) {
			temp = rand.nextInt(max);
			if (temp >= min)
				break;
		}

		return temp;
	}

	/**
	 * URL 解码
	 *
	 * @return String
	 * @author lifq
	 * @date 2015-3-17 下午04:09:51
	 */
	public static String getURLDecoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * URL 转码
	 *
	 * @return String
	 * @author lifq
	 * @date 2015-3-17 下午04:10:28
	 */
	public static String getURLEncoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getBank(String a) {
		String cardNo = "";
		for (int i = 0; i < a.length(); i++) {
			if (i <= 5 || a.length() - 5 < i) {
				cardNo += a.substring(i, i + 1);
			} else {
				cardNo += "*";
			}
		}
		return cardNo;
	}

}
