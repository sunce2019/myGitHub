package com.pay.handleBusiness;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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
 * 黎睿支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class LiRui extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("pay_version", "vb1.0"); // 商户号
		map.put("pay_memberid", business.getUid());
		map.put("pay_orderid", payOrder.getGameOrderNumber());
		map.put("pay_applydate", StringUtils.SDF2.format(new Date()));
		map.put("pay_bankcode", business.getPayCode());
		map.put("pay_notifyurl", business.getNotifyUrl());
		map.put("pay_amount", Double.toString(payOrder.getPrice()));
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new HashMap<String, String>();
		vmap.put("pay_memberid", map.get("pay_memberid")); // 商户号
		vmap.put("pay_orderid", map.get("pay_orderid"));
		vmap.put("pay_bankcode", map.get("pay_bankcode"));
		vmap.put("pay_notifyurl", map.get("pay_notifyurl"));
		vmap.put("pay_amount", map.get("pay_amount"));
		String str = getMapParam(vmap);
		map.put("pay_md5sign", Encryption.md5_32(str + business.getToken(), "GB2312").toLowerCase());
		map.put("state", "SUCCESS");
	}

	private static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String orderid = request.getParameter("orderid");
		String ovalue = request.getParameter("ovalue");
		String sysorderid = request.getParameter("sysorderid");
		String opstate = request.getParameter("opstate");
		String sign = request.getParameter("sign");
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("orderid", orderid);
		vmap.put("ovalue", ovalue);
		vmap.put("opstate", opstate);
		logger.info("黎睿支付支付,api回调：" + vmap.toString());
		if (!opstate.equals("0")) {
			logger.info("黎睿支付,api回调 状态不等于0：" + vmap.toString());
			return map;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(vmap) + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter, "GB2312").toUpperCase();
				sign = key2; // 第三方不给回签字段，第三方提供的字段是错的，老虎同意不回签。
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(ovalue))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "OK");
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
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "weixin";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "alipaywap";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "weixinwap";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信H5" };
	}

}
