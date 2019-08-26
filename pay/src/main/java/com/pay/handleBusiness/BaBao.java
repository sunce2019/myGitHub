package com.pay.handleBusiness;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.dao.BaseDao;
import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.AmountUtils;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 八宝支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class BaBao extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<PayOrder> payorderDao;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("version", "1.0");
		map.put("partnerid", business.getUid());
		map.put("orderid", payOrder.getGameOrderNumber());
		map.put("payamount", AmountUtils.changeY2F(Double.toString(payOrder.getPrice())));
		map.put("payip", "100.100.100.100");
		map.put("notifyurl", business.getNotifyUrl());
		map.put("returnurl", business.getNotifyViewUrl());
		map.put("paytype", business.getPayCode());
		map.put("phoneNo", "138");
		map.put("remark", "remark");

		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
//		String resultJsonStr =  ToolKit.request(business.getApiUrl(),map.get("reqParam"));
//		if(resultJsonStr==null)return;
//		logger.info("八宝api返回："+resultJsonStr);
//		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
//		if(resultJsonObj.getString("sys_code").equals("1")) {
//			map.put("state", "SUCCESS");
//			map.put("apiUrl",resultJsonObj.getString("pay_url"));
//		}
	}

	public static void main(String[] args) {
		String s = "{\"message\":\"0\",\"okordertime\":\"2019/5/10 20:02:33\",\"orderno\":\"31159373848288051273rlk8yg\",\"orderstatus\":\"4\",\"partnerid\":\"73rlk8yg\",\"partnerorderid\"\r\n"
				+ ":\"1000001009665796\",\"payamount\":\"10000\",\"paytype\":\"alWap\",\"remark\":\"remark\",\"sign\":\"4fa8c55b308dba76067f202ad984c97d\",\"version\":\"1.0\"}";
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String version = resultJsonObj.getString("version");
		String partnerid = resultJsonObj.getString("partnerid");
		String partnerorderid = resultJsonObj.getString("partnerorderid");
		String payamount = resultJsonObj.getString("payamount");
		String orderstatus = resultJsonObj.getString("orderstatus");
		String orderno = resultJsonObj.getString("orderno");
		String okordertime = resultJsonObj.getString("okordertime");
		String paytype = resultJsonObj.getString("paytype");
		String message = resultJsonObj.getString("message");
		String remark = resultJsonObj.getString("remark");
		String sign = resultJsonObj.getString("sign");

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("version", version);
		vmap.put("partnerid", partnerid);
		vmap.put("partnerorderid", partnerorderid);

		vmap.put("payamount", payamount);
		vmap.put("orderstatus", orderstatus);
		vmap.put("orderno", orderno);
		vmap.put("okordertime", okordertime);
		vmap.put("paytype", paytype);
		vmap.put("message", message);
		vmap.put("remark", remark);

		String parameter = getMapParam(vmap) + "&key=3f39f937df007aec";
		logger.info("加密参数：" + parameter);
		String key2 = Encryption.md5_32(parameter, "").toLowerCase();
		System.out.println(key2);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String s = getJson(request);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String version = resultJsonObj.getString("version");
		String partnerid = resultJsonObj.getString("partnerid");
		String partnerorderid = resultJsonObj.getString("partnerorderid");
		String payamount = resultJsonObj.getString("payamount");
		String orderstatus = resultJsonObj.getString("orderstatus");
		String orderno = resultJsonObj.getString("orderno");
		String okordertime = resultJsonObj.getString("okordertime");
		String paytype = resultJsonObj.getString("paytype");
		String message = resultJsonObj.getString("message");
		String remark = resultJsonObj.getString("remark");
		String sign = resultJsonObj.getString("sign");

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("version", version);
		vmap.put("partnerid", partnerid);
		vmap.put("partnerorderid", partnerorderid);

		vmap.put("payamount", payamount);
		vmap.put("orderstatus", orderstatus);
		vmap.put("orderno", orderno);
		vmap.put("okordertime", okordertime);
		vmap.put("paytype", paytype);
		vmap.put("message", message);
		vmap.put("remark", remark);
		logger.info("八宝支付,api回调：" + vmap.toString());
		if (!message.equals("0")) {
			logger.info("八宝支付,api回调 状态不等于0：" + vmap.toString());
			return map;
		}
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(partnerorderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String parameter = getMapParam(vmap) + "&key=" + business.getToken();
				logger.info("加密参数：" + parameter);
				String key2 = Encryption.md5_32(parameter, "").toLowerCase();
				if (StringUtils.equals(key2, sign)) {
					Long l = Long.parseLong(payamount);
					String money = AmountUtils.changeF2Y(l);
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(money))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + sign);
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(parameter);
				}
			} else {
				logger.info("商务号不存在：" + vmap.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + vmap.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("version", map.get("version"));
		vmap.put("partnerid", map.get("partnerid"));
		vmap.put("orderid", map.get("orderid"));
		vmap.put("payamount", map.get("payamount"));
		vmap.put("payip", payOrder.getIp());
		vmap.put("notifyurl", map.get("notifyurl"));
		vmap.put("returnurl", map.get("returnurl"));
		vmap.put("paytype", map.get("paytype"));
		vmap.put("phoneNo", map.get("phoneNo"));
		vmap.put("remark", map.get("remark"));
		String str = getMapParam(vmap);
		map.put("sign", Encryption.md5_32(str + "&key=" + business.getToken(), "").toLowerCase());
		map.put("state", "SUCCESS");
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "wcScan";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "wcWap";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alScan";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "alWap";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "微信扫码", "支付宝跳转", "微信跳转" };
	}

}
