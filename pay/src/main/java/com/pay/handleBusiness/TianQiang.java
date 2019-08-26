package com.pay.handleBusiness;

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
 * 天强聚合
 * 
 * @author star
 * @version 创建时间：2019年4月15日下午2:43:16
 */
@Repository
public class TianQiang extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("pay_memberid", business.getUid());
		map.put("pay_orderid", payOrder.getGameOrderNumber());
		map.put("pay_applydate", StringUtils.SDF.format(new Date()));
		map.put("pay_bankcode", business.getPayCode());
		map.put("pay_notifyurl", business.getNotifyUrl());
		map.put("pay_callbackurl", business.getNotifyViewUrl());
		map.put("pay_amount", Double.toString(payOrder.getPrice()));
		map.put("pay_productname", payOrder.getGoodsName());
		map.put("pay_returnType", "json");
		map.put("clientip", payOrder.getIp());
		signature(map, business, payOrder);
		map.put("retPath", "pay");
		String resultJsonStr = ToolKit.request(business.getApiUrl(), map.get("reqParam"));
		if (resultJsonStr == null)
			return;
		logger.info("天强api返回：" + resultJsonStr);
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		if (resultJsonObj.getString("status").equals("0000")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("pay_info"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		String memberid = request.getParameter("memberid");
		String orderid = request.getParameter("orderid");
		String amount = request.getParameter("amount");
		String transaction_id = request.getParameter("transaction_id");
		String datetime = request.getParameter("datetime");
		String returncode = request.getParameter("returncode");
		String attach = request.getParameter("attach");
		String sign = request.getParameter("sign");
		if (StringUtils.isBlank(memberid) || StringUtils.isBlank(orderid) || StringUtils.isBlank(amount)
				|| StringUtils.isBlank(transaction_id) || StringUtils.isBlank(datetime)
				|| StringUtils.isBlank(returncode) || StringUtils.isBlank(sign)) {
			logger.info("必需参数没有接收到：");
			return map;
		}
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("memberid", memberid);
		metaSignMap.put("orderid", orderid);
		metaSignMap.put("amount", amount);
		metaSignMap.put("transaction_id", transaction_id);
		metaSignMap.put("datetime", datetime);
		metaSignMap.put("returncode", returncode);
		logger.info("天强api回调：" + metaSignMap.toString());

		if (!returncode.equals("00")) {
			logger.info("天强支付失败回调了。：" + metaSignMap.toString());
			return map;
		}
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = getMapParam(metaSignMap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "utf-8").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(amount))) {
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
				logger.info("商务号不存在：");
			}
		} else {
			logger.info("订单号 和 状态 不存在：");
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
		vmap.put("pay_memberid", map.get("pay_memberid"));
		vmap.put("pay_orderid", map.get("pay_orderid"));
		vmap.put("pay_applydate", map.get("pay_applydate"));
		vmap.put("pay_bankcode", map.get("pay_bankcode"));
		vmap.put("pay_notifyurl", map.get("pay_notifyurl"));
		vmap.put("pay_callbackurl", map.get("pay_callbackurl"));
		vmap.put("pay_amount", map.get("pay_amount"));
		String str = getMapParam(vmap);
		map.put("pay_md5sign", Encryption.md5_32(str + "&key=" + business.getToken(), "").toUpperCase());
		map.put("reqParam", getMapParam(map));
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "902";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "914";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "903";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "904";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "916";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "915";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信扫码", "微信跳转", "支付宝扫码", "支付宝跳转", "银联扫码", "银联在线支付" };
	}

}
