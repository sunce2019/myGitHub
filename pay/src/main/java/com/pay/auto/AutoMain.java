package com.pay.auto;

import java.util.ArrayList;
import java.util.List;

import com.pay.auto.enums.AmountType;
import com.pay.enums.PayTran;
import com.pay.vo.Vla;

/**
*@author star
*@version 创建时间：2019年6月26日下午2:35:55
*/
public class AutoMain {
	
	
	private int businessid;	//商户号 ID
	private String singType = "AUTOMD5";	//加密方式  默认 MD5
	private String signCode = "UTF-8";	//加密编码
	private List<Parameter> paramList = new ArrayList<Parameter>();	//商户参数集合。
	private List<PayType> payTypeList = new ArrayList<PayType>();	//支付类型参数集合。
	private String amountType = "DOUBLE";	//金额类型
	private String keyStr1 = "&";	//key拼接字符1
	private String keyStr2 = "";	//key拼接字符2
	private String keyStr3 = "=";	//key拼接字符1
	private int keySort;	//0：不参与参数排序，1：最后拼接
	private String sort = "ASCIIUP";	//字段加密排序, 默认 升序
	private String customSort = "";	//自定义
	private String signStr1 = "&";
	private String signStr2 = "=";
	private AutoLoad autoLoad;	// 页面下拉框数据 这个对象
	private int  sizeWrite; //签名   0:大写，1小写 	 默认 大写
	private Integer  yqSizeWrite; //验签   0:大写，1小写 	 默认 大写
	
	public Integer getYqSizeWrite() {
		return yqSizeWrite;
	}

	public void setYqSizeWrite(Integer yqSizeWrite) {
		this.yqSizeWrite = yqSizeWrite;
	}

	private String requestMethod = "FRPOSTFROM";	//请求方式   默认：前端POST表单提交
	private int  requestData; //0:传统拼接 1：JSON格式  默认 传统拼接
	private String flagField;	//判断字段名
	private String sucstr;		//成功字符串值
	private List<Vla> layerList = new ArrayList<Vla>();	//多层级字段	angular 不支持数组绑定。必须有key val 对应才行。
	private List<Parameter> backParamList = new ArrayList<Parameter>();	//回调参数集合。
	private String backSort;
	private String banckSuccess;
	
	public AutoMain() {}
	
	public AutoMain(AutoLoad autoLoad) {
		this.autoLoad = autoLoad;
		this.paramList.add(new Parameter());	//初始化一个空对象，刚加载页面显示一个 参数列表
		this.layerList.add(new Vla(""));
		this.backParamList.add(new Parameter());	//初始化一个空对象，刚加载页面显示一个 参数列表
		this.payTypeList.add(new PayType("",PayTran.ZFB.getCode()));//初始化一个空对象，刚加载页面显示一个 参数列表
	}
	
	public AutoLoad getAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(AutoLoad autoLoad) {
		this.autoLoad = autoLoad;
	}

	public List<Parameter> getParamList() {
		return paramList;
	}

	public void setParamList(List<Parameter> paramList) {
		this.paramList = paramList;
	}

	public int getBusinessid() {
		return businessid;
	}

	public void setBusinessid(int businessid) {
		this.businessid = businessid;
	}

	public String getSingType() {
		return singType;
	}

	public void setSingType(String singType) {
		this.singType = singType;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getSignStr1() {
		return signStr1;
	}

	public void setSignStr1(String signStr1) {
		this.signStr1 = signStr1;
	}

	public String getSignStr2() {
		return signStr2;
	}

	public void setSignStr2(String signStr2) {
		this.signStr2 = signStr2;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	
	public int getSizeWrite() {
		return sizeWrite;
	}

	public void setSizeWrite(int sizeWrite) {
		this.sizeWrite = sizeWrite;
	}

	public int getRequestData() {
		return requestData;
	}

	public void setRequestData(int requestData) {
		this.requestData = requestData;
	}

	public int getKeySort() {
		return keySort;
	}

	public void setKeySort(int keySort) {
		this.keySort = keySort;
	}

	public String getFlagField() {
		return flagField;
	}

	public void setFlagField(String flagField) {
		this.flagField = flagField;
	}

	public String getSucstr() {
		return sucstr;
	}

	public void setSucstr(String sucstr) {
		this.sucstr = sucstr;
	}

	public List<Vla> getLayerList() {
		return layerList;
	}

	public void setLayerList(List<Vla> layerList) {
		this.layerList = layerList;
	}

	public List<Parameter> getBackParamList() {
		return backParamList;
	}

	public void setBackParamList(List<Parameter> backParamList) {
		this.backParamList = backParamList;
	}

	public String getKeyStr1() {
		return keyStr1;
	}

	public void setKeyStr1(String keyStr1) {
		this.keyStr1 = keyStr1;
	}

	public String getKeyStr2() {
		return keyStr2;
	}

	public void setKeyStr2(String keyStr2) {
		this.keyStr2 = keyStr2;
	}

	public String getSignCode() {
		return signCode;
	}

	public void setSignCode(String signCode) {
		this.signCode = signCode;
	}

	public List<PayType> getPayTypeList() {
		return payTypeList;
	}

	public void setPayTypeList(List<PayType> payTypeList) {
		this.payTypeList = payTypeList;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public String getCustomSort() {
		return customSort;
	}

	public void setCustomSort(String customSort) {
		this.customSort = customSort;
	}

	public String getBackSort() {
		return backSort;
	}

	public void setBackSort(String backSort) {
		this.backSort = backSort;
	}

	public String getBanckSuccess() {
		return banckSuccess;
	}

	public void setBanckSuccess(String banckSuccess) {
		this.banckSuccess = banckSuccess;
	}

	public String getKeyStr3() {
		return keyStr3;
	}

	public void setKeyStr3(String keyStr3) {
		this.keyStr3 = keyStr3;
	}

}
