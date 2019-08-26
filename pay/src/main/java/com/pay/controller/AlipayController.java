package com.pay.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.bank.CCB;
import com.pay.bank.ICBC;
import com.pay.dao.BaseDao;
import com.pay.dao.MemberDao;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.handleBusiness.Base;
import com.pay.handleBusiness.SuBao;
import com.pay.model.ArrivalAccount;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.Business;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.PayOrder;
import com.pay.model.Users;
import com.pay.model.WechatCode;
import com.pay.payAssist.ToolKit;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.vo.BankVo;

import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年4月17日下午3:48:37
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController extends BaseController {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Value("#{configProperties['zb.addMoney']}")
	private String addMoney;
	@Value("#{configProperties['zb.checkAccount']}")
	private String checkAccount;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private SuBao suBao;
	@Autowired
	private BaseDao<ArrivalAccount> arrivalAccountDao;
	@Autowired
	private BaseDao<Bank> bankDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private ICBC icbc;
	@Autowired
	private CCB ccb;
	@Autowired
	private BusinessService businessService;

	@RequestMapping("/req")
	public String req(Model model, String u) {
		if (StringUtils.isBlank(u))
			return "";
		model.addAttribute("username", u);
		return "alipay";
	}

	/**
	 * 速宝支付 创建订单
	 * 
	 * @param model
	 * @param u
	 * @param payUserName
	 * @param price
	 * @return
	 */
	@RequestMapping("/deposit")
	public String deposit(Model model, String u, String n) {
		if (StringUtils.isBlank(u) || StringUtils.isBlank(n))
			return "";
		try {
			model.addAttribute("u", u);
			model.addAttribute("n", n);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "alipay";
	}

	/**
	 * 速宝支付 创建订单
	 * 
	 * @param model
	 * @param u
	 * @param payUserName
	 * @param price
	 * @return
	 */
	@RequestMapping("/WXDeposit")
	public String WXDeposit(Model model, String u, String n) {
		if (StringUtils.isBlank(u) || StringUtils.isBlank(n))
			return "";
		try {
			model.addAttribute("u", u);
			model.addAttribute("n", n);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "weChat";
	}

	/**
	 * 支付宝转银行卡 消息通知回调
	 * 
	 * @param model
	 * @param u
	 * @param payUserName
	 * @param price
	 * @return
	 */
	@RequestMapping("/depositCallBack")
	@ResponseBody
	public String depositCallBack(Model model, String time, String content, String machine_num, String name,
			String cardnum, Double amount, HttpServletRequest request, String typeName, String bankType) {
		try {
			logger.info("支付宝转银行卡 消息通知回调：time=" + time + ",content=" + content + ",machine_num=" + machine_num + ",name="
					+ name + ",cardnum=" + cardnum + ",amount=" + amount);
			BankVo bankVo = new BankVo(cardnum, StringUtils.SDF.parse(time), name, machine_num, amount);
			Calendar beforeTime = Calendar.getInstance();
			beforeTime.setTime(bankVo.getTime());
			beforeTime.add(Calendar.MINUTE, -30);// 10分钟之前的时间
			Date beforeD = beforeTime.getTime();
			DetachedCriteria dc = getBanck(bankVo, beforeD, typeName, name, bankType);
			List<PayOrder> list = payOrderDao.findList(dc);
			if (list.size() == 1) {
				logger.info("匹配成功：" + bankVo.toString());
				Business business = businessService.getBusiness(
						new Business(list.get(0).getUid(), list.get(0).getOrderType(), list.get(0).getCustomer_id()));
				String ip = getClientIP(request);
				if (business == null || !business.getCallbackip().contains(ip)) {
					logger.info("商户不存在或者，ip不在白名单内");
					logger.info("list.get(0).getUid()=" + list.get(0).getUid());
					logger.info("数据库IP=" + business.getCallbackip() + "====requestIP=" + ip);
					return "11";
				}
				ReturnBan eb = payOrderService.upperScore(list.get(0).getId(), new Users("自动上分"), false, 0d);
				if (eb.isState()) {
					arrivalAccountDao.save(new ArrivalAccount(list.get(0).getId(), bankVo.getCardNo(), new Date(), 1,
							"自动匹配成功单号：" + list.get(0).getOrderNumber(), bankVo.getPrice()));
					return "00";
				} else {
					return "22";
				}
			} else if (list.size() > 1) {
				logger.info("匹配失败大于等于2条数据：" + bankVo.toString());
				for (PayOrder p : list)
					payOrderService.updatePayOrder(p, "金额相同");
				arrivalAccountDao.save(
						new ArrivalAccount(0, bankVo.getCardNo(), new Date(), 2, "10分钟内多条相同存款订单", bankVo.getPrice()));
				return "33";
			} else {
				logger.info("匹配失败：" + bankVo.toString());
				arrivalAccountDao
						.save(new ArrivalAccount(0, bankVo.getCardNo(), new Date(), 2, "匹配失败", bankVo.getPrice()));
				return "44";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "55";
	}

	private DetachedCriteria getBanck(BankVo bankVo, Date beforeD, String typeName, String name, String bankType) {
		Class classs = null;
		if (bankType.equals(PayTran.ZFB_BANK.getCode()) || bankType.equals(PayTran.WX_BANK.getCode())) {
			classs = Bank.class;
		} else if (bankType.equals(PayTran.YSF.getCode())) {
			classs = CloudFlasHover.class;
		} else if (bankType.equals(PayTran.WX_MD.getCode())) {
			classs = WechatCode.class;
		}

		DetachedCriteria bankdc = DetachedCriteria.forClass(classs);
		bankdc.add(Restrictions.like("tail", "%" + bankVo.getCardNo() + "%"));
		List<BankBase> banklist = bankDao.findBankBaseList(bankdc);
		String cardNo = "";
		if (banklist.size() == 0) {
			bankdc = DetachedCriteria.forClass(classs);
			bankdc.add(Restrictions.like("cardNo", "%" + bankVo.getCardNo() + "%"));
			banklist = bankDao.findBankBaseList(bankdc);
			if (banklist.size() > 0) {
				cardNo = banklist.get(0).getCardNo();
			} else {
				logger.info("没有 匹配到银行卡：" + bankVo.getCardNo());
			}
		} else {
			cardNo = banklist.get(0).getCardNo();
		}

		DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
		dc.add(Restrictions.eq("realprice", Double.parseDouble(StringUtils.df.format(bankVo.getPrice()))));
		dc.add(Restrictions.eq("cardNo", cardNo));
		dc.add(Restrictions.ge("addTime", beforeD)); // 大于等于
		dc.add(Restrictions.le("addTime", bankVo.getTime())); // 小于等于
		dc.add(Restrictions.eq("flag", PayState.INIT.getCode()));
		if (!StringUtils.isBlank(bankType))
			dc.add(Restrictions.eq("orderType", PayTran.getText(bankType)));
		if (!StringUtils.isBlank(typeName))
			dc.add(Restrictions.eq("customer_name", typeName));
		if (!StringUtils.isBlank(name))
			dc.add(Restrictions.eq("depositorName", name));
		dc.addOrder(Order.asc("addTime"));
		return dc;
	}

	private com.pay.bank.BankBase getBankType(String content) {
		if (content.contains("【工商银行】")) {
			return icbc;
		} else if (content.contains("[建设银行]")) {
			return ccb;
		}
		return null;
	}

	private boolean queryLoginName(String u) throws Exception {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("app_id", "1543495534070");
		map.put("account", u);
		String p = Base.getMapParam(map);
		p += "&sign=" + Encryption.sign(p + "&key=2609e6e3fa144cea9da2347375bfd952", "GB2312").toLowerCase();
		String resultJsonStr = ToolKit.request(checkAccount, p);
		logger.info("查询中博账号是否存在返回：" + resultJsonStr);
		if (StringUtils.isBlank(resultJsonStr))
			return false;
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		if (resultJsonObj.getString("status").equals("true"))
			return true;
		return false;
	}

}
