package com.pay.enums;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author star
 * @version 创建时间：2019年6月26日下午3:07:06
 */
public enum ConfigCode {
	NOTIFYURL("NOTIFYURL", "自动对接异步地址"), NOTIFYVIEWURL("NOTIFYVIEWURL", "自动对接同步地址"), AUTHCODE("AUTHCODE", "谷歌验证器开关"),
	CORSDIMAIN("CORSDIMAIN", "跨域域名白名单"), GOOGLETIME("GOOGLETIME", "谷歌免输入时间,单位分钟"),
	CALLBACKTIME("CALLBACKTIME", "回调处理多少分钟内,单位分钟"), MANUAL("MANUAL", "第三方支付手动上分多少分钟内");
	public static String getText(String code) {
		ConfigCode[] p = values();
		for (int i = 0; i < p.length; ++i) {
			ConfigCode type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	public static String getCode(String code) {
		ConfigCode[] p = values();
		for (int i = 0; i < p.length; ++i) {
			ConfigCode type = p[i];
			if (type.getText().equals(code))
				return type.getCode();
		}
		return null;
	}

	public static String[] getPerText() {
		String[] text = new String[values().length];
		ConfigCode[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i] = p[i].getText();
		}
		return text;
	}

	private String code;

	private String text;

	private ConfigCode(String code, String text) {
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return this.code;
	}

	public String getText() {
		return this.text;
	}

	public static String toJson() {
		JSONArray jsonArray = new JSONArray();
		for (ConfigCode e : ConfigCode.values()) {
			JSONObject object = new JSONObject();
			object.put("code", e.getCode());
			object.put("text", e.getText());
			jsonArray.add(object);
		}
		return jsonArray.toString();
	}

	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("text", text);
		return object.toString();
	}

	public static boolean contains(String type) {
		for (PayTran typeEnum : PayTran.values()) {
			if (typeEnum.getText().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
