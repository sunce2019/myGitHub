package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
*@author star
*@version 创建时间：2019年5月30日下午3:17:18
*/
@Entity(name="osssetup")
public class OssSetup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8183189529297957564L;
	
	private int id;
	private String endpoint;
	private	String accessKeyId;
	private String accessKeySecret;
	private String bucketName;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="endpoint")
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	@Column(name="accessKeyId")
	public String getAccessKeyId() {
		return accessKeyId;
	}
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	
	@Column(name="accessKeySecret")
	public String getAccessKeySecret() {
		return accessKeySecret;
	}
	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
	
	@Column(name="bucketName")
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
}
