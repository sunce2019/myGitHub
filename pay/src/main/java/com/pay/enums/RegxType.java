package com.pay.enums;


/**短信正则匹配
*@author star
*@version 创建时间：2019年3月27日下午2:32:42
*/
public enum RegxType {
	CARD("(?<card>\\d{4,4})","尾号"),
	AMOUNT("(?<amount>.*?)","金额"),
	BALANCE("(?<balance>.*?)","余额"),
	NAME("(?<name>.*?)","姓名"),
	TIME("(?<time>\\d\\+月\\d\\+日\\d\\+时\\d\\+分)","月日时分格式时间"),
	TIME2("(?<time2>\\d\\+月\\d\\+日\\d{4,4})","月日时:分格式时间"),	
	TYPE("(?<type>.*?)","支付类型");

	public static String getText(String code) {
		RegxType[] p = values();
		for (int i = 0; i < p.length; ++i) {
			RegxType type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	private String code;

	private String text;

	private RegxType(String code, String text) {
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
		for(RegxType typeEnum : RegxType.values()){
			if(typeEnum.name().equals(type)){
				return true;
			}
		}
		return false;
	}
    
    public static RegxType getType(String dataTypeCode){
    	for(RegxType enums:RegxType.values()){
    		if(enums.getCode().equals(dataTypeCode)){
    			return enums;
    		}
    	}
    	return null;
    }
}
