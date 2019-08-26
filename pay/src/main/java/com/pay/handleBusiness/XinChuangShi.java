package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.payAssist.CmcPayOuterRequestUtil;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;

import net.sf.json.JSONObject;

/**
 * 新创世支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class XinChuangShi extends Base implements PayBase {
	private final String PATH = this.getClass().getResource("/secretKey/xinchuangshi/").getPath();
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("appid", business.getUid());
		map.put("pay_id", business.getPayCode());
		map.put("version", "1.0");
		map.put("sign_type", "MD5");
		map.put("order_type", "1");
		map.put("currency_type", "CNY");
		map.put("total_fee", doubleToString(payOrder.getPrice()));
		map.put("return_url", business.getNotifyViewUrl());
		map.put("goods_name", Base64.getEncoder().encodeToString("梅菜扣肉".getBytes("UTF-8")));
		map.put("notify_url", business.getNotifyUrl());
		map.put("out_trade_no", payOrder.getGameOrderNumber());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		LinkedMap params = new LinkedMap();
		params.put("appid", map.get("appid"));
		params.put("pay_id", map.get("pay_id"));
		params.put("version", map.get("version"));
		params.put("sign_type", map.get("sign_type"));
		params.put("order_type", map.get("order_type"));
		params.put("currency_type", map.get("currency_type"));
		params.put("total_fee", map.get("total_fee"));
		params.put("return_url", map.get("return_url"));
		params.put("goods_name", map.get("goods_name"));
		params.put("out_trade_no", map.get("out_trade_no"));
		String _sign = CmcPayOuterRequestUtil.getSign(params, business.getToken());
		map.put("sign", _sign);
		String urlWithParams = CmcPayOuterRequestUtil.post(business.getApiUrl(), map);
		JSONObject resultJsonObj = JSONObject.fromObject(urlWithParams);
		JSONObject obj = JSONObject.fromObject(resultJsonObj.getString("result"));
		String code = obj.getString("code");
		if (code.equals("00")) {
			resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
			map.put("apiUrl", resultJsonObj.getString("payment"));
			map.put("state", "SUCCESS");
		} else {

		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("新创世支付,api回调参数：" + parame.toString());

		LinkedMap vmap = new LinkedMap();
		vmap.put("amount", parame.get("amount"));
		vmap.put("appid", parame.get("appid"));
		vmap.put("currency_type", parame.get("currency_type"));
		vmap.put("goods_name", parame.get("goods_name"));
		vmap.put("out_trade_no", parame.get("out_trade_no"));
		vmap.put("pay_id", parame.get("pay_id"));
		vmap.put("pay_no", parame.get("pay_no"));
		vmap.put("payment", parame.get("payment"));
		vmap.put("resp_code", parame.get("resp_code"));
		vmap.put("resp_desc", parame.get("resp_desc"));
		vmap.put("sign_type", parame.get("sign_type"));
		vmap.put("tran_amount", parame.get("tran_amount"));
		vmap.put("version", parame.get("version"));

		// 表示订单收款【成功】的情况，请根据实际情况自行处理
		if (!"00".equals(parame.get("resp_code"))) {
			logger.info("状态不对：" + parame.toString());
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("out_trade_no")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String _sign = CmcPayOuterRequestUtil.getSign(vmap, business.getToken());
				if (_sign.equals(parame.get("sign"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("tran_amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "SUCCESS");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					} else {
						logger.info("这里失败：");

					}
				} else {
					logger.info("延签失败：" + parame.toString());
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
		return false;
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "ALI_SCAN";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "WX_SCAN";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "ALIPAY";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "WECHAT";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码", "微信跳转", "支付宝跳转" };
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
