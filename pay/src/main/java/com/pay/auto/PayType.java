package com.pay.auto;
/**
*@author star
*@version 创建时间：2019年6月28日下午3:19:40
*/
public class PayType {
	
	private String values;
	private String typeCode;
	private String jumpType = "FROMSUB";
	
	public PayType() {}
	public PayType(String values,String typeCode) {
		this.values = values;
		this.typeCode = typeCode;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getJumpType() {
		return jumpType;
	}
	public void setJumpType(String jumpType) {
		this.jumpType = jumpType;
	}
	
}
