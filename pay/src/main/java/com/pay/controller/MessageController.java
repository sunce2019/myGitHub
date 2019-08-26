package com.pay.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import com.pay.enums.BankCode;
import com.pay.enums.ButClick;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.enums.RegxType;
import com.pay.model.ArrivalAccount;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.Business;
import com.pay.model.CloudFlasHover;
import com.pay.model.Message;
import com.pay.model.PayOrder;
import com.pay.model.SpeildKV;
import com.pay.model.Users;
import com.pay.model.WechatCode;
import com.pay.service.BusinessService;
import com.pay.service.PayOrderService;
import com.pay.util.JsonTools;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.vo.BankVo;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/sms")
public class MessageController extends BaseController {

	@Value("#{configProperties['zb.addMoney']}")
	private String addMoney;
	@Value("#{configProperties['zb.checkAccount']}")
	private String checkAccount;
	@Autowired
	private PayOrderService payOrderService;

	@Autowired
	private BaseDao<ArrivalAccount> arrivalAccountDao;
	@Autowired
	private BaseDao<Bank> bankDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private ICBC icbc;
	@Autowired
	private CCB ccb;
	@Autowired
	private BusinessService businessService;
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private BaseDao<Message> messageDao;

	@Autowired
	private Message message;

	@RequestMapping("/getRegx")
	@ResponseBody
	public String getRegx() {
		List<Object> list = new ArrayList<Object>();
		for (RegxType rt : RegxType.values()) {
			Map<String, String> d = new HashMap<String, String>();
			d.put("key", rt.getCode());
			d.put("value", rt.getText());
			list.add(d);
		}
		return JSONArray.fromObject(list).toString();
	}

	@RequestMapping("/getBank")
	@ResponseBody
	public String getBank() {
		List<Object> map = new ArrayList<Object>();
		for (BankCode rt : BankCode.values()) {
			SpeildKV skv = new SpeildKV();
			skv.setKey(rt.getCode());
			skv.setValue(rt.getText());
			map.add(skv);
		}
		return JSONArray.fromObject(map).toString();
	}

	/**
	 * 信息显示
	 */
	@RequestMapping("/messageInfo")
	public String getMessageInfo() {
		return "messageList";
	}

