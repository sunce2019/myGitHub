package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.Date;
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
 * 鸡汤2支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class JiTang2 extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("payType", business.getPayCode());
		map.put("price", changeY2F(payOrder.getPrice()));
		map.put("orderNO", payOrder.getGameOrderNumber());
		map.put("appid", business.getUid());
		map.put("subject", "标题");
		map.put("body", "内容");
		map.put("orderTime", StringUtils.SDF.format(new Date().getTime()));
		map.put("notifyUrl", business.getNotifyUrl());
		map.remove("state");
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		String price = request.getParameter("price");
		String orderNo = request.getParameter("orderNo");
		String sysOrderNo = request.getParameter("sysOrderNo");
		String orderStatus = request.getParameter("orderStatus");
		String sign = request.getParameter("sign");

		StringBuffer sb = new StringBuffer();
		sb.append(price).append(orderNo).append(sysOrderNo).append("&");

		logger.info("鸡汤2支付,api回调：" + sb.toString() + ":单号：" + sysOrderNo);

		if (!orderStatus.equals("0")) {
			logger.info("鸡汤2,api回调 状态不等于0result_code：" + sb.toString());
			return map;
		}
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderNo));
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
				payQueryMap.put("sysOrderNo", sysOrderNo);

				if (StringUtils.equals(key2, sign) && payQuery(payQueryMap, business, payOrder)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(price) / 100)) {
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
		String s = ToolKit.getPostResponse(map.get("apiUrl").replace("pay", "search"), map);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		if (resultJsonObj.getString("code").equals("1"))
			return true;
		logger.info("查询订单未支付：" + s);
		System.out.println(s);
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("appid")).append(map.get("price")).append(map.get("orderNO")).append(map.get("orderTime"))
				.append(map.get("notifyUrl")).append("&").append(business.getToken());
		String sign = Encryption.sign(sb.toString(), "UTF-8").toLowerCase();
		map.put("sign", sign);
		String s = ToolKit.getPostResponse(business.getApiUrl(), map);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String code = resultJsonObj.getString("code");
		if (code.equals("1")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("url"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			logger.info("请求支付失败。" + payOrder.toString());
			logger.info("api 返回值" + s);
			map.put("msg", s);
		}
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "ZFB";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "WX";
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
