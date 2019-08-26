package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.stereotype.Repository;

/**
 * 银行卡类， 基类
*@author star
*@version 创建时间：2019年6月1日下午1:14:19
*/
@Entity(name="discounts")
@Repository
public class Discounts implements Serializable{
	private static final long serialVersionUID = -5604579225527613307L;
	private int id;
	private String theme;
	private String vipAccount;
	private String specialField;
	private Integer status;
	private String disTime;
	
	private boolean editClick;
	private boolean delClick;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="theme")
	public String gettheme() {
		return theme;
	}
	public void settheme(String theme) {
		this.theme = theme;
	}
	
	@Column(name="vipAccount")
	public String getvipAccount() {
		return vipAccount;
	}
	public void setvipAccount(String vipAccount) {
		this.vipAccount = vipAccount;
	}
	
	@Column(name="specialField")
	public String getspecialField() {
		return specialField;
	}
	public void setspecialField(String specialField) {
		this.specialField = specialField;
	}
	
	@Column(name="status")
	public Integer getstatus() {
		return status;
	}
	public void setstatus(Integer status) {
		this.status = status;
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
	@Column(name="disTime")
	public String getDisTime() {
		return disTime;
	}

	public void setDisTime(String disTime) {
		this.disTime = disTime;
	}


	
	
	/*
	 * @ManyToMany(fetch=FetchType.LAZY) //增加一张表，维护之间的关系
	 * 
	 * @JoinTable( name="groupsuser",//中间表的名字 //关联当前表的外键名
	 * joinColumns=@JoinColumn(name="userid"), //关联对方表的外键名
	 * inverseJoinColumns=@JoinColumn(name="groupsid") ) public Set<Groups>
	 * getGroupsList() { return groupsList; }
	 * 
	 * 
	 * public void setGroupsList(Set<Groups> groupsList) { this.groupsList =
	 * groupsList; }
	 */
	
	
	
}
