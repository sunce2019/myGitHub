package com.pay.service;

import com.pay.model.Settings;

public interface BaseService {
	
	/**
	 * 支付成功
	 * @param business
	 * @param payOrder
	 * @throws Exception
	 */
	public boolean successfulPayment(int payorderId,int businessId,Double price)throws Exception;
	
	public Settings getSettings(Settings settings)throws Exception;
	
	/**
	 * 获取云闪付 二维码模式
	 * @param cardNo
	 * @return
	 * @throws Exception
	 */
	public int getCloudPattern(String cardNo)throws Exception;
	
}
