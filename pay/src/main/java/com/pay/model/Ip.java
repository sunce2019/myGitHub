package com.pay.model;
/**
*@author star
*@version 创建时间：2019年4月11日下午4:38:15
*/

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.pay.util.StringUtils;
@Entity(name="ip")
public class Ip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1920153813046843435L;
	private int id;
	private String ip;
	private String addLoginName;
	private Date addTime;
	private String addTimes;
	private String remarks;
	private boolean delClicke;
	
	public Ip() {}
	public Ip(String ip,String addLoginName,String remarks) {
		this.ip = ip;
		this.remarks = remarks;
		this.addTime = new Date();
		this.addLoginName = addLoginName;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="ip")
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Column(name="addLoginName")
	public String getAddLoginName() {
		return addLoginName;
	}
	public void setAddLoginName(String addLoginName) {
		this.addLoginName = addLoginName;
	}
	
	@Column(name="addTime")
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	@Column(name="remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Transient
	public boolean isDelClicke() {
		return delClicke;
	}
	public void setDelClicke(boolean delClicke) {
		this.delClicke = delClicke;
	}
	
	@Transient
	public String getAddTimes() {
		if(addTime!=null)return StringUtils.SDF.format(addTime);
		return addTimes;
	}
	public void setAddTimes(String addTimes) {
		this.addTimes = addTimes;
	}
}
