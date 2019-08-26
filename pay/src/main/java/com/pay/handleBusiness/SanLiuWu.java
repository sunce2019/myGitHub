package com.pay.handleBusiness;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
 * 三六五支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class SanLiuWu extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("version", "V1.0"); // 商户号
		map.put("partner_id", business.getUid());
		map.put("pay_key", business.getPayType().contains("支付宝") ? "ALIPAY" : "WEIXIN");
		map.put("pay_type", business.getPayCode());
		map.put("order_no", payOrder.getGameOrderNumber());
		map.put("amount", Double.toString(payOrder.getPrice()));
		map.put("notify_url", business.getNotifyUrl());
		map.put("payer_id", "1000");
		map.remove("state");
		signature(map, business, payOrder);
		if (business.getJumpType() == 2) {
			String resultJsonStr = ToolKit.requestJson(business.getApiUrl(), map.get("reqParam"));
			if (resultJsonStr == null)
				return;
			logger.info("三六五api返回：" + resultJsonStr);
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonStr);
		} else {
			map.put("apiUrl", business.getApiUrl());
			map.put("state", "SUCCESS");
			map.remove("reqParam");
		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		String param = null;

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		param = responseStrBuilder.toString();

		JSONObject resultJsonObj = JSONObject.fromObject(param);
		String order_no = resultJsonObj.getString("order_no");
		String littlepay_id = resultJsonObj.getString("littlepay_id");
		String amount = resultJsonObj.getString("amount");
		String status = resultJsonObj.getString("status");
		// String key = resultJsonObj.getString("key");
		String sign = resultJsonObj.getString("sign");
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("amount", amount);
		metaSignMap.put("littlepay_id", littlepay_id);
		metaSignMap.put("order_no", order_no);
		metaSignMap.put("status", status);

		if (!status.equals("1")) {
			logger.info("三六五,api回调 状态不等于1：" + metaSignMap.toString());
			return map;
		}

		logger.info("三六五,api回调：" + metaSignMap.toString());
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(order_no));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				metaSignMap.put("key", business.getToken());
				String parameter = getMapParam(metaSignMap);
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter.toLowerCase(), "").toLowerCase();
				if (StringUtils.equals(key2, sign)) {
					metaSignMap.put("partner_id", business.getUid());
					metaSignMap.put("key", business.getToken());
					metaSignMap.put("order_no", payOrder.getGameOrderNumber());
					metaSignMap.put("trade_no", littlepay_id);
					metaSignMap.put("apiUrl", business.getApiUrl().replace("orderpay", "orderquery"));
					if (payQuery(metaSignMap, business, payOrder) && baseService.successfulPayment(payOrder.getId(),
							business.getId(), Double.parseDouble(amount))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "SUCCESS");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					} else {
						logger.info("回调失败：" + payOrder.toString());
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
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("version", "V1.0"); // 商户号
		vmap.put("partner_id", map.get("partner_id"));
		vmap.put("order_no", map.get("order_no"));
		vmap.put("trade_no", "");
		vmap.put("key", map.get("key"));
		String str = getMapParam(vmap);
		String sign = Encryption.md5_32(str.toLowerCase(), "").toLowerCase();
		vmap.remove("key");
		vmap.put("sign", sign);
		String parameter = ToolKit.mapToJson(vmap);
		String resultJsonStr = ToolKit.requestJson(map.get("apiUrl"), parameter);
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String success = resultJsonObj.getString("success");
		if (success.equals("true")) {
			resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
			String status = resultJsonObj.getString("status");
			if (status.equals("1")) {
				return true;
			}
		}
		logger.info("查询订单未支付成功：" + resultJsonStr + " 订单信息：" + parameter);
		return false;
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("partner_id", "1000000089");
		vmap.put("order_no", "1000001749988577");
		vmap.put("trade_no", "");
		vmap.put("key", "7c9c9b91f8a15013d3044ab7ea436f39");
		vmap.put("apiUrl", "http://api.xbgpay.com/api/pay/orderquery");
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("amount", map.get("amount")); // 商户号
		vmap.put("key", business.getToken());
		vmap.put("notify_url", map.get("notify_url"));
		vmap.put("order_no", map.get("order_no"));
		vmap.put("partner_id", map.get("partner_id"));
		vmap.put("pay_key", map.get("pay_key"));
		vmap.put("pay_type", map.get("pay_type"));
		vmap.put("payer_id", map.get("payer_id"));
		vmap.put("version", map.get("version"));
		String str = getMapParam(vmap);
		String sign = Encryption.md5_32(str.toLowerCase(), "").toLowerCase();
		vmap.put("sign", sign);
		map.put("sign", sign);
		map.put("reqParam", ToolKit.mapToJson(map));
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "0002";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "0005";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "0004";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "0006";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码", "支付宝跳转", "微信跳转" };
	}

}
