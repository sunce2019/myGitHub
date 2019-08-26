package com.pay.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 银行卡类， 基类
*@author star
*@version 创建时间：2019年6月1日下午1:14:19
*/
@MappedSuperclass
public class BankBase {
	private int id;
	private String cardNo;
	private String bankAccount;
	private String bankName;
	private Integer flag;
	private String remarks;
	private Double maxLimit;
	private Integer useStartTime;
	private Integer useEndTime;
	private Double price;
	private String tail;
	private Integer cashierCount;
	private boolean editClick;
	private boolean delClick;
	private int customerid;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="cardNo")
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	
	@Column(name="bankAccount")
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	@Column(name="bankName")
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	@Column(name="flag")
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	@Column(name="remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Column(name="maxLimit")
	public Double getMaxLimit() {
		return maxLimit;
	}
	public void setMaxLimit(Double maxLimit) {
		this.maxLimit = maxLimit;
	}
	
	@Column(name="useStartTime")
	public Integer getUseStartTime() {
		return useStartTime;
	}
	public void setUseStartTime(Integer useStartTime) {
		this.useStartTime = useStartTime;
	}
	
	@Column(name="useEndTime")
	public Integer getUseEndTime() {
		return useEndTime;
	}
	public void setUseEndTime(Integer useEndTime) {
		this.useEndTime = useEndTime;
	}

	@Transient
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Column(name="tail")
	public String getTail() {
		return tail;
	}
	public void setTail(String tail) {
		this.tail = tail;
	}
	
	@Column(name="cashierCount")
	public Integer getCashierCount() {
		return cashierCount;
	}

	public void setCashierCount(Integer cashierCount) {
		this.cashierCount = cashierCount;
	}
	
	@Transient
	public boolean isEditClick() {
		return editClick;
	}

	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}
	
	@Transient
	public boolean isDelClick() {
		return delClick;
	}

	public void setDelClick(boolean delClick) {
		this.delClick = delClick;
	}
	
	@Column(name="customerid")
	public int getCustomerid() {
		return customerid;
	}

	public void setCustomerid(int customerid) {
		this.customerid = customerid;
	}
	
}
