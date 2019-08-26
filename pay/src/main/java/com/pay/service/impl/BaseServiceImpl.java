package com.pay.service.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.enums.CallBackGameStage;
import com.pay.enums.PayState;
import com.pay.model.Business;
import com.pay.model.CloudFlasHover;
import com.pay.model.PayOrder;
import com.pay.model.Settings;
import com.pay.service.BaseService;

@Service("baseService")
public class BaseServiceImpl implements BaseService {

	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private BaseDao<Settings> settingsDao;
	@Autowired
	private BaseDao<CloudFlasHover> cloudFlasHoverDao;

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean successfulPayment(int payorderId, int businessId, Double price) throws Exception {
		PayOrder payOrder = payOrderDao.get(PayOrder.class, payorderId, LockMode.UPGRADE);
		if (payOrder.getFlag() == PayState.INIT.getCode()) {
			if (payOrder.getPrice() + 1 > price && price > payOrder.getPrice() - 1) {// 控制金额上下浮动，不超过1
				payOrder.setRealprice(price); // 实际支付金额 有些第三方支付没有该参数
				// payOrder.setRealprice(payOrder.getPrice()); //
				payOrder.setCallbackTime(new Date());
				payOrder.setFlag(PayState.SUCCESS.getCode());
				payOrder.setCallGameFlag(CallBackGameStage.MOVE.getCode());
				payOrder.setCallGameNumber(1);
				payOrderDao.update(payOrder);
				logger.info("回调成功：" + payOrder.toString());
				return true;
			} else {
				payOrder.setRemarks(payOrder.getRemarks() + "回调金额不匹配。");
				logger.info("回调失败，金额不想符合：" + payOrder.toString());
			}
		} else {
			logger.info("订单状态已被改变,放弃修改:" + payOrder.toString());
		}
		return false;
	}

	@Override
	public Settings getSettings(Settings settings) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Settings.class);
		if (settings.getBusiness_id() > 0)
			dc.add(Restrictions.eq("business_id", settings.getBusiness_id()));
		if (settings.getId() > 0)
			dc.add(Restrictions.eq("id", settings.getId()));
		return settingsDao.findObj(dc);
	}

	@Override
	public int getCloudPattern(String cardNo) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(CloudFlasHover.class);
		dc.add(Restrictions.eq("cardNo", cardNo));
		List<CloudFlasHover> list = cloudFlasHoverDao.findList(dc, 0, 1);
		if (list.size() == 1)
			return list.get(0).getPattern();
		return 0;
	}

}
