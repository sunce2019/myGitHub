package com.pay.enums;
/**
*@author star
*@version 创建时间：2019年6月8日下午4:39:16
*/
public enum RedisKey {
	YSF_ADD("YSF_ADD","云闪付创建订单key前缀"),
	YSF_OSS("YSF_OSS","云闪付创建订单二维码生成完成前缀");
	
	
	public static String getText(String code) {
		RedisKey[] p = values();
		for (int i = 0; i < p.length; ++i) {
			RedisKey type = p[i];
			if (type.getCode()==code)
				return type.getText();
		}
		return null;
	}
	
	public static String getCode(String code) {
		RedisKey[] p = values();
		for (int i = 0; i < p.length; ++i) {
			RedisKey type = p[i];
			if (type.getText().equals(code))
				return type.getCode();
		}
		return "";
	}
	
	public static String[] getPerText(){
		String[] text = new String[values().length];
		RedisKey[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i]=p[i].getText();
		}
		return text;
	}

	private String code;

	private String text;

	private RedisKey(String code, String text) {
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
		for(RedisKey typeEnum : RedisKey.values()){
			if(typeEnum.getText().equals(type)){
				return true;
			}
		}
		return false;
	}
}
