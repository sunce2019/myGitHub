package com.pay.auto.enums;

import com.pay.enums.PayTran;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
*@author star
*@version 创建时间：2019年6月26日下午3:20:18
*/
public enum ParameType {
	FIXED("FIXED","固定参数"),
	UID("UID","商户号"),
	TOKEN("TOKEN","商户秘钥"),
	ORDERNO("ORDERNO","订单号"),
	AMOUNT("AMOUNT","金额"),
	PAYTYPE("PAYTYPE","支付类型"),
	NOTIFYURL("NOTIFYURL","异步通知地址"),
	NOTIFYVIEWURL("NOTIFYVIEWURL","同步通知地址"),
	USERIP("USERIP","用户IP"),
	USERID("USERID","用户账户"),
	DATESTR("DATESTR","时间字符yyyy-MM-dd HH:mm:ss"),
	DATESTR2("DATESTR2","时间字符yyyyMMddHHmmss"),
	DATESTR2SSS("DATESTR2SSS","时间字符yyyyMMddHHmmssSSS"),
	DATE10("DATE13","时间戳10位"),
	DATE13("DATE13","时间戳13位"),
	RANDOMINT("RANDOMINT","随机整数"),
	RANDOMSTR("RANDOMSTR","随机字符串"),
	BACKSUC("BACKSUC","回调判断成功字段"),
	SIGN("SIGN","签名");
	
	public static String getText(String code) {
		ParameType[] p = values();
		for (int i = 0; i < p.length; ++i) {
			ParameType type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}
	
	public static String getCode(String code) {
		ParameType[] p = values();
		for (int i = 0; i < p.length; ++i) {
			ParameType type = p[i];
			if (type.getText().equals(code))
				return type.getCode();
		}
		return null;
	}
	
	public static String toJson(){
        JSONArray jsonArray = new JSONArray();
        for (SingType e : SingType.values()) {
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
        object.put("code",code);
        object.put("text",text);
        return object.toString();
    }
	
	public static String[] getPerText(){
		String[] text = new String[values().length];
		ParameType[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i]=p[i].getText();
		}
		return text;
	}

	private String code;

	private String text;

	private ParameType(String code, String text) {
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return this.code;
	}

	public String getText() {
		return this.text;
	}
	
    public static boolean contains(String type){
		for(PayTran typeEnum : PayTran.values()){
			if(typeEnum.getText().equals(type)){
				return true;
			}
		}
		return false;
	}
}
