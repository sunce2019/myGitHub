package com.pay.model;
/**
*@author star
*@version 创建时间：2019年4月11日下午4:38:15
*/

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "bank")
public class Bank extends BankBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5604579225527613307L;

	private String bankMark;
	private Integer type;
	private String cardIndex;
	private Integer pattern;

	public Bank() {
	}

	public Bank(String cardNo) {
		setCardNo(cardNo);
	}

	public Bank(String cardNo, String bankAccount, String bankName, String bankMark, Integer flag, String remarks,
			Double maxLimit, Integer type, String cardIndex) {
		setCardNo(cardNo);
		setBankAccount(bankAccount);
		setBankName(bankName);
		this.bankMark = bankMark;
		setFlag(flag);
		setRemarks(remarks);
		setMaxLimit(maxLimit);
		this.type = type;
		this.cardIndex = cardIndex;
	}

	@Column(name = "bankMark")
	public String getBankMark() {
		return bankMark;
	}

	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "cardIndex")
	public String getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(String cardIndex) {
		this.cardIndex = cardIndex;
	}

	@Column(name = "pattern")
	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

}
