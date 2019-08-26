package com.pay.auto;
/**
 * 页面加载数据类
*@author star
*@version 创建时间：2019年6月26日下午2:36:51
*/

import java.util.ArrayList;
import java.util.List;

import com.pay.auto.enums.AmountType;
import com.pay.auto.enums.Attribute;
import com.pay.auto.enums.JumpType;
import com.pay.auto.enums.ParameType;
import com.pay.auto.enums.RequestMethod;
import com.pay.auto.enums.SingType;
import com.pay.auto.enums.Sort;
import com.pay.enums.PayTran;


public class AutoLoad {
	
	private List<Business> business;		//商户号 集合
	private SingType[] singType = SingType.values();	//加密方式集合
	private ParameType[] parameType = ParameType.values();	//商户字段类型
	private Attribute[] attribute = Attribute.values();	//值属性 
	private Sort[] sort = Sort.values();	//排序方式
	private RequestMethod[] requestMethod = RequestMethod.values();	// 请求方式
	private PayTran[] payTran = PayTran.values();	// 请求方式
	private AmountType[] amountType = AmountType.values();	//金额集合
	private JumpType[] jumpType = JumpType.values();	//金额集合
	
	public AutoLoad() {}
	
	public AutoLoad(List<Business> business) {
		this.business = business;
	}
	
	public List<Business> getBusiness() {
		return business;
	}

	public void setBusiness(List<Business> business) {
		this.business = business;
	}

	public SingType[] getSingType() {
		return singType;
	}

	public void setSingType(SingType[] singType) {
		this.singType = singType;
	}

	public ParameType[] getParameType() {
		return parameType;
	}

	public void setParameType(ParameType[] parameType) {
		this.parameType = parameType;
	}

	public Attribute[] getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute[] attribute) {
		this.attribute = attribute;
	}

	public Sort[] getSort() {
		return sort;
	}

	public void setSort(Sort[] sort) {
		this.sort = sort;
	}

	public RequestMethod[] getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod[] requestMethod) {
		this.requestMethod = requestMethod;
	}

	public PayTran[] getPayTran() {
		return payTran;
	}

	public void setPayTran(PayTran[] payTran) {
		this.payTran = payTran;
	}

	public AmountType[] getAmountType() {
		return amountType;
	}

	public void setAmountType(AmountType[] amountType) {
		this.amountType = amountType;
	}

	public JumpType[] getJumpType() {
		return jumpType;
	}

	public void setJumpType(JumpType[] jumpType) {
		this.jumpType = jumpType;
	}

}
