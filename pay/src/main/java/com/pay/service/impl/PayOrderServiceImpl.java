package com.pay.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.dao.MemberDao;
import com.pay.enums.CallBackGameStage;
import com.pay.enums.ConfigCode;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.enums.PayType;
import com.pay.handlePay.GameApi;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.BankBusiness;
import com.pay.model.BankRelation;
import com.pay.model.Business;
import com.pay.model.CloudBusiness;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.PayOrder;
import com.pay.model.RecorDamount;
import com.pay.model.SystConfig;
import com.pay.model.Users;
import com.pay.model.WechatBusiness;
import com.pay.model.WechatCode;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.service.SystemService;
import com.pay.util.Encryption;
import com.pay.util.PayUtils;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.vo.PaymentApi;

import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年3月28日下午6:29:03
 */
@Service("payOrderService")
public class PayOrderServiceImpl implements PayOrderService {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private BaseDao<RecorDamount> recorDamountDao;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<BankBase> bankBaseDao;
	@Autowired
	private BaseDao<BankRelation> bankRelationDao;
	@Autowired
	private BaseService baseService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PayOrder addPayOrder(PaymentApi paymentApi, Business business) throws Exception {
		PayOrder payOrder = new PayOrder();
		payOrder.setUserName(paymentApi.getUserName());
		payOrder.setOrderType(business.getPayType());
		payOrder.setUid(business.getUid());
		payOrder.setPrice(paymentApi.getPrice());
		payOrder.setAddTime(new Date());
		payOrder.setFlag(PayState.INIT.getCode());
		payOrder.setCallGameFlag(CallBackGameStage.INIT.getCode());
		payOrder.setCallGameNumber(0);
		payOrder.setIp(paymentApi.getIp());
		if (business.getName().equals(PayType.SUBAO.getText())) { // 如果是速宝支付
			payOrder.setOrderNumber(memberDao.getOrderNumber());
			payOrder.setGameOrderNumber(paymentApi.getOrderid());
			if (payOrder.getOrderType().contains("支付宝")) {
				payOrder.setOrderNumber("ZFB" + payOrder.getOrderNumber());
			} else if (payOrder.getOrderType().contains("微信买单")) {
				payOrder.setOrderNumber("MD" + payOrder.getOrderNumber());
				paymentApi.setRemarks("第四方订单;");
			} else if (payOrder.getOrderType().contains("微信")) {
				payOrder.setOrderNumber("WX" + payOrder.getOrderNumber());
			} else if (payOrder.getOrderType().contains("云闪付")) {
				payOrder.setOrderNumber("YSF" + payOrder.getOrderNumber());
				String remarks = paymentApi.getRemarks() == null ? "" : paymentApi.getRemarks();
				paymentApi.setRemarks(remarks + "未获取二维码;");
			}
		} else {
			payOrder.setOrderNumber(memberDao.getOrderNumber());
			payOrder.setGameOrderNumber(paymentApi.getOrderid());
		}

		payOrder.setGoodsName("maifeiji");
		payOrder.setCallbackapiurl(paymentApi.getCallbackapiurl());
		payOrder.setCallbackviewurl(paymentApi.getCallbackviewurl());
		payOrder.setCustomer_id(paymentApi.getCustomer_id());
		payOrder.setPayName(business.getName());
		payOrder.setTime(paymentApi.getTime());
		payOrder.setCustomer_name(paymentApi.getCustomer_name());
		payOrder.setCardNo(paymentApi.getCardNo());
		payOrder.setDepositorName(paymentApi.getDepositorName());
		payOrder.setRealprice(paymentApi.getRealprice());
		payOrder.setRemarks(paymentApi.getRemarks());
		payOrder.setUseragent(paymentApi.getUseragent());
		payOrder.setVip(paymentApi.getVip());
		payOrderDao.save(payOrder);

		if (business.getBankPattern() != null && business.getBankPattern().intValue() == 3) {// 订单轮询方式，每次创建订单，轮询银行卡
			BankCount(payOrder);
		}

		return payOrder;
	}

