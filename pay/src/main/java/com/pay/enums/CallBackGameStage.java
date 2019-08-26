package com.pay.enums;

/**
 * 支付状态
*@author star
*@version 创建时间：2019年3月29日下午3:49:56
*/
public enum CallBackGameStage {
	INIT(1, "创建订单成功"), 
	MOVE(2, "回调中"),
	SUCCESS(3, "回调成功"),
	FAIL(4, "回调失败");
	public static String getText(Integer code) {
		CallBackGameStage[] p = values();
		for (int i = 0; i < p.length; ++i) {
			CallBackGameStage type = p[i];
			if (type.getCode()==code)
				return type.getText();
		}
		return null;
	}

	private int code;

	private String text;

	private CallBackGameStage(int code, String text) {
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
