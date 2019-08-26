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
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年4月4日上午9:43:06
*/
@Entity(name="users")
public class Users implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6347142774579394977L;
	private int id;
	private String loginname;
	private String password;
	private Integer type;
	private String name;
	private Integer flag;
	private String key;
	private String url;
	private Integer customer_id;
	private String customer_name;
	private Set<Groups> groupsList;
	private boolean editClick;
	
	public Users() {}
	public Users(String name) {
		this.name = name;
	}
	public Users(String loginname,String password,String name,Integer type,Integer flag,String key,String url,Integer customer_id,String customer_name) {
		this.loginname = loginname;
		this.name = name;
		this.type = type;
		this.flag = flag;
		this.password = password;
		this.key= key;
		this.url = url;
		this.customer_id = customer_id;
		this.customer_name = customer_name;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="loginname")
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	
	@Column(name="password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="flag")
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	@Column(name="googlekey")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", loginname=" + loginname + ", password=" + password + ", type=" + type + ", name="
				+ name + ", flag=" + flag + ", key=" + key + ", url=" + url + "]";
	}
	
	@Column(name="customer_id")
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	
	@Column(name="customer_name")
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	
	
	@ManyToMany(fetch=FetchType.LAZY)
	//增加一张表，维护之间的关系
		@JoinTable(
				name="groupsuser",//中间表的名字
				//关联当前表的外键名
				joinColumns=@JoinColumn(name="userid"),
				//关联对方表的外键名
				inverseJoinColumns=@JoinColumn(name="groupsid")
				)
	public Set<Groups> getGroupsList() {
		return groupsList;
	}
	
	
	public void setGroupsList(Set<Groups> groupsList) {
		this.groupsList = groupsList;
	}
	
	@Transient
	public boolean isEditClick() {
		return editClick;
	}
	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}
	
	
}
