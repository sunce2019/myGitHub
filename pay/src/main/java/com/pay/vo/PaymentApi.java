package com.pay.vo;
/**
 * 
*@author star
*@version 创建时间：2019年3月27日下午1:51:43
*/
public class PaymentApi {
	
	private Double price;			//金额
	private Double realprice;		//实际金额
	private String istype;			//支付类型
	private String userName;		//用户id
	private String orderid;			//商户订单号
	private String goodsName;		//商品名称
	private String uid;				//商户id
	private String sign;			//签名
	private Integer customer_id;	//游戏方商户id
	private String customer_name;	//游戏方商户中文名
	private String callbackapiurl;	//回调游戏方api url
	private String callbackviewurl;	//回调游戏方api 显示页面api
	private String time;	//时间戳
	private String ip;
	private String cardNo;	//使用银行卡
	private String depositorName;
	private String customerUid;
	private String remarks;
	private Integer vip;
	private String useragent;
	private String payTran;
	private String type;
	
	public PaymentApi(double price) {
		this.price = price;
	}
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getIstype() {
		return istype;
	}
	public void setIstype(String istype) {
		this.istype = istype;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public PaymentApi() {}
	public PaymentApi(Double price, String istype, String userName, String orderid, String goodsName, String uid,
			String sign) {
		this.price = price;
		this.istype = istype;
		this.userName = userName;
		this.orderid = orderid;
		this.goodsName = goodsName;
		this.uid = uid;
		this.sign = sign;
	}
	
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	public String getCallbackapiurl() {
		return callbackapiurl;
	}
	public void setCallbackapiurl(String callbackapiurl) {
		this.callbackapiurl = callbackapiurl;
	}
	public String getCallbackviewurl() {
		return callbackviewurl;
	}
	public void setCallbackviewurl(String callbackviewurl) {
		this.callbackviewurl = callbackviewurl;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	@Override
	public String toString() {
		return "PaymentApi [price=" + price + ", istype=" + istype + ", userName=" + userName + ", orderid=" + orderid
				+ ", goodsName=" + goodsName + ", uid=" + uid + ", sign=" + sign + ", customer_id=" + customer_id
				+ ", customer_name=" + customer_name + ", callbackapiurl=" + callbackapiurl + ", callbackviewurl="
				+ callbackviewurl + ", time=" + time + ", ip=" + ip + ", cardNo=" + cardNo + "]";
	}
	public String getDepositorName() {
		return depositorName;
	}
	public void setDepositorName(String depositorName) {
		this.depositorName = depositorName;
	}
	public Double getRealprice() {
		if(realprice==null)return 0d;
		return realprice;
	}
	public void setRealprice(Double realprice) {
		this.realprice = realprice;
	}
	public String getCustomerUid() {
		return customerUid;
	}
	public void setCustomerUid(String customerUid) {
		this.customerUid = customerUid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getVip() {
		return vip;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public String getPayTran() {
		return payTran;
	}

	public void setPayTran(String payTran) {
		this.payTran = payTran;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
