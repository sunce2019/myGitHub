package com.pay.service;
/**
*@author star
*@version 创建时间：2019年3月29日下午4:31:15
*/

import java.util.List;

import com.pay.model.Business;

public interface BusinessService {
	
	/**
	 * 查询商户号
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Business getBusiness(Business business)throws Exception;
	
	/**
	 * 获取商户号
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Business getBusiness(String orderType,Double price,Integer customer_id,int vip)throws Exception;
	
	/**
	 * 获取商户号
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public void updateOnekey(String code,int flag)throws Exception;
	
	
	/**
	 * 查询支付类型是否没有最后一个
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public boolean getBusinessTypeFlag(int id,String orderType)throws Exception;
	
	/**
	 * 修改增加商户
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Business saveOrUpdateBusiness(Integer id,String uid,String orderType,Double maxLimit,Integer flag,Double range_min,Double range_max,String fixed_price,String uids,String token,Integer customer,String public_key,String private_key,String[] bankId,String callbackip,String apiUrl,Integer vip,Integer bankPattern,boolean openflag,String name,String payCode,String jumpType)throws Exception;}
