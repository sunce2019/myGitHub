package com.pay.service;

import com.pay.auto.AutoMain;
import com.pay.model.Auto;

/**
*@author star
*@version 创建时间：2019年6月27日下午3:07:45
*/
public interface AuToService {
	
	public void addOrUpdateAuto(int id,AutoMain autoMain)throws Exception;
	
	public Auto getAuto(int business_id,String code)throws Exception;
	
	public Auto getAuto(Auto auto)throws Exception;
	
}
