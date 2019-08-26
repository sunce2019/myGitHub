package com.pay.model;

import java.io.Serializable;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.stereotype.Repository;

/**
 * @author star
 * @version
 */
@Entity(name = "message")
@Repository
public class Message implements Serializable {

	

	private static final long serialVersionUID = -6714356277890700365L;
	private int id;	
	private String content;   //内容
	private Date addTime;
	private Boolean status;
    private String modul;   //模板	
    private String bank;
    private String ip;
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



	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	@Column(name="addTime")
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@Column(name="modul")
	public String getModul() {
		return modul;
	}

	public void setModul(String modul) {
		this.modul = modul;
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

	@Column(name="bank")
	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
    @Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	 @Column(name="ip")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}



	



}
