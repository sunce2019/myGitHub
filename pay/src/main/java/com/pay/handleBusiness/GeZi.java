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
 * 格子支付
 * 
 * @author acer
 * 
 *
 */
@Repository
public class GeZi extends Base implements PayBase {

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
		map.put("mid", business.getUid());// 系统分配商户号
		map.put("paytype", business.getPayCode());
		map.put("money", Double.toString(payOrder.getPrice()));// 订单金额
		map.put("did", payOrder.getGameOrderNumber());// 订单号
		map.put("message", "test gezhizhifu");
		map.put("notifyurl", business.getNotifyUrl());// 同步消息返回地址
		// 获取签名
		signature(map, business, payOrder);

	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		String str = map.get("did") + map.get("money") + map.get("paytype") + map.get("notifyurl");
		String sign1 = Encryption.md5_32(str, "");
		String sign = Encryption.md5_32(sign1 + business.getToken(), "");
		map.put("sign", sign);

		// 发送请求，获取返回参数
		String strReturn = ToolKit.request(business.getApiUrl(), getMapParam(map));
		JSONObject job = JSONObject.fromObject(strReturn);
		if (job.getString("code").equals("0")) {
			String codeKey = job.getString("codekey");
			String url = business.getApiUrl().replace("dopay.php", "p.php");
			map.put("state", "SUCCESS");
			map.put("apiUrl", url + "?k=" + codeKey);
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), strReturn);
		}

	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);
		logger.info("格子支付,api回调参数：" + parame.toString());
		Map<String, String> vmap = new TreeMap<String, String>();
		vmap.put("merchant", parame.get("merchant"));
		vmap.put("money", parame.get("money"));
		vmap.put("token", parame.get("token"));
		vmap.put("did", parame.get("did"));

		PayOrder payOrder = payOrderService.getPayOrder(new PayOrder(parame.get("did")));
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				String param = business.getToken() + parame.get("merchant") + parame.get("did");
				logger.info("加密参数：" + param);
				String key2 = Encryption.md5_32(param, "");
				if (StringUtils.equals(key2, parame.get("token"))) {
					// 修改订单和商户信息成功，提示回调成功
					if (baseService.successfulPayment(payOrder.getId(), business.getId(),
							StringToDouble(parame.get("money")))) {
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

	/**
	 * 订单查询
	 */
	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		/******************************
		 * 回调参数 sign=md5(did+mid+商户秘钥) k=订单key
		 ******************************/
		/*******************************************************************
		 * **返回参数 code=0:表示查询成功，1:表示查询失败 did =网站订单号 date =订单提交时间 ordermoney=订单金额
		 * pay=支付状态：0表示未支付，1表示已支付，2表示订单未支付而且订单已经超时 mermoney=商户入账金额 fee=手续费 paydate=支付时间
		 *******************************************************************/
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

	/**
	 * 支付类型编码转换
	 */
	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		if (PayTran.contains(payOrder.getOrderType())) {
			if (payOrder.getOrderType().contains(PayTran.ZFB.getText())) {
				return "1";
			} else if (payOrder.getOrderType().contains(PayTran.WX.getText())) {
				return "2";
			}
		} else {
			throw new Exception("支付类型不存在" + payOrder.toString());
		}
		return null;
	}

	@Override
	public String[] getPayType() throws Exception {
		// TODO Auto-generated method stub
		return new String[] { "支付宝扫码", "微信扫码" };
	}

}
