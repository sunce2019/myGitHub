package com.pay.controller;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.auto.AutoMain;
import com.pay.dao.BaseDao;
import com.pay.dao.MemberDao;
import com.pay.dao.RedisModel;
import com.pay.enums.PayTran;
import com.pay.enums.PayType;
import com.pay.enums.RedisKey;
import com.pay.enums.State;
import com.pay.handleBusiness.Asan;
import com.pay.handleBusiness.AuToMD5;
import com.pay.handleBusiness.BaBiLun;
import com.pay.handleBusiness.BaiJia;
import com.pay.handleBusiness.BaiWei;
import com.pay.handleBusiness.ChangYiFu;
import com.pay.handleBusiness.DingDing;
import com.pay.handleBusiness.GDD;
import com.pay.handleBusiness.GaoSheng;
import com.pay.handleBusiness.GeZi;
import com.pay.handleBusiness.Gt;
import com.pay.handleBusiness.HaiXing;
import com.pay.handleBusiness.HePing;
import com.pay.handleBusiness.HengTong;
import com.pay.handleBusiness.HiPay;
import com.pay.handleBusiness.JiTang;
import com.pay.handleBusiness.JiTang2;
import com.pay.handleBusiness.JuYou;
import com.pay.handleBusiness.KS;
import com.pay.handleBusiness.Ksk;
import com.pay.handleBusiness.LiRui;
import com.pay.handleBusiness.LongFa;
import com.pay.handleBusiness.NiuNiu;
import com.pay.handleBusiness.One4Bank;
import com.pay.handleBusiness.Pay5566;
import com.pay.handleBusiness.QiSheng;
import com.pay.handleBusiness.QiTongBao;
import com.pay.handleBusiness.QuanQiu;
import com.pay.handleBusiness.Redbull;
import com.pay.handleBusiness.SanJiu;
import com.pay.handleBusiness.SanLiuWu;
import com.pay.handleBusiness.SanQiErYi;
import com.pay.handleBusiness.Shanrubao;
import com.pay.handleBusiness.SiHai;
import com.pay.handleBusiness.SuBao;
import com.pay.handleBusiness.TianQiang;
import com.pay.handleBusiness.WanDing;
import com.pay.handleBusiness.WeiHuBao;
import com.pay.handleBusiness.XiaoMei;
import com.pay.handleBusiness.XinChuangShi;
import com.pay.handleBusiness.XinChuangShi2;
import com.pay.handleBusiness.XingFa;
import com.pay.handleBusiness.XingRuBao;
import com.pay.handleBusiness.YiLian;
import com.pay.handleBusiness.YingFuBao;
import com.pay.handleBusiness.YongXiong;
import com.pay.handleBusiness.YuFu;
import com.pay.handlePay.GameApi;
import com.pay.handlePay.PayBase;
import com.pay.model.AliCode;
import com.pay.model.Auto;
import com.pay.model.Bank;
import com.pay.model.Business;
import com.pay.model.Customer;
import com.pay.model.PayOrder;
import com.pay.model.WechatCode;
import com.pay.payAssist.ToolKit;
import com.pay.service.AuToService;
import com.pay.service.BankService;
import com.pay.service.BaseService;
import com.pay.service.BusinessService;
import com.pay.service.CustomerService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.PayUtils;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;
import com.pay.vo.PaymentApi;

import Decoder.BASE64Encoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年3月27日下午2:17:11
 */
