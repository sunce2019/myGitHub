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
import com.pay.util.SihaiUtil;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 四海
 * 
 * @author acer
 * 
 *
 */
@Repository
public class SiHai extends Base implements PayBase {

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
		map.put("p0_Cmd", "Buy");
		map.put("p1_MerId", business.getUid());// 系统分配商户号
		map.put("p2_Order", payOrder.getGameOrderNumber());// 订单号
		map.put("p3_Amt", Double.toString(payOrder.getPrice()));// 订单金额
		map.put("p4_Cur", "CNY");
		map.put("p5_Pid", "ICBC");// 商品名称
		map.put("p6_Pcat", "game");// 商品种类
		map.put("p7_Pdesc", "aaaa");// 商品描述
		map.put("p8_Url", business.getNotifyUrl());// 同步消息返回地址
		map.put("pa_MP", "bbbbbb");// 商品扩展信息
		map.put("pd_FrpId", business.getPayCode());
		map.put("pr_NeedResponse", "1");
		// 获取签名并发送请求
		signature(map, business, payOrder);

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		Map<String, String> mapv = new TreeMap<String, String>();
		mapv.put("p0_Cmd", map.get("p0_Cmd"));
		mapv.put("p1_MerId", map.get("p1_MerId"));
		mapv.put("p2_Order", map.get("p2_Order"));
		mapv.put("p3_Amt", map.get("p3_Amt"));
		mapv.put("p4_Cur", map.get("p4_Cur"));
		mapv.put("p5_Pid", map.get("p5_Pid"));
		mapv.put("p6_Pcat", map.get("p6_Pcat"));
		mapv.put("p7_Pdesc", map.get("p7_Pdesc"));
		mapv.put("p8_Url", map.get("p8_Url"));
		mapv.put("pa_MP", map.get("pa_MP"));
		mapv.put("pd_FrpId", map.get("pd_FrpId"));
		mapv.put("pr_NeedResponse", map.get("pr_NeedResponse"));
		// 根据算法获得签名
		// String hmac =
		// Encryption.md5_32(getMapParam(mapv)+"&token="+business.getToken(),"");
		String hmac = SihaiUtil.hmacSign(getMapParam(mapv), business.getToken());
		map.put("hmac", hmac);
		String strReturn = ToolKit.request(business.getApiUrl(), getMapParam(map));
		JSONObject job = JSONObject.fromObject(strReturn);
		if (job.getString("status").equals("0")) {
			String data = job.getString("payImg");
			map.put("state", "SUCCESS");
			map.put("apiUrl", data);
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), strReturn);
		}

	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);

		logger.info("四海云付,api回调参数：" + parame.toString());
		if (!parame.get("r1_Code").equals("1")) {
			return null;
		}
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("p1_MerId", parame.get("p1_MerId"));

		vmap.put("r0_Cmd", "Buy");
		vmap.put("r1_Code", parame.get("r1_Code"));
		vmap.put("r2_TrxId", parame.get("r2_TrxId"));
		vmap.put("r3_Amt", parame.get("r3_Amt"));
		vmap.put("r4_Cur", "CNY");
		vmap.put("r5_Pid", parame.get("r5_Pid"));
		vmap.put("r6_Order", parame.get("r6_Order"));
		vmap.put("r7_Uid", parame.get("r7_Uid"));
		vmap.put("r8_MP", parame.get("r8_MP"));
		vmap.put("r9_BType", "2");
		// vmap.put("rp_PayDate", parame.get("rp_PayDate"));
		StringBuffer sb = new StringBuffer();
		sb.append(parame.get("p1_MerId"));
		sb.append("Buy");
		sb.append(parame.get("r1_Code"));
		sb.append(parame.get("r2_TrxId"));
		sb.append(parame.get("r3_Amt"));
		sb.append("CNY");
		sb.append(parame.get("r5_Pid"));
		sb.append(parame.get("r6_Order"));
		sb.append(parame.get("r7_Uid"));
		sb.append(parame.get("r8_MP"));
		sb.append("2");
		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("r6_Order")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String key2 = SihaiUtil.hmacSign(sb.toString(), business.getToken());
				if (StringUtils.equals(key2, parame.get("hmac"))) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("r3_Amt")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "success");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("hmac"));
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
			if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "weixin";
			}
			if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getText())) {
				return "bdpay";
			}
			if (payOrder.getOrderType().contains(PayTran.JD.getText())) {
				return "jdpay";
			} else if (payOrder.getOrderType().contains(PayTran.QQ_WAP.getText())) {
				return "tenpaywap";
			} else if (payOrder.getOrderType().contains(PayTran.QQ.getText())) {
				return "qqmobile";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "alipay";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "alipaywap";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "wxwap";
			} else if (payOrder.getOrderType().contains(PayTran.WX_YS.getText())) {
				return "weixincode";
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP_YS.getText())) {
				return "alipaycode";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getText())) {
				return "yinlian";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		// TODO Auto-generated method stub
		return new String[] { "微信扫码", "银联扫码", "京东扫码", "QQ支付H5", "QQ扫码", "支付宝扫码", "支付宝跳转", "微信跳转", "微信付款码", "支付宝付款码",
				"快捷支付" };
	}

}
