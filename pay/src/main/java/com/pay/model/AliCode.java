package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 
 * @author acer
 *
 */
@Entity(name="alicode")
public class AliCode extends BankBase implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 233611597878522955L;
	private String ossURL;
	
	public AliCode() {}
	public AliCode(String cardNo) {
		setCardNo(cardNo);
	}
	public AliCode(String cardNo, String bankAccount, String bankName, Integer flag, String remarks,
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
