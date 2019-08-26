package com.pay.task;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pay.dao.BaseDao;
import com.pay.enums.CallBackGameStage;
import com.pay.enums.PayState;
import com.pay.handlePay.GameApi;
import com.pay.model.Customer;
import com.pay.model.PayOrder;


/**
*@author star
*@version 创建时间：2019年4月2日下午6:47:51
*/
@Component
public class CallBackGameTask {
	
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private BaseDao<Customer> customerdDao;
	
	
	@Scheduled(fixedDelay= 60000)  //每隔5秒执行一次定时任务
    public void consoleInfo(){
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		dc.add(Restrictions.eq("flag", PayState.SUCCESS.getCode()));
		dc.add(Restrictions.le("callGameNumber", 10));
		dc.add(Restrictions.eq("callGameFlag", CallBackGameStage.MOVE.getCode()));
		List<PayOrder> list = payOrderDao.findList(dc);
		for(PayOrder payOrder:list) {
			if(new Date().getTime() - payOrder.getCallbackTime().getTime() > 60000) {
				try {
					Customer customer = customerdDao.get(Customer.class, payOrder.getCustomer_id());
					if(customer.getCallbackType()==1) {	//中博第二方回调
						gameApi.zbCallBackGame(payOrder.getId(),true,customer,false);
					}else if(customer.getCallbackType()==2) {	//游乐场回调
						gameApi.callBackGame(payOrder.getId(),true,true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
    }
	
	public static void main(String[] args) {
		Date date = new Date();
		try {
			Thread.sleep(1000);
			Date date2 = new Date();
			System.out.println(date2.getTime()-date.getTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
