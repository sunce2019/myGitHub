package com.pay.handleBusiness;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

/**
 * Ksk
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class HengTong extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("customer", business.getUid());
		map.put("banktype", business.getPayCode());
		map.put("amount", Double.toString(payOrder.getPrice()) + "0");
		map.put("orderid", payOrder.getGameOrderNumber());
		map.put("asynbackurl", business.getNotifyUrl());
		map.put("request_time", StringUtils.SDF2.format(new Date()));
		map.put("synbackurl", business.getNotifyViewUrl());
		map.put("israndom", "Y");
		map.put("attach", UUID.randomUUID().toString());

		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
		map.put("state", "SUCCESS");
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		String orderid = request.getParameter("orderid");
		String result = request.getParameter("result");
		String payamount = request.getParameter("payamount");
		String systemorderid = request.getParameter("systemorderid");
		String completetime = request.getParameter("completetime");
		String notifytime = request.getParameter("notifytime");
		String attach = request.getParameter("attach");
		String sourceamount = request.getParameter("sourceamount");
		String amount = request.getParameter("amount");
		String sign = request.getParameter("sign");
		if (StringUtils.isBlank(orderid) || StringUtils.isBlank(result) || StringUtils.isBlank(payamount)
				|| StringUtils.isBlank(systemorderid) || StringUtils.isBlank(completetime)
				|| StringUtils.isBlank(notifytime) || StringUtils.isBlank(attach) || StringUtils.isBlank(sourceamount)
				|| StringUtils.isBlank(amount) || StringUtils.isBlank(sign)) {
			return map;
		}

		String str = "orderid=" + orderid + "&result=" + result + "&amount=" + amount + "&systemorderid="
				+ systemorderid + "&completetime=" + completetime;
		if (!result.equals("1")) {
			logger.info("恒通支付失败回调了。：" + str);
			return map;
		}

		logger.info("恒通,api回调：" + str);
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = str + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "utf-8").toLowerCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(payamount))) {
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
				logger.info("商务号不存在：" + str);
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + str);
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("customer=").append(map.get("customer"));
		sb.append("&banktype=").append(map.get("banktype"));
		sb.append("&amount=").append(map.get("amount"));
		sb.append("&orderid=").append(map.get("orderid"));
		sb.append("&asynbackurl=").append(map.get("asynbackurl"));
		sb.append("&request_time=").append(map.get("request_time"));
		sb.append("&key=").append(business.getToken());
		map.put("sign", Encryption.sign(sb.toString(), "utf-8").toLowerCase());
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "1000";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())
					|| payOrder.getOrderType().contains(PayTran.WX_H5.getText())) {
				return "1002";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "1003";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())
					|| payOrder.getOrderType().contains(PayTran.ZFB_HB.getText())
					|| payOrder.getOrderType().contains(PayTran.ZFB_HB_H5.getText())) {
				return "1004";
			} else if (payOrder.getOrderType().contains(PayTran.QQ.getText())) {
				return "1005";
			} else if (payOrder.getOrderType().contains(PayTran.QQ_WAP.getText())) {
				return "1006";
			} else if (payOrder.getOrderType().contains(PayTran.JD.getText())) {
				return "1007";
			} else if (payOrder.getOrderType().contains(PayTran.JD_WAP.getText())) {
				return "1008";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "1009";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转", "京东扫码", "银联扫码", "银联在线支付" };
	}

}
