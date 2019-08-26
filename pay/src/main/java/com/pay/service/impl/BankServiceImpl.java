package com.pay.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.dao.BaseDao;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.enums.State;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.BankRelation;
import com.pay.model.Business;
import com.pay.model.CloudFlasHover;
import com.pay.model.PayOrder;
import com.pay.service.BankService;
import com.pay.service.PayOrderService;
import com.pay.util.StringUtils;
import com.pay.vo.PaymentApi;

/**
 * @author star
 * @version 创建时间：2019年4月26日下午5:20:31
 */
@Service("bankService")
public class BankServiceImpl implements BankService {

	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private BaseDao<Bank> bankDao;
	@Autowired
	private BaseDao<CloudFlasHover> cloudFlasHoverDao;
	@Autowired
	private BaseDao<BankBase> bankBaseDao;
	@Autowired
	private BaseDao<BankRelation> bankRelationDao;
	@Autowired
	private PayOrderService payOrderService;

	@Override
	public void getBankCode(PaymentApi paymentApi, Business business) throws Exception {
		if (business.getBankPattern().intValue() == 1 || business.getBankPattern().intValue() == 3) {// 一成功一切换,订单轮询
			changeIntoOne(paymentApi, business);
		} else if (business.getBankPattern().intValue() == 2) {// 金额平摊收款
			amountEqualization(paymentApi, business);
		}
	}

	private void changeIntoOne(PaymentApi paymentApi, Business business) throws Exception {
		String relation = "bankbusiness"; // 默认关联查询 银行卡 关联表
		String bankorcloud = "bank"; // 默认银行卡表，

		// 如果是 云闪付 跟换成 云闪付关联表
		if (business.getPayType().equals(PayTran.YSF.getText())) {
			relation = "cloudbusiness";
			bankorcloud = "cloudflashover";
		}
		if (business.getPayType().equals(PayTran.WX_MD.getText())) {
			relation = "wechatbusiness";
			bankorcloud = "wechatcode";
		}

		Date date = new Date();
		// sql 原型 SELECT b.* from bank b left join bankbusiness bb on b.cardNo=bb.cardNo
		// where bb.businessId=65 and b.flag=1 and
		// if(b.useEndTime>b.useStartTime,b.useStartTime<=16 and
		// b.useEndTime>=16,((b.useStartTime<=16 and 16>=b.useEndTime) or
		// (b.useStartTime>16 and 16<=b.useEndTime))) ORDER BY b.cashierCount asc
		String sql = "SELECT b.* from  " + bankorcloud + " b left join " + relation
				+ " bb on b.cardNo=bb.cardNo where bb.businessId=:businessId and  b.flag=1 and if(b.useEndTime>b.useStartTime,b.useStartTime<='"
				+ date.getHours() + "' and  b.useEndTime>='" + date.getHours() + "',((b.useStartTime<='"
				+ date.getHours() + "' and '" + date.getHours() + "'>=b.useEndTime) or (b.useStartTime>'"
				+ date.getHours() + "' and '" + date.getHours()
				+ "'<=b.useEndTime))) ORDER BY b.cashierCount asc LIMIT 0,1";

		Map<String, Object> params = new HashMap<>();
		params.put("businessId", business.getId());
		List<Map<String, Object>> remap = bankDao.findMapBySql(sql, params);
		if (remap != null && remap.size() == 1) {
			paymentApi.setCardNo(remap.get(0).get("cardNo").toString());
		}

	}

