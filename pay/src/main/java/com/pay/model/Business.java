package com.pay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 商务号类
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午1:48:24
 */
@Entity(name = "business")
public class Business implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6572890014382554989L;
	private int id;
	private String uid; // 商务号
	private String code; // 商务代码
	private String name; // 商务名称
	private String token; // 商务号token
	private Double maxLimit; // 每日最大额度
	private Double currentLimit; // 今日已使用额度
	private String apiUrl; // 官网api post请求url
	private String queryUrl; // 官网api post 查询订单请求url
	private String public_key; // 公钥
	private String private_key; // 私钥
	private String notifyUrl; // 回调URL
	private String notifyViewUrl; // 回调页面显示URL
	private String payType; // 支付类型
	private Integer jumpType; // api 接口调转方式, 1:form表单post 提交方式， 2：js window.location 方式
	private String currentTime;
	private Integer flag;
	private Double range_min; // 范围最小金额
	private Double range_max; // 范围最大金额
	private String fixed_price; // 固定金额 -隔开
	private Integer customer_id; // 商户所属id
	private String customer_name; // 商户所属名称
	private String callbackip;
	private Double price;
	private Integer isauto;
	private String autoType;
	private Integer vip;
	private Integer bankPattern;
	private boolean editClick;
	private boolean testClick;
	private String payCode;

	public Business() {
	}

	public Business(String uid) {
		this.uid = uid;
	}

	public Business(String uid, String payType, Integer customer_id) {
		this.uid = uid;
		this.payType = payType;
		this.customer_id = customer_id;
	}

	@Column(name = "uid")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "token")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "maxLimit")
	public Double getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(Double maxLimit) {
		this.maxLimit = maxLimit;
	}

	@Column(name = "currentLimit")
	public Double getCurrentLimit() {
		return currentLimit;
	}

	public void setCurrentLimit(Double currentLimit) {
		this.currentLimit = currentLimit;
	}

	@Column(name = "apiUrl")
	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	@Column(name = "public_key")
	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	@Column(name = "private_key")
	public String getPrivate_key() {
		return private_key;
	}

	public void setPrivate_key(String private_key) {
		this.private_key = private_key;
	}

	@Column(name = "notifyUrl")
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	@Column(name = "notifyViewUrl")
	public String getNotifyViewUrl() {
		return notifyViewUrl;
	}

	public void setNotifyViewUrl(String notifyViewUrl) {
		this.notifyViewUrl = notifyViewUrl;
	}

	@Column(name = "payType")
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "jumpType")
	public Integer getJumpType() {
		return jumpType;
	}

	public void setJumpType(Integer jumpType) {
		this.jumpType = jumpType;
	}

	@Column(name = "currentTime")
	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	@Column(name = "queryUrl")
	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	@Column(name = "flag")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Column(name = "range_min")
	public Double getRange_min() {
		return range_min;
	}

	public void setRange_min(Double range_min) {
		this.range_min = range_min;
	}

	@Column(name = "range_max")
	public Double getRange_max() {
		return range_max;
	}

	public void setRange_max(Double range_max) {
		this.range_max = range_max;
	}

	@Column(name = "fixed_price")
	public String getFixed_price() {
		return fixed_price;
	}

	public void setFixed_price(String fixed_price) {
		this.fixed_price = fixed_price;
	}

	@Column(name = "customer_id")
	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	@Column(name = "customer_name")
	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	@Override
	public String toString() {
		return "Business [id=" + id + ", uid=" + uid + ", code=" + code + ", name=" + name + ", token=" + token
				+ ", maxLimit=" + maxLimit + ", currentLimit=" + currentLimit + ", apiUrl=" + apiUrl + ", queryUrl="
				+ queryUrl + ", public_key=" + public_key + ", private_key=" + private_key + ", notifyUrl=" + notifyUrl
				+ ", notifyViewUrl=" + notifyViewUrl + ", payType=" + payType + ", jumpType=" + jumpType
				+ ", currentTime=" + currentTime + ", flag=" + flag + ", range_min=" + range_min + ", range_max="
				+ range_max + ", fixed_price=" + fixed_price + ", customer_id=" + customer_id + ", customer_name="
				+ customer_name + "]";
	}

	@Column(name = "callbackip")
	public String getCallbackip() {
		return callbackip;
	}

	public void setCallbackip(String callbackip) {
		this.callbackip = callbackip;
	}

	@Transient
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "isauto")
	public Integer getIsauto() {
		return isauto;
	}

	public void setIsauto(Integer isauto) {
		this.isauto = isauto;
	}

	@Column(name = "autoType")
	public String getAutoType() {
		return autoType;
	}

	public void setAutoType(String autoType) {
		this.autoType = autoType;
	}

	@Column(name = "vip")
	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	@Column(name = "bankPattern")
	public Integer getBankPattern() {
		return bankPattern;
	}

	public void setBankPattern(Integer bankPattern) {
		this.bankPattern = bankPattern;
	}

	@Transient
	public boolean isEditClick() {
		return editClick;
	}

	public void setEditClick(boolean editClick) {
		this.editClick = editClick;
	}

	@Transient
	public boolean isTestClick() {
		return testClick;
	}

	public void setTestClick(boolean testClick) {
		this.testClick = testClick;
	}

	@Column(name = "payCode")
	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
}
