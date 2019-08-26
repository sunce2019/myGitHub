package com.pay.enums;


/**银行编码
*@author star
*@version 创建时间：2019年3月27日下午2:32:42
*/
public enum BankCode {
	CCB("CCB","建设银行"),
	ICBC("ICBC","工商银行"),
	HRXJB("HRXJB","华融湘江银行"),
	ABC("ABC","中国农业银行");

	public static String getText(String code) {
		BankCode[] p = values();
		for (int i = 0; i < p.length; ++i) {
			BankCode type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}

	private String code;

	private String text;

	private BankCode(String code, String text) {
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
    
    public static BankCode getType(String dataTypeCode){
    	for(BankCode enums:BankCode.values()){
    		if(enums.getCode().equals(dataTypeCode)){
    			return enums;
    		}
    	}
    	return null;
    }
}
