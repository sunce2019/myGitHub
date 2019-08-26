package com.pay.handleBusiness;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.pay.util.RedRSAUtils;

import net.sf.json.JSONObject;

/**
 * 红牛支付
 * 
 * @author acer
 */
@Repository
public class Redbull extends Base implements PayBase {

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

		DecimalFormat df = new DecimalFormat("######0.00");
		map.put("brandNo", business.getUid());// 系统分配商户号
		map.put("callbackUrl", business.getNotifyUrl()); // 回调地址
		map.put("frontUrl", business.getNotifyViewUrl());// 同步通知地址
		map.put("serviceType", business.getPayCode()); // 支付类型
		map.put("orderNo", payOrder.getGameOrderNumber());// 订单号
		map.put("price", df.format(payOrder.getPrice()));// 订单金额
		map.put("signType", "RSA-S");// 加密算法
		map.put("userName", business.getCustomer_name()); // 客户名称
		map.put("clientIP", payOrder.getIp()); // 客户ip
		// 获取签名
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> mapv = new TreeMap<String, String>();
		mapv.put("brandNo", map.get("brandNo")); // 商户号
		mapv.put("clientIP", map.get("clientIP")); // 客户ip
		mapv.put("orderNo", map.get("orderNo")); // 订单号
		mapv.put("price", map.get("price")); // 价格
		mapv.put("serviceType", map.get("serviceType")); // 支付类型
		mapv.put("userName", payOrder.getUserName()); // 用户姓名
		String signStr = getMapParam(mapv); // ASCII
		byte[] sign = RedRSAUtils.signMd5ByPriKey(signStr, business.getPrivate_key()); // 私钥加密
		mapv.put("signature", Base64.getEncoder().encodeToString(sign));// 签名转码
		mapv.put("callbackUrl", map.get("callbackUrl"));// 设置回调地址
		String js = ToolKit.mapToJson(mapv); // 转换成json
		String strReturn = ToolKit.requestJson(business.getApiUrl(), js); // 发送请求，获取返回参数
		JSONObject job = JSONObject.fromObject(strReturn);
		if (job.getString("isSuccess").equals("true")) {
			String data = job.getString("data");
			JSONObject jobData = JSONObject.fromObject(data);
			String pay_url = jobData.getString("payUrl"); // 拿到跳转地址
			map.put("state", "SUCCESS");
			map.put("apiUrl", pay_url);
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), strReturn);
		}
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static SimpleDateFormat signSdf = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 回调函数
	 */
	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);
		logger.info("红牛支付,api回调参数：" + parame.toString());
		Map<String, String> vmap = new TreeMap<String, String>();
		// 获取返回的参数
		vmap.put("orderNo", parame.get("orderNo"));
		vmap.put("orderStatus", parame.get("orderStatus"));
		vmap.put("tradeNo", parame.get("tradeNo"));
		// 转换价格
		vmap.put("price", parame.get("price"));
		vmap.put("actualPrice", parame.get("actualPrice"));
		// 转换时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String orderTime = parame.get("orderTime");
		Date ot = sdf.parse(orderTime);
		orderTime = sdf.format(ot);
		String dealTime = parame.get("dealTime");
		Date dt = sdf.parse(dealTime);
		dealTime = sdf.format(dt);
		String tmdeal = null, tmorder = null;
		try {
			tmdeal = Redbull.signSdf.format(Redbull.sdf.parse(parame.get("orderTime")));
			tmorder = Redbull.signSdf.format(Redbull.sdf.parse(parame.get("dealTime")));
		} catch (ParseException e) {
			return null;
		}
		vmap.put("orderTime", tmdeal);
		vmap.put("dealTime", tmorder);

		// 得到对应的订单信息
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderNo")));
		if (payOrder != null) {
			// 获得对应的商户，方便拿到公钥
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				// 检查白名单
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = getMapParam(vmap); // 拼接加密字符串
				logger.info("加密参数：" + param);
				String newSign = URLDecoder.decode(parame.get("signature"), "UTF-8");

				boolean isValid = RedRSAUtils.checkSignByPubkey(param, Base64.getDecoder().decode(newSign),
						business.getPublic_key());
				if (isValid) {
					if (!"1".equals(parame.get("orderStatus"))) {
						logger.info("下单失败：" + parame.get("orderNo"));
					} else {
						// 修改订单和商户信息成功，提示回调成功
						if (baseService.successfulPayment(payOrder.getId(), business.getId(),
								StringToDouble(parame.get("price")))) {
							logger.info("第三方回调成功：" + payOrder.toString());
							map.put("state", "SUCCESS");
							map.put("reBusiness", "OK");
							map.put("payorder_id", Integer.toString(payOrder.getId()));
						}
					}
				} else {
					logger.info("加密值不相等==>提供的sign：" + parame.get("signature"));
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

	/**
	 * 支付类型编码转换
	 */
	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "1102";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "1105";
			} else if (payOrder.getOrderType().contains(PayTran.JD.getText())) {
				return "1104";
			} else if (payOrder.getOrderType().contains(PayTran.JD_WAP.getText())) {
				return "1204";
			} else if (payOrder.getOrderType().contains(PayTran.QQ.getText())) {
				return "1103";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "1101";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "1201";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "1202";
			} else if (payOrder.getOrderType().contains(PayTran.QQ_WAP.getText())) {
				return "1203";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "1205";
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
		return new String[] { "微信扫码", "银联扫码", "京东扫码", "QQ支付H5", "QQ扫码", "支付宝扫码", "支付宝跳转", "微信跳转", "微信付款码", "京东H5",
				"快捷支付" };
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

}
