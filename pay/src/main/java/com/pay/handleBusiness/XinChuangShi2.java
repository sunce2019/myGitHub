package com.pay.handleBusiness;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.colotnet.util.JsonFormat;
import com.colotnet.util.SSLClient;
import com.colotnet.util.SignUtils;
import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.RSAUtil;

import net.sf.json.JSONObject;

/**
 * 新创世支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class XinChuangShi2 extends Base implements PayBase {
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
		String run = "";
		if (business.getPayType().equals(PayTran.WX.getText()) || business.getPayType().equals(PayTran.ZFB.getText())) {
			run = ScanCode(business, payOrder);
		} else if (business.getPayType().equals(PayTran.WX_WAP.getText())
				|| business.getPayType().equals(PayTran.ZFB_WAP.getText())) {
			run = h5(business, payOrder);
		}
		logger.info(run);
		JSONObject resultJsonObj = JSONObject.fromObject(run);
		if (resultJsonObj.getString("respCode").equals("P000")) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", resultJsonObj.getString("payInfo"));
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), run);
		}
	}

	private String ScanCode(Business business, PayOrder payOrder) throws Exception {
		DefaultHttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(business.getApiUrl());
		Map<String, String> transMap = new TreeMap<String, String>();
		transMap.put("version", "1.0.0");
		transMap.put("transType", "SALES");
		transMap.put("productId", typeConversion(payOrder));// 0101 微信扫码交易,0102 QQ扫码交易 , 0103 支付宝扫码交易，0104 银联扫描[云闪付]
		transMap.put("merNo", business.getUid());
		transMap.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		transMap.put("orderNo", payOrder.getGameOrderNumber());
		transMap.put("returnUrl", business.getNotifyViewUrl());
		transMap.put("notifyUrl", business.getNotifyUrl());
		transMap.put("transAmt", changeY2F(payOrder.getPrice()));
		transMap.put("commodityDetail", "上送可口可乐");
		transMap.put("signature", SignUtils.signData(transMap, PATH + business.getUid() + "_prv.pem"));
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : transMap.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse resp = httpClient.execute(postMethod);
			String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return JsonFormat.responseFormat(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String h5(Business business, PayOrder payOrder) throws Exception {
		DefaultHttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(business.getApiUrl());

		Map<String, String> transMap = new TreeMap<String, String>();
		transMap.put("version", "1.0.0");
		transMap.put("transType", "SALES");
		transMap.put("productId", typeConversion(payOrder));// 0001 网关消费,0111 QQH5, 0121 微信H5 支付,0131 支付宝H5 支付,0122
															// 微信收银台扫码
		transMap.put("merNo", business.getUid());
		transMap.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		transMap.put("orderNo", payOrder.getGameOrderNumber());
		transMap.put("returnUrl", business.getNotifyViewUrl());
		transMap.put("notifyUrl", business.getNotifyUrl());
		transMap.put("transAmt", changeY2F(payOrder.getPrice()));// 30 50 100 200 300 500
		transMap.put("commodityDetail", "上送百事可乐");
		transMap.put("custId", payOrder.getUserName());
		transMap.put("signature", SignUtils.signData(transMap, PATH + business.getUid() + "_prv.pem"));
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : transMap.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse resp = httpClient.execute(postMethod);
			String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
			return JsonFormat.responseFormat(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> respMap = new TreeMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				System.out.println(en + "=" + value);
				respMap.put(en, value);
			}
		}

		StringBuffer buf = new StringBuffer();
		String signature = "";
		for (Map.Entry<String, String> m : respMap.entrySet()) {
			if ("signature".equals(m.getKey())) {
				signature = m.getValue();
				continue;
			}
			System.out.println("排序验签字段：" + m.getKey() + "=" + m.getValue());
			buf.append(m.getKey()).append("=").append(m.getValue()).append("&");
		}
		String signatureStr = buf.substring(0, buf.length() - 1);
		String publicK;
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(respMap.get("orderNo")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				publicK = RSAUtil.getKey(PATH + business.getUid() + "_pub.pem");
				logger.info("公钥：" + publicK + "===" + signature);
				boolean f = RSAUtil.verify(signatureStr, signature, publicK, "UTF-8");
				logger.info("验签结果：" + f);

				if (f) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							changeF2Y(respMap.get("transAmt")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					} else {
						logger.info("这里失败：");

					}
				} else {
					logger.info("延签失败：" + respMap.toString());
				}
			} else {
				logger.info("商务号不存在：" + respMap.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + respMap.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "0103";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "0101";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "0131";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "0121";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝跳转", "支付宝扫码", "微信扫码", "微信跳转" };
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
