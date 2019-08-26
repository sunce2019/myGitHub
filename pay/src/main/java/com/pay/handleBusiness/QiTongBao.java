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
import com.pay.payAssist.ApiUtil;
import com.pay.payAssist.HttpClientUtil;
import com.pay.payAssist.PayConfig;
import com.pay.payAssist.ToolKit;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 启通宝支付
 * 
 * @author star
 * @version 创建时间：2019年4月6日下午4:54:28
 */
@Repository
public class QiTongBao extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	@Override
	public void handleMain(Map<String, String> map, Business busines, PayOrder payOrder) throws Exception {
		JSONObject common = buildCommonParameters(busines);
		JSONObject business = buildBusinessParameters(busines, payOrder);
		// 会员私钥签名
		common.put("sign", ApiUtil.sign(business, busines.getPrivate_key()));
		common.put("content", business);
		System.out.println(common.toString());
		// 发送支付请求
		String response = HttpClientUtil.doPost(busines.getApiUrl() + PayConfig.UNIFIED_PAY, common.toString());
		System.out.println(response);
		JSONObject responseJson = JSONObject.fromObject(response);
		if (!responseJson.getString("retCode").equals(PayConfig.TWO_HUNDRED)) {
			payOrderService.updatePayOrder(payOrder.getId(), response);
			return;
		}
		JSONObject content = responseJson.getJSONObject("content");
		String sign = responseJson.getString("sign");
		// 平台公钥验签
		if (!ApiUtil.verify(content, sign, busines.getToken())) {
			// 验签失败
			return;
		}
		map.put("apiUrl", QRCode.generalQRCode(content.getString("content")));
		map.put("state", "SUCCESS");

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		String sign = Encryption.md5_32(map.get("mid") + map.get("oid") + map.get("amt") + map.get("way")
				+ map.get("back") + map.get("notify") + business.getToken(), "").toLowerCase();
		map.put("sign", sign);// 商品名称
		map.put("apiUrl", business.getApiUrl());
		map.put("state", "SUCCESS");
	}

	/**
	 * 公共参数
	 *
	 * @return
	 */
	public static JSONObject buildCommonParameters(Business busines) {
		JSONObject common = new JSONObject();
		common.put("memberNumber", busines.getUid());// 商户号
		common.put("method", "UNIFIED_PAYMENT");// 请求接口名称
		common.put("version", "V2.0.0");// 版本号
		common.put("sign", "");// 签名
		common.put("content", "");// 业务参数
		return common;
	}

	/**
	 * 业务参数
	 *
	 * @return
	 */
	public static JSONObject buildBusinessParameters(Business busines, PayOrder payOrder) {
		JSONObject business = new JSONObject();
		business.put("defrayalType", busines.getPayCode());// 支付方式(必填)
		business.put("memberOrderNumber", payOrder.getGameOrderNumber());// 商户订单号(必填),每次交易唯一
		business.put("tradeCheckCycle", "T1");// 结算周期(必填)
		business.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 订单时间(必填)
		business.put("currenciesType", "CNY");// 币种(必填)
		business.put("tradeAmount", changeY2F(payOrder.getPrice()));// 交易金额(必填)
		business.put("commodityBody", "商品信息");// 商品信息(必填)
		business.put("commodityDetail", "商品详情");// 商品详情(必填)
		business.put("commodityRemark", "商品备注");// 商品备注
		business.put("terminalId", payOrder.getUserName());// 设备ID
		business.put("terminalIP", payOrder.getIp());// 设备IP(必填)
		business.put("userId", payOrder.getUserName());// 用户标示(ALI_SCAN/WECHAT_SCAN必填)
		business.put("notifyUrl", busines.getNotifyUrl());// 异步通知地址(必填)
		business.put("returnUrl", busines.getNotifyViewUrl());// 同步通知地址
		business.put("attach", "");// 附加信息
		return business;
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");

		Map<String, String> parame = getCallBackParameter(request);
		logger.info("启通宝支付,api回调参数：" + parame.toString());

		JSONObject requestJson = JSONObject.fromObject(parame.get("content"));
		if (!"SUCCESS".equals(requestJson.getString("orderStatus"))) {
			return null;
		}

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(requestJson.getString("memberOrderNumber")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				// 验签
				if (ApiUtil.verify(requestJson, parame.get("sign"), business.getToken())) {
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							changeF2Y(requestJson.getString("tradeAmount")))) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", "ok");
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parame.get("sign"));
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
			} else if (payOrder.getOrderType().contains(PayTran.ZFB_WAP.getText())) {
				return "4";
			} else if (payOrder.getOrderType().contains(PayTran.WX_WAP.getText())) {
				return "3";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		ToolKit.requestJson("http://localhost:8080/callBack/qiTongBao.do",
				"{\"content\":{\"tradeAmount\":\"1000\",\"memberOrderNumber\":\"1000001157008544\",\"orderNumber\":\"TO01190720193526073376000013\",\"defrayalType\":\"WECHAT_JSAPI\"\r\n"
						+ ",\"tradeFee\":\"4\",\"orderStatus\":\"SUCCESS\",\"attach\":\"\",\"payBank\":\"\",\"paymentTime\":\"20190720193544\",\"currenciesType\":\"CNY\"},\"memberNumber\":\"A02000013000025\",\"method\":\"NOTIFY\",\"sign\":\"ipDgAPd2bCRwwNNRrbRB4qb5/D30O/AxcQogVGKpcuKs80sjNEdudcX48e+Zl7gyVfJWMrwq3PdKuu3cdmq9MTI2ZC+bwSE9VS4od\r\n"
						+ "iPWdSsjXr3lk+4aMxc+rRwMQNponY4/uIiabLEx5t+hQ18VSjw6UZkyCxa4odYHiWUpa0o=\",\"version\":\"V2.0.0\"}");
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "微信跳转", "支付宝跳转", "微信扫码" };
	}

}
