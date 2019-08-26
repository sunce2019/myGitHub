package com.pay.handleBusiness;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

/**
 * 海鑫
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class HaiXing extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("pay_memberid", business.getUid()); // 商户号
		map.put("pay_applydate", StringUtils.SDF.format(new Date()));
		map.put("pay_orderid", payOrder.getGameOrderNumber());
		map.put("pay_bankcode", business.getPayCode());
		map.put("pay_notifyurl", business.getNotifyUrl());
		map.put("pay_callbackurl", business.getNotifyViewUrl());
		map.put("pay_amount", Double.toString(payOrder.getPrice()));
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pay_memberid", map.get("pay_memberid")); // 商户号
		vmap.put("pay_orderid", map.get("pay_orderid"));
		vmap.put("pay_applydate", map.get("pay_applydate"));
		vmap.put("pay_bankcode", map.get("pay_bankcode"));
		vmap.put("pay_notifyurl", map.get("pay_notifyurl"));
		vmap.put("pay_callbackurl", map.get("pay_callbackurl"));
		vmap.put("pay_amount", map.get("pay_amount"));
		String str = getMapParam(vmap);
		map.put("pay_md5sign", Encryption.md5_32(str + "&key=" + business.getToken(), "").toUpperCase());
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
		String memberid = request.getParameter("memberid");
		String appid = request.getParameter("appid");
		String submchid = request.getParameter("submchid");
		String orderid = request.getParameter("orderid");
		String amount = request.getParameter("amount");
		String datetime = request.getParameter("datetime");
		String returncode = request.getParameter("returncode");
		String attach = request.getParameter("attach");
		String sign = request.getParameter("sign");
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("memberid", memberid);
		vmap.put("orderid", orderid);
		vmap.put("amount", amount);
		vmap.put("datetime", datetime);
		vmap.put("returncode", returncode);
		logger.info("海鑫支付,api回调：" + vmap.toString());
		if (!returncode.equals("00")) {
			logger.info("海鑫,api回调 状态不等于2：" + vmap.toString());
			return map;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(vmap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter, "").toUpperCase();
				sign = key2; // 第三方不给回签字段，第三方提供的字段是错的，老虎同意不回签。
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(amount))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "SUCCESS");
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

	public static void main(String[] args) {
		String s = "amount=50.0000&datetime=20190423154441&memberid=10066&orderid=1000001382724619&returncode=00&key=kz54oi7f65axd7lskyxm97dvg4zh0mmu";
		String key2 = Encryption.md5_32(s, "").toUpperCase();
		System.out.println(key2);
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
				return "901";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "901";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "911";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信H5", "银联扫码" };
	}

}
