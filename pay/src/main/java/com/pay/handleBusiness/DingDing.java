package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.Date;
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
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 钉钉支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class DingDing extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<PayOrder> payorderDao;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("merchant_no", business.getUid());
		map.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
		map.put("request_no", payOrder.getGameOrderNumber());
		map.put("amount", Double.toString(payOrder.getPrice()));
		map.put("pay_channel", business.getPayCode());
		map.put("request_time", Integer.toString(getSecondTimestampTwo(new Date())));
		map.put("notify_url", business.getNotifyUrl());
		map.put("return_url", business.getNotifyViewUrl());
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String s = getJson(request);
		JSONObject jsonObject = JSONObject.fromObject(s);

		String success = jsonObject.getString("success");// 是您在发起支付接口传入的您的自定义订单号

		if (!success.equals("true")) {
			logger.info("钉钉,api回调 状态不等于true result_code：" + s);
			return map;
		}
		jsonObject = JSONObject.fromObject(jsonObject.getString("data"));

		String status = jsonObject.getString("status"); // 是此订单在Api服务器上的唯一编号
		String request_no = jsonObject.getString("request_no"); // 支付金额
		String request_time = jsonObject.getString("request_time"); // 实际支付金额
		String order_no = jsonObject.getString("order_no"); // 描述
		String pay_time = jsonObject.getString("pay_time"); // 描述
		String amount = jsonObject.getString("amount"); // 描述
		String order_amount = jsonObject.getString("order_amount"); // 描述
		String merchant_no = jsonObject.getString("merchant_no"); // 描述
		String pay_channel = jsonObject.getString("pay_channel"); // 描述
		String nonce_str = jsonObject.getString("nonce_str"); // 描述
		String call_nums = jsonObject.getString("call_nums"); // 签名认证串
		String sign = jsonObject.getString("sign"); // 签名认证串
		if (!status.equals("3")) {
			logger.info("钉钉,api回调 状态不等于3 result_code：" + s);
			return map;
		}

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("status", status);
		vmap.put("request_no", request_no);
		vmap.put("request_time", request_time);
		vmap.put("order_no", order_no);
		vmap.put("pay_time", pay_time);
		vmap.put("amount", amount);
		vmap.put("order_amount", order_amount);
		vmap.put("merchant_no", merchant_no);
		vmap.put("pay_channel", pay_channel);
		vmap.put("nonce_str", nonce_str);
		vmap.put("call_nums", call_nums);
		logger.info("钉钉支付,api回调：" + vmap.toString());

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(request_no));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String str = getMapParam(vmap);
				String parameter = str + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.MD5Encoder(parameter).toUpperCase();
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

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("amount", map.get("amount"));
		vmap.put("pay_channel", map.get("pay_channel"));
		vmap.put("merchant_no", map.get("merchant_no"));
		vmap.put("request_no", map.get("request_no"));
		vmap.put("request_time", map.get("request_time"));
		vmap.put("nonce_str", map.get("nonce_str"));
		vmap.put("notify_url", map.get("notify_url"));
		vmap.put("return_url", map.get("return_url"));
		String str = getMapParam(vmap);
		String sign = Encryption.MD5Encoder(str + "&key=" + business.getToken());
		vmap.put("sign", sign.toUpperCase());
		String p = getMapParam(vmap);
		logger.info("api:" + business.getApiUrl());
		String s = ToolKit.request(business.getApiUrl(), p);
		try {
			JSONObject resultJsonObj = JSONObject.fromObject(s);
			String success = resultJsonObj.getString("success");
			JSONObject data = JSONObject.fromObject(resultJsonObj.getString("data"));
			if (success.equals("true")) {
				map.put("state", "SUCCESS");
				map.put("apiUrl", data.getString("bank_url"));
			} else {
				payOrderService.updatePayOrder(payOrder.getId(), s);
				logger.info("请求支付失败。" + payOrder.toString());
				logger.info("api 返回值" + s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("请求支付失败。" + payOrder.toString());
			logger.info("api 返回值" + s);
		}

	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "ALH5";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝跳转" };
	}

	public static void main(String[] args) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(
				"{\"success\":true,\"data\":{\"status\":\"3\",\"request_no\":\"2019050811614\",\"request_time\":\"1557321122\",\"order_no\":\"JSP7E5A1E67F144B15A9\",\"pay_time\":\"1557321160\",\"amount\":\"100.00\",\"merchant_no\":\"JSP1801120\",\"pay_channel\":\"ALH5\",\"order_amount\":\"100.00\",\"nonce_str\":\"5cd2d67d0d3e7\",\"call_nums\":3,\"sign\":\"8C3F438907C7F3C5A96EBF649E02188E\"}}");

		String status = jsonObject.getString("status");// 是您在发起支付接口传入的您的自定义订单号
		String request_no = jsonObject.getString("request_no"); // 是此订单在Api服务器上的唯一编号
		String request_time = jsonObject.getString("request_time"); // 支付金额
		String order_no = jsonObject.getString("order_no"); // 实际支付金额
		String pay_time = jsonObject.getString("pay_time"); // 描述
		String amount = jsonObject.getString("amount"); // 描述
		String order_amount = jsonObject.getString("order_amount"); // 描述
		String merchant_no = jsonObject.getString("merchant_no"); // 描述
		String pay_channel = jsonObject.getString("pay_channel"); // 描述
		String body = jsonObject.getString("body"); // 描述
		String nonce_str = jsonObject.getString("nonce_str"); // 描述
		String call_nums = jsonObject.getString("call_nums"); // 描述
		String sign = jsonObject.getString("sign"); // 签名认证串

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
