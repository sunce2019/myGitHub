package com.pay.vo;
/**
*@author star
*@version 创建时间：2019年5月4日下午4:54:25
*/

import java.util.Date;

public class BankVo {
	private String cardNo;	//卡号
	private Date time;		//转账时间
	private String transferPerson;	//转账人
	private String	machine_num;	//收账机器
	private Double price;
	@Override
	public String toString() {
		return "BankVo [cardNo=" + cardNo + ", time=" + time + ", transferPerson=" + transferPerson + ", machine_num="
				+ machine_num + ", price=" + price + "]";
	}
	public BankVo() {}
	public BankVo(String cardNo, Date time, String transferPerson, String machine_num, Double price) {
		this.cardNo = cardNo;
		this.time = time;
		this.transferPerson = transferPerson;
		this.machine_num = machine_num;
		this.price = price;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getTransferPerson() {
		return transferPerson;
	}
	public void setTransferPerson(String transferPerson) {
		this.transferPerson = transferPerson;
	}
	public String getMachine_num() {
		return machine_num;
	}
	public void setMachine_num(String machine_num) {
		this.machine_num = machine_num;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
}
