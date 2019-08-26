package com.pay.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年3月29日下午6:01:37
*/
@Entity(name="customer")
public class Customer implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4907443427840591429L;
	private int id;
	private String key;		//秘钥key
	private String uid;		//商户id
	private String name;		//客户名
	private Integer flag;	//状态
	private Date addTime;	//添加时间
	private String public_key1;		
	private String private_key1;		
	private String public_key_2;		
	private String private_key_2;		
	private String ip;
	private String symmetric;		// 对称秘钥
	private Integer callbackType;
	private boolean editClick;
	
	public Customer() {}
	public Customer(String uid) {
		this.uid = uid;
	}
	public Customer(String uid,Integer flag) {
		this.uid = uid;
		this.flag = flag;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="token")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name="uid")
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
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
	
	@Column(name="addTime")
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	@Column(name="public_key1")
	public String getPublic_key1() {
		return public_key1;
	}

	public void setPublic_key1(String public_key1) {
		this.public_key1 = public_key1;
	}
	
	@Column(name="private_key1")
	public String getPrivate_key1() {
		return private_key1;
	}

	public void setPrivate_key1(String private_key1) {
		this.private_key1 = private_key1;
	}
	
	@Column(name="public_key_2")
	public String getPublic_key_2() {
		return public_key_2;
	}

	public void setPublic_key_2(String public_key_2) {
		this.public_key_2 = public_key_2;
	}
	
	@Column(name="private_key_2")
	public String getPrivate_key_2() {
		return private_key_2;
	}

	public void setPrivate_key_2(String private_key_2) {
		this.private_key_2 = private_key_2;
	}
	
	@Column(name="ip")
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Column(name="symmetric")
	public String getSymmetric() {
		return symmetric;
	}
	public void setSymmetric(String symmetric) {
		this.symmetric = symmetric;
	}
	
	@Column(name="callbackType")
	public Integer getCallbackType() {
		return callbackType;
	}
	public void setCallbackType(Integer callbackType) {
		this.callbackType = callbackType;
	}
	
	@Transient
	public boolean isEditClick() {
		return editClick;
	}
	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}

}
