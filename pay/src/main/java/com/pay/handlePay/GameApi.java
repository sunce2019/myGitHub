package com.pay.handlePay;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.enums.CallBackGameStage;
import com.pay.enums.CustomerState;
import com.pay.model.Business;
import com.pay.model.Customer;
import com.pay.model.PayOrder;
import com.pay.payAssist.ToolKit;
import com.pay.service.BusinessService;
import com.pay.service.CustomerService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.PayUtils;
import com.pay.util.StringUtils;
import com.pay.vo.PaymentApi;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年3月29日下午5:11:50
 */
@Service("gameApi")
public class GameApi {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;

	/**
	 * 支付请求 api 校验
	 * 
	 * @param data
	 * @param merchNo
	 * @return
	 * @throws Exception
	 */
	public PaymentApi checkApiParam(String data, String merchNo) throws Exception {
		Customer customer = customerService.getCustomer(new Customer(merchNo, CustomerState.NORMAL.getCode()));
		if (customer == null) {
			logger.info("商户号不存在 或者 商户号已禁用：" + merchNo);
			return null;
		}
		// 解密data
		byte[] result = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data), customer.getPrivate_key1());
		if (result == null) {
			logger.info("解密失败：" + data);
			return null;
		}
		String resultData = new String(result, ToolKit.CHARSET);// 解密数据
		JSONObject jsonObj = JSONObject.fromObject(resultData);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("price", jsonObj.getString("price"));
		metaSignMap.put("istype", jsonObj.getString("istype"));
		metaSignMap.put("userName", jsonObj.getString("userName"));
		metaSignMap.put("orderid", jsonObj.getString("orderid"));
		metaSignMap.put("uid", jsonObj.getString("uid"));
		metaSignMap.put("callbackapiurl", jsonObj.getString("callbackapiurl"));
		metaSignMap.put("callbackviewurl", jsonObj.getString("callbackviewurl"));
		metaSignMap.put("time", jsonObj.getString("time"));
		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		logger.info("拼接参数metaSignJsonStr=" + metaSignJsonStr);
		String sign = ToolKit.MD5(metaSignJsonStr + customer.getKey(), ToolKit.CHARSET);// 32位
		if (sign.equals(jsonObj.getString("sign"))) {
			JSONObject jsonObject = JSONObject.fromObject(metaSignJsonStr);
			PaymentApi paymentApi = (PaymentApi) JSONObject.toBean(jsonObject, PaymentApi.class);
			paymentApi.setCustomer_id(customer.getId());
			paymentApi.setCustomer_name(customer.getName());
			paymentApi.setIp(customer.getIp());
			paymentApi.setCustomerUid(customer.getUid());
			logger.info("解密成功：" + paymentApi.toString());
			return paymentApi;
		} else {
			logger.info("签名失败：" + metaSignJsonStr);
		}
		return null;
	}

	/**
	 * 查询订单请求 api 校验
	 * 
	 * @param data
	 * @param merchNo
	 * @return 校验成功 true, 失败false
	 * @throws Exception
	 */
	public Map<String, Object> checkApiOrderParam(String data, String merchNo) throws Exception {
		Customer customer = customerService.getCustomer(new Customer(merchNo, CustomerState.NORMAL.getCode()));
		if (customer == null) {
			logger.info("商户号不存在：" + merchNo);
			return null;
		}
		byte[] result = ToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data), customer.getPrivate_key1());
		if (result == null) {
			logger.info("解密失败：" + data);
			return null;
		}
		String resultData = new String(result, ToolKit.CHARSET);// 解密数据
		JSONObject jsonObj = JSONObject.fromObject(resultData);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("orderid", jsonObj.getString("orderid")); // 我们系统订单号
		metaSignMap.put("payType", jsonObj.getString("payType")); // 支付类型
		metaSignMap.put("orderNo", jsonObj.getString("orderNo")); // 客户订单号
		metaSignMap.put("amount", jsonObj.getString("amount")); // 金额
		metaSignMap.put("uid", jsonObj.getString("uid")); // 商户号
		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		String sign = ToolKit.MD5(metaSignJsonStr + customer.getKey(), ToolKit.CHARSET);// 32位
		if (sign.equals(jsonObj.getString("sign"))) {
			String orderNumber = jsonObj.getString("orderid");
			PayOrder payOrder = payOrderService.getPayOrder2(new PayOrder(orderNumber));
			if (payOrder == null) {
				logger.info("订单号不存在：" + orderNumber);
				return null;
			}

			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business == null) {
				logger.info("商户号不存在：" + payOrder.toString());
				return null;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", business.getCode());
			map.put("payOrder", payOrder);
			map.put("business", business);
			return map;
		} else {
			logger.info("签名失败：" + metaSignJsonStr);
		}
		return null;
	}

	/**
	 * 游乐场回调游戏api
	 * 
	 * @param payorder_id
	 * @param flag        是否自动回调， true是的，false 手动回调
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean callBackGame(int payorder_id, boolean flag, boolean isauto) throws Exception {
		PayOrder payOrder = payOrderDao.get(PayOrder.class, payorder_id, LockMode.UPGRADE);
		logger.info("开始游戏方回调：" + payOrder.toString());
		if (payOrder == null) {
			logger.info("订单id不存在：" + payorder_id);
			return false;
		}

		if (!PayUtils.isOwnPay(payOrder.getOrderType())) { // 支付类型不是 自营
			if (payOrder.getCallGameFlag() != CallBackGameStage.MOVE.getCode()
					|| (flag && payOrder.getCallGameNumber() > 10)) {
				logger.info("任务执行完成：" + payOrder.toString());
				return false;
			}
		} else {
			if (payOrder.getCallGameFlag() != CallBackGameStage.INIT.getCode()) {
				logger.info("任务执行完成：" + payOrder.toString());
				return false;
			}
		}

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("orderNumber", payOrder.getOrderNumber()); // 我们系统订单号
		metaSignMap.put("payStateCode", "00"); // 00代表支付成功
		metaSignMap.put("userName", payOrder.getUserName()); // 游戏方提供 客户的唯一id账号
		if (isauto) {
			metaSignMap.put("price", Double.toString(payOrder.getPrice())); // 支付金额
		} else {
			metaSignMap.put("price", Double.toString(payOrder.getRealprice())); // 支付金额
		}

		String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
		logger.info("回调参数：" + metaSignJsonStr);
		Customer customer = customerDao.get(Customer.class, payOrder.getCustomer_id());
		if (customer == null) {
			logger.info("游戏方，商户id不存在：" + payOrder.toString());
		}

		String sign = ToolKit.MD5(metaSignJsonStr + customer.getKey(), ToolKit.CHARSET);// 32位
		metaSignMap.put("sign", sign);
		try {
			byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(metaSignMap).getBytes(ToolKit.CHARSET),
					customer.getPublic_key_2());
			String param = new BASE64Encoder().encode(dataStr);
			String s = "data=" + URLEncoder.encode(param, "UTF-8") + "&merchNo=" + payOrder.getGameOrderNumber();
			String re = ToolKit.request(payOrder.getCallbackapiurl(), s);
			boolean flag2 = false;
			if (StringUtils.equals("SUCCESS", re)) { // 只有收到SUCCESS 才代表成功
				payOrder.setCallGameFlag(CallBackGameStage.SUCCESS.getCode());
				payOrder.setCallGameSUCTime(new Date());
				logger.info("回调游戏api成功：" + payOrder.toString());
				flag2 = true;
			} else {
				payOrder.setCallGameNumber(payOrder.getCallGameNumber() + 1);
				logger.info("回调游戏方失败：" + payOrder.toString());
				logger.info(re);
			}
			if (flag)
				payOrderDao.update(payOrder);
			return flag2;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("回调游戏方出错：" + payOrder.toString());
			payOrder.setCallGameNumber(payOrder.getCallGameNumber() + 1);
			payOrderDao.update(payOrder);
		}
		return false;
	}

	/**
	 * 中博回调游戏api
	 * 
	 * @param payOrder
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Transactional(rollbackFor = Exception.class)
	public boolean zbCallBackGame(int payorder_id, boolean flag, Customer customer, boolean manual) throws Exception {
		@SuppressWarnings("deprecation")
		PayOrder payOrder = payOrderDao.get(PayOrder.class, payorder_id, LockMode.UPGRADE);
		if (payOrder == null) {
			logger.info("订单id不存在：" + payorder_id);
			return false;
		}

		if ((!manual && payOrder.getCallGameFlag() != CallBackGameStage.INIT.getCode())
				|| (flag && payOrder.getCallGameNumber() > 10)) {
			logger.info("任务执行完成：" + payOrder.toString());
			return false;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("account=").append(payOrder.getUserName()).append("&amount=").append(payOrder.getRealprice())
				.append("&orderno=").append(payOrder.getGameOrderNumber()).append("&tradeno=")
				.append(payOrder.getOrderNumber()).append("&tradestatus=success");
		String s = ToolKit.desEncrypt(sb.toString() + "&" + customer.getKey(), customer.getSymmetric());
		s = Encryption.sign(s, "UTF-8").toLowerCase();
		sb.append("&actionType=").append("outreachpay").append("&sign=").append(s);
		logger.info("回调参数：" + sb.toString());
		String returstr = ToolKit.request(payOrder.getCallbackapiurl(), sb.toString());
		if (returstr.equals("success")) {
			logger.info("中博第二方上分成功：" + payOrder.toString());
			return true;
		}
		logger.info("中博回调参数：" + returstr);
		return false;
	}

	public static void main(String[] args) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("account=").append("fdjkpoi").append("&amount=").append("50.28").append("&orderno=")
				.append("OAA1035744357").append("&tradeno=").append("ZFB2019051110008")
				.append("&tradestatus=outreachpay");
		String s = ToolKit.desEncrypt(sb.toString() + "&" + "96FA25D845A9419B87B609FA7304A3BB",
				"1E0E4EDC808443FD8965CDE627AAC7C9");
		s = Encryption.sign(s, "UTF-8").toLowerCase();
		sb.append("&sign=").append(s);
		String returstr = ToolKit.request("http://outreachpay1.abspocapi.net/pay/PayMerchant", sb.toString());
		System.out.println("中博回调参数：" + returstr);
	}
}