@Controller
@RequestMapping("/api")
public class PayAPIController extends BaseController {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private BaseDao<Bank> bankDao;
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private Shanrubao shanrubao;
	@Autowired
	private XingFa xingFa;
	@Autowired
	private Ksk ksk;
	@Autowired
	private HengTong hengtong;
	@Autowired
	private TianQiang tianQiang;
	@Autowired
	private Asan asan;
	@Autowired
	private XiaoMei xiaoMei;
	@Autowired
	private BaBiLun baBiLun;
	@Autowired
	private HaiXing haixing;
	@Autowired
	private LiRui lirui;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private KS ks;
	@Autowired
	private BankService bankService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private BaiWei baiWei;
	@Autowired
	private Gt gt;
	@Autowired
	private SuBao subao;
	@Autowired
	private JiTang jiTang;
	@Autowired
	private DingDing dingding;
	@Autowired
	private SanJiu sanjiu;
	@Autowired
	private com.pay.handleBusiness.BaBao baBao;
	@Autowired
	private SanLiuWu sanLiuWu;
	@Autowired
	private JiTang2 jiTang2;
	@Autowired
	private QuanQiu quanQiu;
	@Autowired
	private WanDing wanDing;
	@Autowired
	private One4Bank one4Bank;
	@Autowired
	private XinChuangShi xinChuangShi;
	@Autowired
	private AuToMD5 auToMD5;
	@Autowired
	private YiLian yiLian;
	@Autowired
	private LongFa longFa;
	@Autowired
	private SanQiErYi sanQiErYi;
	@Autowired
	private GaoSheng gaoSheng;
	@Autowired
	private RedisModel redisModel;
	@Autowired
	private XingRuBao xingRuBao;
	@Autowired
	private YongXiong yongXiong;
	@Autowired
	private XinChuangShi2 xinChuangShi2;
	@Autowired
	private HePing hePing;
	@Autowired
	private WeiHuBao weiHuBao;
	@Autowired
	private GDD koudai;
	@Autowired
	private BaiJia baijia;
	@Autowired
	private YingFuBao yingFuBao;
	@Autowired
	private JuYou juyou;
	@Autowired
	private HiPay hiPay;
	@Autowired
	private NiuNiu niuniu;
	@Autowired
	private YuFu yufu;
	@Autowired
	private AuToService auToService;
	@Autowired
	private ChangYiFu test;
	@Autowired
	private GeZi gezi;
	@Autowired
	private QiSheng qisheng;
	@Autowired
	private SiHai sihai;
	@Autowired
	private QiTongBao qiTongBao;
	@Autowired
	private Redbull redbull;
	@Autowired
	private Pay5566 pay5566;

