package com.pay.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Entity(name = "active")
@Repository
public class Active implements Serializable {
	private static final long serialVersionUID = -5604579225527613307L;
	private int id;
	private String title;
	private String content;
	private String details;
	private Date startTime;
	private Date endTime;
	private Integer sort;
	private String img;
	private String specialField;
	private Boolean status;
	private List<Map<String, String>> specialList;
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

	@Column(name = "title")
	public String gettitle() {
		return title;
	}

	public void settitle(String title) {
		this.title = title;
	}

	@Column(name = "content")
	public String getcontent() {
		return content;
	}

	public void setcontent(String content) {
		this.content = content;
	}

	@Column(name = "details")
	public String getdetails() {
		return details;
	}

	public void setdetails(String details) {
		this.details = details;
	}

	@Column(name = "startTime")
	public Date getstartTime() {
		return startTime;
	}

	public void setstartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endTime")
	public Date getendTime() {
		return endTime;
	}

	public void setendTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "sort")
	public Integer getsort() {
		return sort;
	}

	public void setsort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "specialField")
	public String getSpecialField() {
		return specialField;
	}

	public void setSpecialField(String specialField) {
		this.specialField = specialField;
	}

	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	@Transient
	public List<Map<String, String>> getspecialList() {
		return specialList;
	}

	public void setspecialList(List<Map<String, String>> specialList) {
		this.specialList = specialList;
	}
	@Column(name = "img")
	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}



}
