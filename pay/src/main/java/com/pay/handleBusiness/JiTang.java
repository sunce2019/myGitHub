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
import com.pay.util.AmountUtils;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 鸡汤支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class JiTang extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("return_type", "json");
		map.put("api_code", business.getUid());
		map.put("is_type", business.getPayCode());
		map.put("price", Double.toString(payOrder.getPrice()));
		map.put("order_id", payOrder.getGameOrderNumber());
		map.put("time", StringUtils.SDF.format(new Date().getTime()));
		map.put("mark", "购买食杂");
		map.put("return_url", business.getNotifyViewUrl());
		map.put("notify_url", business.getNotifyUrl());

		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		JSONObject jsonObject = JSONObject.fromObject(getJson(request));

		String order_id = jsonObject.getString("order_id");// 是您在发起支付接口传入的您的自定义订单号
		String paysapi_id = jsonObject.getString("paysapi_id"); // 是此订单在Api服务器上的唯一编号
		String price = jsonObject.getString("price"); // 支付金额
		String real_price = jsonObject.getString("real_price"); // 实际支付金额
		String mark = jsonObject.getString("mark"); // 描述
		String sign = jsonObject.getString("sign"); // 签名认证串

		String code = jsonObject.getString("code"); // 订单状态 0 未处理 1 交易成功 2 支付失败 3 关闭交易 4 支付超时
		String is_type = jsonObject.getString("is_type"); // 支付类型

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("order_id", order_id);
		vmap.put("paysapi_id", paysapi_id);
		vmap.put("price", price);
		vmap.put("real_price", real_price);
		vmap.put("mark", mark);
		vmap.put("code", code);
		vmap.put("is_type", is_type);

		// vmap.put("sign_type", sign_type);
		logger.info("鸡汤支付,api回调：" + vmap.toString());

		if (!code.equals("1")) {
			logger.info("鸡汤,api回调 状态不等于1result_code：" + vmap.toString());
			return map;
		}
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(order_id));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				vmap.put("api_code", business.getUid());
				String str = getMapParam(vmap);
				String parameter = str + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(real_price))) {
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
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("return_type", map.get("return_type"));
		vmap.put("api_code", map.get("api_code"));
		vmap.put("is_type", map.get("is_type"));
		vmap.put("price", map.get("price"));
		vmap.put("order_id", map.get("order_id"));
		vmap.put("time", map.get("time"));
		vmap.put("mark", map.get("mark"));
		vmap.put("return_url", map.get("return_url"));
		vmap.put("notify_url", map.get("notify_url"));
		String str = getMapParam(vmap);

		String sign = Encryption.MD5Encoder(str + "&key=" + business.getToken());
		vmap.put("sign", sign.toUpperCase());
		String p = getMapParam(vmap);
		String s = ToolKit.request(business.getApiUrl(), p);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String messages = resultJsonObj.getString("messages");
		JSONObject messagesState = JSONObject.fromObject(messages);
		String returncode = messagesState.getString("returncode");
		if (returncode.equals("SUCCESS")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("payurl")));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			logger.info("请求支付失败。" + payOrder.toString());
			logger.info("api 返回值" + s);
			map.put("msg", messages);
		}
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wechat";
			}
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "alipay_nhtpay";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码", "支付宝跳转" };
	}

	public static void main(String[] args) throws Exception {
		Long l = Long.parseLong("199800000");
		String money = AmountUtils.changeF2Y(l);

		System.out.println(money);
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
