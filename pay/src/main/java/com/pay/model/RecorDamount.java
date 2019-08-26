package com.pay.model;
/**
*@author star
*@version 创建时间：2019年4月11日下午4:38:15
*/

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity(name="recordamount")
public class RecorDamount implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -563051101649331463L;
	private int id;
	private Double price;
	private Double cumulative;
	public RecorDamount() {}
	public RecorDamount(Double price, Double cumulative) {
		super();
		this.price = price;
		this.cumulative = cumulative;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="price")
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@Column(name="cumulative")
	public Double getCumulative() {
		return cumulative;
	}
	public void setCumulative(Double cumulative) {
		this.cumulative = cumulative;
	}
	
	
	
	
}
