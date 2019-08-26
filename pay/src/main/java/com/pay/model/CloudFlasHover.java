package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
*@author star
*@version 创建时间：2019年5月30日下午3:34:16
*/
@Entity(name="cloudflashover")
public class CloudFlasHover extends BankBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5196850157358339525L;
	private String ossURL;
	private Integer pattern;
	
	public CloudFlasHover() {}
	public CloudFlasHover(String cardNo) {
		setCardNo(cardNo);
	}
	public CloudFlasHover(String cardNo, String bankAccount, String bankName, Integer flag, String remarks,
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
	
	@Column(name="pattern")
	public Integer getPattern() {
		return pattern;
	}
	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}
	
}
