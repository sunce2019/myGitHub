package com.pay.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pay.dao.BaseDao;
import com.pay.enums.PayType;
import com.pay.handleBusiness.WeiHuBao;
import com.pay.handlePay.GameApi;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;

/**
 * @author star
 * @version 创建时间：2019年4月2日下午6:47:51
 */
@Component
public class QueryPayOrderTask {

	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private BaseService baseService;
	@Autowired
	private WeiHuBao weihubao;

	private String[] payName = new String[] { PayType.WEIHUBAO.getText() };

	@Scheduled(fixedDelay = 30000) // 每隔30秒执行一次定时任务
	public void consoleInfo() throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		dc.add(Restrictions.eq("flag", 1)); // 我方订单状态为1
		dc.add(Restrictions.in("payName", payName)); // 支付方名称
		// 获取10分钟前的时间
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -10);
		Date time = beforeTime.getTime();
		// 设置订单时间范围
		dc.add(Restrictions.ge("addTime", time));
		dc.add(Restrictions.le("addTime", new Date()));
		List<PayOrder> list = payOrderDao.findList(dc);
		Business business = null;
		DetachedCriteria business_dc = null;
		for (PayOrder payOrder : list) {
			business_dc = DetachedCriteria.forClass(Business.class); // 获取当前订单对应的商户信息
			business_dc.add(Restrictions.eq("customer_id", payOrder.getCustomer_id()));
			business_dc.add(Restrictions.eq("uid", payOrder.getUid()));
			business = businessDao.findObj(business_dc);
			String amount = weihubao.payOrderQuery(business, payOrder); // 调用查询接口
			if (amount != null) {
				if (baseService.successfulPayment(payOrder.getId(), business.getId(), StringToDouble(amount))) { // 修改订单状态
					gameApi.callBackGame(payOrder.getId(), true, true); // 回调游戏方
				}
			}
		}
	}

	public static String doubleToString(Double d) throws Exception {
		return String.format("%.2f", d);
	}

	public static Double StringToDouble(String d) throws Exception {
		return Double.parseDouble(d);
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Date date = new Date(); try { Thread.sleep(1000); Date date2 = new Date();
		 * System.out.println(date2.getTime()-date.getTime()); } catch
		 * (InterruptedException e) { e.printStackTrace(); }
		 */

		new QueryPayOrderTask().consoleInfo();

	}

}
