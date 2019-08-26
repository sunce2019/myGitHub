package com.pay.vo;

import java.math.BigInteger;

import com.pay.util.StringUtils;

/**
*@author star
*@version 创建时间：2019年4月19日下午2:55:28
*/
public class PayOrderCount {
	private Integer count;	//最大条数
	private Double priceSum;	//支付金额总数
	private Double realpriceSum;	//成功支付实际总数
	private int successRateCount;	//成功支付条数
	
	public PayOrderCount() {
		this.count = 0;
		this.priceSum = 0d;
		this.realpriceSum = 0d;
	}
	public PayOrderCount(int count, Double priceSum, Double realpriceSum) {
		this.count = count;
		this.priceSum = priceSum;
		this.realpriceSum = realpriceSum;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(BigInteger count) {
		this.count = count.intValue();
	}
	public Double getPriceSum() {
		
		return priceSum;
	}
	public void setPriceSum(Double priceSum) {
		this.priceSum = priceSum;
	}
	public Double getRealpriceSum() {
		return realpriceSum;
	}
	public void setRealpriceSum(Double realpriceSum) {
		this.realpriceSum = realpriceSum;
	}
	public int getSuccessRateCount() {
		return successRateCount;
	}
	
	public void setSuccessRateCount(BigInteger successRateCount) {
		this.successRateCount = successRateCount.intValue();
	}
	public Double getSuccessRate() {
		if(realpriceSum==null || realpriceSum==0)return 0.00;
		Double d = (realpriceSum/priceSum)*100;
		return Double.parseDouble(String.format("%.2f",d));
	}
	
	
	public Double getSuccessNumber() {
		if(successRateCount==0 || count==null || count==0)return 0.00;
		Double d = ((double)successRateCount/(double)count)*100;
		return Double.parseDouble(String.format("%.2f",d));
	}
	public static void main(String[] args) {
		PayOrderCount a = new PayOrderCount();
		a.setRealpriceSum(2d);
		a.setPriceSum(10d);
		System.out.println(a.getSuccessRate());
	}
	
}
