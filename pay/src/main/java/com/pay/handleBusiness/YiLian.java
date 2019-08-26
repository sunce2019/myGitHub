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
 * 易联支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class YiLian extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("pay_memberid", business.getUid()); // 商户号
		map.put("pay_orderid", payOrder.getGameOrderNumber());
		map.put("pay_amount", doubleToString(payOrder.getPrice()));
		map.put("pay_applydate", StringUtils.SDF.format(new Date()));
		map.put("pay_bankcode", business.getPayCode());
		map.put("pay_callbackurl", business.getNotifyViewUrl());
		map.put("pay_notifyurl", business.getNotifyUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pay_memberid", map.get("pay_memberid")); // 商户号
		vmap.put("pay_orderid", map.get("pay_orderid"));
		vmap.put("pay_amount", map.get("pay_amount"));
		vmap.put("pay_applydate", map.get("pay_applydate"));
		vmap.put("pay_bankcode", map.get("pay_bankcode"));
		vmap.put("pay_callbackurl", map.get("pay_callbackurl"));
		vmap.put("pay_notifyurl", map.get("pay_notifyurl"));
		String param = getMapParam(vmap);
		map.put("pay_md5sign", Encryption.md5_32(param + "&key=" + business.getToken(), "").toUpperCase());
		map.put("apiUrl", business.getApiUrl());
		map.put("state", "SUCCESS");
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("易联支付,api回调参数：" + parame.toString());

		if (!parame.get("returncode").equals("00")) {
			logger.info("不等于00" + parame.toString());
			return null;
		}

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("memberid", parame.get("memberid"));
		vmap.put("orderid", parame.get("orderid"));
		vmap.put("amount", parame.get("amount"));
		vmap.put("datetime", parame.get("datetime"));
		vmap.put("returncode", parame.get("returncode"));
		vmap.put("transaction_id", parame.get("transaction_id"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderid")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = getMapParam(vmap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter, "").toUpperCase();
				if (StringUtils.equals(key2, parame.get("sign"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "OK");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("sign"));
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
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
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pay_memberid", business.getUid());
		vmap.put("success_pay_orderid", payOrder.getGameOrderNumber());
		vmap.put("pay_memberid", StringUtils.SDF.format(new Date()));
		String sign = Encryption.sign(getMapParam(vmap) + "&key=" + business.getToken(), "UTF-8").toUpperCase();
		vmap.put("pay_md5sign", sign);
		String re = ToolKit.request(business.getApiUrl(), getMapParam(vmap));
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		if (resultJsonObj.getString("returncode").equals("00"))
			return true;
		logger.info("订单查询失败：" + payOrder.toString() + " 返回参数：" + re);
		return false;
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "901";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "902";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "904";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "903";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	public static void main(String[] args) {
		String s = "transaction_id=f2019052616165395843762&amount=100.00&datetime=20190526164516&orderid=1000000534964578&returncode=00&attach=&memberid=10464&key=lepdc89qrdzt0ggcg21dc4ni6af009iq";
		System.out.println(Encryption.MD5Encoder(s).toUpperCase());
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信跳转", "支付宝跳转", "微信扫码", "支付宝扫码" };
	}

}