	private void amountEqualization(PaymentApi paymentApi, Business business) throws Exception {
		String relation = "bankbusiness"; // 默认关联查询 银行卡 关联表
		String bankorcloud = "bank"; // 默认银行卡表，

		// 如果是 云闪付 跟换成 云闪付关联表
		if (business.getPayType().equals(PayTran.YSF.getText())) {
			relation = "cloudbusiness";
			bankorcloud = "cloudflashover";
		}
		if (business.getPayType().equals(PayTran.WX_MD.getText())) {
			relation = "wechatbusiness";
			bankorcloud = "wechatcode";
		}
		if (business.getPayType().equals(PayTran.ALI_FM.getText())) {
			relation = "alibusiness";
			bankorcloud = "alicode";
		}

		Date date = new Date();
//		 sql 原型  SELECT news.cardNo from (SELECT sum(p.price) as sumprice,p.cardNo as cardNo FROM  (SELECT * from bank where flag=1) b right  join  payorder p  on p.uid=b.cardNo where p.cardNo is not null and p.addTime>='2019-06-03 00:00:00' and p.flag=2 GROUP BY p.cardNo UNION ALL SELECT IFNULL(p.price,0) as sumprice,b.cardNo as cardNo from (SELECT * from bank where flag=1) b left join (SELECT p.* FROM  bank b right  join  payorder p  on p.uid=b.cardNo where p.cardNo is not null and p.addTime>='2019-06-03 00:00:00' and p.flag=2 ) p on b.cardNo=p.cardNo where p.cardNo is null ) as news right  join bankbusiness bk  on bk.cardNo  = news.cardNo right join bank ba on ba.cardNo = bk.cardNo  where bk.businessId=65 and ba.flag=1 and ba.maxLimit>=news.sumprice and if(ba.useEndTime>ba.useStartTime,ba.useStartTime<=2 and  ba.useEndTime>=2,((ba.useStartTime<=2 and 2>=ba.useEndTime) or (ba.useStartTime>2 and 2<=ba.useEndTime)))  ORDER BY news.sumprice asc LIMIT 0,1
		String sql = "SELECT news.cardNo from (SELECT sum(p.price) as sumprice,p.cardNo as cardNo FROM  (SELECT * from "
				+ bankorcloud
				+ " where flag=:bankflag3) b right  join  payorder p  on p.uid=b.cardNo where p.cardNo is not null and p.addTime>=:date1 and p.flag=:payorderflag1 GROUP BY p.cardNo UNION ALL SELECT IFNULL(p.price,0) as sumprice,b.cardNo as cardNo from (SELECT * from "
				+ bankorcloud + " where flag=:bankflag1) b left join (SELECT p.* FROM  " + bankorcloud
				+ " b right  join  payorder p  on p.uid=b.cardNo where p.cardNo is not null and p.addTime>=:date2 and p.flag=:payorderflag2 ) p on b.cardNo=p.cardNo where p.cardNo is null ) as news right  join "
				+ relation + " bk  on bk.cardNo  = news.cardNo right join " + bankorcloud
				+ " ba on ba.cardNo = bk.cardNo  where bk.businessId=:businessId and ba.flag=:bankflag2 and ba.maxLimit>=news.sumprice and if(ba.useEndTime>ba.useStartTime,ba.useStartTime<='"
				+ date.getHours() + "' and  ba.useEndTime>='" + date.getHours() + "',((ba.useStartTime<='"
				+ date.getHours() + "' and '" + date.getHours() + "'>=ba.useEndTime) or (ba.useStartTime>'"
				+ date.getHours() + "' and '" + date.getHours()
				+ "'<=ba.useEndTime))) ORDER BY news.sumprice asc LIMIT 0,1";

		Map<String, Object> params = new HashMap<>();
		Calendar cal_1 = Calendar.getInstance();// 获取当前日期
		cal_1.setTime(date);
		cal_1.set(Calendar.HOUR_OF_DAY, 0);
		cal_1.set(Calendar.MINUTE, 0);
		cal_1.set(Calendar.SECOND, 0);
		cal_1.set(Calendar.MILLISECOND, 0);
		params.put("date1", StringUtils.SDF.format(cal_1.getTime()));
		params.put("date2", StringUtils.SDF.format(cal_1.getTime()));
		params.put("payorderflag1", PayState.SUCCESS.getCode());
		params.put("payorderflag2", PayState.SUCCESS.getCode());
		params.put("bankflag1", State.NORMAL.getCode());
		params.put("bankflag2", State.NORMAL.getCode());
		params.put("bankflag3", State.NORMAL.getCode());
		params.put("businessId", business.getId());
		List<Map<String, Object>> remap = bankDao.findMapBySql(sql, params);
		if (remap != null && remap.size() == 1) {
			paymentApi.setCardNo(remap.get(0).get("cardNo").toString());
		}
	}

	private Object[] getListDate(List<Bank> list) {
		Object[] obj = new Object[list.size()];
		for (int i = 0; i < list.size(); i++)
			obj[i] = list.get(i).getCardNo();
		return obj;
	}

	@Override
	public Bank getBank(Bank bank) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Bank.class);

		if (!StringUtils.isBlank(bank.getCardNo()))
			dc.add(Restrictions.eq("cardNo", bank.getCardNo()));

		List<Bank> list = bankDao.findList(dc, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public BankBase getCloudflashover(BankBase bankBase, Class classs) throws Exception {

		DetachedCriteria dc = DetachedCriteria.forClass(classs);
		if (!StringUtils.isBlank(bankBase.getCardNo()))
			dc.add(Restrictions.eq("cardNo", bankBase.getCardNo()));

		List<BankBase> list = bankBaseDao.findList(dc, 0, 1);

		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

}
