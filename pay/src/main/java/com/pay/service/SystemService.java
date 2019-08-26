package com.pay.service;

import com.pay.model.SystConfig;

/**
*@author star
*@version 创建时间：2019年6月18日下午3:27:38
*/
public interface SystemService {
	
	public void addSystemPermiOrUpdate(Integer id,String menuid)throws Exception;
	
	public void deleteGroups(int id)throws Exception;
	
	public SystConfig getSystConfig(SystConfig systConfig)throws Exception;
	
}
