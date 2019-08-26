package com.pay.model;

import java.io.Serializable;

import javax.persistence.Entity;

/**
*@author star
*@version 创建时间：2019年5月31日下午4:30:42
*/
@Entity(name="cloudbusiness")
public class CloudBusiness extends BankRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2444169529798343243L;
	public CloudBusiness(){}
	public CloudBusiness(String cardNo, Integer businessId) {
		setCardNo(cardNo);
		setBusinessId(businessId);
	}
}
