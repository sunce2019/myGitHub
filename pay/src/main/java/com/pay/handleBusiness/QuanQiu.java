package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

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
 * 全球支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class QuanQiu extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("parter", business.getUid());
		map.put("type", business.getPayCode());
		map.put("value", doubleToString(payOrder.getPrice()));
		map.put("orderid", payOrder.getGameOrderNumber());
		map.put("callbackurl", business.getNotifyUrl());
		map.put("hrefbackurl", business.getNotifyViewUrl());
		map.put("apiUrl", business.getApiUrl());
		map.remove("state");
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("全球支付,api回调参数：" + parame.toString());
		if (!parame.get("opstate").equals("0")) {
			logger.info("全球,api回调 状态不等于 0：" + parame.toString());
			return map;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("orderid=").append(parame.get("orderid")).append("&opstate=").append(parame.get("opstate"))
				.append("&ovalue=").append(parame.get("ovalue"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderid")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = sb.toString() + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toLowerCase();
				Map<String, String> payQueryMap = new HashMap<String, String>();
				payQueryMap.put("apiUrl", business.getApiUrl());
				payQueryMap.put("orderid", parame.get("orderid"));
				payQueryMap.put("parter", business.getUid());
				payQueryMap.put("key", business.getToken());
				if (StringUtils.equals(key2, parame.get("sign")) && payQuery(payQueryMap, business, payOrder)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(parame.get("ovalue")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "ok");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("sign"));
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(parameter);
				}
			} else {
				logger.info("商务号不存在：" + sb.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + sb.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("orderid=").append(map.get("orderid")).append("&parter=").append(map.get("parter"));
		String sign = Encryption.sign(sb.toString() + map.get("key"), "UTF-8").toLowerCase();
		sb.append("&sign=").append(sign);
		String s = ToolKit.request(map.get("apiUrl").replace("Pay/GateWay", "Order/Search"), sb.toString());
		logger.info("查询返回：" + s);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		if (resultJsonObj.getString("opstate").equals("0"))
			return true;
		logger.info("查询订单返回：" + s);
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("parter=").append(map.get("parter")).append("&type=").append(map.get("type")).append("&value=")
				.append(map.get("value")).append("&orderid=").append(map.get("orderid")).append("&callbackurl=")
				.append(map.get("callbackurl")).append(business.getToken());
		String sign = Encryption.sign(sb.toString(), "UTF-8").toLowerCase();
		map.put("sign", sign);
		map.put("state", "SUCCESS");
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "992";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "1004";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "1007";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "1006";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转" };
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
