package com.pay.handleBusiness;

import java.net.URLEncoder;
import java.util.Base64;
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
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Md5Util;
import com.pay.util.PayInfo;
import com.pay.util.RSADemo;
import com.pay.util.StringUtils;

/**
 * 三七二一
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午4:24:30
 */
@Repository
public class SanQiErYi extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business Business, PayOrder payOrder) throws Exception {
		PayInfo payInfo = new PayInfo();
		payInfo.setAccount("167078");
		// 1微信 2支付宝
		payInfo.setType(typeConversion(payOrder));
		payInfo.setAmount(doubleToString(payOrder.getPrice()));
		// 线上
		// 商户号
		payInfo.setMerchantNo(Business.getUid());
		// 商户公钥
		payInfo.setKey(Business.getToken());
		payInfo.setMerchantOrderNo(payOrder.getGameOrderNumber());
		payInfo.setNotifyUrl(Business.getNotifyUrl());
		String mdSign = payInfo.getMDSign();

		byte[] decoded = Base64.getDecoder().decode(Business.getPublic_key());
		// 公钥加密传送数据到第三方
		byte[] encryptByPublicKey = RSADemo.encryptByPublicKey(payInfo.toString().getBytes(), decoded);
		String encodeToString = Base64.getEncoder().encodeToString(encryptByPublicKey);
		String encode = URLEncoder.encode(encodeToString, "UTF-8");
		String url = Business.getApiUrl() + "?pr=" + encode + "&sign=" + mdSign;
		map.put("apiUrl", url);
		map.put("state", "SUCCESS");
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

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

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("易联支付,api回调参数：" + parame.toString());

		if (!parame.get("status").equals("1")) {
			logger.info("不等于1" + parame.toString());
			return null;
		}

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("amount", parame.get("amount"));
		vmap.put("merchantOrderNo", parame.get("merchantOrderNo"));
		vmap.put("platOrderNo", parame.get("platOrderNo"));
		vmap.put("status", parame.get("status"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("platOrderNo")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = getMapParam(vmap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Md5Util.getMD5String(parameter);
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
		return false;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码" };
	}

}
