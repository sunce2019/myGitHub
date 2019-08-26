package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.AmountUtils;
import com.pay.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 小美支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class XiaoMei extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("accessToken", getAccessToken(business)); // 商户号
		map.put("outTradeNo", payOrder.getGameOrderNumber());
		map.put("money", AmountUtils.changeY2F(Double.toString(payOrder.getPrice())));
		map.put("type", "T0");
		map.put("body", "飞机");
		map.put("detail", "大炮");
		map.put("notifyUrl", business.getNotifyUrl());
		map.put("productId", "121");
		map.put("successUrl", business.getNotifyViewUrl());
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String param = getJson(request);
		JSONObject resultJsonObj = JSONObject.fromObject(param);
		String no = resultJsonObj.getString("no");
		String outTradeNo = resultJsonObj.getString("outTradeNo");
		String merchantNo = resultJsonObj.getString("merchantNo");
		String productId = resultJsonObj.getString("productId");
		String money = resultJsonObj.getString("money");
		String body = resultJsonObj.getString("body");
		String detail = resultJsonObj.getString("detail");
		String tradeType = resultJsonObj.getString("tradeType");
		String date = resultJsonObj.getString("date");
		String nonce = resultJsonObj.getString("nonce");
		String timestamp = resultJsonObj.getString("timestamp");
		String success = resultJsonObj.getString("success");
		String sign = resultJsonObj.getString("sign");

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", merchantNo);
		metaSignMap.put("no", no);
		metaSignMap.put("nonce", nonce);
		metaSignMap.put("timestamp", timestamp);

		if (StringUtils.isBlank(merchantNo) || StringUtils.isBlank(no) || StringUtils.isBlank(nonce)
				|| StringUtils.isBlank(timestamp) || StringUtils.isBlank(sign)) {
			logger.info("参数不完整：" + metaSignMap.toString());
			return map;
		}

		if (!success.equals("true")) {
			logger.info("小美,api回调 状态不等于true：" + metaSignMap.toString());
			return map;
		}

		logger.info("小美,api回调：" + metaSignMap.toString());
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(outTradeNo));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(metaSignMap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = MD5(parameter).toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					Long l = Long.parseLong(money);
					money = AmountUtils.changeF2Y(l);
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(money))) {
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
				logger.info("商务号不存在：" + metaSignMap.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + metaSignMap.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		SortedMap<String, Object> p = new TreeMap<>();
		p.put("accessToken", map.get("accessToken"));

		SortedMap<String, Object> paramMap = new TreeMap<>();
		paramMap.put("outTradeNo", map.get("outTradeNo"));
		paramMap.put("money", Double.parseDouble(map.get("money")));
		paramMap.put("type", map.get("type"));
		paramMap.put("body", map.get("body"));
		paramMap.put("detail", map.get("detail"));
		paramMap.put("notifyUrl", map.get("notifyUrl"));
		paramMap.put("successUrl", map.get("successUrl"));
		paramMap.put("callbackSuccessUrl", "http://szceft.natappfree.cc/gopay/callback");
		paramMap.put("productId", map.get("productId"));

		p.put("param", paramMap);
		String a = JSONArray.fromObject(p).toString();
		String s = ToolKit.requestJson(business.getApiUrl() + typeConversion(payOrder), a.substring(1, a.length() - 1));
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String stateCode = resultJsonObj.getString("success");
		if (stateCode.equals("true")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("value"));
		} else {
			logger.info(payOrder.toString());
			logger.info(s);
			map.put("msg", resultJsonObj.getString("message"));
			payOrderService.updatePayOrder(payOrder.getId(), s);
		}
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "open/v1/order/alipayWapPay";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "open/v1/order/alipayScan";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "open/v1/order/wechatScan";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "open/v1/order/wechatWapPay";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "open/v1/order/unionpayScan";
			} else if (payOrder.getOrderType().contains(PayTran.QQ.getText())) {
				return "open/v1/order/qqScan";
			} else if (payOrder.getOrderType().contains(PayTran.JD_WAP.getText())) {
				return "open/v1/order/jdPay";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转", "银联扫码", "QQ钱包扫码", "京东跳转" };
	}

	private static String getAccessToken(Business business) {
		SortedMap<String, Object> paramMap = new TreeMap<>();
		paramMap.put("merchantNo", business.getUid());
		paramMap.put("nonce", UUID.randomUUID().toString().substring(0, 8));
		paramMap.put("timestamp", Long.toString(new Date().getTime()));
		StringBuilder sb = new StringBuilder();
		paramMap.forEach((k, v) -> sb.append(k + "=" + v + "&"));
		String signStr = sb.append("key=" + business.getToken()).toString();
		String sign = MD5(signStr);
		paramMap.put("sign", sign);
		Map<String, Object> resultMap = new RestTemplate()
				.postForObject("http://api.qwebank.top/open/v1/getAccessToken/merchant", paramMap, Map.class);
		if (!resultMap.containsKey("success")) {
			logger.info("获取Token失败：" + resultMap.toString());
		}
		if ((boolean) resultMap.get("success")) {
			Map<String, Object> valueMap = (Map<String, Object>) resultMap.get("value");
			String accessToken = (String) valueMap.get("accessToken");
			return accessToken;
		} else {
			logger.info("获取Token失败：" + resultMap.toString());
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		Long l = Long.parseLong("199800000");
		String money = AmountUtils.changeF2Y(l);

		System.out.println(money);
	}

	public static String MD5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(s.getBytes("utf-8"));
			return toHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHex(byte[] bytes) {
		final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}

}
