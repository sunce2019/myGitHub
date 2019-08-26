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
 * 牛牛支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class NiuNiu extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("login_id", business.getUid());
		map.put("create_time", timestampToDate());
		map.put("create_ip", payOrder.getIp());
		map.put("nonce", Integer.toString((int) (Math.random() * 10000000)));
		map.put("sign_type", "MD5");
		map.put("pay_type", business.getPayCode());
		map.put("order_type", "1");
		map.put("order_sn", payOrder.getGameOrderNumber());
		map.put("amount", doubleToString(payOrder.getPrice()));
		map.put("send_currency", "cny");
		map.put("recv_currency", "cny");
		map.put("extra", "aaaa");
		map.put("notify_url", business.getNotifyUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, Object> vmap = new TreeMap<String, Object>();
		vmap.put("login_id", map.get("login_id"));
		vmap.put("create_time", map.get("create_time"));
		vmap.put("create_ip", map.get("create_ip"));
		vmap.put("nonce", map.get("nonce"));
		vmap.put("sign_type", map.get("sign_type"));
		vmap.put("pay_type", map.get("pay_type"));
		vmap.put("order_type", map.get("order_type"));
		vmap.put("order_sn", map.get("order_sn"));
		vmap.put("amount", map.get("amount"));
		vmap.put("send_currency", map.get("send_currency"));
		vmap.put("recv_currency", map.get("recv_currency"));
		vmap.put("extra", map.get("extra"));
		vmap.put("notify_url", map.get("notify_url"));
		String mdSign = Encryption.md5_32(getMapParamobj(vmap) + "&api_secret=" + business.getToken(), "")
				.toLowerCase();
		map.put("sign", mdSign);
		map.remove("state");
		String s = ToolKit.getPostResponse(business.getApiUrl(), map);
		JSONObject messagesState = JSONObject.fromObject(s);
		String code = messagesState.getString("code");
		if (code.equals("0")) {
			map.put("apiUrl", messagesState.getString("http_url"));
			map.put("state", "SUCCESS");
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			map.put("state", "");
		}

	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("牛牛支付,api回调参数：" + parame.toString());

		if (!parame.get("pay_state").equals("1")) {
			return null;
		}

		Map<String, String> vmap = new TreeMap<String, String>();

		vmap.put("code", parame.get("pay_state"));
		vmap.put("login_id", parame.get("login_id"));
		vmap.put("order_id", parame.get("order_id"));
		vmap.put("order_type", parame.get("order_type"));
		vmap.put("order_sn", parame.get("order_sn"));
		vmap.put("currency", parame.get("currency"));
		vmap.put("order_amount", parame.get("order_amount"));
		vmap.put("amount", parame.get("amount"));
		vmap.put("charge", parame.get("charge"));
		vmap.put("pay_type", parame.get("pay_type"));
		vmap.put("pay_time", parame.get("pay_time"));
		// vmap.put("extra", parame.get("extra"));
		vmap.put("nonce", parame.get("nonce"));
		vmap.put("sign_type", parame.get("sign_type"));
		vmap.put("pay_state", parame.get("ay_state"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("order_sn")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = getMapParam(vmap) + "&api_secret=" + business.getToken();
				logger.info("加密参数：" + param);
				String key2 = Encryption.md5_32(param, "").toLowerCase();
				if (StringUtils.equals(key2, parame.get("sign"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
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
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wxqr";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "aliqr";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	public static void main(String[] args) {
		String s = "back_url=http://149.129.79.167:8080/callBack/heping.do&member_id=test&merchant_id=600063&notify_url=http://149.129.79.167:8080/success.do&order_id=1000001739326561&type=recharge&secret=559cf2b94c4fd517e76847fa8accf267";
		String ss = "back_url=http://149.129.79.167:8080/callBack/heping.do&member_id=test&merchant_id=600063&money=1000.00&notify_url=http://149.129.79.167:8080/success.do&order_id=1000001739326561&payment=wechat&type=recharge&secret=559cf2b94c4fd517e76847fa8accf267";
		System.out.println(Encryption.md5_32(s, "utf-8"));
		System.out.println(Encryption.md5_32(ss, "UTF-8"));
		System.out.println("");
		System.out.println(Encryption.md5_32(s, ""));
		System.out.println(Encryption.md5_32(ss, ""));
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码" };
	}

}
