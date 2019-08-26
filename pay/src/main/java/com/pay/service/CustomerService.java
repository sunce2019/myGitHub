package com.pay.service;
/**
*@author star
*@version 创建时间：2019年3月29日下午6:14:15
*/

import com.pay.enums.PayTran;
import com.pay.model.Customer;

public interface CustomerService {
	
	/**
	 * 添加用户
	 * @param customer
	 * @return 
	 * @throws Exception
	 */
	public void addCustomer(Customer customer)throws Exception;
	
	/**
	 * 查询用户
	 * @param customer
	 * @return
	 * @throws Exception
	 */
	public Customer getCustomer(Customer customer)throws Exception;
	
	/**
	 * 查询支付宝转用户 微信转银行
	 * @param payTran
	 * @return
	 * @throws Exception
	 */
	public Customer getCustomer(PayTran payTran)throws Exception;
	
	/**
	 * 查询用户是否有权限
	 * @param userid
	 * @param customerid
	 * @return
	 * @throws Exception
	 */
	public boolean userJurisdiction(int userid,int customerid)throws Exception;
	
}