	public PayOrder getPayOrder(PayOrder payOrder) throws Exception {
		if (payOrder == null)
			return null;

		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		if (!StringUtils.isBlank(payOrder.getGameOrderNumber()))
			dc.add(Restrictions.eq("gameOrderNumber", payOrder.getGameOrderNumber()));
		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.CALLBACKTIME.getCode(), 1));
		if (systConfig != null) {
			Calendar nowTime = Calendar.getInstance();
			nowTime.add(Calendar.MINUTE, StringUtils.unAbs(Integer.parseInt(systConfig.getContent())));
			dc.add(Restrictions.ge("addTime", nowTime.getTime())); // 回调只能 回调半小时内的订单
		}

		return payOrderDao.findObj(dc);
	}

	public PayOrder getPayOrder2(PayOrder payOrder) {
		if (payOrder == null)
			return null;
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		if (!StringUtils.isBlank(payOrder.getGameOrderNumber()))
			dc.add(Restrictions.eq("gameOrderNumber", payOrder.getGameOrderNumber()));
		return payOrderDao.findObj(dc);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePayOrder(PayOrder payOrder, String remarks) throws Exception {
		PayOrder nowpayOrder = payOrderDao.get(PayOrder.class, payOrder.getId(), LockMode.UPGRADE);
		nowpayOrder.setRemarks(nowpayOrder.getRemarks() + remarks + ";");
		payOrderDao.update(nowpayOrder);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePayOrder(PayOrder payOrder) throws Exception {
		PayOrder nowpayOrder = payOrderDao.get(PayOrder.class, payOrder.getId(), LockMode.UPGRADE);
		nowpayOrder.setRemarks(payOrder.getRemarks().replace("未获取二维码;", ""));
		payOrderDao.update(nowpayOrder);
	}

	@Override
	public PayOrder payOrder(PayOrder payOrder) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		if (!StringUtils.isBlank(payOrder.getTime()))
			dc.add(Restrictions.eq("time", payOrder.getTime()));
		if (!StringUtils.isBlank(payOrder.getOrderNumber()))
			dc.add(Restrictions.eq("orderNumber", payOrder.getOrderNumber()));
		if (payOrder.getFlag() != null && payOrder.getFlag() > 0)
			dc.add(Restrictions.eq("flag", payOrder.getFlag()));
		if (payOrder.getCallGameFlag() != null && payOrder.getCallGameFlag() > 0)
			dc.add(Restrictions.eq("callGameFlag", payOrder.getCallGameFlag()));
		return payOrderDao.findObj(dc);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ReturnBan upperScore(int id, Users users, boolean manual, Double upperMoney) throws Exception {
		PayOrder payOrder = payOrderDao.get(PayOrder.class, id, LockMode.UPGRADE);
		if (!PayUtils.isOwnPay(payOrder.getOrderType()))
			return new ReturnBan("系统异常", false);

		logger.info("开始回调上分：" + payOrder.toString() + "user:" + users.toString());
		if (upperMoney != null && upperMoney > 0)
			payOrder.setRealprice(upperMoney); // 手动输入

		if (manual && upperMoney > (payOrder.getPrice() + 1)) {// 手动上分
			return new ReturnBan("金额错误,不能输入大于订单金额+1", false);
		}

		if (manual && ((new Date().getTime() - payOrder.getAddTime().getTime()) / 60 / 1000) > 60) {// 超过30分钟 不接受手动。
			return new ReturnBan("订单超时，不能处理。", false);
		}

		if (payOrder.getFlag() != PayState.INIT.getCode()) {
			logger.info("该订单状态发生改变：" + payOrder.toString() + " 操作人：" + users.getName());
			return new ReturnBan("上分失败", false);
		}

		if ((!manual && payOrder.getRemarks().contains("第二方"))
				|| payOrder.getRemarks().contains("第二方") && payOrder.getPrice().equals(upperMoney)) {
			if (!gameApi.zbCallBackGame(payOrder.getId(), true,
					customerDao.get(Customer.class, payOrder.getCustomer_id()), manual)) { // 第二方上分
				return new ReturnBan("上分失败", false);
			}
		} else if (payOrder.getRemarks().contains("第四方") || payOrder.getRemarks().contains("第二方")) { // 第四方上分 和第二方上分
																										// 第二方人工输入金额和
																										// 订单金额不相等。走四方上分
			if (!gameUpperScore(payOrder)) { // 第四方上分
				return new ReturnBan("上分失败", false);
			}
		} else {
			if (!gameApi.callBackGame(payOrder.getId(), true, false)) {
				return new ReturnBan("上分失败", false);
			}
		}

		Date date = new Date();
		payOrder.setFlag(PayState.SUCCESS.getCode());
		payOrder.setCallbackTime(date);
		payOrder.setCallGameSUCTime(date);
		payOrder.setCallGameFlag(CallBackGameStage.SUCCESS.getCode());

		payOrder.setRemarks(
				payOrder.getRemarks() + (users.getName().equals("自动上分") ? "上分人：" : "手动:") + users.getName());
		payOrderDao.update(payOrder);

		Business business = businessService
				.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
		if (business.getBankPattern() != null && business.getBankPattern().intValue() == 1)// 只有一成一换模式 支付成功 增加次数
			BankCount(payOrder);
		logger.info("上分成功：" + payOrder.toString() + " 操作人：" + users.getName());

		return new ReturnBan("上分成功", true);
	}

	/**
	 * 转银行卡类 增加转账成功次数
	 * 
	 * @param payOrder
	 */
	public void BankCount(PayOrder payOrder) throws Exception {
		if (!StringUtils.isBlank(payOrder.getCardNo())) {
			BankBase bank = new Bank(); // 父级 银行卡类
			Class bankClasss = Bank.class;
			Class bankRelationClass = BankBusiness.class;

			if (payOrder.getOrderType().equals(PayTran.YSF.getText())) {
				bank = new CloudFlasHover();
				bankClasss = CloudFlasHover.class;
				bankRelationClass = CloudBusiness.class;
			}
			if (payOrder.getOrderType().equals(PayTran.WX_MD.getText())) {
				bank = new WechatCode();
				bankClasss = WechatCode.class;
				bankRelationClass = WechatBusiness.class;
			}

			if (!getALLYHK(payOrder, bankRelationClass, bankClasss)) {
				DetachedCriteria dc = DetachedCriteria.forClass(bankClasss);
				dc.add(Restrictions.eq("cardNo", payOrder.getCardNo()));
				List<BankBase> bankBaseList = bankBaseDao.findList(dc, 0, 1);
				if (bankBaseList.size() > 0) {
					BankBase bankBase = bankBaseDao.get(bankClasss, bankBaseList.get(0).getId(), LockMode.UPGRADE);
					bankBase.setCashierCount(bankBase.getCashierCount() + 1);
					bankBaseDao.save(bankBase);
				}
			}

		}
	}

	private boolean getALLYHK(PayOrder payOrder, Class bankRelationClass, Class bankClasss) throws Exception {
		Business business = businessService
				.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
		DetachedCriteria dc = DetachedCriteria.forClass(bankRelationClass);

		dc.add(Restrictions.eq("businessId", business.getId()));
		dc.setProjection(Projections.groupProperty("cardNo"));

		List bBankRelationList = bankRelationDao.findList(dc);

		dc = DetachedCriteria.forClass(bankClasss);
		dc.add(Restrictions.in("cardNo", bBankRelationList));
		List<BankBase> bankBaseList = bankBaseDao.findList(dc);

		boolean flag = false;
		for (BankBase b : bankBaseList) {
			if (b.getCashierCount() > 1) {
				flag = true;
				break;
			}
		}
		if (flag) {
			for (BankBase b : bankBaseList) {
				b.setCashierCount(0);
				bankBaseDao.update(b);
			}
		}

		return flag;
	}

	private boolean gameUpperScore(PayOrder payOrder) throws Exception {
		Business business = businessService
				.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
		String url = business.getApiUrl() + "/fourth_payment_platform/pay/addMoney";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("app_id", business.getUid());
		map.put("account", payOrder.getUserName());
		map.put("money", Double.toString(payOrder.getRealprice()));
		map.put("order_no", payOrder.getOrderNumber());
		map.put("status", "0");
		map.put("type", "0");
		map.put("tradeType", "1");
		String p = getMapParam(map);
		p += "&sign=" + Encryption.sign(p + "&key=" + business.getToken(), "UTF-8").toLowerCase();
		p += "&remark=" + "测试";

		try {
			String re = ToolKit.request(url, p);
			JSONObject resultJsonObj = JSONObject.fromObject(re);
			if (resultJsonObj.getString("status").equals("true")) {
				logger.info("中博第二方上分成功：" + payOrder.toString());
				return true;
			} else {
				logger.info("上分接口失败：" + re);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("上分接口失败：");
			return false;
		}

	}

	private boolean zbquery(String loginName, PayOrder payOrder) throws Exception {
		Business business = businessService
				.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
		String url = business.getApiUrl() + "/fourth_payment_platform/pay/checkAccount";
		Map<String, String> map = new TreeMap<String, String>();
		map.put("app_id", business.getUid());
		map.put("account", loginName);
		String p = getMapParam(map);
		p += "&sign=" + Encryption.sign(p + "&key=" + business.getToken(), "GB2312").toLowerCase();
		String re = ToolKit.request(url, p);
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		if (resultJsonObj.getString("status").equals("true")) {
			return true;
		} else {
			logger.info("查询中博接口失败：" + re);
			return false;
		}
	}

	private static String getMapParam(Map<String, String> mapv) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : mapv.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		return s.substring(0, s.length() - 1);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Double updateRecorDamount(PaymentApi paymentApi) throws Exception {
		Double returnPrice = 0d;
		DetachedCriteria dc = DetachedCriteria.forClass(RecorDamount.class);
		dc.add(Restrictions.eq("price", paymentApi.getPrice()));
		dc.addOrder(Order.asc("id"));
		List<RecorDamount> list = recorDamountDao.findList(dc);
		if (list.size() > 0) {
			RecorDamount rd = recorDamountDao.get(RecorDamount.class, list.get(0).getId(), LockMode.UPGRADE);
			Double price = rd.getCumulative() + 0.01;
			if (price >= 0.51)
				price = 0.01;
			rd.setCumulative(price);
			returnPrice = paymentApi.getPrice() + price;
			recorDamountDao.update(rd);
		} else {
			returnPrice = paymentApi.getPrice() + 0.01;
			recorDamountDao.save(new RecorDamount(paymentApi.getPrice(), 0.01));
		}
		return returnPrice;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updatePay(int id) throws Exception {
		logger.info("手动上分修改订单状态");
		PayOrder payOrder = payOrderDao.get(PayOrder.class, id, LockMode.UPGRADE);
		if (payOrder == null) {
			logger.info("订单不存在");
		}
		if (payOrder.getFlag() != PayState.INIT.getCode()
				|| payOrder.getCallGameFlag() != CallBackGameStage.INIT.getCode()) {
			logger.info("订单状态发生变化:" + payOrder.toString());
		}
		if (!payOrder.getOrderType().equals(PayTran.ZFB_BANK.getText())
				&& !payOrder.getOrderType().equals(PayTran.WX_BANK.getText())) {
			logger.info("该订单不是支付宝　微信　转银行卡，不能上分：" + payOrder.toString());
			return false;
		}
		payOrder.setFlag(PayState.SUCCESS.getCode());
		payOrder.setCallGameFlag(CallBackGameStage.MOVE.getCode());
		payOrder.setCallbackTime(new Date());
		payOrderDao.update(payOrder);
		logger.info("手动上分修改订单状态成功：" + payOrder.toString());
		return true;
	}

	@Override
	public void updatePayOrder(int id, String remarks) throws Exception {
		PayOrder nowpayOrder = payOrderDao.get(PayOrder.class, id, LockMode.UPGRADE);
		nowpayOrder.setReturnError(remarks);
		payOrderDao.update(nowpayOrder);
	}

	@Override
	public boolean queryPay(String time, String gameOrderNumber) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		dc.add(Restrictions.or(Restrictions.eq("time", time), Restrictions.eq("gameOrderNumber", gameOrderNumber)));
		List<PayOrder> list = payOrderDao.findList(dc);
		if (list.size() > 0)
			return true;
		return false;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ReturnBan manualCallBackGame2(int id, Double upperMoney, String name) throws Exception {
		PayOrder payOrder = payOrderDao.get(PayOrder.class, id, LockMode.UPGRADE);
		if (upperMoney > payOrder.getPrice() * 2)
			return new ReturnBan("非法金额", false);
		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.MANUAL.getCode(), 1));
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, StringUtils.unAbs(Integer.parseInt(systConfig.getContent())));
		if (payOrder.getAddTime().getTime() > nowTime.getTime().getTime() && payOrder.getFlag().intValue() == 1) {
			payOrder.setCallbackTime(new Date());
			payOrder.setFlag(PayState.SUCCESS.getCode());
			payOrder.setCallGameFlag(CallBackGameStage.MOVE.getCode());
			payOrder.setCallGameNumber(1);
			payOrder.setRemarks(payOrder.getRemarks() + "手动回调人：" + name + ";");
			payOrder.setRealprice(upperMoney);
			payOrderDao.update(payOrder);

			if (gameApi.callBackGame(payOrder.getId(), false, false)) {
				return new ReturnBan("回调上分成功", true);
			}

		} else {
			return new ReturnBan("状态被改变，或已超时", false);
		}
		throw new Exception("手动回调游乐场失败，进行回滚");
	}

}
