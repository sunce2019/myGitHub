package com.pay.enums;


/**商务号类型
*@author star
*@version 创建时间：2019年3月27日下午2:32:42
*/
public enum MessageModul {

	MODUL_ONE("MODUL_ONE","贵公司尾号(?<card>\\d{4,4})的账户(?<time>\\d+月\\d+日\\d+时\\d+分)(?<name>.*?)支付宝转账收入人民币(?<amount>.*?)元余额(?<balance>.*?)元对方户名(?<type>.*?)（中国）网络技术有限公司建设银行"),
	MODUL_TWO("MODUL_TWO","您尾号(?<card>\\d{4,4})的储蓄卡账户(?<time>\\d+月\\d+日\\d+时\\d+分)(?<type>.*?)提现收入人民币(?<amount>.*?)元活期余额(?<balance>.*?)元建设银行"),
	MODUL_THREE("MODUL_THREE","华融湘江银行您尾号为(?<card>\\d{4,4})的账户于(?<time>\\d+月\\d+日\\d+时\\d+分)(?<type>.*?)来账存入(?<amount>.*?)元余额(?<balance>.*?)元"),
	MODUL_FOUR("MODUL_FOUR","(?<name>.*?)(?<time>\\d+月\\d+日\\d+时\\d+分)向您尾号(?<card>\\d{4,4})的(理财卡|储蓄卡账户)(?<type>.*?)入账收入人民币(?<amount>.*?)元活期余额(?<balance>.*?)元建设银行"),
	MODUL_FIFTH("MODUL_FIFTH","贵公司尾号(?<card>\\d{4,4})的账户(?<time>\\d+月\\d+日\\d+时\\d+分)转账到银行卡收入人民币(?<amount>.*?)元余额(?<balance>.*?)元对方户名(?<type>.*?)支付科技有限公司建设银行"),
	MODUL_SIX("MODUL_SIX","中国农业银行(?<name>.*?)于(?<time>\\d+月\\d+日\\d{4,4})向您尾号(?<card>\\d{4,4})账户完成(?<type>.*?)入账交易人民币(?<amount>.*?)余额(?<balance>.*?)");
	
	public static String getText(String code) {
		MessageModul[] p = values();
		for (int i = 0; i < p.length; ++i) {
			MessageModul type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	private String code;

	private String text;

	private MessageModul(String code, String text) {
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
    
    public static MessageModul getType(String dataTypeCode){
    	for(MessageModul enums:MessageModul.values()){
    		if(enums.getCode().equals(dataTypeCode)){
    			return enums;
    		}
    	}
    	return null;
    }
}
