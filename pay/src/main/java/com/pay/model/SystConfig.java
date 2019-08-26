package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
*@author star
*@version 创建时间：2019年7月1日下午4:24:52
*/
@Entity(name="systconfig")
public class SystConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5060943469443896851L;
	private int id;
	private String code;
	private String content;
	private String json;
	private int flag;
	private String remarks;
	private boolean editClick;
	
	public SystConfig() {}
	
	public SystConfig(String code) {
		this.code=code;
	}
	
	public SystConfig(String code,int flag) {
		this.code=code;
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
	
	@Column(name="code")
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="content")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name="json")
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	@Column(name="flag")
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	@Column(name="remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Transient
	public boolean isEditClick() {
		return editClick;
	}
	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}
	
}
