package com.pay.enums;
/**
*@author star
*@version 创建时间：2019年3月29日下午5:56:49
*/
public enum CustomerState {
	NOTACTIVE(3, "未激活"), 
	NORMAL(1, "正常使用"),
	DISABLED(2, "禁用");
	
	public static String getText(Integer code) {
		CustomerState[] p = values();
		for (int i = 0; i < p.length; ++i) {
			CustomerState type = p[i];
			if (type.getCode()==code)
				return type.getText();
		}
		return null;
	}

	private int code;

	private String text;

	private CustomerState(int code, String text) {
		this.code = code;
		this.text = text;
	}

	public int getCode() {
		return this.code;
	}

	public String getText() {
		return this.text;
	}
}
