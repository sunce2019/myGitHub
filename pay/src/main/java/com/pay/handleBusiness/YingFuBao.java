package com.pay.handleBusiness;

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
import com.pay.util.DateUtils;
import com.pay.util.Encryption;
import com.pay.util.HttpClients;
import com.pay.util.QRCode;
import com.pay.util.RSAUtils;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 盈付宝支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class YingFuBao extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("merchantId", business.getUid());
		map.put("version", "V1.1.0");
		map.put("askTime", DateUtils.getSdfTimes());
		map.put("money", changeY2F(payOrder.getPrice())); // 单位分
		map.put("billNumero", payOrder.getGameOrderNumber());// 商户单号 需要确保唯一
		map.put("currency", "CNY");
		map.put("goodsDesc", "商品描述");// 商品描述
		map.put("goodsName", "商品名称");// 商品名称
		map.put("goodsRemark", "商品备注");// 商品备注
		map.put("notifyUrl", business.getNotifyUrl());
		map.put("orderCreateIp", payOrder.getIp());// 商户发起支付请求的IP
		map.put("payType", business.getPayCode());// 1.微信扫码 WECHAT_NATIVE 2.银联扫码 UNION_NATIVE 3.QQ钱包扫码 QQ_NAIVE
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		JSONObject resulthead = new JSONObject();
		resulthead.put("merchantId", map.get("merchantId"));
		resulthead.put("version", map.get("version"));
		resulthead.put("askTime", map.get("askTime"));

		JSONObject resultbody = new JSONObject();
		resultbody.put("money", map.get("money")); // 单位分
		resultbody.put("billNumero", map.get("billNumero"));// 商户单号 需要确保唯一
		resultbody.put("currency", map.get("currency"));
		resultbody.put("goodsDesc", map.get("goodsDesc"));// 商品描述
		resultbody.put("goodsName", map.get("goodsName"));// 商品名称
		resultbody.put("goodsRemark", map.get("goodsRemark"));// 商品备注
		resultbody.put("notifyUrl", map.get("notifyUrl"));
		resultbody.put("orderCreateIp", map.get("orderCreateIp"));// 商户发起支付请求的IP
		resultbody.put("payType", map.get("payType"));// 1.微信扫码 WECHAT_NATIVE 2.银联扫码 UNION_NATIVE 3.QQ钱包扫码 QQ_NAIVE
		String context = RSAUtils.verifyAndEncryptionToString(resultbody, resulthead, business.getPrivate_key(),
				business.getPublic_key());
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("context", context);
		JSONObject jsonResult = HttpClients.doPost(business.getApiUrl(), jsonParam);
		if (jsonResult.getBoolean("success")) {
			String resultContext = jsonResult.getString("context");
			String result = RSAUtils.decryptByPrivateKey(resultContext, business.getPrivate_key());
			boolean isVerify = RSAUtils.verify(result, business.getPublic_key());
			if (isVerify) {
				JSONObject resultJsonObj = JSONObject.fromObject(result);
				resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("businessContext"));
				map.put("state", "SUCCESS");
				map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("payurl")));
			}
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), context);
		}
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("盈付宝支付,api回调参数：" + parame.toString());

		String resultContext = parame.get("context");

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderNumber")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String result = RSAUtils.decryptByPrivateKey(resultContext, business.getPrivate_key());
				boolean isVerify = RSAUtils.verify(result, business.getPublic_key());
				if (isVerify) {
					JSONObject resultJsonObj = JSONObject.fromObject(result);
					resultJsonObj = JSONObject.fromObject(resultJsonObj.getString("businessContext"));
					logger.info(result);
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							changeF2Y(resultJsonObj.getString("amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "SUC");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("sign"));
					logger.info("我方加密的sign：" + result);
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
				return "WECHAT_NATIVE";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "ALI_H5";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "WECHAT_H5";
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
		return new String[] { "微信跳转", "支付宝跳转", "微信扫码" };
	}

}
