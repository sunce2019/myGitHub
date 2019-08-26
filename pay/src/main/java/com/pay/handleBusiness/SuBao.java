package com.pay.handleBusiness;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;

/**
 * 速宝支付，我们自己系统的 支付宝转银行卡 支付通道
*@author star
*@version 创建时间：2019年4月17日下午4:54:56
*/
@Repository
public class SuBao extends Base implements PayBase {

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean payQuery(Map<String,String> map,Business business,PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] {PayTran.ZFB_BANK.getText(),PayTran.WX_BANK.getText(),PayTran.YSF.getText(),PayTran.WX_MD.getText()};
	}

}
