package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.controller.BaseController;
import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.AmountUtils;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * GT支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class Gt extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("mcnNum", business.getUid()); // 商户号
		map.put("orderId", payOrder.getGameOrderNumber());
		map.put("payType", business.getPayCode());
		map.put("returnUrl", business.getNotifyViewUrl());
		map.put("backUrl", business.getNotifyUrl());
		map.put("amount", AmountUtils.changeY2F(Double.toString(payOrder.getPrice())));
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String json = getJson(request);
		// vmap.put("sign_type", sign_type);
		logger.info("GT支付,api回调：" + json);
		JSONObject resultJsonObj = JSONObject.fromObject(json);
		String payStatus = resultJsonObj.getString("payStatus");
		if (!payStatus.equals("1")) {
			logger.info("GT,api回调 状态不等于result_code：" + json);
			return map;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("orderId=").append(resultJsonObj.getString("orderId")).append("&payTime=")
				.append(resultJsonObj.getString("payTime")).append("&payStatus=")
				.append(resultJsonObj.getString("payStatus"));
		String sign = resultJsonObj.getString("sign");
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(resultJsonObj.getString("orderId")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = sb.toString() + "&secreyKey=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					Long l = Long.parseLong(resultJsonObj.getString("amount"));
					String money = AmountUtils.changeF2Y(l);
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
				logger.info("商务号不存在：" + sb.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + sb.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, Object> vmap = new HashMap<String, Object>();
		vmap.put("amount", Integer.parseInt(map.get("amount")));
		vmap.put("backUrl", map.get("backUrl"));
		vmap.put("mcnNum", map.get("mcnNum"));
		vmap.put("orderId", map.get("orderId"));
		vmap.put("payType", Integer.parseInt(map.get("payType")));
		vmap.put("ip", payOrder.getIp());

		StringBuffer sb = new StringBuffer();
		sb.append("mcnNum=").append(map.get("mcnNum")).append("&orderId=").append(map.get("orderId"))
				.append("&backUrl=").append(map.get("backUrl")).append("&payType=").append(map.get("payType"))
				.append("&amount=").append(map.get("amount")).append("&secreyKey=").append(business.getToken());

		String sign = Encryption.sign(sb.toString(), "UTF-8").toUpperCase();
		vmap.put("sign", sign);
		String jsonstr = BaseController.toJson(vmap);

		String s = ToolKit.requestJson(business.getApiUrl(), jsonstr);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String status = resultJsonObj.getString("status");
		if (status.equals("0")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("qrCode")));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			logger.info(payOrder.toString());
			logger.info(s);
			map.put("msg", resultJsonObj.getString("message"));
		}
		System.out.println(s);
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "12";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "2";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "1";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "11";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转" };
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
