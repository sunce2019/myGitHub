package com.pay.auto;


/**
*@author star
*@version 创建时间：2019年6月26日下午3:16:36
*/
public class Parameter {
	
	private String name = "";	//字段名
	private String val = "";		//字段值
	private String valType = "VARCHAR";		//字段值的类型
	private String type = "FIXED";	//商户字段类型   默认给固定参数
	private int flag;	//是否参与签名  0：参与，1：不参与  默认参与
	private int urlFlag;	//url 类型是否urlencode
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getUrlFlag() {
		return urlFlag;
	}
	public void setUrlFlag(int urlFlag) {
		this.urlFlag = urlFlag;
	}
	
}
