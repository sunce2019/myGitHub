package com.pay.handleBusiness;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 高盛
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午4:24:30
 */
@Repository
public class GaoSheng extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business Business, PayOrder payOrder) throws Exception {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("appId", Business.getUid());
		metaSignMap.put("payType", Business.getPayCode());// WX:微信支付,ZFB:支付宝支付 QQ：QQ支付 BAI_DU：百度支付 JD：京东支付
															// UNIONPAY_QRCODE：银联二维码支付 WX_WAP：微信H5支付
		metaSignMap.put("nonceStr", StringUtils.getRandomString(4));// 4位随机数
		metaSignMap.put("outTradeNo", payOrder.getGameOrderNumber());
		metaSignMap.put("requestIp", payOrder.getIp());// 单位:分
		metaSignMap.put("totalAmount", changeY2F(payOrder.getPrice()));// 单位:分
		metaSignMap.put("goodsInfo", "买笔");// 商品名称：20位
		metaSignMap.put("notifyUrl", Business.getNotifyUrl());// 回调地址
		metaSignMap.put("returnUrl", Business.getNotifyViewUrl());// 回显地址
		String metaSignJsonStr = mapToJson(metaSignMap);
		String sign = MD5(metaSignJsonStr + Business.getToken(), "UTF-8");// 32位
		metaSignMap.put("sign", sign.toUpperCase());
		String reqParam = "reqData=" + mapToJson(metaSignMap);
		String resultJsonStr = request(Business.getApiUrl() + "/api/qrCodePay.action", reqParam);
		try {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			if (resultJsonObj.getString("resultCode").equals("00")) {
				if (Business.getPayType().contains("扫码")) {
					map.put("state", "SUCCESS");
					map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("qrCode")));
					Business.setJumpType(3);
				} else {
					map.put("state", "SUCCESS");
					map.put("apiUrl", resultJsonObj.getString("qrCode"));
					Business.setJumpType(2);
				}
			} else {
				payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			payOrderService.updatePayOrder(payOrder.getId(), resultJsonStr);
		}

	}

	private static String request(String url, String params) {
		try {
			System.out.println("请求报文:" + params);
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(1000 * 5);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(params.toString().getBytes("UTF-8"));
			outStream.flush();
			outStream.close();
			return getResponseBodyAsString(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getResponseBodyAsString(InputStream in) {
		try {
			BufferedInputStream buf = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			StringBuffer data = new StringBuffer();
			int readDataLen;
			while ((readDataLen = buf.read(buffer)) != -1) {
				data.append(new String(buffer, 0, readDataLen, "UTF-8"));
			}
			System.out.println("响应报文=" + data);
			return data.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private final static String MD5(String s, String encoding) {
		try {
			byte[] btInput = s.getBytes(encoding);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String mapToJson(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		StringBuffer json = new StringBuffer();
		json.append("{");
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			json.append("\"").append(key).append("\"");
			json.append(":");
			json.append("\"").append(value).append("\"");
			if (it.hasNext()) {
				json.append(",");
			}
		}
		json.append("}");
		System.out.println("mapToJson=" + json.toString());
		return json.toString();
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "WX";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "ZFB";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "ZFB_WAP";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "WX_WAP";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("高盛支付,api回调参数：" + parame.toString());

		JSONObject jsonObj = JSONObject.fromObject(parame.get("reqData"));
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("appId", jsonObj.getString("appId"));
		metaSignMap.put("payType", jsonObj.getString("payType"));
		metaSignMap.put("outTradeNo", jsonObj.getString("outTradeNo"));
		metaSignMap.put("totalAmount", jsonObj.getString("totalAmount"));
		metaSignMap.put("goodsInfo", jsonObj.getString("goodsInfo"));
		metaSignMap.put("resultCode", jsonObj.getString("resultCode"));// 支付状态
		metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyy-MM-dd

		if (!jsonObj.getString("resultCode").equals("00")) {
			logger.info("不等于00" + jsonObj.toString());
			return null;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(jsonObj.getString("outTradeNo")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = mapToJson(metaSignMap);
				logger.info("加密参数：" + parameter);
				String key2 = MD5(parameter + business.getToken(), "UTF-8");
				if (StringUtils.equals(key2, jsonObj.getString("sign"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							changeF2Y(jsonObj.getString("totalAmount")))) {
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
		return false;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码", "微信跳转", "支付宝跳转" };
	}

}