	@RequestMapping("/smsList")
	@ResponseBody
	public String messageList(int pageNo, String bank, String startTime, String endTime, String status) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Message.class);
			if (!StringUtils.isBlank(bank))
				dc.add(Restrictions.eq("bank", bank));
			// 将字符串转Date类型
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (!StringUtils.isBlank(startTime))
				dc.add(Restrictions.ge("startTime", format.parse(startTime)));
			if (!StringUtils.isBlank(endTime))
				dc.add(Restrictions.le("endTime", format.parse(endTime)));
			if (!"-1".equals(status)) {
				if ("0".equals(status))
					dc.add(Restrictions.eq("status", false));
				if ("1".equals(status))
					dc.add(Restrictions.eq("status", true));
			}
			List<Message> list = messageDao.findList(dc, pageNo, pageSize);
			setMessageBut(list);
			int count = messageDao.count(dc);
			return toListJsonIgnoreTime(list, count, button(new ButClick[] { ButClick.ADDUSER }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	/**
	 * 设置按钮
	 * 
	 * @param list
	 */
	private void setMessageBut(List<Message> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDSMS, ButClick.DELSMS });
		if (map.isEmpty())
			return;
		for (Message b : list) {
			if (map.containsKey(ButClick.UPDSMS.getCode()))
				b.setEditClick(true);
			if (map.containsKey(ButClick.DELSMS.getCode()))
				b.setDelClick(true);
		}
	}

	/**
	 * 删除
	 * 
	 * @param message
	 */
	@RequestMapping(value = "/deletesms")
	@ResponseBody
	public ReturnBan deletesms(Integer id, String code) {
		try {
			ReturnBan rb = gugeyzq(code);
			if (rb != null)
				return rb;
			message.setId(id);
			messageDao.delete(message);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping("/messageAddPage")
	public String userAddPage(Integer id, Model model) {
		return messageDetail(null, model);
	}

	@RequestMapping("/messageUpdatePage")
	public String messageUpdatePage(Integer id, Model model) {
		return messageDetail(id, model);
	}

	private String messageDetail(Integer id, Model model) {
		Message message = null;
		if (id != null && id > 0) {
			message = messageDao.get(Message.class, id);
			model.addAttribute("message", message);
		}
		return "messageDetail";
	}

	// **获取模板
	@RequestMapping("/getMoBan")
	@ResponseBody
	private String getMoBan(Integer id) {
		message = messageDao.get(Message.class, id);
		List<SpeildKV> list = JsonTools.getListByJSONArray(SpeildKV.class, message.getModul());
		return JSONArray.fromObject(list).toString();
	}

	@RequestMapping(value = "/addMessage")
	@ResponseBody
	public ReturnBan addmessage(Integer id, String content, String code, String modul, String ip) {
		return saveOrUpdatemessage(id, content, code, modul, ip);
	}

	@RequestMapping(value = "/updateMessage")
	@ResponseBody
	public ReturnBan updatemessage(Integer id, String content, String code, String modul, String ip) {
		return saveOrUpdatemessage(id, content, code, modul, ip);
	}

	/**
	 * 新增或更改活动信息
	 * 
	 * @return
	 */
	private ReturnBan saveOrUpdatemessage(Integer id, String content, String code, String modul, String ip) {
		try {
			ReturnBan rb = gugeyzq(code);
			if (rb != null)
				return rb;

			if (StringUtils.isBlank(content))
				return new ReturnBan("请输入模板样式内容", false);
			if (id == null) {
				DetachedCriteria dc = DetachedCriteria.forClass(Message.class);
				dc.add(Restrictions.eq("modul", modul));
				message = messageDao.findObj(dc);
				if (message != null) {
					return new ReturnBan("该模板已存在，请重新设置", false);
				} else {
					message = new Message();
				}
			}
			message.setContent(content.trim());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			message.setAddTime(formatter.parse(formatter.format(new Date())));
			message.setStatus(true);
			message.setIp(ip.trim());
			if (content.contains("建设银行")) {
				message.setBank("CCB");
			} else if (content.contains("工商银行")) {
				message.setBank("ICBC");
			} else if (content.contains("华融湘江银行")) {
				message.setBank("HRXJB");
			} else if (content.contains("中国农业银行")) {
				message.setBank("ABC");
			}
			message.setModul(modul);

			if (id != null) {
				message.setId(id);
				messageDao.update(message);
			} else {
				messageDao.save(message);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	/**
	 * 匹配模板
	 * 
	 * @param message
	 * @return
	 */
	public Map<String, String> matchSMS(String message) {
		// 匹配模板
		Pattern pattern = null;
		Matcher matcher = null;
		// 过滤调[]
		message = StringFilter(message);
		Map<String, String> map = new TreeMap<String, String>();
		DetachedCriteria dc = DetachedCriteria.forClass(Message.class);
		// 获取所有模板
		List<Message> list = messageDao.findList(dc);

		for (int i = 0; i < list.size(); i++) {
			String str = "";
			List<SpeildKV> list1 = JsonTools.getListByJSONArray(SpeildKV.class, list.get(i).getModul());
			for (int j = 0; j < list1.size(); j++) {
				str += list1.get(j).getKey().replace("(", "\\(").replace(")", "\\)").replace("（", "\\（").replace("）", "\\）") + list1.get(j).getValue().replace("\\+", "+").replace("\\:", ":");
			}
			pattern = Pattern.compile(StringFilter(str)); // 设置一个正则表达式
			String str1 = "(?<name>.*?)(?<time>" + "\\d" + "+月" + "\\d" + "+日" + "\\d" + "+时" + "\\d"
					+ "+分)向您尾号(?<card>" + "\\d" + "{4,4})的储蓄卡账户银联入账收入人民币(?<amount>.*?)元,活期余额(?<balance>.*?)元。建设银行";
			String str2 = "(?<name>.*?)(?<time>" + "\\d" + "+月" + "\\d" + "+日" + "\\d" + "+时" + "\\d"
					+ "+分)向您尾号(?<card>" + "\\d" + "{4,4})的理财卡银联入账收入人民币(?<amount>.*?)元,活期余额(?<balance>.*?)元。建设银行";
			matcher = pattern.matcher(message); // 进行匹配
			if(i==list.size()-1) {
				System.out.println(StringFilter(str));
				System.out.println(message);
			}
			if (matcher.matches()) {
				logger.info("匹配信息模板成功。。。。。");
				if (str.contains("?<card>"))
					map.put("card", matcher.group("card")); // 尾号
				if (str.contains("?<amount>"))
					map.put("amount", matcher.group("amount")); // 金额
				if (str.contains("?<name>"))
					map.put("name", matcher.group("name")); // 姓名
				if (str.contains("?<balance>"))
					map.put("balance", matcher.group("balance")); // 卡余额
				if (str.contains("?<type>"))
					map.put("type", matcher.group("type")); // 支付类型
				if (StringFilter(str).equals(str1) || StringFilter(str).equals(str2)) {
					map.put("ok", "ok");
				}
				map.put("mb", list.get(i).getIp());
				break;
				/*
				 * String[] times=null; String[] days=null; String month=null; String day=null;
				 * if("MODUL_SIX".equals(enums.getCode())) {
				 * times=matcher.group("time").split("月"); days=times[1].split("日");
				 * month=times[0]; day =days[0]; String
				 * h=days[1].substring(0,days[1].length()-2); String
				 * m=days[1].substring(days[1].length()-2,days[1].length()); map.put("time",
				 * month+"-"+day+" "+h+":"+m); //时间 }else {
				 * times=matcher.group("time").split("月"); days=times[1].split("日");
				 * month=times[0]; if(month.length()==1)month="0"+month; day =days[0]; String[]
				 * h=days[1].split("时"); String[] m=h[1].split("分"); map.put("time",
				 * month+"-"+day+" "+h[0]+":"+m[0]); //时间 }
				 */
			}
		}
		return map;
	}

	@RequestMapping("/jxMessage")
	@ResponseBody
	public String depositCallBack(String time, String content, String machine_num, HttpServletRequest request) {
		try {
			// wx-ccb-4562-ylc
			// wx-ysf-ddd-ylc-fcyl
			String[] typeNameArray = machine_num.split("-");
			String bankTypeName = typeNameArray[0];
			String bankTypeName_YSF = typeNameArray[1];
			Map<String, String> map = matchSMS(content);
			String bankType = "";
			String customer_name = "";
			if (map.get("ok") != null && map.get("ok").equals("ok") && bankTypeName_YSF.equals("YSF")) {
				bankType = "YSF"; // 支付类型
				customer_name = typeNameArray[typeNameArray.length - 2]; // 平台编码
			} else if (bankTypeName.equals("ZFB")) {
				bankType = "ZFB_BANK";
				customer_name = typeNameArray[typeNameArray.length - 1];
			} else if (bankTypeName.equals("WX")) {
				bankType = "WX_BANK";
				customer_name = typeNameArray[typeNameArray.length - 1];
			} else if (bankTypeName.equals("YSF")) {
				bankType = "YSF";
				customer_name = typeNameArray[typeNameArray.length - 1];
			}
			String cardnum = "";
			String name = null;
			Double amount = 0D;
			Double balance = 0D;
			if (map != null && map.size() != 0) {
				cardnum = map.get("card"); // 尾号
				name = map.get("name"); // 姓名
				amount = Double.parseDouble(map.get("amount")); // 金额
				balance = Double.parseDouble(map.get("balance")); // 余额
			}
			String bank = "";
			if (content.contains("建设银行")) {
				bank = "CCB";
			} else if (content.contains("工商银行")) {
				bank = "ICBC";
			} else if (content.contains("华融湘江银行")) {
				bank = "HRXJB";
			} else if (content.contains("中国农业银行")) {
				bank = "ABC";
			}
			logger.info(bankType + " 消息通知回调：time=" + time + ",machine_num=" + machine_num + ",name=" + name
					+ ",cardnum=" + cardnum + ",amount=" + amount + ",content=" + content);
			BankVo bankVo = new BankVo(cardnum, StringUtils.SDF.parse(time), name, machine_num, amount);
			Calendar beforeTime = Calendar.getInstance();
			beforeTime.setTime(bankVo.getTime());
			beforeTime.add(Calendar.MINUTE, -30);// 30分钟之前的时间
			Date beforeD = beforeTime.getTime();
			DetachedCriteria dc = getBanck(bankVo, beforeD, customer_name, name, bankType);
			List<PayOrder> list = payOrderDao.findList(dc);
			if (list.size() == 1) {
				logger.info("订单匹配成功===》：" + list.get(0).getGameOrderNumber());
				Business business = businessService.getBusiness(
						new Business(list.get(0).getUid(), list.get(0).getOrderType(), list.get(0).getCustomer_id()));
				String ip = getClientIP(request);
				if (map.get("mb") != null && !map.get("mb").contains(ip)) {
					logger.info("ip不在白名单内，请求IP=" + ip);
					logger.info("数据库IP=" + map.get("mb"));
					return "11";
				}
				if (business == null) {
					logger.info("商户不存在或者不可用");
					logger.info("list.get(0).getUid()=" + list.get(0).getUid());
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

	/**
	 * 获取银行卡信息
	 * 
	 * @param bankVo
	 * @param beforeD
	 * @param typeName
	 * @param name
	 * @param bankType
	 * @return
	 */
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
		bankdc.add(Restrictions.like("tail", "%" + bankVo.getCardNo() + "%")); // 多卡尾号模糊匹配
		List<BankBase> banklist = bankDao.findBankBaseList(bankdc);
		String cardNo = "";
		if (banklist.size() == 0) {
			bankdc = DetachedCriteria.forClass(classs);
			bankdc.add(Restrictions.like("cardNo", "%" + bankVo.getCardNo() + "%")); // 银行卡匹配
			banklist = bankDao.findBankBaseList(bankdc);
			if (banklist.size() > 0) {
				cardNo = banklist.get(0).getCardNo();
			} else {
				logger.info("没有 匹配到银行卡：" + bankVo.getCardNo());
			}
		} else {
			cardNo = banklist.get(0).getCardNo();
		}

		// 匹配对应订单信息
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

	@SuppressWarnings("unused")
	private com.pay.bank.BankBase getBankType(String content) {
		if (content.contains("【工商银行】")) {
			return icbc;
		} else if (content.contains("[建设银行]")) {
			return ccb;
		}
		return null;
	}

	// 过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// 清除掉所有特殊字符
		// String
		// regEx="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		String regEx = "\\[|\\]|【|】|:";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	// 测试
	public static void main(String[] args) {
		String str1 = "贵公司尾号4859的账户5月10日20时31分陈东杰支付宝转账收入人民币1000.07元，余额2159.84元。对方户名:支付宝（中国）网络技术有限公司。[建设银行]";

		String str2 = "您尾号6096的储蓄卡账户5月10日20时10分支付机构提现收入人民币700.02元,活期余额1340.76元。[建设银行]";
		// 您尾号(?<card>\\d{4,4})的储蓄卡账户(?<time>\\d+月\\d+日\\d+时\\d+分)(?<type>.*?)提现收入人民币(?<amount>.*?)元活期余额(?<balance>.*?)元建设银行

		String str3 = "【华融湘江银行】您尾号为2178的账户于05月11日21时29分网联平台来账存入31.01元，余额4011.55元。";
		String str4 = "伍元5月31日19时10分向您尾号6554的储蓄卡账户银联入账收入人民币10.06元,活期余额47.58元。[建设银行]";
		String str5 = "段小平6月11日18时58分向您尾号2339的理财卡银联入账收入人民币50.40元,活期余额390.79元。[建设银行]";
		// (?<name>.*?)(?<time>\\d+月\\d+日\\d+时\\d+分)向您尾号(?<card>\\d{4,4})的(理财卡|储蓄卡账户)(?<type>.*?)入账收入人民币(?<amount>.*?)元活期余额(?<balance>.*?)元建设银行
		String str6 = "贵公司尾号6097的账户7月3日19时23分转账到银行卡收入人民币10.23元，余额1161.40元。对方户名:财付通支付科技有限公司。[建设银行]";
		String str7 = "【中国农业银行】李运宝于08月13日19:14向您尾号1474账户完成银联入账交易人民币10.54，余额10.74。";
		// 中国农业银行(?<name>.*?)于(?<time>\d+月\d+日)向您尾号(?<card>\d{4,4})账户完成(?<type>.*?)入账交易人民币(?<amount>.*?)余额(?<balance>.*?)

		
	}

}
