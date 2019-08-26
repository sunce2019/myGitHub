package com.pay.handleBusiness;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.dao.BaseDao;
import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;
import com.pay.vo.PaymentApi;

/**
 * KS
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class KS extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		double money = payOrderService.updateRecorDamount(new PaymentApi(payOrder.getPrice())); // 获取尾数
		map.put("money", doubleToString(money)); // 商户号
		map.put("name", "111");
		map.put("notify_url", business.getNotifyUrl());
		map.put("out_trade_no", payOrder.getGameOrderNumber());
		map.put("pid", business.getUid());
		map.put("return_url", business.getNotifyViewUrl());
		map.put("type", "wxpay");
		map.put("sitename", "111");
		map.put("sign_type", "MD5");
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("money", map.get("money")); // 商户号
		vmap.put("name", map.get("name"));
		vmap.put("notify_url", map.get("notify_url"));
		vmap.put("out_trade_no", map.get("out_trade_no"));
		vmap.put("pid", map.get("pid"));
		vmap.put("return_url", map.get("return_url"));
		vmap.put("type", map.get("type"));
		vmap.put("sitename", map.get("sitename"));
		String str = getMapParam(vmap);
		map.put("sign", Encryption.sign(str + business.getToken(), "UTF-8").toLowerCase());
		map.put("state", "SUCCESS");

	}

	private static String getUUID() {
		/*
		 * UUID uuid = UUID.randomUUID(); String str = uuid.toString(); // 去掉"-"符号
		 * String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14,
		 * 18) + str.substring(19, 23) + str.substring(24); return temp;
		 */

		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String pid = request.getParameter("pid");
		String trade_no = request.getParameter("trade_no");
		String out_trade_no = request.getParameter("out_trade_no");
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		String money = request.getParameter("money");
		String trade_status = request.getParameter("trade_status");
		String sign_type = request.getParameter("sign_type");
		String sign = request.getParameter("sign");
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pid", pid);
		vmap.put("trade_no", trade_no);
		vmap.put("out_trade_no", out_trade_no);
		vmap.put("type", type);
		vmap.put("name", name);
		vmap.put("money", money);
		vmap.put("trade_status", trade_status);
		// vmap.put("sign_type", sign_type);
		logger.info("KS支付,api回调：" + vmap.toString());

		if (!trade_status.equals("TRADE_SUCCESS")) {
			logger.info("KS,api回调 状态不等于TRADE_SUCCESS：" + vmap.toString());
			return map;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(out_trade_no));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(vmap) + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toLowerCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(money))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + sign);
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(parameter);
				}
			} else {
				logger.info("商务号不存在：" + vmap.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + vmap.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wechat_wap";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay_h5";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信扫码" };
	}

}
