package com.pay.handleBusiness;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pay.auto.AutoMain;
import com.pay.auto.Parameter;
import com.pay.auto.PayType;
import com.pay.auto.enums.AmountType;
import com.pay.auto.enums.JumpType;
import com.pay.auto.enums.ParameType;
import com.pay.auto.enums.RequestMethod;
import com.pay.auto.enums.Sort;
import com.pay.dao.BaseDao;
import com.pay.dao.RedisModel;
import com.pay.enums.ConfigCode;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.handlePay.PayBase;
import com.pay.model.Auto;
import com.pay.model.Business;
import com.pay.model.PayOrder;
import com.pay.model.SystConfig;
import com.pay.payAssist.ToolKit;
import com.pay.service.AuToService;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.service.SystemService;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;
import com.pay.vo.Vla;

import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年5月26日上午10:31:51
 */
@Repository
public class AuToMD5 extends Base implements PayBase {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private AuToService auToService;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private RedisModel redisModel;
	@Autowired
	private BaseService baseService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private BaseDao<Auto> autoDao;
	@Autowired
	private SystemService systemService;

	@Override
	public void handleMain(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		AutoMain autoMain = getAutoMain(business);
		if (autoMain == null) {
			logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + " 商户渠道没有设置自动数据。");
			return;
		}
		paramAssemble(map, business, payOrder, autoMain); // 组装所有参数
		map.remove("state");
		signature(map, business, payOrder, autoMain);
	}

	private void signature(Map<String, String> val, Business business, PayOrder payOrder, AutoMain autoMain)
			throws Exception {
		String paramStr = plaintext(val, business, payOrder, autoMain, "REQUEST"); // 组装 加密前明文
		if (StringUtils.isBlank(paramStr))
			return;
		sign(val, business, payOrder, autoMain, paramStr); // 进行MD5加密
		String returnStr = request(val, business, payOrder, autoMain, paramStr); // 请求第三方
		if (autoMain.getRequestMethod().equals(RequestMethod.FRPOSTFROM.getCode()))
			return; // 前端请求直接返回。

		JSONObject resultJsonObj = JSONObject.fromObject(returnStr);
		if (resultJsonObj.getString(autoMain.getFlagField()).equals(autoMain.getSucstr())) {
			for (int i = 0; i + 1 < autoMain.getLayerList().size(); i++) { // 循环 JSON结构
				resultJsonObj = JSONObject.fromObject(resultJsonObj.getString(autoMain.getLayerList().get(i).getVal()));
			}
			// 拿到 url地址
			String text = resultJsonObj
					.getString(autoMain.getLayerList().get(autoMain.getLayerList().size() - 1).getVal());
			for (com.pay.auto.PayType payType : autoMain.getPayTypeList()) { // 循环该商户 配置的所有 支付类型
				if (payType.getTypeCode().equals(PayTran.getCode(payOrder.getOrderType()))) {
					if (payType.getJumpType().equals(JumpType.URLOPEN.getCode())) {
						business.setJumpType(2);
						val.put("apiUrl", text);
						val.put("state", "SUCCESS");
					} else if (payType.getJumpType().equals(JumpType.QRCODE.getCode())) {
						business.setJumpType(3);
						val.put("apiUrl", QRCode.generalQRCode(text));
						val.put("state", "SUCCESS");
					}
				}
			}
		} else {
			payOrderService.updatePayOrder(payOrder.getId(), returnStr);
		}

	}

	/**
	 * 组装三方所有参数。
	 * 
	 * @param map
	 * @param business
	 * @param payOrder
	 * @param autoMain
	 * @throws Exception
	 */
	private void paramAssemble(Map<String, String> map, Business business, PayOrder payOrder, AutoMain autoMain)
			throws Exception {
		for (Parameter p : autoMain.getParamList()) {
			if (p.getType().equals(ParameType.PAYTYPE.getCode())) {// 支付类型
				String payType = typeConversion(payOrder, autoMain);
				if (payType == null) {
					logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + " 商户渠道 没有配置支付类型。");
					return;
				}
				map.put(p.getName(), payType);
			} else if (p.getType().equals(ParameType.UID.getCode())) {// 商户号
				map.put(p.getName(), business.getUid());
			} else if (p.getType().equals(ParameType.ORDERNO.getCode())) {// 订单号
				map.put(p.getName(), payOrder.getGameOrderNumber());
			} else if (p.getType().equals(ParameType.FIXED.getCode())) {// 固定参数
				map.put(p.getName(), p.getVal());
			} else if (p.getType().equals(ParameType.NOTIFYURL.getCode())) {// 异步通知地址
				map.put(p.getName(), business.getNotifyUrl());
			} else if (p.getType().equals(ParameType.NOTIFYVIEWURL.getCode())) {// 同步通知地址
				if (p.getUrlFlag() == 1) { // 是 urlencode
					map.put(p.getName(), URLEncoder.encode(business.getNotifyViewUrl()));
				} else {
					map.put(p.getName(), business.getNotifyViewUrl());
				}
			} else if (p.getType().equals(ParameType.USERIP.getCode())) {// 用户IP
				String ip = payOrder.getIp();
				if (ip.equals("127.0.0.1")) {
					ip = "202.53.62.146";
				} else if (ip.equals("0:0:0:0:0:0:0:1")) {
					ip = "202.53.62.146";
				}
				map.put(p.getName(), ip);
			} else if (p.getType().equals(ParameType.DATESTR.getCode())) {// 时间字符yyyy-MM-dd HH:mm:ss
				map.put(p.getName(), StringUtils.SDF.format(new Date()));
			} else if (p.getType().equals(ParameType.USERID.getCode())) {// 用户角色
				map.put(p.getName(), payOrder.getUserName());
			} else if (p.getType().equals(ParameType.DATESTR2.getCode())) {// 时间字符yyyyMMddHHmmss
				map.put(p.getName(), StringUtils.SDF2.format(new Date()));
			} else if (p.getType().equals(ParameType.DATESTR2SSS.getCode())) {// 时间字符yyyy-MM-dd HH:mm:ss.SSS
				map.put(p.getName(), StringUtils.SDF3.format(new Date()));
			} else if (p.getType().equals(ParameType.DATE13.getCode())) {// 时间戳13位
				map.put(p.getName(), Long.toString(new Date().getTime()));
			} else if (p.getType().equals(ParameType.DATE10.getCode())) {// 时间戳10位
				map.put(p.getName(), timestampToDate());
			} else if (p.getType().equals(ParameType.RANDOMINT.getCode())) {// 随机整数
				map.put(p.getName(), Integer.toString((int) (Math.random() * 10000000)));
			} else if (p.getType().equals(ParameType.RANDOMSTR.getCode())) {// 随机字符串
				map.put(p.getName(), StringUtils.getRandomString(8));
			} else if (p.getType().equals(ParameType.AMOUNT.getCode())) {// 金额
				if (autoMain.getAmountType().equals(AmountType.DOUBLE.getCode())) { // 两位小数金额
					map.put(p.getName(), doubleToString(payOrder.getPrice()));
				} else if (autoMain.getAmountType().equals(AmountType.FEN.getCode())) { // 单位分。
					map.put(p.getName(), changeY2F(payOrder.getPrice()));
				}
			}
		}
	}

	private String typeConversion(PayOrder payOrder, AutoMain autoMain) throws Exception {
		for (com.pay.auto.PayType payType : autoMain.getPayTypeList()) {
			if (payType.getTypeCode().equals(PayTran.getCode(payOrder.getOrderType())))
				return payType.getValues();
		}
		return null;
	}

	/**
	 * 加密函数
	 * 
	 * @param val
	 * @param business
	 * @param payOrder
	 * @param autoMain
	 * @param paramStr
	 * @return
	 */
	private void sign(Map<String, String> val, Business business, PayOrder payOrder, AutoMain autoMain,
			String paramStr) {
		logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + "加密前明文：" + paramStr);
		redisModel.redisTemplate.opsForValue().set("TESTparamStr" + payOrder.getGameOrderNumber(), paramStr,
				1 * 60 * 1000, TimeUnit.MILLISECONDS);
		String sign = "";
		if (autoMain.getSingType().equals(com.pay.enums.PayType.AUTOMD5.getCode())) { // 传统MD5 加密
			sign = Encryption.sign(paramStr, autoMain.getSignCode());
		} else if (autoMain.getSingType().equals(com.pay.enums.PayType.MD5HEX.getCode())) { // 可逆MD5 加密
			sign = Encryption.md5Hex(paramStr);
		}

		if (autoMain.getSizeWrite() == 0) {
			sign = sign.toUpperCase();
		} else {
			sign = sign.toLowerCase();
		}
		logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + "加密后：" + sign);
		redisModel.redisTemplate.opsForValue().set("TESTsign" + payOrder.getGameOrderNumber(), sign, 1 * 60 * 1000,
				TimeUnit.MILLISECONDS);
		String signName = "";
		for (Parameter p : autoMain.getParamList()) {
			if (p.getType().equals(ParameType.SIGN.getCode())) {// 找到签名字段。
				signName = p.getName();
			}
			;
		}
		val.put(signName, sign);
	}

	/**
	 * 请求第三方
	 * 
	 * @param val
	 * @param business
	 * @param payOrder
	 * @param autoMain
	 * @param signName
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	private String request(Map<String, String> val, Business business, PayOrder payOrder, AutoMain autoMain,
			String paramStr) throws Exception {
		String returnStr = "";
		if (autoMain.getRequestMethod().equals(RequestMethod.FRPOSTFROM.getCode())) {
			val.put("apiUrl", business.getApiUrl());
			business.setJumpType(1);
			val.put("state", "SUCCESS");
			return null;
		} else if (autoMain.getRequestMethod().equals(RequestMethod.AFPOSTFROM.getCode())) { // POST表单提交
			returnStr = ToolKit.getPostResponse(business.getApiUrl(), val);
		} else if (autoMain.getRequestMethod().equals(RequestMethod.AFPOST.getCode())) { // POST提交
			if (autoMain.getRequestData() == 0) {
				returnStr = ToolKit.request(business.getApiUrl(), getMapParam(val)); // 传统拼接
			} else if (autoMain.getRequestData() == 1) {
				returnStr = ToolKit.requestJson(business.getApiUrl(), ToolKit.mapToJson(val)); // JSON提交
			}
		}
		logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + "请求三方返回：" + returnStr);
		redisModel.redisTemplate.opsForValue().set("TESTreturnStr" + payOrder.getGameOrderNumber(), returnStr,
				1 * 60 * 1000, TimeUnit.MILLISECONDS);
		return returnStr;
	}

	/**
	 * 组装加密前明文
	 * 
	 * @param val
	 * @param business
	 * @param payOrder
	 * @param autoMain
	 * @return
	 */
	private String plaintext(Map<String, String> val, Business business, PayOrder payOrder, AutoMain autoMain,
			String state) {
		String paramStr = ""; // 加密前文明

		if (!autoMain.getSort().equals(Sort.CUSTOM.getCode())) {
			Map<String, String> mapv = null;
			if (autoMain.getSort().equals(Sort.ASCIIUP.getCode())) {
				mapv = new TreeMap<String, String>(); // ASCII 升序
			} else if (autoMain.getSort().equals(Sort.ASCIILO.getCode())) {
				mapv = new TreeMap<String, String>(new Comparator<String>() {
					@Override
					public int compare(String a, String b) {
						return b.compareTo(a);
					}
				}); // ASCII 降序
			}
			paramAssemble(mapv, val, business, payOrder, autoMain, state); // 组装加密字段
			paramStr = getMapParamStr(mapv, autoMain.getSignStr1(), autoMain.getSignStr2());
		} else if (autoMain.getSort().equals(Sort.CUSTOM.getCode())) { // 自定义 字段排序
			String[] sort = null;
			if (state.equals("REQUEST")) {
				sort = autoMain.getCustomSort().split(",");
			} else if (state.equals("BACK")) {
				sort = autoMain.getBackSort().split(",");
			}
			Map<String, String> mapv = new LinkedHashMap<String, String>();
			for (String str : sort) {
				String value = val.get(str);
				if (value != null) {
					mapv.put(str, value);
				}
			}
			paramStr = getMapParamStr(mapv, autoMain.getSignStr1(), autoMain.getSignStr2());
		}

		if (StringUtils.isBlank(paramStr)) {
			logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + " 商户渠道  请配置请求参数");
			return "";
		}
		if (autoMain.getKeySort() == 0) { // key 不参与排序 组装数据，字符串最后拼接
			if (!StringUtils.isBlank(autoMain.getKeyStr1())) {
				paramStr += autoMain.getKeyStr1();
			}

			if (!StringUtils.isBlank(autoMain.getKeyStr2())) {
				paramStr += autoMain.getKeyStr2();
			}

			if (!StringUtils.isBlank(autoMain.getKeyStr3())) {
				paramStr += autoMain.getKeyStr3();
			}
			paramStr += business.getToken();
		}
		return paramStr;
	}

	/**
	 * 组装所有需要参与签名字段
	 * 
	 * @param mapv
	 * @param map
	 * @param business
	 * @param payOrder
	 * @param autoMain
	 */
	private void paramAssemble(Map<String, String> mapv, Map<String, String> map, Business business, PayOrder payOrder,
			AutoMain autoMain, String state) {
		List<Parameter> list = null;
		if (state.equals("REQUEST")) {
			list = autoMain.getParamList();
		} else if (state.equals("BACK")) {
			list = autoMain.getBackParamList();
		}
		for (Parameter p : list) {
			if (p.getFlag() == 0) {
				// 如果是秘钥字段。不参与排序。
				if (p.getType().equals(ParameType.TOKEN.getCode()) && autoMain.getKeySort() == 0)
					continue;
				// 如果是签名字段。
				if (p.getType().equals(ParameType.SIGN.getCode()))
					continue;
				if (p.getType().equals(ParameType.TOKEN.getCode())) {
					mapv.put(p.getName(), business.getToken()); // 如果是秘钥字段，从商户表取值。
				} else {
					String value = map.get(p.getName());
					if (state.equals("REQUEST") && p.getUrlFlag() == 1) { // URLEncoder
						value = URLEncoder.encode(value);
					} else if (state.equals("BACK") && p.getUrlFlag() == 1) {
						value = URLDecoder.decode(value);
					}
					mapv.put(p.getName(), value);
				}
			}
		}
	}

	private AutoMain getAutoMain(Business business) throws Exception {
		Auto auto = auToService.getAuto(business.getId(), null);
		if (auto == null)
			return null;
		return autoToAutoMain(auto);
	}

	@Override
	public Map<String, String> payCallback(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "error");
		Map<String, String> parame = getCallBackParameter(request);
		logger.info("自动对接,api回调参数：" + parame.toString());

		String[] sqlParame = new String[parame.size()];
		int index = -1;
		for (String s : parame.values())
			sqlParame[++index] = s;
		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		dc.add(Restrictions.in("gameOrderNumber", sqlParame)); // 获取所有 传过来的参数,in查询订单号。
		dc.add(Restrictions.eq("flag", PayState.INIT.getCode()));

		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.CALLBACKTIME.getCode(), 1));
		if (systConfig != null) {
			Calendar nowTime = Calendar.getInstance();
			nowTime.add(Calendar.MINUTE, StringUtils.unAbs(Integer.parseInt(systConfig.getContent())));
			dc.add(Restrictions.ge("addTime", nowTime.getTime())); // 回调只能 回调半小时内的订单
		}

		PayOrder payOrder = payOrderDao.findObj(dc);
		if (payOrder != null) {
			Business business = businessService
					.getBusiness(new Business(payOrder.getUid(), payOrder.getOrderType(), payOrder.getCustomer_id()));
			if (business != null) {
				AutoMain autoMain = getAutoMain(business);
				if (autoMain == null)
					return null;
				// 获取判断字段
				Parameter parameter = queryParameName(ParameType.BACKSUC, autoMain.getBackParamList(), business,
						payOrder);
				// 返回状态不是 支付成功返回。
				if (null != parameter && !parame.get(parameter.getName()).equals(parameter.getVal())) {
					return null;
				}
				// 检查白名单ip
				if (CheckIP(request, business.getName(), business.getCallbackip()))
					return null;
				// 获取加密明文
				String param = plaintext(parame, business, payOrder, autoMain, "BACK");
				logger.info("加密参数：" + param);
				String sign = Encryption.sign(param, autoMain.getSignCode());
				sign = sign.toLowerCase();
				// 获取回调 sign参数值
				String parameSign = parame.get(
						queryParameName(ParameType.SIGN, autoMain.getBackParamList(), business, payOrder).getName());

				if (StringUtils.equals(sign, parameSign.toLowerCase())) {
					double amount = amount(autoMain, parame, business, payOrder);
					if (baseService.successfulPayment(payOrder.getId(), business.getId(), amount)) {
						logger.info("第三方回调成功：" + payOrder.toString());
						map.put("state", "SUCCESS");
						map.put("reBusiness", autoMain.getBanckSuccess());
						map.put("payorder_id", Integer.toString(payOrder.getId()));
					}
				} else {
					logger.info("提供的sign 加密值不相等：" + parameSign);
					logger.info("我方加密的sign：" + sign);
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

	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("http://localhost:8080/index.do?阿萨德"));
	}

	private double amount(AutoMain autoMain, Map<String, String> parame, Business business, PayOrder payOrder)
			throws Exception {
		double amount = 0d;
		String parameName = queryParameName(ParameType.AMOUNT, autoMain.getBackParamList(), business, payOrder)
				.getName();
		if (autoMain.getAmountType().equals(AmountType.DOUBLE.getCode())) { // 两位小数金额
			amount = StringToDouble(parame.get(parameName));
		} else if (autoMain.getAmountType().equals(AmountType.FEN.getCode())) { // 单位分。
			amount = changeF2Y(parame.get(parameName));
		}
		return amount;
	}

	private AutoMain getAuto(Business business, PayOrder payOrder) {
		DetachedCriteria dc = DetachedCriteria.forClass(Auto.class, "a");
		DetachedCriteria b = dc.createAlias("business", "b");
		b.add(Restrictions.like("b.id", business.getId()));
		Auto auto = autoDao.findObj(dc);
		if (auto != null) {
			return autoToAutoMain(auto);
		}
		logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + " 没有配置自动信息");
		return null;
	}

	public static AutoMain autoToAutoMain(Auto auto) {
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		classMap.put("paramList", Parameter.class);
		classMap.put("payTypeList", PayType.class);
		classMap.put("backParamList", Parameter.class);
		classMap.put("layerList", Vla.class);
		return (AutoMain) JSONObject.toBean(JSONObject.fromObject(auto.getDatas()), AutoMain.class, classMap);
	}

	/**
	 * 根据参数类型，查询字段名
	 * 
	 * @param parameType
	 * @param autoMain
	 * @param business
	 * @param payOrder
	 * @return
	 */
	private Parameter queryParameName(ParameType parameType, List<Parameter> paramList, Business business,
			PayOrder payOrder) {
		for (Parameter parameter : paramList) {
			if (parameter.getType().equals(parameType.getCode()))
				return parameter;
		}
		logger.info(business.getName() + "订单号：" + payOrder.getGameOrderNumber() + " 没有配置自动信息");
		return null;
	}

	@Override
	public boolean payQuery(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void signature(Map<String, String> map, Business business, PayOrder payOrder) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getPayType() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String typeConversion(PayOrder payOrder) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
