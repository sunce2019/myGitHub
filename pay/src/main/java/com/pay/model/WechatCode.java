package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
*@author star
*@version 创建时间：2019年5月30日下午3:34:16
*/
@Entity(name="weChatcode")
public class WechatCode extends BankBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8131943102886240681L;
	private String ossURL;
	
	public WechatCode() {}
	public WechatCode(String cardNo) {
		setCardNo(cardNo);
	}
	public WechatCode(String cardNo, String bankAccount, String bankName, Integer flag, String remarks,
			Double maxLimit) {
		setCardNo(cardNo);
		setBankAccount(bankAccount);
		setBankName(bankName);
		setFlag(flag);
		setRemarks(remarks);
		setMaxLimit(maxLimit);
	}
	
	
	@Column(name="ossURL")
	public String getOssURL() {
		return ossURL;
	}
	public void setOssURL(String ossURL) {
		this.ossURL = ossURL;
	}
	
}
