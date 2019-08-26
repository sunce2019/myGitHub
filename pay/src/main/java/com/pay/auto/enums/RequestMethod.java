package com.pay.auto.enums;

import com.pay.enums.PayTran;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 值属性
*@author star
*@version 创建时间：2019年6月26日下午3:07:06
*/
public enum RequestMethod {
	FRPOSTFROM("FRPOSTFROM","前端POST表单提交"),
	AFPOSTFROM("AFPOSTFROM","后端POST表单提交"),
	AFPOST("AFPOST","后端POST提交");
	
	public static String getText(String code) {
		RequestMethod[] p = values();
		for (int i = 0; i < p.length; ++i) {
			RequestMethod type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}
	
	public static String getCode(String code) {
		RequestMethod[] p = values();
		for (int i = 0; i < p.length; ++i) {
			RequestMethod type = p[i];
			if (type.getText().equals(code))
				return type.getCode();
		}
		return null;
	}
	
	public static String[] getPerText(){
		String[] text = new String[values().length];
		RequestMethod[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i]=p[i].getText();
		}
		return text;
	}

	private String code;

	private String text;

	private RequestMethod(String code, String text) {
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return this.code;
	}

	public String getText() {
		return this.text;
	}
	
	public static String toJson(){
        JSONArray jsonArray = new JSONArray();
        for (RequestMethod e : RequestMethod.values()) {
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
	
    public static boolean contains(String type){
		for(PayTran typeEnum : PayTran.values()){
			if(typeEnum.getText().equals(type)){
				return true;
			}
		}
		return false;
	}
}
