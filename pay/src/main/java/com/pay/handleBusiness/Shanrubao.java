package com.pay.handleBusiness;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * 闪入宝
 * 
 * @author star
 * @version 创建时间：2019年3月27日下午1:42:25
 */
@Repository
public class Shanrubao implements PayBase {

	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseService baseService;

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		map.put("uid", business.getUid());
		map.put("price", Double.toString(payOrder.getPrice()));
		map.put("istype", business.getPayCode());
		map.put("notify_url", business.getNotifyUrl());
		map.put("return_url", business.getNotifyViewUrl());
		map.put("orderid", payOrder.getGameOrderNumber());
		map.put("orderuid", payOrder.getUserName());
		map.put("goodsname", payOrder.getGoodsName());
		map.put("version", "2");
		map.put("retPath", "pay");
		map.put("apiUrl", business.getApiUrl());
		signature(map, business, payOrder);
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("goodsname=").append(map.get("goodsname"));
		sb.append("&istype=").append(map.get("istype"));
		sb.append("&notify_url=").append(map.get("notify_url"));
		sb.append("&orderid=").append(map.get("orderid"));
		sb.append("&orderuid=").append(map.get("orderuid"));
		sb.append("&price=").append(map.get("price"));
		sb.append("&return_url=").append(map.get("return_url"));
		sb.append("&token=").append(business.getToken());
		sb.append("&uid=").append(map.get("uid"));
		sb.append("&version=").append(map.get("version"));
		map.put("key", Encryption.md5_32(sb.toString(), ""));
		map.put("state", "SUCCESS");
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			//// 必填。1：支付宝；2：微信支付；3：QQ；4：京东；5：网关；6：银联扫码；7：银联快捷 所有通道支持电脑+手机H5页面自动识别
			if (payOrder.getOrderType().contains(PayTran.ZFB.getCode())) {
				return "1";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getCode())) {
				return "2";
			} else if (payOrder.getOrderType().contains(PayTran.QQ.getCode())) {
				return "3";
			} else if (payOrder.getOrderType().contains(PayTran.JD.getCode())) {
				return "4";
			} else if (payOrder.getOrderType().contains(PayTran.WANGGUAN.getCode())) {
				return "5";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WALLET.getCode())) {
				return "6";
			} else if (payOrder.getOrderType().contains(PayTran.UNION_WAP.getCode())) {
				return "7";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		String paysapi_id = request.getParameter("paysapi_id");
		String orderid = request.getParameter("orderid");
		String price = request.getParameter("price");
		String realprice = request.getParameter("realprice");
		String orderuid = request.getParameter("orderuid");
		String key = request.getParameter("key");
		if (StringUtils.isBlank(paysapi_id) || StringUtils.isBlank(orderid) || StringUtils.isBlank(price)
				|| StringUtils.isBlank(realprice) || StringUtils.isBlank(orderuid) || StringUtils.isBlank(key)) {
			return map;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("orderid=").append(orderid);
		sb.append("&orderuid=").append(orderuid);
		sb.append("&paysapi_id=").append(paysapi_id);
		sb.append("&price=").append(price);
		sb.append("&realprice=").append(realprice);
		logger.info("闪入宝,api回调：" + sb.toString());

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(orderid));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				sb.append("&token=").append(business.getToken());
				String key2 = Encryption.md5_32(sb.toString(), "");
				if (StringUtils.equals(key2, key)) {
					logger.info("回调成功：" + payOrder.toString());
					baseService.successfulPayment(payOrder.getId(), business.getId(), Double.parseDouble(realprice));
					map.put("state", "SUCCESS");
					map.put("reBusiness", "SUCCESS");
					map.put("payorder_id", Integer.toString(payOrder.getId()));
				} else {
					logger.info("提供的sign 加密值不相等：" + key);
					logger.info("我方加密的sign：" + key2);
					logger.info(business.toString());
					logger.info(sb.toString());
				}
			} else {
				logger.info("商务号不存在：" + sb.toString());
			}
		} else {
			logger.info("订单号 和 状态 不存在：" + sb.toString());
		}

		return map;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		return false;
	}

	public static void main(String[] args) {
		String s = "{\r\n" + "        //提示文字信息，成功失败。\r\n" + "        \"msg\":\"OK\",\r\n" + "        \"data\":{\r\n"
				+ "            //商户自定义订单号\r\n" + "            \"orderid\":\"201804292202098874835\",\r\n"
				+ "            //订单状态：1-支付中,2-失败,3-成功,4-超时关闭,5-无可用二维码\r\n" + "            \"status\":\"1\",\r\n"
				+ "        },\r\n" + "        //code:1:成功;-1:失败。\r\n" + "        \"code\":1或-1,\r\n"
				+ "        //url暂时没用\r\n" + "        \"url\":\"\"\r\n" + "    }\r\n" + "";
		JSONObject resultJsonObj = JSONObject.fromObject(s);
		String stateCode = resultJsonObj.getString("data");
		resultJsonObj = JSONObject.fromObject(stateCode);
		System.out.println(resultJsonObj.getString("status"));
	}

	@Override
	public String[] getPayType() throws Exception {
		return new String[] { "支付宝扫码", "支付宝跳转", "微信扫码", "微信跳转", "京东扫码", "银联扫码", "银联在线支付" };
	}

}
