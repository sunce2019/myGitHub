package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年5月26日下午1:13:03
*/
@Entity(name="auto")
public class Auto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3902135061665621515L;
	private int id;
	private String datas;
	private Business business;
	private boolean editClick;
	private boolean syClick;
	private String synNo;
	public Auto() {}
	public Auto(String synNo) {
		this.synNo = synNo; 
	}
	public Auto(String datas,Business business,String synNo) {
		this.datas = datas;
		this.business = business;
		this.synNo = synNo;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="datas")
	public String getDatas() {
		return datas;
	}
	public void setDatas(String datas) {
		this.datas = datas;
	}
	
	
	@ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "business_id",referencedColumnName="id",nullable=false,insertable=true)  
	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}
	
	@Transient
	public boolean isEditClick() {
		return editClick;
	}

	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}
	
	@Transient
	public boolean isSyClick() {
		return syClick;
	}

	public void setSyClick(boolean syClick) {
		this.syClick = syClick;
	}
	
	@Column(name="synNo")
	public String getSynNo() {
		return synNo;
	}

	public void setSynNo(String synNo) {
		this.synNo = synNo;
	} 
	
}
