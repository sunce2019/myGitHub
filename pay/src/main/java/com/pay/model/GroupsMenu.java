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
@Entity(name="groupsmenu")
public class GroupsMenu implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8776431937666097989L;
	private int id;
	private Integer menuid;
	private Integer groupsId;
	
	public GroupsMenu() {}
	
	public GroupsMenu(Integer menuid,Integer groupsId) {
		this.menuid = menuid;
		this.groupsId = groupsId;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="menuid")
	public Integer getMenuid() {
		return menuid;
	}
	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}
	
	@Column(name="groupsId")
	public Integer getGroupsId() {
		return groupsId;
	}
	public void setGroupsId(Integer groupsId) {
		this.groupsId = groupsId;
	}

}
