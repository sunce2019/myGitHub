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
 * One4Bank支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class One4Bank extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("action", "order");
		map.put("userid", business.getUid());
		map.put("cusid", payOrder.getUserName());
		map.put("token", Long.toString(new Date().getTime()) + StringUtils.getRandomString(6));
		map.put("out_trade_no", payOrder.getGameOrderNumber());
		map.put("value_cny", doubleToString(payOrder.getPrice()));
		map.put("pay_type", business.getPayCode());
		map.put("ip", payOrder.getIp());
		map.remove("state");
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("One4Bank支付,api回调参数：" + parame.toString());

		Map<String, String> vmap = new TreeMap<String, String>();

		vmap.put("cusid", parame.get("cusid"));
		vmap.put("trade_no", parame.get("trade_no"));
		vmap.put("out_trade_no", parame.get("out_trade_no"));
		vmap.put("coin", parame.get("coin"));
		vmap.put("value_cny", parame.get("value_cny"));
		vmap.put("value_coin", parame.get("value_coin"));
		vmap.put("action", parame.get("action"));
		vmap.put("token", parame.get("token"));

		String sign = parame.get("sign");
		parame.remove("sign");
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("out_trade_no")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String s = getMapParam(vmap);
				String parameter = s + "&" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.string2MD5(parameter).toLowerCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(parame.get("value_cny")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "ok");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + sign);
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
		StringBuffer sb = new StringBuffer();
		sb.append("orderid=").append(map.get("orderid")).append("&parter=").append(map.get("parter"));
		String sign = Encryption.sign(sb.toString() + map.get("key"), "UTF-8").toLowerCase();
		sb.append("&sign=").append(sign);
		String s = ToolKit.request(map.get("apiUrl").replace("Pay/GateWay", "Order/Search"), sb.toString());
		logger.info("查询返回：" + s);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		if (resultJsonObj.getString("opstate").equals("0"))
			return true;
		logger.info("查询订单返回：" + s);
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, Object> vmap = new TreeMap<String, Object>();
		vmap.put("action", map.get("action"));
		vmap.put("userid", map.get("userid"));
		vmap.put("cusid", map.get("cusid"));
		vmap.put("token", map.get("token"));
		vmap.put("out_trade_no", map.get("out_trade_no"));
		vmap.put("value_cny", Double.parseDouble(map.get("value_cny")));
		vmap.put("pay_type", map.get("pay_type"));
		vmap.put("ip", map.get("ip"));
		String str = getMapParamobj(vmap);
		String sign = Encryption.string2MD5(str + "&" + business.getToken()).toLowerCase();
		vmap.put("sign", sign);

		String s = ToolKit.requestJson(business.getApiUrl(), ToolKit.mapToJsonobj(vmap));
		JSONObject messagesState = JSONObject.fromObject(s);
		String error = messagesState.getString("error");
		if (error.equals("0")) {
			map.put("state", "SUCCESS");
			messagesState = JSONObject.fromObject(messagesState.getString("data"));
			map.put("apiUrl", messagesState.getString("url"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			logger.info("请求支付失败。" + payOrder.toString());
			logger.info("api 返回值" + s);
		}
	}

	public static void main(String[] args) {
		// action=order&cusid=mingz&out_trade_no=1000000352854261&pay_type=wxpay&token=1558522281235&userid=10955&value_cny=100.0a28b2eaa39f8a682cc04b51623d0703aaaac272a
		System.out.println(Encryption.string2MD5(
				"action=order&cusid=mingz&out_trade_no=1000001681111006&pay_type=wxpay&token=1558523323937&userid=10955&value_cny=100.0a28b2eaa39f8a682cc04b51623d0703aaaac272a"));
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wxpay";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "wxh5";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "alipaywap";
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