	/**
	 * 支付请求
	 * 
	 * @param data
	 * @param merchNo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/req", produces = "text/html;charset=UTF-8")
	public String req(String data, String orderid, Model model, String type, Integer businessid, String vip,
			String orderNumber) {
		logger.info("商户号：" + orderid + "  密文data:" + data + "  orderNumber=" + orderNumber);
		Map<String, String> map = new TreeMap<String, String>();
		map.put("state", "");
		try {
			if (data == null || orderid == null) {
				logger.info("参数不全：" + orderid);
				return "";
			}
			// 进行商户校验，解密参数，签名验证
			PaymentApi paymentApi = gameApi.checkApiParam(data, orderid);
			if (paymentApi == null) {
				model.addAttribute("msg", "出小差了，请稍后重试！");
				return "error";
			}
			paymentApi.setIp(getClientIP(request));
			try {
				paymentApi.setVip(Integer.parseInt(vip));
			} catch (Exception e) {
				paymentApi.setVip(0);
			}
			paymentApi.setUseragent(request.getHeader("User-Agent"));
			Business business = null;

			if (businessid != null && businessid > 0) { // 测试使用 公司外网IP
				if ("127.0.0.1,0:0:0:0:0:0:0:1,154.211.8.74,202.57.61.156,149.129.70.234,154.211.8.72,154.211.8.85,154.211.8.75,47.52.198.67,202.57.61.156,149.129.70.234"
						.contains(paymentApi.getIp())) {
					business = businessDao.get(Business.class, businessid);// 测试使用
				}
			} else {
				business = businessService.getBusiness(paymentApi.getIstype(), paymentApi.getPrice(),
						paymentApi.getCustomer_id(), paymentApi.getVip());// 筛选支付渠道
			}
			if (business == null) {
				logger.info("没有可用的商户号：" + paymentApi.toString());
				model.addAttribute("msg", "出小差了，请稍后重试！");
				return "error";
			}
			if (payOrderService.queryPay(paymentApi.getTime(), paymentApi.getOrderid())) {
				model.addAttribute("msg", "订单重复，请重新发起订单！");
				logger.info("订单重复：" + orderid);
				return "error";
			}
			// 自营 支付类。进行重定向
			if (PayUtils.isOwnPay(business.getPayType())) {
				return RedirectAttributes(paymentApi, business, type);
			}
			PayOrder payOrder = payOrderService.addPayOrder(paymentApi, business); // 创建订单
			String code = business.getCode();
			// 使用自动对接类型。
			if (business.getIsauto() != null && business.getIsauto() == 2) {
				code = business.getAutoType();
			}
			getPayType(code).handleMain(map, business, payOrder); // 调用第三方接口

			return payReturn(map, business, paymentApi, model, payOrder);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		logger.info("报错了");
		model.addAttribute("msg", "出小差了，请稍后重试！");
		return "error";
	}

	// 以下第三方支付 流程
	private String payReturn(Map<String, String> map, Business business, PaymentApi paymentApi, Model model,
			PayOrder payOrder) {
		if (StringUtils.equals(map.get("state"), "SUCCESS")) {
			map.put("jumpType", Integer.toString(business.getJumpType()));
			if (map.get("jumpType").equals("3")) { // jumpType==3 第三方支付不提供 二维码页面，自己生成二维码显示到网页
				String msg = "";
				if (paymentApi.getIstype().equals("WX")) {
					msg = "微信";
					model.addAttribute("JumpURL", "weixin://scanqrcode");
				} else if (paymentApi.getIstype().equals("ZFB")) {
					msg = "支付宝";
					model.addAttribute("JumpURL", "alipayqr://platformapi/startapp?saId=10000007");
				} else if (paymentApi.getIstype().equals("UNION")) {
					msg = "银联";
				}
				model.addAttribute("orderNo", payOrder.getOrderNumber());
				model.addAttribute("type", msg);
				model.addAttribute("map", map);
				logger.info("返回成功:" + payOrder.toString());
				return "QRCode";
			} else if (map.get("jumpType").equals("2")) { // window.location方式
				logger.info("返回成功:" + payOrder.toString());
				return "redirect:" + map.get("apiUrl");
			} else if (map.get("jumpType").equals("1")) { // 表单post提交
				map.remove("state");
				model.addAttribute("jumpType", map.get("jumpType"));
				model.addAttribute("apiUrl", map.get("apiUrl"));
				map.remove("jumpType");
				map.remove("apiUrl");
				model.addAttribute("map", map);
				logger.info("返回成功:" + payOrder.toString());
				return "pay";
			}
		}
		model.addAttribute("msg", "出小差了，请稍后重试！");
		return "error";
	}

	private String RedirectAttributes(PaymentApi paymentApi, Business business, String type) {
		paymentApi.setPayTran(business.getPayType());
		paymentApi.setType(type);
		String u = UUID.randomUUID().toString().replace("-", "");
		redisModel.redisTemplate.opsForValue().set(u, toJson(paymentApi), 1000 * 60 * 10, TimeUnit.MILLISECONDS);
		return "redirect:/api/alipayToBank.do?u=" + u;
	}

	/**
	 * 丰彩 支付入口 1：解密数据 2：加密数据 3：调req函数。
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/fcApi")
	public String fcApi(Model model, String banktype, String bankkey, String account, String merchantno, String amount,
			String orderno, String callbackurl, String sign, String pay, String tradeno, String tradestatus,
			String actionType) {
		try {
			logger.info("pay=" + pay + "banktype=" + banktype + ";bankkey=" + bankkey + ";account=" + account
					+ ";merchantno=" + merchantno + ";amount=" + amount + ";orderno=" + orderno + ";callbackurl="
					+ callbackurl + ";sign=" + sign + ";tradeno=" + tradeno + ";tradestatus=" + tradestatus
					+ ";actionType=" + actionType);
			Customer customer = decrypt(banktype, bankkey, account, merchantno, amount, orderno, callbackurl, sign,
					tradeno, tradestatus, actionType);
			if (customer != null) {
				String key = customer.getKey();
				String public_key = customer.getPublic_key1();
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("price", amount);
				metaSignMap.put("istype", pay);
				metaSignMap.put("userName", account);
				metaSignMap.put("orderid", orderno);
				metaSignMap.put("uid", customer.getUid());
				metaSignMap.put("callbackapiurl", callbackurl);
				String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ request.getServletPath();
				url = url.replace("api/fcApi", "success");
				metaSignMap.put("callbackviewurl", url);
				metaSignMap.put("time", Long.toString(new Date().getTime()));
				String metaSignJsonStr = ToolKit.mapToJson(metaSignMap);
				sign = ToolKit.MD5(metaSignJsonStr + key, ToolKit.CHARSET);// 32位
				metaSignMap.put("sign", sign);
				byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(metaSignMap).getBytes(ToolKit.CHARSET),
						public_key);
				String param = new BASE64Encoder().encode(dataStr);
				String s = "data=" + URLEncoder.encode(param) + "&orderid=" + customer.getUid() + "&type=fc";
				return "redirect:/api/req.do?" + s;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("msg", "出小差了，请稍后重试！");
		return "error";
	}

	private Customer decrypt(String banktype, String bankkey, String account, String merchantno, String amount,
			String orderno, String callbackurl, String sign, String tradeno, String tradestatus, String actionType)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		// 支付宝 微信 签名参数
		sb.append("account=").append(account).append("&amount=").append(amount).append("&bankkey=").append(bankkey)
				.append("&banktype=").append(banktype).append("&merchantno=").append(merchantno).append("&orderno=")
				.append(orderno);

		Customer customer = customerService.getCustomer(new Customer(merchantno));
		if (customer == null) {
			logger.info("商户号不存在：" + merchantno);
			return null;
		}
		sb.append("&").append(customer.getKey());
		String s = ToolKit.desEncrypt(sb.toString(), customer.getSymmetric());
		s = Encryption.sign(s, "UTF-8").toLowerCase();
		if (s.equals(sign)) {
			return customer;
		}
		logger.info("加密错误 我方：" + s);
		logger.info("中博：" + sign);
		logger.info(sb.toString());
		logger.info("callbackurl=" + callbackurl);
		return null;
	}

	public static void main(String[] args) throws Exception {
		Date date = StringUtils.SDF.parse("1019-01-01 10:00:00");
		Date date2 = StringUtils.SDF.parse("1019-01-01 10:05:00");
		System.out.println((date2.getTime() - date.getTime()) / 1000);
	}

	/**
	 * 转银行卡
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/alipayToBank")
	public String alipayToBank(Double price, String account, String name, String orderid, Model model, String payTran,
			String u) {
		try {
			model.addAttribute("msg", "出小差了，请稍后重试！");
			String callbackapiurl = "";
			String callbackviewurl = "";
			String gameOrderNumber = "";
			Double realprice = 0d;// 实际支付金额
			String remarks = "";
			Integer vip = 0;
			if (!StringUtils.isBlank(u)) { // 重定向传过来的值 中博第二方过来
				String v = redisModel.redisTemplate.opsForValue().get(u);
				if (StringUtils.isBlank(v)) {
					model.addAttribute("msg", "订单已失效，请重新下订单。");
					return "error";
				}
				JSONObject jsonObject = JSONObject.fromObject(v);
				PaymentApi paymentApi = (PaymentApi) JSONObject.toBean(jsonObject, PaymentApi.class);

				price = paymentApi.getPrice();
				account = paymentApi.getUserName();
				orderid = paymentApi.getCustomerUid();
				payTran = paymentApi.getPayTran();
				vip = paymentApi.getVip();
				callbackapiurl = paymentApi.getCallbackapiurl();
				callbackviewurl = paymentApi.getCallbackviewurl();
				gameOrderNumber = paymentApi.getOrderid();

				if (paymentApi.getType() != null && paymentApi.getType().equals("fc")) {
					remarks = "第二方订单;";
					realprice = price;
				} else {
					realprice = payOrderService.updateRecorDamount(new PaymentApi(paymentApi.getPrice())); // 获取尾数
				}
			} else { // 中博第四方过来。
				realprice = payOrderService.updateRecorDamount(new PaymentApi(price)); // 获取尾数
				price = realprice;
				gameOrderNumber = memberDao.getOrderNumber();
				remarks = "第四方订单;";
			}

			String re = check(realprice, account, orderid);
			if (re != null)
				return re;

			Customer customer = customerService.getCustomer(new Customer(orderid));
			if (customer == null) {
				logger.info("转银行卡 用户不存在：" + orderid);
				return "error";
			}
			if (customer.getFlag() != State.NORMAL.getCode()) {
				logger.info("转银行卡 用户 状态不正常：" + customer.toString());
				return "error";
			}
			// 判断支付类型
			String type = reverseType(payTran);
			Business business = businessService.getBusiness(type, price, customer.getId(), vip == null ? 0 : vip); // 筛选支付渠道
			if (business == null) {
				logger.info("转银行卡 商户号没有可用的商户渠道：" + type + " price=" + price + "orderid=" + orderid);
				return "error";
			}
			PaymentApi paymentApi = new PaymentApi();
			paymentApi.setIp(getClientIP(request));
			paymentApi.setUseragent(request.getHeader("User-Agent"));
			assemble(paymentApi, customer, price, account, name, orderid, callbackapiurl, callbackviewurl,
					gameOrderNumber, realprice, remarks); // 组装数据

			PayOrder p = payOrderService.getPayOrder2(new PayOrder(paymentApi.getOrderid()));
			if (p != null)
				return toBankReturn(model, p, false); // 重复订单。

			bankService.getBankCode(paymentApi, business); // 获取银行卡
			if (!StringUtils.isBlank(paymentApi.getCardNo())) {
				PayOrder payOrder = payOrderService.addPayOrder(paymentApi, business);// 创建订单
				return toBankReturn(model, payOrder, true);
			} else {
				logger.info("转银行卡 没有可用的银行卡：" + type + " price=" + price + "orderid=" + orderid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}

	private String check(Double price, String account, String orderid) throws Exception {
		if (price < 1 || price > 50000) {
			logger.info("请输入正确金额：" + orderid);
			return "error";
		}

		if (StringUtils.isBlank(account) || account.length() > 20) {
			logger.info("请输入正确账户：" + orderid);
			return "error";
		}

		if (StringUtils.isBlank(orderid)) {
			logger.info("orderid==null：" + orderid);
			return "error";
		}

		return null;
	}

	private String toBankReturn(Model model, PayOrder payOrder, boolean isYSF) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		model.addAttribute("map", map);
		model.addAttribute("bank", bankService.getBank(new Bank(payOrder.getCardNo())));
		model.addAttribute("price", payOrder.getRealprice());
		model.addAttribute("orderNo", payOrder.getOrderNumber());
		if (payOrder.getOrderType().equals(PayTran.ZFB_BANK.getText())) {
			return toAiliPayBank(map, payOrder, model);
		} else if (payOrder.getOrderType().equals(PayTran.WX_BANK.getText())) {
			model.addAttribute("type", "微信");
			model.addAttribute("JumpURL", "weixin://");
			return "WXCode";
		} else if (payOrder.getOrderType().equals(PayTran.YSF.getText())) {// 云闪付支付类型
			int pattern = baseService.getCloudPattern(payOrder.getCardNo());
			if (isYSF && pattern == 1) {// 新的订单，发送通知脚本设置金额二维码
				redisModel.redisTemplate.opsForList().rightPush(RedisKey.YSF_ADD.getCode() + payOrder.getCardNo(),
						toJson(payOrder));
			}

			return "ysf";
		} else if (payOrder.getOrderType().equals(PayTran.WX_MD.getText())) {// 微信买单
			model.addAttribute("png_base64",
					((WechatCode) bankService.getCloudflashover(new WechatCode(payOrder.getCardNo()), WechatCode.class))
							.getOssURL());
			return "WXMDCode";
		} else if (payOrder.getOrderType().equals(PayTran.ALI_FM.getText())) { // 支付宝付码
			String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getServletPath();
			url = url.replace("alipayToBank", "alipayPay");
			url += "?price=" + payOrder.getRealprice() + "&n=" + payOrder.getCardNo(); // 扫码打开 支付宝红包页面url
			// 扫一扫地址
			String qrurl = QRCode.generalQRCode(url);
			map.put("apiUrl", qrurl);
			model.addAttribute("JumpURL", url);
			model.addAttribute("alipayToBak",
					"alipays://platformapi/startapp?appId=20000067&url=" + URLEncoder.encode(url));
			model.addAttribute("type", "支付宝付码");
			model.addAttribute("png_base64",
					((AliCode) bankService.getCloudflashover(new AliCode(payOrder.getCardNo()), AliCode.class))
							.getOssURL());
			return "ZFBFMCode";
		}
		return "";
	}

	private String toAiliPayBank(Map<String, String> map, PayOrder payOrder, Model model) throws Exception {
		Bank bank = bankService.getBank(new Bank(payOrder.getCardNo()));
		if (bank == null) {
			logger.info("银行卡未找到：" + payOrder.getCardNo());
			return "error";
		}
		if (bank.getPattern() != null && bank.getPattern().intValue() == 0) {
			String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getServletPath();
			url = url.replace("alipayToBank", "alipayPay");
			url += "?price=" + payOrder.getRealprice() + "&n=" + payOrder.getCardNo(); // 扫码打开 支付宝红包页面url
			String qrurl = QRCode.generalQRCode(url);
			map.put("apiUrl", qrurl);
			model.addAttribute("JumpURL", url);
			model.addAttribute("alipayToBak",
					"alipays://platformapi/startapp?appId=20000067&url=" + URLEncoder.encode(url)); // 打开支付宝扫一扫
			model.addAttribute("type", "支付宝");
			return "QRCode2";
		} else {// 飞行模式
			String cardNo = bank.getCardNo();
			if (!StringUtils.isBlank(bank.getCardIndex()))
				cardNo = StringUtils.getBank(bank.getCardNo());

			String s = "alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + cardNo
					+ "&bankAccount=" + bank.getBankAccount() + "&money=" + payOrder.getRealprice() + "&amount="
					+ payOrder.getRealprice() + "&bankMark=" + bank.getBankMark() + "&bankName=**请关闭飞行模式付款**&cardIndex="
					+ bank.getCardIndex() + "&cardNoHidden=true&cardChannel=HISTORY_CARD";
			model.addAttribute("alipayToBak", s); // 打开支付宝扫一扫
			Calendar nowTime = Calendar.getInstance();
			nowTime.setTime(payOrder.getAddTime());
			nowTime.add(Calendar.MINUTE, 10);
			long time = nowTime.getTime().getTime() - new Date().getTime();
			if ((time > 2)) {
				model.addAttribute("times", (nowTime.getTime().getTime() - new Date().getTime()) / 1000); // 打开支付宝扫一扫
				return "ailiPayFlightMode";
			}
			model.addAttribute("msg", "订单已失效，请重新下订单。");
			// System.out.println(s);
			// System.out.println(
			// "alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNo=622908***0715&bankAccount=林伟&money=231.22&amount=231.22&bankMark=CIB&bankName=**请关闭飞行模式付款**&cardIndex=1907031238811463551&cardNoHidden=true&cardChannel=HISTORY_CARD");
			return "error";
		}
	}

	private String reverseType(String payTran) {
		switch (payTran) {
		case "2":
			return PayTran.WX_BANK.getText();
		case "4":
			return PayTran.WX_BANK.getText();
		case "5":
			return PayTran.ZFB_BANK.getText();
		case "6":
			return PayTran.ZFB_BANK.getText();
		case "微信转银行":
			return PayTran.WX_BANK.getText();
		case "支付宝转银行":
			return PayTran.ZFB_BANK.getText();
		case "云闪付":
			return PayTran.YSF.getText();
		case "微信买单":
			return PayTran.WX_MD.getText();
		case "支付宝付码":
			return PayTran.ALI_FM.getText();
		default:
			return "";
		}
	}

	/**
	 * 支付宝转红包 页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/alipayPay")
	public String alipayPay(double price, String n, Model model) {
		model.addAttribute("n", n);
		model.addAttribute("price", price);
		return "alipayPay";
	}

	/**
	 * 支付宝转银行卡 页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/showBank")
	public String showBank(double price, String n, Model model) {
		try {
			Bank bank = bankService.getBank(new Bank(n));
			if (bank != null) {
				if (!StringUtils.isBlank(bank.getCardIndex())) {
					bank.setCardNo(StringUtils.getBank(bank.getCardNo()));
				}
				model.addAttribute("bank", bank);
				model.addAttribute("price", price);
				String uid1 = UUID.randomUUID().toString().replace("-", "");
				String uid2 = UUID.randomUUID().toString().replace("-", "");
				model.addAttribute("uid", uid1 + "-" + uid2);
				return "bank";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("msg", "出小差了，请稍后重试！");
		return "error";
	}

	private void assemble(PaymentApi paymentApi, Customer customer, double price, String account, String name,
			String orderid, String callbackapiurl, String callbackviewurl, String gameOrderNumber, Double realprice,
			String remarks) {

		paymentApi.setPrice(price);
		paymentApi.setUserName(account);
		// paymentApi.setOrderid(memberDao.getOrderNumber());
		paymentApi.setCustomer_id(customer.getId());
		paymentApi.setCustomer_name(customer.getName());
		paymentApi.setTime(Long.toString(new Date().getTime()));
		paymentApi.setDepositorName(name);
		paymentApi.setCallbackapiurl(callbackapiurl);
		paymentApi.setCallbackviewurl(callbackviewurl);
		paymentApi.setOrderid(gameOrderNumber);
		paymentApi.setRealprice(realprice);
		paymentApi.setRemarks(remarks);
	}

	@RequestMapping(value = "/getPayTypeApi")
	@ResponseBody
	public String getPayTypeApi(String type) {
		try {
			if (type.length() >= 32)
				return getAutoType(type); // 32位属于自动对接类型。
			PayBase pb = getPayType(type);
			if (pb != null)
				return JSONArray.fromObject(getPayType(type).getPayType()).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private String getAutoType(String type) throws Exception {
		List<String> list = new ArrayList<String>();
		Auto auto = auToService.getAuto(0, type);
		if (auto != null) {
			AutoMain autoMain = AuToMD5.autoToAutoMain(auto);
			if (autoMain != null) {
				for (com.pay.auto.PayType payType : autoMain.getPayTypeList())
					list.add(PayTran.getText(payType.getTypeCode()));
				return JSONArray.fromObject(list).toString();
			}
		}

		return "[]";
	}

	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

	/**
	 * 查询订单
	 * 
	 * @param data
	 * @param uid
	 * @param model
	 * @return
	 */
	@RequestMapping("/queryOrder")
	@ResponseBody
	public String queryOrder(String data, String uid, Model model) {
//		try {
//			if(data==null||uid==null)return "";	
//			Map<String,Object> map = gameApi.checkApiOrderParam(data,uid);	//校验
//			if(map==null)return "";
//			
//			Map<String,String> re = getPayType(map.get("code").toString()).payQuery((PayOrder)map.get("payOrder")
//					,(Business)map.get("business"));
//			
//			Customer customer = customerService.getCustomer(new Customer(uid));
//			
//			String metaSignJsonStr = ToolKit.mapToJson(re);
//			String sign = ToolKit.MD5(metaSignJsonStr + customer.getKey(), ToolKit.CHARSET);// 32位
//			re.put("sign", sign);
//			
//			byte[] dataStr = ToolKit.encryptByPublicKey(ToolKit.mapToJson(re).getBytes(ToolKit.CHARSET),
//					customer.getPublic_key_2());
//			return new BASE64Encoder().encode(dataStr);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return "error";
	}

