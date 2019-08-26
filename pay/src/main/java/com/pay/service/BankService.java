package com.pay.service;

import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.Business;
import com.pay.vo.PaymentApi;

public interface BankService {
	
	/**
	 * 获取银行卡 今日额度使用最低的卡
	 * @param paymentApi
	 * @param business
	 * @throws Exception
	 */
	public void getBankCode(PaymentApi paymentApi,Business business) throws Exception;
	
	
	
	public Bank getBank(Bank bank)throws Exception;
	
	
	public BankBase getCloudflashover(BankBase bankBase,Class classs)throws Exception;
	
	
	
}
