package com.pay.util;

import com.pay.enums.PayTran;

/**
*@author star
*@version 创建时间：2019年6月3日下午5:54:49
*/
public class PayUtils {
	
	/**
	 * 判断是否是 自营支付类
	 * @param orderType
	 * @return
	 */
	public static boolean isOwnPay(String orderType) {
		if(!orderType.equals(PayTran.ZFB_BANK.getText()) && 
				!orderType.equals(PayTran.WX_BANK.getText())&&
				!orderType.equals(PayTran.YSF.getText())&&
				!orderType.equals(PayTran.WX_MD.getText())&&
			    !orderType.equals(PayTran.ALI_FM.getText())) {
			return false;
		}
		return true;
	}
}