	private PayBase getPayType(String code) {
		PayType payType = PayType.getType(code);
		switch (payType) {
		case SHANRUBAO:
			return shanrubao;
		case XINGFA:
			return xingFa;
		case KSK:
			return ksk;
		case HENGTONG:
			return hengtong;
		case TIANGQIANG:
			return tianQiang;
		case ASAN:
			return asan;
		case XIAOMEI:
			return xiaoMei;
		case BABILUN:
			return baBiLun;
		case HAIXING:
			return haixing;
		case LIRUI:
			return lirui;
		case KS:
			return ks;
		case BAIWEI:
			return baiWei;
		case GT:
			return gt;
		case SUBAO:
			return subao;
		case JITANG:
			return jiTang;
		case DINGDING:
			return dingding;
		case SANJIU:
			return sanjiu;
		case BABAO:
			return baBao;
		case SANLIUWU:
			return sanLiuWu;
		case JITANG2:
			return jiTang2;
		case QUANQIU:
			return quanQiu;
		case WANDING:
			return wanDing;
		case ONE4BANK:
			return one4Bank;
		case XINCHUANGSHI:
			return xinChuangShi;
		case AUTOMD5:
			return auToMD5;
		case MD5HEX:
			return auToMD5;
		case YILIAN:
			return yiLian;
		case LONGFA:
			return longFa;
		case SANQIERYI:
			return sanQiErYi;
		case GAOSHENG:
			return gaoSheng;
		case XINGRUBAO:
			return xingRuBao;
		case YONGXING:
			return yongXiong;
		case XINCHUANGSHI2:
			return xinChuangShi2;
		case HEPING:
			return hePing;
		case WEIHUBAO:
			return weiHuBao;
		case GDD:
			return koudai;
		case BAIJIA:
			return baijia;
		case YINGFUBAO:
			return yingFuBao;
		case JUYOU:
			return juyou;
		case HIPAY:
			return hiPay;
		case NIUNIU:
			return niuniu;
		case CHANGYIFU:
			return test;
		case YUFU:
			return yufu;
		case GEZI:
			return gezi;
		case QISHENG:
			return qisheng;
		case SIHAI:
			return sihai;
		case QIDONGBAO:
			return qiTongBao;
		case REDBULL:
			return redbull;
		case PAY5566:
			return pay5566;

		default:
			logger.info("商户类型不存在：" + code);
			return null;
		}
	}

}
