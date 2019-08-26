package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
*@author star
*@version 创建时间：2019年6月18日上午11:13:49
*/
@Entity(name="groupsuser")
public class GroupsUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4708632039691009309L;
	private int id;
	private Integer userid;
	private Integer groupsid;
	
	public GroupsUser() {}
	
	public GroupsUser(Integer userid,Integer groupsid) {
		this.userid = userid;
		this.groupsid = groupsid;
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
	
	@Column(name="groupsid")
	public Integer getGroupsid() {
		return groupsid;
	}
	public void setGroupsid(Integer groupsid) {
		this.groupsid = groupsid;
	}
	
	
}
