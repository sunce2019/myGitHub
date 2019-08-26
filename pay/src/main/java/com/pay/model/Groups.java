package com.pay.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年5月20日下午6:23:29
*/
@Entity(name="groups")
public class Groups implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4127269776850046172L;
	private int id;
	private Integer userid;
	private Integer customerid;
	private String name;
	@OrderBy("sortby DESC")
	private Set<Menu> menuList;
	private String remarks;
	private boolean editClick;
	private boolean delClick;
	
	@Override
	public String toString() {
		return "Groups [userid=" + userid + ", customerid=" + customerid + ", name=" + name + "]";
	}
	public Groups() {}
	public Groups(String name,String remarks) {
		this.name=name;
		this.remarks = remarks;
	}
	public Groups(int id,String name) {
		this.name=name;
		this.id = id;
	}
	public Groups(Integer userid,Integer customerid,String name) {
		this.userid = userid;
		this.customerid = customerid;
		this.name =name;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="userid")
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	
	@Column(name="customerid")
	public Integer getCustomerid() {
		return customerid;
	}
	public void setCustomerid(Integer customerid) {
		this.customerid = customerid;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToMany(fetch=FetchType.LAZY)
	//增加一张表，维护之间的关系
	@JoinTable(name="groupsmenu",//中间表的名字
				//关联当前表的外键名
				joinColumns=@JoinColumn(name="groupsId"),
				//关联对方表的外键名
				inverseJoinColumns=@JoinColumn(name="menuid")
				)
	public Set<Menu> getMenuList() {
		return menuList;
	}
	public void setMenuList(Set<Menu> menuList) {
		this.menuList = menuList;
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
	
	@Column(name="remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
