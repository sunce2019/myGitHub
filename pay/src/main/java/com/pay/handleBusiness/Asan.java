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
 * 阿三支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class Asan extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("merchant_no", business.getUid()); // 商户号
		map.put("total_fee", Double.toString(payOrder.getPrice()));
		map.put("pay_type", business.getPayCode());
		map.put("m_order_code", payOrder.getGameOrderNumber());
		map.put("notify_url", business.getNotifyUrl());
		map.put("return_url", business.getNotifyViewUrl());
		map.put("return_type", "json");
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
		String resultJsonStr = ToolKit.request(business.getApiUrl(), map.get("reqParam"));
		if (resultJsonStr == null)
			return;
		logger.info("阿三api返回：" + resultJsonStr);
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		if (resultJsonObj.getString("sys_code").equals("1")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("pay_url"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
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
		String order_status = resultJsonObj.getString("order_status");
		String order_status_msg = resultJsonObj.getString("order_status_msg");
		String sys_order_code = resultJsonObj.getString("sys_order_code");
		String m_order_code = resultJsonObj.getString("m_order_code");
		String total_fee = resultJsonObj.getString("total_fee");
		String pay_time = resultJsonObj.getString("pay_time");
		String sign = resultJsonObj.getString("sign");
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("order_status", order_status);
		metaSignMap.put("order_status_msg", order_status_msg);
		metaSignMap.put("sys_order_code", sys_order_code);
		metaSignMap.put("m_order_code", m_order_code);
		metaSignMap.put("total_fee", total_fee);
		metaSignMap.put("pay_time", pay_time);

		if (StringUtils.isBlank(order_status) || StringUtils.isBlank(order_status_msg)
				|| StringUtils.isBlank(sys_order_code) || StringUtils.isBlank(m_order_code)
				|| StringUtils.isBlank(total_fee) || StringUtils.isBlank(pay_time) || StringUtils.isBlank(sign)) {
			logger.info("参数不完整：" + metaSignMap.toString());
			return map;
		}

		if (!order_status.equals("2")) {
			logger.info("阿三,api回调 状态不等于2：" + metaSignMap.toString());
			return map;
		}

		logger.info("阿三,api回调：" + metaSignMap.toString());
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(m_order_code));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(metaSignMap) + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "utf-8").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(total_fee))) {
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
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("merchant_no", map.get("merchant_no")); // 商户号
		vmap.put("total_fee", map.get("total_fee"));
		vmap.put("pay_type", map.get("pay_type"));
		vmap.put("m_order_code", map.get("m_order_code"));
		vmap.put("notify_url", map.get("notify_url"));
		vmap.put("return_url", map.get("return_url"));
		vmap.put("return_type", map.get("return_type"));
		String str = getMapParam(vmap);
		vmap.put("sign", Encryption.md5_32(str + business.getToken(), "").toUpperCase());
		map.put("reqParam", getMapParam(vmap));
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
		return new String[] { "支付宝扫码", "微信扫码" };
	}

}
