package com.pay.handleBusiness;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * Ksk
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class Ksk extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("member_id", business.getUid());
		map.put("amount", Double.toString(payOrder.getPrice()));
		map.put("type_name", business.getPayCode());
		map.put("notify_url", business.getNotifyUrl());
		map.put("callback_url", business.getNotifyViewUrl());
		map.put("submit_order_id", payOrder.getGameOrderNumber());
		map.put("retPath", "pay");
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
		String resultJsonStr = ToolKit.request(business.getApiUrl(), map.get("reqParam"));
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("code");
		if (stateCode.equals("200")) {
			map.put("state", "SUCCESS");
			resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
			map.put("apiUrl", resultJsonObj.getString("qrcode"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
			logger.info(payOrder.toString());
			logger.info(resultJsonStr);
			map.put("msg", resultJsonObj.getString("msg"));
		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		String return_code = request.getParameter("return_code");
		String member_id = request.getParameter("member_id");
		String amount = request.getParameter("amount");
		String submit_order_id = request.getParameter("submit_order_id");
		String success_date = request.getParameter("success_date");
		String sign = request.getParameter("sign");
		if (StringUtils.isBlank(return_code) || StringUtils.isBlank(member_id) || StringUtils.isBlank(amount)
				|| StringUtils.isBlank(submit_order_id) || StringUtils.isBlank(success_date)
				|| StringUtils.isBlank(sign)) {
			return map;
		}

		TreeMap<String, String> mapv = new TreeMap<String, String>();
		mapv.put("return_code", return_code);
		mapv.put("member_id", member_id);
		mapv.put("amount", amount);
		mapv.put("submit_order_id", submit_order_id);
		mapv.put("success_date", success_date);
		String p = "return_code=" + return_code + ",member_id=" + member_id + ",amount=" + amount + ",submit_order_id="
				+ submit_order_id + ",success_date=" + success_date;
		logger.info("K2K,api回调：" + p);

		if (!return_code.equals("0")) {
			logger.info("KSK支付失败回调了。：" + p);
			return map;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(submit_order_id));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(mapv) + "&key=" + business.getToken();
				String key2 = Encryption.md5_32(parameter, "").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(amount))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "OK");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + sign);
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(parameter);
				}
			} else {
				logger.info("商务号不存在：" + p);
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + p);
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		TreeMap<String, String> mapv = new TreeMap<String, String>();
		mapv.put("amount", map.get("amount"));
		mapv.put("callback_url", map.get("callback_url"));
		mapv.put("member_id", map.get("member_id"));
		mapv.put("notify_url", map.get("notify_url"));
		mapv.put("submit_order_id", map.get("submit_order_id"));
		mapv.put("type_name", map.get("type_name"));
		String str = getMapParam(mapv) + "&key=" + business.getToken();
		str += "&sign=" + Encryption.md5_32(str, "").toUpperCase();
		map.put("reqParam", str);

	}

	public static void main(String[] args) {
		TreeMap<String, String> mapv = new TreeMap<String, String>();
		mapv.put("return_code", "0");
		mapv.put("member_id", "M031553684508");
		mapv.put("amount", "50.0000");
		mapv.put("submit_order_id", "FC2019040810010");
		mapv.put("success_date", "1554709770");
		String parameter = getMapParam(mapv) + "&key=e677e6e27b50b41cd3017eb123fcdbc6";
		String key2 = Encryption.md5_32(parameter, "").toUpperCase();
		System.out.println(key2);
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wechat";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码" };
	}

}
