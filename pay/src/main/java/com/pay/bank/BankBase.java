package com.pay.bank;

import com.pay.vo.BankVo;

/**
*@author star
*@version 创建时间：2019年5月4日下午4:52:47
*/
public interface BankBase {
	
	public BankVo analysis(String time,String content,String machine_num)throws Exception;
	
}
