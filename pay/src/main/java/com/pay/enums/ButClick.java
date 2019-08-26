package com.pay.enums;
/**
*@author star
*@version 创建时间：2019年6月24日下午5:23:28
*/
public enum ButClick {
	
	ONEOPEN("ONEOPEN","一键开启"),
	ONECLOSE("ONECLOSE","一键关闭"),
	ADDMERC("ADDMERC","新增商户页面"),
	UPDMERC("UPDMERC","编辑商户页面"),
	TESTBUS("TESTBUS","测试通道"),
	EXPORT("EXPORT","订单导出"),
	UPSO("UPSO","订单上分"),
	BACKGAME("BACKGAME","回调游戏"),
	QUERYAPI("QUERYAPI","查看API"),
	ADDCUST("ADDCUST","新增对接页面"),
	UPDCUS("UPDCUS","编辑对接页面"),
	ADDUSER("ADDUSER","新增账户页面"),
	UPDUSER("UPDUSER","编辑账户页面"),
	DELETEIP("DELETEIP","删除ip"),
	ADDIP("ADDIP","新增ip页面"),
	UPDBANK("UPDBANK","编辑银行页面"),
	DELBANK("DELBANK","删除银行"),
	ADDBANK("ADDBANK","新增银行页面"),
	ADDGROUPS("ADDGROUPS","新增角色页面"),
	UPDGROUPS("UPDGROUPS","编辑角色页面"),
	DELGROUPS("DELGROUPS","删除角色"),
	UPDACTIVE("UPDACTIVE","活动编辑"),	
	DELACTIVE("DELACTIVE","活动删除"),
	UPDSYSTCONFIG("UPDSYSTCONFIG","编辑配置页面"),
	UPDAUTO("UPDAUTO","编辑对接页面"),
	SYAUTO("SYAUTO","同步"),
	ADDAUTO("ADDAUTO","新增对接页面"), 	
	UPDDISCOUNT("UPDDISCOUNT","审核优惠"),
	DELDISCOUNT("DELDISCOUNT","删除优惠"),	
	UPDSMS("UPDSMS","修改模板"),
	DELSMS("DELSMS","删除模板");

	public static String getText(String code) {
		ButClick[] p = values();
		for (int i = 0; i < p.length; ++i) {
			ButClick type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	private String code;

	private String text;

	private ButClick(String code, String text) {
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
			if(typeEnum.name().equals(type)){
				return true;
			}
		}
		return false;
	}
    
    public static PayType getType(String dataTypeCode){
    	for(PayType enums:PayType.values()){
    		if(enums.getCode().equals(dataTypeCode)){
    			return enums;
    		}
    	}
    	return null;
    }
}
