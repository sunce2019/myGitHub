package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

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
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 百威支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class BaiWei extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("mch_id", business.getUid()); // 商户号
		map.put("out_trade_no", payOrder.getGameOrderNumber());
		map.put("body", "111");
		map.put("callback_url", business.getNotifyViewUrl());
		map.put("notify_url", business.getNotifyUrl());
		map.put("total_fee", Double.toString(payOrder.getPrice()));
		map.put("service", business.getPayCode());
		map.put("way", payOrder.getOrderType().contains("扫码") ? "pay" : "wap");
		map.put("format", "json");
		signature(map, business, payOrder);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String mch_id = request.getParameter("mch_id");
		String time_end = request.getParameter("time_end");
		String out_trade_no = request.getParameter("out_trade_no");
		String ordernumber = request.getParameter("ordernumber");
		String transtypeid = request.getParameter("transtypeid");
		String transaction_id = request.getParameter("transaction_id");
		String total_fee = request.getParameter("total_fee");
		String service = request.getParameter("service");
		String way = request.getParameter("way");
		String result_code = request.getParameter("result_code");
		String sign = request.getParameter("sign");

		StringBuffer sb = new StringBuffer();
		sb.append(mch_id).append(time_end).append(out_trade_no).append(ordernumber).append(transtypeid)
				.append(transaction_id).append(total_fee).append(service).append(way).append(result_code);
		// vmap.put("sign_type", sign_type);
		logger.info("百威支付,api回调：" + sb.toString());

		if (!result_code.equals("0")) {
			logger.info("百威,api回调 状态不等于result_code：" + sb.toString());
			return map;
		}
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(out_trade_no));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				String parameter = sb.toString() + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.sign(parameter, "UTF-8").toUpperCase();
				if (StringUtils.equals(key2, sign)) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.parseDouble(total_fee))) {
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
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("mch_id")).append(map.get("out_trade_no")).append(map.get("callback_url"))
				.append(map.get("notify_url")).append(map.get("total_fee")).append(map.get("service"))
				.append(map.get("way")).append(map.get("format")).append(business.getToken());
		String sign = Encryption.sign(sb.toString(), "UTF-8");
		map.put("sign", sign);
		String p = getMapParam(map);
		String s = ToolKit.sendGet(business.getApiUrl(), p);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String success = resultJsonObj.getString("success");
		if (success.equals("true")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("pay_info"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			logger.info(payOrder.toString());
			logger.info(s);
			map.put("msg", resultJsonObj.getString("msg"));
		}
		System.out.println(s);
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "al";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "al";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wx";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "wx";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转" };
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
