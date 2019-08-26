package com.pay.handleBusiness;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
import com.pay.util.AmountUtils;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 三九支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class SanJiu extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<PayOrder> payorderDao;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("mch", business.getUid()); // 商户号
		map.put("pay_type", business.getPayCode());
		map.put("money", AmountUtils.changeY2F(Double.toString(payOrder.getPrice())));
		map.put("time", Integer.toString(getSecondTimestampTwo(new Date())));
		map.put("order_id", payOrder.getGameOrderNumber());
		map.put("return_url", business.getNotifyViewUrl());
		map.put("notify_url", business.getNotifyUrl());
		map.put("extra", "1");
		// map.put("apiUrl",business.getApiUrl());
		map.remove("state");
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("order_id")).append(map.get("money")).append(map.get("pay_type")).append(map.get("time"))
				.append(map.get("mch")).append(Encryption.sign(business.getToken(), "UTF-8").toLowerCase());
		map.put("sign", Encryption.sign(sb.toString(), "UTF-8").toLowerCase());
		String param = getMapParam(map);

		String resultJsonStr = ToolKit.sendGet(business.getApiUrl(), param);

		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String ok = resultJsonObj.getString("ok");
		if (ok.equals("true")) {
			map.put("apiUrl", resultJsonObj.getString("data"));
			map.put("state", "SUCCESS");
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
		}

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

		Map<String, String> parame = getCallBackParameter(request);
		if (parame.get("order_id") == null) {
			Enumeration<String> temp = request.getParameterNames();
			String parameterName = "";
			if (null != temp) {
				while (temp.hasMoreElements()) {
					parameterName = temp.nextElement();
					break;
				}
			}
			if (!parameterName.equals("")) {
				parame = getMapByJson(parameterName);
			}
		}

		String order_id = parame.get("order_id");
		String orderNo = parame.get("orderNo");
		String money = parame.get("money");
		String mch = parame.get("mch");
		String pay_type = parame.get("pay_type");
		String commodity_name = parame.get("commodity_name");
		String extra = parame.get("extra");
		String transactionId = parame.get("transactionId");
		String status = parame.get("status");
		String sign = parame.get("sign");
		String time = parame.get("time");

		StringBuffer sb = new StringBuffer();
		sb.append(order_id).append(orderNo).append(money).append(mch).append(pay_type).append(time);
		logger.info("三九支付支付,api回调：" + sb.toString());

		if (!status.equals("1")) {
			logger.info("三九支付,api回调 状态不等于1：" + sb.toString());
			return map;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(order_id));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;

				sb.append(Encryption.sign(business.getToken(), "UTF-8").toLowerCase());
				String parameter = sb.toString();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toLowerCase();
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
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX_H5.getText())) {
				return "wxhtml";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "aliwap";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信H5", "支付宝跳转" };
	}

}
