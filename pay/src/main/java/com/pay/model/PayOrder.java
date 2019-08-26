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
 * 商务号类
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午1:48:24
 */
@Entity(name = "payorder")
public class PayOrder extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1846360296313262846L;
	private int id;
	private String orderNumber; // 自己系统 订单号
	private String userName; // 订单人
	private String orderType; // 支付宝 微信 或者其他
	private String uid; // 使用的商户号 渠道
	private String payApiId; // 商户订单号，
	private String payName; // 商户中文名
	private Double price; // 订单价格
	private Double realprice; // 实际支付价格
	private Date addTime; // 订单创建时间
	private Date callbackTime; // 商户回调时间
	private Integer flag; // 支付状态 1：创建订单成功，2：支付成功，3：支付失败
	private String gameOrderNumber; // 游戏方订单号
	private Integer callGameFlag; // 通知游戏回调状态， 1：初始化 ，2：回调中, 3:回调成功,4:回调失败
	private Integer callGameNumber; // 回调游戏次数
	private Date callGameSUCTime; // 回调游戏成功实际， callGameFlag==3 创建这个时间
	private Integer customer_id; // 游戏方商户id
	private String customer_name; // 游戏方商户id
	private String callbackapiurl; // 回调游戏方api url
	private String callbackviewurl; // 回调游戏方api 显示页面api
	private String goodsName;
	private String times;
	private String cardNo;
	private String depositorName;
	private String remarks;
	private String ip;

	private String returnError;
	private String useragent;
	private Integer vip;

	//// 以下不映射数据库
	private String tail;
	private boolean upScoreClick;
	private boolean backClick;
	private boolean backClick2;
	private boolean apiClick;

	public PayOrder() {
	}

	public PayOrder(String gameOrderNumber) {
		this.gameOrderNumber = gameOrderNumber;
	}

	public PayOrder(String orderNumber, String time) {
		this.orderNumber = orderNumber;
		this.times = time;
	}

	public PayOrder(String orderNumber, Integer flag, Integer callGameFlag) {
		this.orderNumber = orderNumber;
		this.flag = flag;
		this.callGameFlag = callGameFlag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "orderNumber")
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Column(name = "userName")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "orderType")
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@Column(name = "uid")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(name = "payApiId")
	public String getPayApiId() {
		return payApiId;
	}

	public void setPayApiId(String payApiId) {
		this.payApiId = payApiId;
	}

	@Column(name = "price")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "realprice")
	public Double getRealprice() {
		return realprice;
	}

	public void setRealprice(Double realprice) {
		this.realprice = realprice;
	}

	@Column(name = "addTime")
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@Column(name = "callbackTime")
	public Date getCallbackTime() {
		return callbackTime;
	}

	public void setCallbackTime(Date callbackTime) {
		this.callbackTime = callbackTime;
	}

	@Column(name = "flag")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Column(name = "callGameFlag")
	public Integer getCallGameFlag() {
		return callGameFlag;
	}

	public void setCallGameFlag(Integer callGameFlag) {
		this.callGameFlag = callGameFlag;
	}

	@Column(name = "callGameNumber")
	public Integer getCallGameNumber() {
		return callGameNumber;
	}

	public void setCallGameNumber(Integer callGameNumber) {
		this.callGameNumber = callGameNumber;
	}

	@Column(name = "callGameSUCTime")
	public Date getCallGameSUCTime() {
		return callGameSUCTime;
	}

	public void setCallGameSUCTime(Date callGameSUCTime) {
		this.callGameSUCTime = callGameSUCTime;
	}

	@Column(name = "gameOrderNumber")
	public String getGameOrderNumber() {
		return gameOrderNumber;
	}

	public void setGameOrderNumber(String gameOrderNumber) {
		this.gameOrderNumber = gameOrderNumber;
	}

	@Column(name = "customer_id")
	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	@Column(name = "callbackapiurl")
	public String getCallbackapiurl() {
		return callbackapiurl;
	}

	public void setCallbackapiurl(String callbackapiurl) {
		this.callbackapiurl = callbackapiurl;
	}

	@Column(name = "callbackviewurl")
	public String getCallbackviewurl() {
		return callbackviewurl;
	}

	public void setCallbackviewurl(String callbackviewurl) {
		this.callbackviewurl = callbackviewurl;
	}

	@Column(name = "goodsName")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	@Column(name = "payName")
	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	@Column(name = "customer_name")
	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	@Column(name = "times")
	public String getTime() {
		return times;
	}

	public void setTime(String time) {
		this.times = time;
	}

	@Column(name = "cardNo")
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Column(name = "depositorName")
	public String getDepositorName() {
		return depositorName;
	}

	public void setDepositorName(String depositorName) {
		this.depositorName = depositorName;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		if (remarks == null)
			return "";
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "ip")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Transient
	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	@Column(name = "returnError")
	public String getReturnError() {
		return returnError;
	}

	public void setReturnError(String returnError) {
		this.returnError = returnError;
	}

	@Column(name = "useragent")
	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	@Column(name = "vip")
	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	@Transient
	public boolean isUpScoreClick() {
		return upScoreClick;
	}

	public void setUpScoreClick(boolean upScoreClick) {
		this.upScoreClick = upScoreClick;
	}

	@Transient
	public boolean isBackClick() {
		return backClick;
	}

	public void setBackClick(boolean backClick) {
		this.backClick = backClick;
	}

	@Transient
	public boolean isApiClick() {
		return apiClick;
	}

	public void setApiClick(boolean apiClick) {
		this.apiClick = apiClick;
	}

	@Override
	public String toString() {
		return "PayOrder [id=" + id + ", orderNumber=" + orderNumber + ", userName=" + userName + ", orderType="
				+ orderType + ", uid=" + uid + ", payApiId=" + payApiId + ", payName=" + payName + ", price=" + price
				+ ", realprice=" + realprice + ", addTime=" + addTime + ", callbackTime=" + callbackTime + ", flag="
				+ flag + ", gameOrderNumber=" + gameOrderNumber + ", callGameFlag=" + callGameFlag + ", callGameNumber="
				+ callGameNumber + ", callGameSUCTime=" + callGameSUCTime + ", customer_id=" + customer_id
				+ ", customer_name=" + customer_name + ", callbackapiurl=" + callbackapiurl + ", callbackviewurl="
				+ callbackviewurl + ", goodsName=" + goodsName + ", times=" + times + ", cardNo=" + cardNo
				+ ", depositorName=" + depositorName + ", remarks=" + remarks + ", ip=" + ip + ", returnError="
				+ returnError + ", useragent=" + useragent + ", vip=" + vip + ", tail=" + tail + ", upScoreClick="
				+ upScoreClick + ", backClick=" + backClick + ", apiClick=" + apiClick + "]";
	}

	@Transient
	public boolean isBackClick2() {
		return backClick2;
	}

	public void setBackClick2(boolean backClick2) {
		this.backClick2 = backClick2;
	}
}
