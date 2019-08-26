package com.pay.handleBusiness;

import java.net.URLEncoder;
import java.util.Base64;
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
import com.pay.util.Md5Util;
import com.pay.util.PayInfo;
import com.pay.util.RSADemo;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * HIPAY支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class HiPay extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("account", payOrder.getUserName());
		map.put("merchantNo", business.getUid());
		map.put("amount", Integer.toString(payOrder.getPrice().intValue()));
		map.put("type", business.getPayCode());// 商户单号 需要确保唯一
		map.put("notifyUrl", business.getNotifyUrl());
		map.put("merchantOrderNo", payOrder.getGameOrderNumber());// 商品描述
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		PayInfo payInfo = new PayInfo();
		payInfo.setAccount(map.get("account"));
		payInfo.setType(map.get("type"));
		payInfo.setAmount(map.get("amount"));
		payInfo.setMerchantNo(map.get("merchantNo"));
		payInfo.setKey(business.getToken());
		payInfo.setMerchantOrderNo(map.get("merchantOrderNo"));
		payInfo.setNotifyUrl(map.get("notifyUrl"));
		String mdSign = payInfo.getMDSign();

		String publicKey = business.getPublic_key();
		byte[] decoded = Base64.getDecoder().decode(publicKey);
		// 公钥加密传送数据到第三方
		byte[] encryptByPublicKey = RSADemo.encryptByPublicKey(payInfo.toString().getBytes(), decoded);
		String encodeToString = Base64.getEncoder().encodeToString(encryptByPublicKey);
		String encode = URLEncoder.encode(encodeToString, "UTF-8");
		String url = business.getApiUrl() + encode + "&sign=" + mdSign;

		map.put("apiUrl", url);
		map.put("state", "SUCCESS");
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("HIPAY支付,api回调参数：" + parame.toString());

		if (!parame.get("status").equals("1")) {
			return null;
		}

		Map<String, String> vmap = new TreeMap<String, String>();

		vmap.put("amount", parame.get("amount"));
		vmap.put("merchantOrderNo", parame.get("merchantOrderNo"));
		vmap.put("platOrderNo", parame.get("platOrderNo"));
		vmap.put("status", parame.get("status"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("merchantOrderNo")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = getMapParam(vmap) + "&key=" + business.getToken();
				logger.info("加密参数：" + param);
				String key2 = Md5Util.getMD5String(param);
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
				return "1";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "2";
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
