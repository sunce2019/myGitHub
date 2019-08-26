package com.pay.model;
/**
*@author star
*@version 创建时间：2019年5月21日下午4:50:11
*/

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="arrivalaccount")
public class ArrivalAccount implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9046168027569447111L;
	private int id;
	private Integer payorder_id;
	private String cardNo;
	private Date addtime;
	private Integer flag;
	private String remarks;
	private Double price;
	
	public ArrivalAccount() {}
	public ArrivalAccount(int id,String cardNo,Date addtime,Integer flag,String remarks,Double price) {
		this.id=id;
		this.cardNo = cardNo;
		this.addtime = addtime;
		this.flag = flag;
		this.remarks = remarks;
		this.price = price;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="payorder_id")
	public Integer getPayorder_id() {
		return payorder_id;
	}
	public void setPayorder_id(Integer payorder_id) {
		this.payorder_id = payorder_id;
	}
	
	@Column(name="cardNo")
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	
	@Column(name="addtime")
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
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
	
	@Column(name="price")
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
}
