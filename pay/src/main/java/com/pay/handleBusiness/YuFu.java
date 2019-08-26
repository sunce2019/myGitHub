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
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 御付
 * 
 * @author acer
 * 
 *
 */
@Repository
public class YuFu extends Base implements PayBase {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		// 参数拼写
		map.put("merchant_id", business.getUid());// 系统分配商户号
		map.put("payment_way", business.getPayCode());
		map.put("order_amount", Double.toString(payOrder.getPrice()));// 订单金额
		map.put("source_order_id", payOrder.getGameOrderNumber());// 订单号
		map.put("goods_name", "dsad");
		map.put("bank_code", "ICBC");// 商品名称
		map.put("client_ip", payOrder.getIp());// 下单IP
		map.put("notify_url", business.getNotifyUrl());// 同步消息返回地址
		map.put("return_url", business.getNotifyViewUrl());// 异步消息通知Url
		// 获取签名并发送请求
		signature(map, business, payOrder);

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> mapv = new TreeMap<String, String>();
		mapv.put("merchant_id", map.get("merchant_id"));
		mapv.put("payment_way", map.get("payment_way"));
		mapv.put("order_amount", map.get("order_amount"));
		mapv.put("source_order_id", map.get("source_order_id"));
		mapv.put("goods_name", map.get("goods_name"));
		mapv.put("bank_code", map.get("bank_code"));
		mapv.put("client_ip", map.get("client_ip"));
		mapv.put("notify_url", map.get("notify_url"));
		mapv.put("return_url", map.get("return_url"));
		// 根据算法获得签名
		String sign = Encryption.md5_32(getMapParam(mapv) + "&token=" + business.getToken(), "");
		map.put("sign", sign);
		map.put("state", "SUCCESS");
		map.put("apiUrl", business.getApiUrl());

	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("御付,api回调参数：" + parame.toString());
		if (!parame.get("status").equals("1")) {
			return null;
		}

		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("merchant_id", parame.get("merchant_id"));
		vmap.put("source_order_id", parame.get("source_order_id"));
		vmap.put("order_amount", parame.get("order_amount"));
		vmap.put("order_code", parame.get("order_code"));
		vmap.put("goods_name", parame.get("goods_name"));
		vmap.put("payTime", parame.get("payTime"));
		vmap.put("status", parame.get("status"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("source_order_id")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = getMapParam(vmap) + "&token=" + business.getToken();
				logger.info("加密参数：" + param);
				String key2 = Encryption.md5_32(param, "");
				if (StringUtils.equals(key2, parame.get("sign"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("order_amount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "ok");
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
		vmap.put("merchantCode", map.get("merchantCode"));// 系统分配商户号
		vmap.put("orderNumber", map.get("orderNumber"));// 订单号
		String sign = Encryption.md5_32(getMapParam(vmap) + business.getToken(), "").toLowerCase();
		vmap.put("sign", sign);
		String re = ToolKit.request(business.getApiUrl(), getMapParam(vmap));
		JSONObject resultJsonObj = JSONObject.fromObject(re);
		if (resultJsonObj.getString("content:payStatus").equals("orderPaid"))
			return true;
		logger.info("订单查询失败：" + payOrder.toString() + " 返回参数：" + re);
		return false;
	}

	/**
	 * 支付类型编码转换
	 */
	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "28";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "27";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "4";
			} else if (payOrder.getOrderType().contains(PayTran.WANGGUAN.getText())) {
				return "30";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "31";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "29";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		// TODO Auto-generated method stub
		return new String[] { "支付宝扫码", "微信扫码", "支付宝跳转", "银联扫码", "银联在线支付", "网关支付" };
	}

}
