package com.pay.handleBusiness;

import java.net.URLEncoder;
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
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import net.sf.json.JSONObject;

/**
 * 隆发
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午4:24:30
 */
@Repository
public class LongFa extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business Business, PayOrder payOrder) throws Exception {
		signature(map, Business, payOrder);
		String resultJsonStr = ToolKit.request(Business.getApiUrl(), map.get("reqParam"));
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("stateCode");
		if (!stateCode.equals("00")) {
			logger.info(payOrder.toString());
			logger.info(resultJsonStr);
			map.put("msg", resultJsonObj.getString("msg"));
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = ToolKit.MD5(resultJsonObj.toString() + Business.getToken(), ToolKit.CHARSET);
		if (targetString.equals(resultSign)) {
			map.clear();
			if (StringUtils.equals(payOrder.getOrderType(), PayTran.WX.getText())
					|| StringUtils.equals(payOrder.getOrderType(), PayTran.ZFB.getText())) {
				map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("qrcodeUrl")));
				Business.setJumpType(3);
			} else {
				map.put("apiUrl", resultJsonObj.getString("qrcodeUrl"));
				Business.setJumpType(2);
			}

			map.put("state", "SUCCESS");
		} else {
			logger.info(payOrder.toString());
			logger.info(targetString);
			map.put("msg", resultJsonObj.getString("msg"));
		}

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("orderNo", payOrder.getGameOrderNumber());
		metaSignMap.put("randomNo", ToolKit.randomStr(4));// 4位随机数

		metaSignMap.put("merchNo", business.getUid());
		metaSignMap.put("netwayType", PayTran.getCode(business.getPayType()));// WX:微信支付,ZFB:支付宝支付
		metaSignMap.put("amount", AmountUtils.changeY2F(Double.toString(payOrder.getPrice())));// 单位:分
		metaSignMap.put("goodsName", "iPhone配件");// 商品名称：20位
		metaSignMap.put("notifyUrl", business.getNotifyUrl());// 回调地址
		metaSignMap.put("notifyViewUrl", business.getNotifyViewUrl());// 回显地址

		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		String sign = ToolKit.MD5(metaSignJsonStr + business.getToken(), ToolKit.CHARSET);// 32位
		metaSignMap.put("sign", sign);

		byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(metaSignMap).getBytes(ToolKit.CHARSET),
				business.getPublic_key());
		String param = new BASE64Encoder().encode(dataStr);
		String reqParam = "data=" + URLEncoder.encode(param, ToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo")
				+ "&version=V3.6.0.0";
		map.put("reqParam", reqParam);
	}

	@Override
	public String typeConversion(PayOrder payOrder) {
		return null;
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String data = request.getParameter("data");

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(request.getParameter("orderNo")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;

				byte[] result = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data),
						business.getPrivate_key());
				String resultData = new String(result, ToolKit.CHARSET);// 解密数据

				JSONObject jsonObj = JSONObject.fromObject(resultData);
				logger.info("隆发api回调：" + jsonObj.toString());
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("merchNo", jsonObj.getString("merchNo"));
				metaSignMap.put("netwayType", jsonObj.getString("netwayType"));
				metaSignMap.put("orderNo", jsonObj.getString("orderNo"));
				metaSignMap.put("amount", jsonObj.getString("amount"));
				metaSignMap.put("goodsName", jsonObj.getString("goodsName"));
				metaSignMap.put("payStateCode", jsonObj.getString("payStateCode"));// 支付状态
				metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyyMMddHHmmss
				String jsonStr = ToolKit.mapToJson(metaSignMap);
				String sign = ToolKit.MD5(jsonStr.toString() + business.getToken(), ToolKit.CHARSET);
				if (!sign.equals(jsonObj.getString("sign"))) {
					logger.info("签名失败：" + jsonObj.getString("sign"));
					logger.info("签名失败：" + sign);
				}

				if (!jsonObj.getString("payStateCode").equals("00")) {
					logger.info("隆发失败回调了。：" + metaSignMap.toString());
					return map;
				}

				Long l = Long.parseLong(jsonObj.getString("amount"));
				String ls = AmountUtils.changeF2Y(l);
				baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(ls));
				map.put("state", "SUCCESS");
				map.put("reBusiness", "SUCCESS");
				map.put("payorder_id", Integer.toString(payOrder.getId()));
				logger.info("回调成功：" + payOrder.toString());
			} else {
				logger.info("商务号不存在：" + request.getParameter("orderNo"));
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + request.getParameter("orderNo"));
		}

		return map;

	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转" };
	}

}
