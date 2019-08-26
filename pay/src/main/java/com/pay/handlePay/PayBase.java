package com.pay.handlePay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.vo.PaymentApi;

/**
*@author star
*@version 创建时间：2019年3月27日下午1:24:00
*/
public interface PayBase {
	/**
	 * 请求支付
	 * @return
	 * @throws Exception
	 */
	public void handleMain(Map<String, String> map,Business business,PayOrder payOrder)throws Exception;
	
	/**
	 * 第三方支付回调函数
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> payCallback(HttpServletRequest request)throws Exception;
	
	/**
	 * 查询订单是否已支付
	 * @param payOrder
	 * @param business
	 * @return
	 * @throws Exception
	 */
	public boolean payQuery(Map<String,String> map,Business business,PayOrder payOrder) throws Exception;
	
	/**
	 * 参数加密函数。
	 * @param map
	 * @param business
	 * @param paymentApi
	 * @throws Exception
	 */
	public void signature(Map<String,String> map,Business business,PayOrder payOrder)throws Exception;
	
	/**
	 * 支付方式类型转换
	 * @param business
	 */
	public String typeConversion(PayOrder payOrder)throws Exception;
	
	/**
	 * 获取支付类型
	 * @return
	 * @throws Exception
	 */
	public String[] getPayType()throws Exception;
	
}
