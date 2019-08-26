package com.pay.handleBusiness;

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
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

/**
 * 星入宝
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午4:24:30
 */
@Repository
public class XingRuBao extends Base implements PayBase {
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
		map.put("uid", Business.getUid());
		map.put("price", doubleToString(payOrder.getPrice()));// WX:微信支付,ZFB:支付宝支付 QQ：QQ支付 BAI_DU：百度支付 JD：京东支付
																// UNIONPAY_QRCODE：银联二维码支付 WX_WAP：微信H5支付
		map.put("istype", Business.getPayCode());// 4位随机数
		map.put("notify_url", Business.getNotifyUrl());
		map.put("return_url", Business.getNotifyViewUrl());// 单位:分
		map.put("orderid", payOrder.getGameOrderNumber());// 单位:分
		map.put("orderuid", payOrder.getUserName());// 商品名称：20位
		map.put("goodsname", "wangzailiun");// 回调地址
		map.put("version", "2");// 回显地址
		signature(map, Business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("goodsname", map.get("goodsname"));
		vmap.put("istype", map.get("istype"));
		vmap.put("notify_url", map.get("notify_url"));
		vmap.put("orderid", map.get("orderid"));
		vmap.put("orderuid", map.get("orderuid"));
		vmap.put("price", map.get("price"));
		vmap.put("return_url", map.get("return_url"));
		vmap.put("token", business.getToken());
		vmap.put("uid", map.get("uid"));
		vmap.put("version", map.get("version"));
		String s = getMapParam(vmap);
		map.put("key", Encryption.md5_32(s, "").toLowerCase());
		map.put("state", "SUCCESS");
		map.put("apiUrl", business.getApiUrl());
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "2";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "1";
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
		logger.info("星入宝支付,api回调参数：" + parame.toString());

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("orderid", parame.get("orderid"));
		metaSignMap.put("orderuid", parame.get("orderuid"));
		metaSignMap.put("paysapi_id", parame.get("paysapi_id"));
		metaSignMap.put("price", parame.get("price"));
		metaSignMap.put("realprice", parame.get("realprice"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderid")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				metaSignMap.put("token", business.getToken());
				String parameter = getMapParam(metaSignMap);
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter, "").toLowerCase();
				if (StringUtils.equals(key2, parame.get("key"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("realprice")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("key"));
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
