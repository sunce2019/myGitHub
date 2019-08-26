package com.pay.enums;


/**商务号类型
*@author star
*@version 创建时间：2019年3月27日下午2:32:42
*/
public enum PayType {
	AUTOMD5("AUTOMD5","传统MD5"),
	MD5HEX("MD5HEX","可逆MD5"),
	SHANRUBAO("SHANRUBAO","闪入宝"),
	XINGFA("XINGFA","鑫发"),
	KSK("KSK","KSK"),
	HENGTONG("HENGTONG","恒通"),
	TIANGQIANG("TIANGQIANG","天强聚合"),
	SUBAO("SUBAO","速宝支付"),
	ASAN("ASAN","阿三支付"),
	XIAOMEI("XIAOMEI","小美支付"),
	BABILUN("BABILUN","巴比伦"),
	HAIXING("HAIXING","海鑫支付"),
	LIRUI("LIRUI","黎睿支付"),
	KS("KS","KS支付"),
	BAIWEI("BAIWEI","百威支付"),
	GT("GT","GT支付"),
	JITANG("JITANG","鸡汤支付"),
	DINGDING("DINGDING","钉钉支付"),
	SANJIU("SANJIU","三九支付"),
	BABAO("BABAO","八宝支付"),
	SANLIUWU("SANLIUWU","三六五支付"),
	JITANG2("JITANG2","鸡汤支付2"),
	QUANQIU("QUANQIU","全球支付"),
	WANDING("WANDING","万鼎支付"),
	ONE4BANK("ONE4BANK","One4Bank"),
	XINCHUANGSHI("XINCHUANGSHI","新创世"),
	YILIAN("YILIAN","易联支付"),
	LONGFA("LONGFA","隆发支付"),
	SANQIERYI("SANQIERYI","3721支付"),
	GAOSHENG("GAOSHENG","高盛支付"),
	XINGRUBAO("XINGRUBAO","星入宝"),
	YONGXING("YONGXING","永兴支付"),
	XINCHUANGSHI2("XINCHUANGSHI2","旧创世"),
	HEPING("HEPING","和平支付"),
	WEIHUBAO("WEIHUBAO","维护宝"),
	GDD("GDD","GDD"),
	BAIJIA("BAIJIA","百佳支付"),
	YINGFUBAO("YINGFUBAO","盈付宝"),
	JUYOU("JUYOU","聚友支付"),
	HIPAY("HIPAY","HIPAY支付"),
	NIUNIU("NIUNIU","牛牛支付"),
	YUFU("YUFU","御付"),
	GEZI("GEZI","格子支付"),
	CHANGYIFU("CHANGYIFU","畅壹付"),
	QISHENG("QISHENG","旗胜支付"),
	SIHAI("SIHAI","四海支付"),
	QIDONGBAO("QIDONGBAO","启通宝"),
	REDBULL("REDBULL","红牛支付"), 
	PAY5566("PAY5566","5566支付");

	public static String getText(String code) {
		PayType[] p = values();
		for (int i = 0; i < p.length; ++i) {
			PayType type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	private String code;

	private String text;

	private PayType(String code, String text) {
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
