package com.pay.model;

import java.io.Serializable;

import javax.persistence.Entity;

/**
*@author star
*@version 创建时间：2019年5月31日下午4:30:42
*/
@Entity(name="wechatbusiness")
public class WechatBusiness  extends BankRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051922445833172031L;
	
	public WechatBusiness(){}
	public WechatBusiness(String cardNo, Integer businessId) {
		setCardNo(cardNo);
		setBusinessId(businessId);
	}
}
