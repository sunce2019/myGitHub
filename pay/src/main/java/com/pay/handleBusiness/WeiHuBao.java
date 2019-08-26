package com.pay.handleBusiness;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.payAssist.MySecureProtocolSocketFactory;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 维护宝支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class WeiHuBao extends Base implements PayBase {
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
		map.put("pay_applydate", StringUtils.getThisDate());
		map.put("pay_bankcode", business.getPayCode());
		map.put("pay_notifyurl", business.getNotifyUrl());
		map.put("pay_callbackurl", business.getNotifyViewUrl());
		map.put("pay_amount", doubleToString(payOrder.getPrice()));
		map.put("pay_productname", "maifeiji");
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pay_memberid", map.get("pay_memberid")); // 商户号
		vmap.put("pay_orderid", map.get("pay_orderid"));
		vmap.put("pay_applydate", map.get("pay_applydate"));
		vmap.put("pay_bankcode", map.get("pay_bankcode"));
		vmap.put("pay_notifyurl", map.get("pay_notifyurl"));
		vmap.put("pay_callbackurl", map.get("pay_callbackurl"));
		vmap.put("pay_amount", map.get("pay_amount"));
		String param = getMapParam(vmap);
		map.put("pay_md5sign", md5(param + "&key=" + business.getToken()).toUpperCase());
		map.put("apiUrl", business.getApiUrl());
		map.put("state", "SUCCESS");
	}

	public static String md5(String str) throws NoSuchAlgorithmException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			// 字符数组转换成字符串
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString().toUpperCase();
			// 16位的加密
			// return buf.toString().substring(8, 24).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("维护宝支付,api回调参数：" + parame.toString());

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("memberid", parame.get("memberid"));
		vmap.put("orderid", parame.get("orderid"));
		vmap.put("amount", parame.get("amount"));
		vmap.put("transaction_id", parame.get("transaction_id"));
		vmap.put("datetime", parame.get("datetime"));
		vmap.put("returncode", parame.get("returncode"));
		vmap.put("attach", parame.get("attach"));

		String SignTemp = "amount=" + parame.get("amount") + "&datetime=" + parame.get("datetime") + "&memberid="
				+ parame.get("memberid") + "&orderid=" + parame.get("orderid") + "&returncode="
				+ parame.get("returncode") + "&transaction_id=" + parame.get("transaction_id") + "&key=";
		if (!parame.get("returncode").equals("00")) {
			return null;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderid")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = SignTemp;
				logger.info("加密参数：" + parameter);
				String key2 = md5(parameter + business.getToken()).toUpperCase();
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

	public String payOrderQuery(Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("pay_memberid", business.getUid());
		vmap.put("pay_orderid", payOrder.getGameOrderNumber());
		String sign = Encryption.sign(getMapParam(vmap) + "&key=" + business.getToken(), "UTF-8").toUpperCase();
		vmap.put("pay_md5sign", sign);
		// 调用查询接口
		String re = ToolKit.getPostResponse(business.getApiUrl().replace("Pay_Index.html", "Pay_Trade_query.html"),
				vmap);
		logger.info("我方自动回调返回参数==》：" + re);
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		if (resultJsonObj.getString("returncode").equals("00")) {
			if ("SUCCESS".equals(resultJsonObj.getString("trade_state"))) {
				logger.info("查询订单支付成功：" + payOrder.toString());
				return resultJsonObj.getString("amount");
			}
		}
		// {"status":"error","msg":"不存在的交易订单.","data":[]}
		return null;
	}

	private static String getPostResponse(String url, Map<String, String> parmMap) throws Exception {
		{
			String result = "";
			PostMethod post = new PostMethod(url);
			ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
			Protocol.registerProtocol("https", new Protocol("https", fcty, 443));

			HttpClient client = new HttpClient();
			Iterator it = parmMap.entrySet().iterator();
			NameValuePair[] param = new NameValuePair[parmMap.size()];
			int i = 0;
			while (it.hasNext()) {
				Map.Entry parmEntry = (Map.Entry) it.next();
				param[i++] = new NameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue());
			}

			post.setRequestBody(param);
			try {
				int statusCode = client.executeMethod(post);

				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header locationHeader = post.getResponseHeader("location");
					String location = "";
					if (locationHeader != null) {
						location = locationHeader.getValue();
						result = ToolKit.getPostResponse(location, parmMap);// 用跳转后的页面重新请求�??
					}
				} else if (statusCode == HttpStatus.SC_OK) {
					InputStream inputStream = post.getResponseBodyAsStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					StringBuffer stringBuffer = new StringBuffer();
					while ((result = br.readLine()) != null) {
						stringBuffer.append(result);
					}
					return stringBuffer.toString();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				post.releaseConnection();
			}
			return result;
		}
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "902";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "903";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "932";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * String s =
		 * "back_url=http://149.129.79.167:8080/callBack/heping.do&member_id=test&merchant_id=600063&notify_url=http://149.129.79.167:8080/success.do&order_id=1000001739326561&type=recharge&secret=559cf2b94c4fd517e76847fa8accf267";
		 * String ss =
		 * "back_url=http://149.129.79.167:8080/callBack/heping.do&member_id=test&merchant_id=600063&money=1000.00&notify_url=http://149.129.79.167:8080/success.do&order_id=1000001739326561&payment=wechat&type=recharge&secret=559cf2b94c4fd517e76847fa8accf267";
		 * System.out.println(Encryption.md5_32(s,"utf-8"));
		 * System.out.println(Encryption.md5_32(ss,"UTF-8")); System.out.println("");
		 * System.out.println(Encryption.md5_32(s,""));
		 * System.out.println(Encryption.md5_32(ss,""));
		 */
		Business business = new Business();
		business.setUid("10118");
		business.setToken("7mjtz8fzfmzrxe5vdcqdjdax9oka6tg1");
		PayOrder payOrder = new PayOrder();
		payOrder.setGameOrderNumber("SP20190808154429776425");
		business.setApiUrl("https://pay.w5z5zc.cn/Pay_Index.html");
		System.out.println(new WeiHuBao().payOrderQuery(business, payOrder));

		// String re =
		// getPostResponse("https://pay.w5z5zc.cn/Pay_Index.html".replace("Pay_Index.html",
		// "Pay_Trade_query.html"), vmap);
		// System.out.println(re);

	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信跳转", "支付宝跳转", "微信扫码", "支付宝扫码" };
	}

}
