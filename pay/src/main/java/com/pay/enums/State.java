package com.pay.enums;
/**
*@author star
*@version 创建时间：2019年4月25日下午2:13:15
*/
public enum State {
	NORMAL(1,"正常"),
	DISABLE(2,"禁用");
	
	
	public static String getText(int code) {
		State[] p = values();
		for (int i = 0; i < p.length; ++i) {
			State type = p[i];
			if (type.getCode()==code)
				return type.getText();
		}
		return null;
	}
	
	public static int getCode(String code) {
		State[] p = values();
		for (int i = 0; i < p.length; ++i) {
			State type = p[i];
			if (type.getText().equals(code))
				return type.getCode();
		}
		return 0;
	}
	
	public static String[] getPerText(){
		String[] text = new String[values().length];
		State[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i]=p[i].getText();
		}
		return text;
	}

	private int code;

	private String text;

	private State(int code, String text) {
		this.code = code;
		this.text = text;
	}

	public int getCode() {
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
