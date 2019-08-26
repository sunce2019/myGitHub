package com.pay.model;

import java.io.Serializable;

import javax.persistence.Entity;

/**
*@author star
*@version 创建时间：2019年4月25日下午2:30:11
*/
@Entity(name="bankbusiness")
public class BankBusiness  extends BankRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2052069434873349652L;
	public BankBusiness(){}
	public BankBusiness(String cardNo, Integer businessId) {
		setCardNo(cardNo);
		setBusinessId(businessId);
	}
}
