package com.pay.service;

import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.model.Users;
import com.pay.util.ReturnBan;
import com.pay.vo.PaymentApi;

/**
 * @author star
 * @version 创建时间：2019年3月28日下午6:28:35
 */
public interface PayOrderService {
	/**
	 * 创建订单
	 * 
	 * @param paymentApi
	 * @return
	 * @throws Exception
	 */
	public PayOrder addPayOrder(PaymentApi paymentApi, Business business) throws Exception;

	/**
	 * 查询初始化订单
	 * 
	 * @param orderNumber
	 * @return
	 * @throws Exception
	 */
	public PayOrder getPayOrder(PayOrder payOrder) throws Exception;

	public PayOrder getPayOrder2(PayOrder payOrder) throws Exception;

	/**
	 * 修改订单备注
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void updatePayOrder(PayOrder payOrder, String remarks) throws Exception;

	/**
	 * 修改订单备注
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void updatePayOrder(PayOrder payOrder) throws Exception;

	/**
	 * API错误信息打印
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void updatePayOrder(int id, String remarks) throws Exception;

	/**
	 * 查询订单
	 * 
	 * @param orderNumber
	 * @return
	 * @throws Exception
	 */
	public PayOrder payOrder(PayOrder payOrder) throws Exception;

	/**
	 * 查询订单
	 * 
	 * @param orderNumber
	 * @return
	 * @throws Exception
	 */
	public boolean queryPay(String time, String gameOrderNumber) throws Exception;

	/**
	 * 回调中博
	 * 
	 * @param id
	 * @param users
	 * @param manual 是否手动
	 * @return
	 * @throws Exception
	 */
	public ReturnBan upperScore(int id, Users users, boolean manual, Double upperMoney) throws Exception;

	/**
	 * 手动回调游乐场
	 * 
	 * @param id
	 * @param users
	 * @param manual 是否手动
	 * @return
	 * @throws Exception
	 */
	public ReturnBan manualCallBackGame2(int id, Double upperMoney, String name) throws Exception;

	/**
	 * 获取支付宝转银行卡累计金额。
	 * 
	 * @param paymentApi
	 * @throws Exception
	 */
	public Double updateRecorDamount(PaymentApi paymentApi) throws Exception;

	/**
	 * 手动上分修改订单状态
	 * 
	 * @throws Exception
	 */
	public boolean updatePay(int id) throws Exception;

	/**
	 * 转银行卡类 增加转账成功次数
	 * 
	 * @param payOrder
	 */
	public void BankCount(PayOrder payOrder) throws Exception;

}
