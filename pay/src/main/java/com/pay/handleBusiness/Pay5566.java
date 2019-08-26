package com.pay.handleBusiness;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import com.robust.pay.sdk.biz.BizHelper;
import com.robust.pay.sdk.util.SignUtil;
import com.robust.pay.sdk.vo.BizVO;
import com.robust.pay.sdk.vo.CallbackVO;
import com.robust.pay.sdk.vo.OrderVO;

import net.sf.json.JSONObject;

/**
 * 红牛支付
 * 
 * @author acer
 */
@Repository
public class Pay5566 extends Base implements PayBase {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		// 配置参数
		BizVO bizVO = new BizVO();
		bizVO.setMerchantId(business.getUid());
		bizVO.setCustomerIp(payOrder.getIp());
		bizVO.setAmount(BigDecimal.valueOf(payOrder.getPrice()));
		bizVO.setPayType(Integer.parseInt(business.getPayCode()));
		bizVO.setOrderNo(payOrder.getGameOrderNumber());
		bizVO.setUsername(payOrder.getUserName());
		bizVO.setTrustLevel(0);
		bizVO.setVipLevel(0);
		bizVO.setCallback(business.getNotifyUrl());

		Map<String, String> map1 = new TreeMap<String, String>();
		map1.put("amount", bizVO.getAmount().toString());
		map1.put("customerIp", bizVO.getCustomerIp());
		map1.put("merchantId", bizVO.getMerchantId());
		map1.put("orderNo", bizVO.getOrderNo());
		map1.put("payType", bizVO.getPayType().toString());
		map1.put("trustLevel", bizVO.getTrustLevel().toString());
		map1.put("username", bizVO.getUsername());
		map1.put("vipLevel", bizVO.getVipLevel().toString());
		map1.put("callback", bizVO.getCallback());

		// 测试签名
		String sign = SignUtil.sign(business.getPrivate_key(), map1.toString());
		OrderVO order = BizHelper.genOrder(bizVO, business.getApiUrl(), business.getPrivate_key());
		if (order != null) {
			map.put("state", "SUCCESS");
			map.put("apiUrl", order.getUrl());
		} else {
			logger.info("5566支付下单失败");
		}

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

	}

	public Map<String, String> payCallback(CallbackVO callbackVO, HttpServletRequest request) throws Exception {
		logger.info("5566支付,api回调参数：" + callbackVO.toString());
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		// 得到对应的订单信息
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(callbackVO.getOrderNo()));
		if (payOrder != null) {
			// 获得对应的商户，方便拿到公钥
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				// 检查白名单
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;

				boolean isOK = BizHelper.verify(business.getPrivate_key(), callbackVO);
				if (isOK) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.valueOf(callbackVO.getAmount().toString()))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "200");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("加密值不相等==>提供的sign：" + callbackVO.getSign());
					logger.info(business.toString());
				}
			} else {
				logger.info("商务号不存在：" + callbackVO.getBillNo());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + callbackVO.getOrderNo());
		}
		return map;
	}

	/**
	 * 支付类型编码转换
	 */
	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "0";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "1";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "2";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	/*
	 * 获取支付类型下拉框
	 */
	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信扫码", "支付宝扫码", "云支付" };
	}

	/**
	 * 订单查询
	 */
	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		String sign = Encryption.md5_32(payOrder.getOrderNumber() + business.getUid() + business.getToken(), "");
		String k = "";
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("sign", sign);
		vmap.put("k", k);
		String re = ToolKit.request(business.getQueryUrl(), getMapParam(vmap));
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		if (resultJsonObj.getString("code").equals("0"))
			return true;
		logger.info("订单查询失败：" + payOrder.toString() + " 返回参数：" + re);
		return false;
	}

	/**
	 * 回调函数
	 */
	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);
		logger.info("5566支付,api回调参数为：" + parame.toString());

		CallbackVO callbackVO = new CallbackVO();
		callbackVO.setAmount(new BigDecimal(parame.get("amount")));
		callbackVO.setBillNo(parame.get("billNo"));
		callbackVO.setOrderNo(parame.get("orderNo"));
		callbackVO.setSign(parame.get("sign"));

		// 得到对应的订单信息
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(callbackVO.getOrderNo()));

		if (payOrder != null) {
			// 获得对应的商户，方便拿到公钥
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				// 检查白名单
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				boolean isOK = verify(business.getPrivate_key(), callbackVO);
				if (isOK) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							Double.valueOf(callbackVO.getAmount().toString()))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "200");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("加密值不相等==>提供的sign：" + callbackVO.getSign());
					logger.info(business.toString());
				}
			} else {
				logger.info("商务号不存在：" + callbackVO.getBillNo());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + callbackVO.getOrderNo());
		}
		return map;
	}

	public static String md5(String origin) {
		byte[] secretBytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(origin.getBytes());
			secretBytes = md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有MD5这个算法");
		}
		StringBuilder md5code = new StringBuilder((new BigInteger(1, secretBytes)).toString(16));
		int len = md5code.length();
		for (int i = 0; i < 32 - len; i++) {
			md5code.insert(0, "0");
		}
		return md5code.toString();
	}

	public static boolean verify(String privateKey, CallbackVO callbackVO) {
		if (null == privateKey || privateKey.length() == 0 || null == callbackVO) {
			return false;
		}
		String sign = callbackVO.getSign();
		String verifySign = md5(privateKey + callbackVO.getOrderNo());
		return sign.equalsIgnoreCase(verifySign);
	}

}
