package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
*@author star
*@version 创建时间：2019年4月2日下午2:06:45
*/
@Entity(name="settings")
public class Settings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7449281067824560235L;
	private int id;
	private int business_id;
	
	public Settings() {}
	public Settings(int id) {
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="business_id")
	public int getBusiness_id() {
		return business_id;
	}
	public void setBusiness_id(int business_id) {
		this.business_id = business_id;
	}
	
}
