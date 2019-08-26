
package com.pay.enums;

import com.pay.auto.enums.SingType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
*@author star
*@version 创建时间：2019年3月28日上午11:06:06
*/
public enum PayTran {
	ZFB("ZFB","支付宝扫码"),		//支付宝-支付宝扫码支付
	ZFB_WAP("ZFB_WAP","支付宝跳转"),		//支付宝WAP-手机端跳转支付宝支付
	ZFB_WAP_YS("ZFB_WAP_YS","支付宝原生"),		//支付宝WAP-原生	
	ZFB_HB("ZFB_HB","支付宝红包"),		//支付宝红包
	ZFB_HB_H5("ZFB_HB_H5","支付宝红包H5"),		//支付宝红包H5-手机端跳转支付宝红包支付
	ZFB_BANK("ZFB_BANK","支付宝转银行"),	//支付宝转银行 我方第三方支付转银行卡专用类型
	WX("WX","微信扫码"),			//微信-微信扫码支付
	WX_YS("WX_YS","微信扫码原生"),		//微信原生扫码
	WX_WAP("WX_WAP","微信跳转"),		//微信WAP-手机端跳转微信支付
	WX_H5("WX_H5","微信H5"),		//微信H5-手机端跳转微信支付
	WX_BANK("WX_BANK","微信转银行"),		//微信转银行
	QQ("QQ","QQ钱包扫码"),			//QQ钱包-QQ钱包扫码支付
	QQ_WAP("QQ_WAP","QQ跳转"),		//QQ钱包WAP-手机端跳转QQ钱包支付
	JD("JD","京东扫码"),			//京东钱包-京东钱包扫码支付
	JD_WAP("JD_WAP","京东跳转"),		//京东钱包WAP-手机端跳转京东钱包支付
	UNION_WALLET("UNION_WALLET","银联扫码"),		//银联钱包(云闪付)-银联钱包扫码支付
	UNION_WAP("UNION_WAP","银联在线支付"),		//银联WAP-手机端银联在线支付
	WANGGUAN("WANGGUAN","网关支付"),		//网关支付
	YSF("YSF","云闪付"),   //云闪付
	WX_MD("WX_MD","微信买单"),   //微信买单
	ALI_FM("ALI_FM","支付宝固码");   //支付宝付码
	
	public static String getText(String code) {
		PayTran[] p = values();
		for (int i = 0; i < p.length; ++i) {
			PayTran type = p[i];
			if (type.getCode().equals(code))
				return type.getText();
		}
		return null;
	}
	
	public static String getCode(String text) {
		PayTran[] p = values();
		for (int i = 0; i < p.length; ++i) {
			PayTran type = p[i];
			if (type.getText().equals(text))
				return type.getCode();
		}
		return null;
	}
	
	public static String[] getPerText(){
		String[] text = new String[values().length];
		PayTran[] p = values();
		for (int i = 0; i < p.length; ++i) {
			text[i]=p[i].getText();
		}
		return text;
	}

	private String code;

	private String text;

	private PayTran(String code, String text) {
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
			if(typeEnum.getText().equals(type)){
				return true;
			}
		}
		return false;
	}
    
    public static String toJson(){
        JSONArray jsonArray = new JSONArray();
        for (SingType e : SingType.values()) {
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
    
}
