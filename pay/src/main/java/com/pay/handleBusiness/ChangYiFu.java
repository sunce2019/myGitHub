package com.pay.handleBusiness;

import java.text.SimpleDateFormat;
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
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 畅壹付
 * 
 * @author acer
 *
 */
@Repository
public class ChangYiFu extends Base implements PayBase {

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
		map.put("merchantCode", business.getUid());// 系统分配商户号
		map.put("amount", doubleToString(payOrder.getPrice()));// 订单金额(元)
		map.put("orderNumber", payOrder.getGameOrderNumber());// 订单号
		map.put("payCode", business.getPayCode());// 支付类型
		map.put("submitTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 订单日期,格式:yyyyMMddHHmmss
		map.put("commodityName", "dsad");// 商品名称
		map.put("submitIp", payOrder.getIp());// 下单IP
		map.put("syncRedirectUrl", business.getNotifyViewUrl());// 同步消息返回地址
		map.put("asyncNotifyUrl", business.getNotifyUrl());// 异步消息通知Url
		map.put("remark", "aaaaa");// 订单备注
		// 获取签名并发送请求
		signature(map, business, payOrder);

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {

		Map<String, String> mapv = new TreeMap<String, String>();
		mapv.put("merchantCode", map.get("merchantCode"));// 系统分配商户号
		mapv.put("amount", map.get("amount"));// 订单金额(元)
		mapv.put("orderNumber", map.get("orderNumber"));// 订单号
		mapv.put("payCode", map.get("payCode"));// 支付类型
		mapv.put("submitTime", map.get("submitTime"));// 订单日期,格式:yyyyMMddHHmmss
		mapv.put("commodityName", map.get("commodityName"));// 商品名称
		mapv.put("submitIp", map.get("submitIp"));// 下单IP
		mapv.put("syncRedirectUrl", map.get("syncRedirectUrl"));// 同步消息返回地址
		mapv.put("asyncNotifyUrl", map.get("asyncNotifyUrl"));// 异步消息通知Url
		mapv.put("remark", map.get("remark"));// 订单备注

		// 根据算法获得签名
		String sign = Encryption.md5_32(getMapParam(mapv) + business.getToken(), "").toLowerCase();
		map.put("sign", sign);
		map.remove("state");
		// 发送请求 获得响应
		String s = ToolKit.getPostResponse(business.getApiUrl(), map);
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String status = resultJsonObj.getString("returnCode");
		if (status.equals("0")) {
			map.put("state", "SUCCESS");
			if (payOrder.getOrderType().equals(PayTran.WX_WAP.getText())
					|| payOrder.getOrderType().equals(PayTran.ZFB_WAP.getText())) {
				business.setJumpType(2);
				map.put("apiUrl", resultJsonObj.getString("content"));
			} else {
				map.put("apiUrl", QRCode.generalQRCode(resultJsonObj.getString("content")));
			}
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), s);
			System.out.println(resultJsonObj.get("message"));
		}
		System.out.println(s);

	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);
		logger.info("畅壹付支付,api回调参数：" + parame.toString());
		if (!parame.get("payStatus").equals("orderPaid")) {
			return null;
		}
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("merchantCode", parame.get("merchantCode"));
		vmap.put("amount", parame.get("amount"));
		vmap.put("orderNumber", parame.get("orderNumber"));
		vmap.put("payCode", parame.get("payCode"));
		vmap.put("trxNo", parame.get("trxNo"));
		vmap.put("submitTime", parame.get("submitTime"));
		vmap.put("commodityName", parame.get("commodityName"));
		vmap.put("payStatus", parame.get("payStatus"));
		vmap.put("payTime", parame.get("payTime"));
		vmap.put("remark", parame.get("remark"));
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("orderNumber")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = getMapParam(vmap) + business.getToken();
				logger.info("加密参数：" + param);
				String key2 = Encryption.md5_32(param, "").toLowerCase();
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
	 * 支付类型转换
	 */
	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "D0_ALIPAY_H5";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "D0_ALIPAY_SCAN";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "D0_WX_SCAN";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "D0_WX_H5";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "D0_UNION_SCAN";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "D0_QUICKPAY";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP_YS.getText())) {
				return "D1_ALIPAY_H5";// 支付宝D1原生
			} else if (payOrder.getOrderType().contains(PayTran.WX_YS.getText())) {
				return "D1_WX_SCAN";// 微信D1扫码
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		// TODO Auto-generated method stub

		return new String[] { "支付宝扫码", "微信扫码", "微信跳转", "支付宝跳转", "银联扫码", "银联在线支付", "支付宝原生", "微信扫码原生" };
	}

}
