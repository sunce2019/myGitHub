package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.Date;
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
 * 万鼎支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class WanDing extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("mer_id", business.getUid());
		map.put("timestamp", StringUtils.SDF.format(new Date()));
		map.put("terminal", business.getPayCode());
		map.put("version", "01");
		map.put("amount", changeY2F(payOrder.getPrice()));
		map.put("backurl", business.getNotifyViewUrl());
		map.put("failUrl", business.getNotifyViewUrl().replace("success", "error"));
		map.put("ServerUrl", business.getNotifyUrl());
		map.put("businessnumber", payOrder.getGameOrderNumber());
		map.put("goodsName", "feiji");
		map.put("IsCredit", "0");
		map.put("BankCode", "0");
		map.put("ProductType", "1");
		map.put("UserId", payOrder.getUserName());
		map.put("UserRealName", "");
		map.put("UserRealMobile", "");
		map.put("UserRealBankCard", "");
		map.put("UserRealIdNumber", "");
		map.remove("state");
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("万鼎支付,api回调参数：" + parame.toString());
		if (!parame.get("status").equals("成功")) {
			logger.info("万鼎,api回调 状态不等于 0：" + parame.toString());
			return map;
		}

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("result", parame.get("result"));
		vmap.put("code", parame.get("code"));
		vmap.put("msg", parame.get("msg"));
		vmap.put("businessnumber", parame.get("businessnumber"));
		vmap.put("status", parame.get("status"));
		vmap.put("transactiondate", parame.get("transactiondate"));
		vmap.put("amount", parame.get("amount"));
		vmap.put("real_amount", parame.get("real_amount"));
		vmap.put("transactiontype", parame.get("transactiontype"));
		vmap.put("inputdate", parame.get("inputdate"));
		vmap.put("remark", parame.get("remark"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("businessnumber")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String s = getMapParam(vmap);
				String parameter = s + "&" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toUpperCase();
				Map<String, String> payQueryMap = new HashMap<String, String>();
				payQueryMap.put("apiUrl", business.getApiUrl());
				payQueryMap.put("key", business.getToken());
				payQueryMap.put("mer_id", parame.get("mer_id"));
				payQueryMap.put("timestamp", parame.get("timestamp"));
				payQueryMap.put("version", parame.get("version"));
				payQueryMap.put("businessnumber", parame.get("businessnumber"));
				payQueryMap.put("sign_type", "md5");

				if (StringUtils.equals(key2, parame.get("sign")) && payQuery(payQueryMap, business, payOrder)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							changeF2Y(parame.get("amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("sign"));
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(parameter);
				}
			} else {
				logger.info("商务号不存在：" + parame.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + parame.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> payQueryMap = new HashMap<String, String>();
		payQueryMap.put("mer_id", map.get("mer_id"));
		payQueryMap.put("timestamp", map.get("timestamp"));
		payQueryMap.put("terminal", map.get("terminal"));
		payQueryMap.put("version", map.get("version"));
		payQueryMap.put("businessnumber", map.get("businessnumber"));
		String s = getMapParam(payQueryMap);

		String sign = Encryption.sign(s + "&" + map.get("key"), "UTF-8").toUpperCase();
		payQueryMap.put("sign", sign);
		payQueryMap.put("sign_type", map.get("sign_type"));

		String re = ToolKit.request(map.get("apiUrl").replace("customer.pay", "customer.Pay.query"),
				getMapParam(payQueryMap));
		logger.info("查询返回：" + re);
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
		if (resultJsonObj.getString("status").equals("成功"))
			return true;
		logger.info("查询订单返回：" + s);
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("mer_id", map.get("mer_id"));
		vmap.put("timestamp", map.get("timestamp"));
		vmap.put("terminal", map.get("terminal"));
		vmap.put("version", map.get("version"));
		vmap.put("amount", map.get("amount"));
		vmap.put("backurl", map.get("backurl"));
		vmap.put("failUrl", map.get("failUrl"));
		vmap.put("ServerUrl", map.get("ServerUrl"));
		vmap.put("businessnumber", map.get("businessnumber"));
		vmap.put("goodsName", map.get("goodsName"));
		vmap.put("IsCredit", map.get("IsCredit"));
		vmap.put("BankCode", map.get("BankCode"));
		vmap.put("ProductType", map.get("ProductType"));
		vmap.put("UserId", map.get("UserId"));
		vmap.put("UserRealName", map.get("UserRealName"));
		vmap.put("UserRealMobile", map.get("UserRealMobile"));
		vmap.put("UserRealBankCard", map.get("UserRealBankCard"));
		vmap.put("UserRealIdNumber", map.get("UserRealIdNumber"));
		String str = getMapParam(vmap);
		String sign = Encryption.sign(str + "&" + business.getToken(), "UTF-8").toUpperCase();
		map.put("sign", sign);
		map.put("sign_type", "md5");
		String resultJsonStr = ToolKit.request(business.getApiUrl(), getMapParam(map));
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String code = resultJsonObj.getString("code");
		if (code.equals("MSG_OK")) {
			map.put("state", "SUCCESS");
			resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
			map.put("apiUrl", resultJsonObj.getString("trade_qrcode"));
		} else {
			logger.info(payOrder.toString());
			logger.info(resultJsonStr);
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
		}
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "WEIXIN_CODE";
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
