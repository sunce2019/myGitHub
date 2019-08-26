package com.pay.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年6月18日上午11:13:49
*/
@Entity(name="menu")
public class Menu implements Serializable,Comparable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8776431937666097989L;
	private int id;
	private String name;
	private List<Menu> menuList;
	private Integer parentid;
	private Customer customer;
	private String url;
	private boolean flag;
	private Integer sortby;
	
	
	public Menu() {}
	public Menu(String name,Integer parentid,Customer customer,String url,Integer sort) {
		this.name = name;
		this.parentid = parentid;
		this.customer = customer;
		this.url = url;
		this.sortby=sort;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany()
	@JoinColumn(name="parentid")
	public List<Menu> getMenuList() {
		return menuList;
	}
	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}
	
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@Column(name="parentid")
	public Integer getParentid() {
		return parentid;
	}
	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}
	@Column(name="sortby")
	public Integer getSortby() {
		return sortby;
	}
	public void setSortby(Integer sort) {
		this.sortby = sort;
	}
	
	
	
	//referencedColumnName：参考列名,默认的情况下是列表的主键
    //nullable=是否可以为空，
    //insertable：是否可以插入，
    //updatable：是否可以更新
    // columnDefinition=列定义，
    //foreignKey=外键
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="customerid",referencedColumnName="id",nullable=true,insertable=true)
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	@Override
	public int compareTo(Object o) {
		Menu m = (Menu)o;
        return this.id - m.id;
	}
	
	@Transient
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	
}
