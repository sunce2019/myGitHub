package com.pay.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dao.BaseDao;
import com.pay.enums.ButClick;
import com.pay.enums.CallBackGameStage;
import com.pay.enums.ConfigCode;
import com.pay.enums.CustomerState;
import com.pay.enums.PayState;
import com.pay.enums.PayTran;
import com.pay.enums.PayType;
import com.pay.handlePay.GameApi;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.BankBusiness;
import com.pay.model.BankRelation;
import com.pay.model.Business;
import com.pay.model.CloudBusiness;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.Groups;
import com.pay.model.Ip;
import com.pay.model.Menu;
import com.pay.model.PayOrder;
import com.pay.model.SystConfig;
import com.pay.model.Users;
import com.pay.model.WechatBusiness;
import com.pay.model.WechatCode;
import com.pay.service.BusinessService;
import com.pay.service.CustomerService;
import com.pay.service.PayOrderService;
import com.pay.service.SystemService;
import com.pay.util.ExcleImpl;
import com.pay.util.KeyPairGenUtil;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.util.ViewButton;
import com.pay.vo.Member;
import com.pay.vo.PayOrderCount;

import net.sf.json.JSONArray;

@Controller
public class MainController extends BaseController {
	@Autowired
	private SystemService systemService;
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private BaseDao<Ip> ipDao;
	@Autowired
	private BusinessService businessService;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private GameApi gameApi;
	@Autowired
	private BaseDao<Bank> bankDao;
	@Autowired
	private BaseDao<Groups> groupsDao;
	@Autowired
	private BaseDao<BankBusiness> bankBusinessDao;
	@Autowired
	private BaseDao<PayOrderCount> payOrderCountDao;
	@Autowired
	private BaseDao<CloudFlasHover> cloudDao;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private BaseDao<CloudBusiness> cloudBusinessDao;
	@Autowired
	private BaseDao<WechatCode> wechatCodeDao;
	@Autowired
	private BaseDao<WechatBusiness> wechatBusinessDao;
	@Autowired
	private BaseDao<Users> usersDao;
	@Autowired
	private BaseDao<BankBase> bankBaseDao;
	@Autowired
	private BaseDao<BankRelation> bankRelationDao;

	@RequestMapping("/index")
	public String machine(Model model) {
		model.addAttribute("user", getLonginName());
		return "index";
	}

	@RequestMapping("/success")
	public String success(Model model) {
		return "success";
	}

	@RequestMapping("/bank")
	public String bank(Model model) {
		return "bankList";
	}

	@RequestMapping("/bankUpdatePage")
	public String bankUpdatePage(Integer id, Model model) {
		return bankDetail(id, model);
	}

	@RequestMapping("/bankAddPage")
	public String bankAddPage(Model model) {
		return bankDetail(null, model);
	}

	private String bankDetail(Integer id, Model model) {
		if (id != null && id > 0) {
			model.addAttribute("bank", bankDao.get(Bank.class, id));
		}
		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		List<Customer> list = customerDao.findList(dc);
		model.addAttribute("list", list);
		return "bankDetail";
	}

	@RequestMapping("/bankDetails")
	@ResponseBody
	public ReturnBan bankDetails(Integer id, Model model) {
		Bank bank = null;
		if (id != null && id > 0) {
			bank = bankDao.get(Bank.class, id);
		}
		return new ReturnBan("", true, bank);
	}

	@RequestMapping("/showApiError")
	public String showApiError(Integer id, Model model) {
		model.addAttribute("payorder", payOrderDao.get(PayOrder.class, id));
		return "apiError";
	}

	@RequestMapping("/ip")
	public String ip(Model model) {
		return "ipList";
	}

	@RequestMapping("/ipDetail")
	public String ipDetail(Model model) {
		return "ipDetail";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/alipay")
	public String alipay(String n, String u, Model model) {
		model.addAttribute("n", n);
		model.addAttribute("u", u);
		return "alipay";
	}

	@RequestMapping("/error")
	public String ERROR(Model model) {
		return "error";
	}

	@RequestMapping("/businessPayOrder")
	public String businessPayOrder() {
		return "businessPayOrderList";
	}

	@RequestMapping("/business")
	public String business() {
		return "businessList";
	}

	@RequestMapping("/payorder")
	public String payorder(Model model) {
		model.addAttribute("user", getLonginName());
		return "payorderList";
	}

	@RequestMapping("/customer")
	public String customer() {
		return "customerList";
	}

	@RequestMapping("/customerUpdatePage")
	public String customerUpdatePage(Integer id, Model model) {
		return customerDetail(id, model);
	}

	@RequestMapping("/customerSavePage")
	public String customerSavePage(Integer id, Model model) {
		return customerDetail(null, model);
	}

	private String customerDetail(Integer id, Model model) {
		if (id != null && id > 0) {
			Customer customer = customerDao.get(Customer.class, id);
			customer.setPublic_key1(customer.getPublic_key1().replace("\n", "").replace("\r", "").replace(" ", ""));
			customer.setPrivate_key_2(customer.getPrivate_key_2().replace("\n", "").replace("\r", "").replace(" ", ""));
			model.addAttribute("customer", customerDao.get(Customer.class, id));
		}
		return "customerDetail";
	}

	@RequestMapping("/customerDetails")
	@ResponseBody
	public ReturnBan customerDetails(Integer id, Model model) {
		Customer customer = null;
		if (id != null && id > 0) {
			customer = customerDao.get(Customer.class, id);
		}
		return new ReturnBan("", true, customer);
	}

	@RequestMapping("/businessAddPage")
	public String businessAddPage(Integer id, Model model) {
		return businessDetail(null, model);
	}

	@RequestMapping("/businessUpdatePage")
	public String businessUpdatePage(Integer id, Model model) {
		Menu menu = getNameMenu("编辑商户");
		if (menu != null) {
			model.addAttribute("isOK", "1");
		} else {
			menu = getNameMenu("编辑商户开关");
			if (menu != null)
				model.addAttribute("isOK", "2");
		}

		return businessDetail(id, model);
	}

	@RequestMapping("/businessDetail")
	public String businessDetail(Integer id, Model model) {
		if (id != null && id > 0) {
			Business b = businessDao.get(Business.class, id);
			model.addAttribute("business", b);
		}

		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		List<Customer> list = customerDao.findList(dc);
		model.addAttribute("list", list);
		return "businessDetail";
	}

	@RequestMapping("/businessAddPages")
	@ResponseBody
	public ReturnBan businessAddPages(Integer id, Model model) {
		return businessDetails(null, model);
	}

	@RequestMapping("/businessUpdatePages")
	@ResponseBody
	public ReturnBan businessUpdatePages(Integer id, Model model) {
		return businessDetails(id, model);
	}

	@RequestMapping("/businessDetails")
	@ResponseBody
	public ReturnBan businessDetails(Integer id, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (id != null && id > 0) {
			Business b = businessDao.get(Business.class, id);
			map.put("business", b);
		}

		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		List<Customer> list = customerDao.findList(dc);
		map.put("customerList", list);
		return new ReturnBan("", true, map);
	}

	@RequestMapping(value = "/businessList")
	@ResponseBody
	public String businessList(int pageNo, String uid, Integer flag, Integer customer_id, String name) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Business.class);

			if (!StringUtils.isBlank(name)) {
				dc.add(Restrictions.like("name", "%" + name + "%"));
			} else {
				if (!StringUtils.isBlank(uid)) {
					if (uid.length() != 32) {
						dc.add(Restrictions.eq("name", PayType.getType(uid).getText()));
					} else {
						dc.add(Restrictions.eq("code", uid));
					}
				}

			}

			if (flag != null && flag > 0)
				dc.add(Restrictions.eq("flag", flag));
			if (customer_id != null && customer_id > 0) {
				dc.add(Restrictions.eq("customer_id", customer_id));
			}

			dc.addOrder(Order.asc("flag"));
			dc.addOrder(Order.asc("customer_name"));
			List<Business> list = businessDao.createSQLQuery(businessSql(pageNo, uid, flag, customer_id, name),
					Business.class);
			int count = businessDao.count(dc);

			setBusinessBut(list);
			return toListJson(list, count,
					button(new ButClick[] { ButClick.ONEOPEN, ButClick.ONECLOSE, ButClick.ADDMERC }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setBusinessBut(List<Business> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDMERC, ButClick.TESTBUS });//
		if (map.isEmpty())
			return;
		for (Business b : list) {
			if (map.containsKey(ButClick.UPDMERC.getCode()))
				b.setEditClick(true);
			if (map.containsKey(ButClick.TESTBUS.getCode()))
				b.setTestClick(true);
		}
	}

	private String businessSql(int pageNo, String uid, Integer flag, Integer customer_id, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT p.price as price,b.* FROM  business b  left  join (  ");
		sb.append(" SELECT sum(realprice) as price,uid,orderType,customer_id from payorder where addTime>='"
				+ StringUtils.SDF.format(getStart()) + "' and flag=2 GROUP BY uid,orderType ");
		sb.append(" ) p  on b.uid=p.uid and b.payType=p.orderType and b.customer_id=p.customer_id  ");
		if (!StringUtils.isBlank(uid) || (customer_id != null && customer_id > 0) || !StringUtils.isBlank(name)
				|| (flag != null && flag > 0))
			sb.append(" where ");

		if (!StringUtils.isBlank(name)) {
			sb.append(" b.name like'%").append(name).append("%'");
		} else {
			if (!StringUtils.isBlank(uid)) {
				if (uid.length() != 32) {
					sb.append("b.name='").append(PayType.getType(uid).getText()).append("'");
				} else {
					sb.append("b.code='").append(uid).append("'");
				}
			}
		}
		if (customer_id != null && customer_id > 0) {
			if (!StringUtils.isBlank(uid) || !StringUtils.isBlank(name))
				sb.append(" and ");
			sb.append("b.customer_id=").append(customer_id).append("");
		}
		if (flag != null && flag > 0) {
			if (!StringUtils.isBlank(uid) || !StringUtils.isBlank(name) || (customer_id != null && customer_id > 0))
				sb.append(" and ");
			sb.append("b.flag=").append(flag).append("");
		}

		sb.append("  ORDER BY b.flag asc,b.customer_name asc LIMIT ").append((pageNo - 1) * pageSize).append(",")
				.append(pageSize);
		return sb.toString();
	}

	@RequestMapping(value = "/upperScore")
	@ResponseBody
	public ReturnBan upperScore(int id, Double upperMoney) {
		try {
			return payOrderService.upperScore(id, getLonginName(), true, upperMoney);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("未知错误", false);
	}

	@RequestMapping(value = "/ipList")
	@ResponseBody
	public String ipList(int pageNo, String ip) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Ip.class);

			if (!StringUtils.isBlank(ip))
				dc.add(Restrictions.eq("ip", ip));
			List<Ip> list = ipDao.findList(dc, pageNo, pageSize);

			setIpBut(list);

			int count = ipDao.count(dc);
			return toListJson(list, count, button(new ButClick[] { ButClick.ADDIP }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setIpBut(List<Ip> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.DELETEIP });//
		if (map.isEmpty())
			return;
		for (Ip i : list) {
			if (map.containsKey(ButClick.DELETEIP.getCode()))
				i.setDelClicke(true);
		}
	}

	@RequestMapping(value = "/bankList")
	@ResponseBody
	public String bankList(int pageNo, String cardNo, String bankAccount, String bankName, Integer flag,
			Integer customer_id) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Bank.class, "b");
			if (!StringUtils.isBlank(cardNo))
				dc.add(Restrictions.eq("cardNo", cardNo));
			if (!StringUtils.isBlank(bankAccount))
				dc.add(Restrictions.eq("bankAccount", bankAccount));
			if (!StringUtils.isBlank(bankName))
				dc.add(Restrictions.eq("bankName", bankName));
			if (flag != null && flag > 0)
				dc.add(Restrictions.eq("flag", flag));
			if (customer_id != null && customer_id > 0)
				dc.add(Restrictions.eq("customerid", customer_id));
			List<Bank> list = bankDao.createSQLQuery(bankSql(pageNo, cardNo, bankAccount, bankName, flag, customer_id),
					Bank.class);
			int count = bankDao.count(dc);

			setBankBut(list);

			return toListJson(list, count, button(new ButClick[] { ButClick.ADDBANK }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setBankBut(List<Bank> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDBANK, ButClick.DELBANK });//
		if (map.isEmpty())
			return;
		for (BankBase b : list) {
			if (map.containsKey(ButClick.UPDBANK.getCode()))
				b.setEditClick(true);
			if (map.containsKey(ButClick.DELBANK.getCode()))
				b.setDelClick(true);
		}
	}

	@RequestMapping(value = "/onekey")
	@ResponseBody
	public ReturnBan onekey(String code, int flag) {
		try {
			String menuName = "";
			if (flag == 1) {
				menuName = ButClick.ONEOPEN.getText();
			} else {
				menuName = ButClick.ONECLOSE.getText();
			}
			if (getNameMenu(menuName) == null) {
				return new ReturnBan("权限不够", false);
			}

			businessService.updateOnekey(code, flag);
			return new ReturnBan("", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("未知错误", false);
	}

	private String bankSql(int pageNo, String cardNo, String bankAccount, String bankName, Integer flag,
			Integer customer_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT p.price as price,b.* FROM  bank b  left  join (  ");
		sb.append(" SELECT sum(realprice) as price,cardNo  from payorder where addTime>='"
				+ StringUtils.SDF.format(getStart()) + "' and flag=2 ");
		sb.append(" and ( orderType = '").append(PayTran.ZFB_BANK.getText()).append("' or orderType ='")
				.append(PayTran.WX_BANK.getText()).append("') ");
		sb.append(" GROUP BY cardNo ) p  on b.cardNo=p.cardNo  ");
		if (!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName)
				|| (flag != null && flag > 0) || customer_id != null && customer_id > 0)
			sb.append(" where ");
		if (!StringUtils.isBlank(cardNo))
			sb.append("b.cardNo='").append(cardNo).append("'");
		if (!StringUtils.isBlank(bankAccount)) {
			if (!StringUtils.isBlank(cardNo))
				sb.append(" and ");
			sb.append("b.bankAccount='").append(bankAccount).append("'");
		}
		if (!StringUtils.isBlank(bankName)) {
			if (!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount))
				sb.append(" and ");
			sb.append(" b.bankName='").append(bankName).append("'");
		}
		if (flag != null && flag > 0) {
			if (!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName))
				sb.append(" and ");
			sb.append(" b.flag=").append(flag).append("");
		}
		if (customer_id != null && customer_id > 0) {
			if (!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName)
					|| (flag != null && flag > 0))
				sb.append(" and ");
			sb.append(" b.customerid=").append(customer_id).append("");
		}
		// if(pageNo<1)pageNo=1;
		sb.append("  LIMIT ").append((pageNo - 1) * pageSize).append(",").append(pageSize);
		return sb.toString();
	}

	@RequestMapping(value = "/addBank")
	@ResponseBody
	public ReturnBan addBank(Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer customerid, Integer pattern) {
		return addBankOrUpdate(id, cardNo, bankAccount, bankName, bankMark, flag, remarks, maxLimit, cardIndex, useTime,
				tail, code, customerid, pattern);
	}

	@RequestMapping(value = "/updateBank")
	@ResponseBody
	public ReturnBan updateBank(Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer customerid, Integer pattern) {
		return addBankOrUpdate(id, cardNo, bankAccount, bankName, bankMark, flag, remarks, maxLimit, cardIndex, useTime,
				tail, code, customerid, pattern);
	}

	private ReturnBan addBankOrUpdate(Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer customerid, Integer pattern) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			if (id == null || id == 0) {

				DetachedCriteria dc = DetachedCriteria.forClass(Bank.class);
				dc.add(Restrictions.eq("cardNo", cardNo));
				List<Bank> list = bankDao.findList(dc);
				if (list.size() > 0)
					return new ReturnBan("卡号已存在", false);
				Bank b = new Bank(cardNo, bankAccount, bankName, bankMark, flag, remarks, maxLimit, 1, cardIndex);
				b.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				b.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				b.setTail(tail);
				b.setCustomerid(customerid);
				b.setPattern(pattern);
				bankDao.save(b);
			} else {
				Bank bank = bankDao.get(Bank.class, id);
				bank.setCardNo(cardNo);
				bank.setBankAccount(bankAccount);
				bank.setBankName(bankName);
				bank.setBankMark(bankMark);
				bank.setFlag(flag);
				bank.setRemarks(remarks);
				bank.setMaxLimit(maxLimit);
				bank.setCardIndex(cardIndex);
				bank.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				bank.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				bank.setTail(tail);
				bank.setCustomerid(customerid);
				bank.setPattern(pattern);
				bankDao.saveOrUpdate(bank);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/deleteBank")
	@ResponseBody
	public ReturnBan deleteBank(Integer id, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			Bank bank = bankDao.get(Bank.class, id);
			bankDao.delete(bank);

			DetachedCriteria dc = DetachedCriteria.forClass(BankBusiness.class);
			dc.add(Restrictions.eq("cardNo", bank.getCardNo()));
			List<BankBusiness> list = bankBusinessDao.findList(dc);
			for (BankBusiness b : list)
				bankBusinessDao.delete(b);
			return new ReturnBan("删除成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/addIp")
	@ResponseBody
	public ReturnBan addIp(String ip, String remarks, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			Ip ips = new Ip(ip, getLonginName().getName(), remarks);
			ipDao.save(ips);
			List<Ip> list = (List<Ip>) request.getServletContext().getAttribute("ipList");
			list.add(ips);
			request.getServletContext().setAttribute("ipList", list);
			return new ReturnBan("添加成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/deleteIp")
	@ResponseBody
	public ReturnBan deleteIp(int id, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			Ip ip = ipDao.get(Ip.class, id);
			ipDao.delete(ip);

			List<Ip> list = (List<Ip>) request.getServletContext().getAttribute("ipList");
			int index = -1;
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).getId() == ip.getId()) {
					index = i;
					break;
				}
			if (index > -1)
				list.remove(index);
			request.getServletContext().setAttribute("ipList", list);
			return new ReturnBan("删除成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/customerList")
	@ResponseBody
	public String customerList(int pageNo, String name) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);

			if (!StringUtils.isBlank(name))
				dc.add(Restrictions.eq("name", name));

			List<Customer> list = customerDao.findList(dc, pageNo, pageSize);
			int count = customerDao.count(dc);

			// **设置页面编辑按钮
			setCustomerBut(list);

			return toListJson(list, count, button(new ButClick[] { ButClick.ADDCUST }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setCustomerBut(List<Customer> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDCUS });//
		if (map.isEmpty())
			return;
		for (Customer c : list) {
			if (map.containsKey(ButClick.UPDCUS.getCode()))
				c.setEditClick(true);
		}
	}

	@RequestMapping(value = "/saveBusiness")
	@ResponseBody
	public ReturnBan saveBusiness(Integer id, String uid, String orderType, Double maxLimit, Integer flag,
			Double range_min, Double range_max, String fixed_price, String uids, String token, Integer customer,
			String public_key, String private_key, String[] bankId, String callbackip, String apiUrl, String code,
			Integer vip, Integer bankPattern, String name, String payCode, String jumpType) {
		try {
			return saveOrUpdateBusiness(id, uid, orderType, maxLimit, flag, range_min, range_max, fixed_price, uids,
					token, customer, public_key, private_key, bankId, callbackip, apiUrl, code, vip, bankPattern, name,
					payCode, jumpType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/updateBusiness")
	@ResponseBody
	public ReturnBan updateBusiness(Integer id, String uid, String orderType, Double maxLimit, Integer flag,
			Double range_min, Double range_max, String fixed_price, String uids, String token, Integer customer,
			String public_key, String private_key, String[] bankId, String callbackip, String apiUrl, String code,
			Integer vip, Integer bankPattern, String payCode, String jumpType) {
		try {
			return saveOrUpdateBusiness(id, uid, orderType, maxLimit, flag, range_min, range_max, fixed_price, uids,
					token, customer, public_key, private_key, bankId, callbackip, apiUrl, code, vip, bankPattern, "",
					payCode, jumpType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	private ReturnBan saveOrUpdateBusiness(Integer id, String uid, String orderType, Double maxLimit, Integer flag,
			Double range_min, Double range_max, String fixed_price, String uids, String token, Integer customer,
			String public_key, String private_key, String[] bankId, String callbackip, String apiUrl, String code,
			Integer vip, Integer bankPattern, String name, String payCode, String jumpType) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			if ((range_min != null && range_min > 0) && (range_max != null && range_max > 0)
					&& !StringUtils.isBlank(fixed_price))
				return new ReturnBan("固定金额与范围金额不能同时设置", false);
			if (((range_min != null && range_min > 0) || (range_max != null && range_max > 0))
					&& !StringUtils.isBlank(fixed_price))
				return new ReturnBan("固定金额与范围金额不能同时设置", false);
			if (((range_min != null && range_min > 0) && (range_max == null || range_max == 0))
					|| (((range_min == null || range_min == 0) && (range_max != null && range_max >= 0)))) {
				return new ReturnBan("范围最小最大必须都输入", false);
			}
			if (id == null || id == 0) {
				DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
				dc.add(Restrictions.eq("code", uid));
				dc.add(Restrictions.eq("payType", orderType));
				dc.add(Restrictions.eq("customer_id", customer));
				Business business = businessDao.findObj(dc);
				if (business != null)
					return new ReturnBan("支付类型已存在", false);

				dc = DetachedCriteria.forClass(Business.class);
				dc.add(Restrictions.eq("name", name));
				business = businessDao.findObj(dc);
				if (business != null)
					return new ReturnBan("商户名已存在", false);

			} else {
				if (flag != null && flag == 2 && businessService.getBusinessTypeFlag(id, orderType))
					return new ReturnBan("最少需要保留一条该支付类型开启状态", false);
			}
			boolean openflag = false;
			if (id != null && id > 0) {
				Menu menu = getNameMenu("编辑商户开关");
				if (menu == null)
					openflag = true;
			}

			businessService.saveOrUpdateBusiness(id, uid, orderType, maxLimit, flag, range_min, range_max, fixed_price,
					uids, token, customer, public_key, private_key, bankId, callbackip, apiUrl, vip, bankPattern,
					openflag, name, payCode, jumpType);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/updateCustomer")
	@ResponseBody
	public ReturnBan updateCustomer(Integer id, String name, Integer flag, String ip, int callbackType, String code) {
		return saveOrUpdateCustomer(id, name, flag, ip, callbackType, code);
	}

	@RequestMapping(value = "/loadBank")
	@ResponseBody
	public String loadBank(String payType, Integer customerid, Integer id) {
		try {
			if (id != null && id > 0)
				return editLoadBank(id, customerid);

			Class class_ = Bank.class;
			if (payType.equals(PayTran.YSF.getText())) {
				class_ = CloudFlasHover.class;
			} else if (payType.equals(PayTran.WX_MD.getText())) {
				class_ = WechatCode.class;
			}

			DetachedCriteria dc = DetachedCriteria.forClass(class_);
			dc.add(Restrictions.eq("flag", 1));
			dc.add(Restrictions.eq("customerid", customerid));
			List<BankBase> list = bankBaseDao.findList(dc);
			return JSONArray.fromObject(list).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "[]";
	}

	private String editLoadBank(int id, Integer customerid) {
		Business business = businessDao.get(Business.class, id);

		Class class_ = Bank.class;
		Class class__ = BankBusiness.class;
		if (business.getPayType().equals(PayTran.YSF.getText())) {
			class_ = CloudFlasHover.class;
			class__ = CloudBusiness.class;
		} else if (business.getPayType().equals(PayTran.WX_MD.getText())) {
			class_ = WechatCode.class;
			class__ = WechatBusiness.class;
		}

		DetachedCriteria dc = DetachedCriteria.forClass(class__);
		dc.add(Restrictions.eq("businessId", id));
		List<BankRelation> list = bankRelationDao.findList(dc);
		dc = DetachedCriteria.forClass(class_);
		dc.add(Restrictions.eq("flag", 1));
		dc.add(Restrictions.eq("customerid", customerid));
		List<BankBase> BankList = bankBaseDao.findList(dc);

		for (BankBase c : BankList) {
			Boolean flag = true;
			for (BankRelation b : list) {
				if (b.getCardNo().equals(c.getCardNo())) {
					flag = false;
					break;
				}
			}
			if (!flag) {
				c.setEditClick(true);
			}
		}
		return JSONArray.fromObject(BankList).toString();
	}

	@RequestMapping(value = "/saveCustomer")
	@ResponseBody
	public ReturnBan saveCustomer(Integer id, String name, Integer flag, String ip, int callbackType, String code) {
		return saveOrUpdateCustomer(id, name, flag, ip, callbackType, code);
	}

	private ReturnBan saveOrUpdateCustomer(Integer id, String name, Integer flag, String ip, int callbackType,
			String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			if (id == null || id == 0) {
				DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
				dc.add(Restrictions.eq("name", name));
				Customer c = customerDao.findObj(dc);
				if (c != null)
					return new ReturnBan("名称已存在", false);

				Customer customer = new Customer();
				customer.setName(name);
				customer.setFlag(flag);
				Map<String, String> map = KeyPairGenUtil.genKeyPair();
				customer.setPublic_key1(map.get("publicKey"));
				customer.setPrivate_key1(map.get("privateKey"));
				map = KeyPairGenUtil.genKeyPair();
				customer.setPublic_key_2(map.get("publicKey"));
				customer.setPrivate_key_2(map.get("privateKey"));
				customer.setKey(getUUID());
				customer.setUid(getUUID());
				customer.setAddTime(new Date());
				customer.setFlag(CustomerState.NOTACTIVE.getCode());
				customer.setIp(ip);
				customer.setSymmetric(getUUID());
				customer.setCallbackType(callbackType);
				customerService.addCustomer(customer);
			} else {

				DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
				dc.add(Restrictions.eq("name", name));
				dc.add(Restrictions.ne("id", id));
				Customer c = customerDao.findObj(dc);
				if (c != null)
					return new ReturnBan("名称已存在", false);

				Customer customer = customerDao.get(Customer.class, id);
				customer.setName(name);
				customer.setFlag(flag);
				customer.setIp(ip);
				customer.setCallbackType(callbackType);
				customerDao.update(customer);
			}

			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		// 去掉“-”符号
		return uuid.replaceAll("-", "").toUpperCase();
	}

	@RequestMapping(value = "/payorderList")
	@ResponseBody
	public String payorderList(int pageNo, String orderNumber, String userName, String uid, Integer customer_id,
			String orderType, Integer flag, String gameOrderNumber, String start, String end, Double minPrice,
			Double maxPrice, String priceType, String remarks) {
		try {
			if (customer_id != null && customer_id > 0 && !CheckPermissions(customer_id, getUserMenus())) {
				logger.info("权限不够：" + getLonginName().getName() + ",查询id：" + customer_id);
				return "";
			}

			StringBuffer selectSql = new StringBuffer();
			selectSql.append(
					"select p.*,(case when b.tail is not null then b.tail when c.tail is not null then c.tail when w.tail is not null then w.tail end) as tail from payorder as p left join bank as b on p.cardNo=b.cardNo and (p.orderType='"
							+ PayTran.ZFB_BANK.getText() + "' or p.orderType='" + PayTran.WX_BANK.getText()
							+ "') left join cloudflashover as c  on p.cardNo=c.cardNo and p.orderType='"
							+ PayTran.YSF.getText()
							+ "' left join wechatcode as w  on p.cardNo=w.cardNo and p.orderType='"
							+ PayTran.WX_MD.getText() + "' ");

			StringBuffer condition = new StringBuffer(); // 参数 sql
			Map<String, Object> params = new HashMap<String, Object>(); // 参数Map
			queryparameter(orderNumber, userName, uid, customer_id, orderType, flag, gameOrderNumber, start, end,
					minPrice, maxPrice, priceType, condition, params, true, remarks);

			params.put("pageNo", (pageNo - 1) * pageSize);
			params.put("pageSize", pageSize);

			condition.append(" LIMIT :pageNo,:pageSize");

			// sql 原型
			// select p.*,(case when b.tail is not null then b.tail when c.tail is not null
			// then c.tail when w.tail is not null then w.tail end) as tail from payorder as
			// p left join bank as b on p.cardNo=b.cardNo and (p.orderType='支付宝转银行' or
			// p.orderType='微信转银行') left join cloudflashover as c on p.cardNo=c.cardNo and
			// p.orderType='云闪付' left join wechatcode as w on p.cardNo=w.cardNo and
			// p.orderType='微信买单' where p.addTime>='2019-05-01 00:00:00' and
			// p.addTime<='2019-05-31 23:59:59' order by p.addTime desc LIMIT 0,20
			List<PayOrder> list = payOrderDao.findObjBySql(selectSql.toString() + " " + condition.toString(), params,
					PayOrder.class);

			selectSql = new StringBuffer();
			selectSql.append("select p.*,b.tail as tail from payorder as p left join bank as b on p.cardNo=b.cardNo ");

			StringBuffer payOrderCountSql = new StringBuffer();
			condition = new StringBuffer(); // 参数 sql
			params = new HashMap<String, Object>(); // 参数Map
			payOrderCountSql
					.append("SELECT a.*,b.* from (select count(*)as count,sum(p.price) priceSum from payorder as p ");
			queryparameter(orderNumber, userName, uid, customer_id, orderType, flag, gameOrderNumber, start, end,
					minPrice, maxPrice, priceType, condition, params, true, remarks);
			payOrderCountSql.append(condition.toString());

			payOrderCountSql.append(
					")as a,(select sum(p.realprice) realpriceSum,count(*)as successRateCount from payorder as p");

			condition = new StringBuffer(); // 参数 sql
			queryparameter(orderNumber, userName, uid, customer_id, orderType, 2, gameOrderNumber, start, end, minPrice,
					maxPrice, priceType, condition, params, false, remarks);
			payOrderCountSql.append(condition.toString());
			payOrderCountSql.append(") as b");
			// 原sql 模型
			// SELECT a.*,b.* from (select count(*)as count,sum(p.price) priceSum from
			// payorder as p where p.addTime>='2019-05-01 00:00:00' and
			// p.addTime<='2019-05-27 23:59:59' order by p.addTime desc )as a,(select
			// sum(p.realprice) realpriceSum,count(*)as successRateCount from payorder as p
			// where p.addTime>='2019-05-01 00:00:00' and p.addTime<='2019-05-27 23:59:59'
			// and p.flag=2 order by p.addTime desc ) as b

			List<PayOrderCount> payOrderCountList = payOrderCountDao.findObjBySql(payOrderCountSql.toString(), params,
					PayOrderCount.class);
			PayOrderCount payOrderCount = payOrderCountList.get(0); // 查询总金额 总实际金额

			setPayorderBut(list);// 设置用户是否有权限操作按钮
			return toListJson(list, payOrderCount, button(new ButClick[] { ButClick.EXPORT }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setPayorderBut(List<PayOrder> list) throws Exception {
		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.MANUAL.getCode(), 1));
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, StringUtils.unAbs(Integer.parseInt(systConfig.getContent())));

		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPSO, ButClick.BACKGAME, ButClick.QUERYAPI });//
		if (map.isEmpty())
			return;
		for (PayOrder p : list) {
			if (map.containsKey(ButClick.UPSO.getCode()))
				p.setUpScoreClick(true);
			if (map.containsKey(ButClick.BACKGAME.getCode()))
				p.setBackClick(true);
			if (map.containsKey(ButClick.QUERYAPI.getCode()))
				p.setApiClick(true);
			if (!p.getPayName().equals(PayType.SUBAO.getText())
					&& p.getAddTime().getTime() > nowTime.getTime().getTime() && p.getFlag().intValue() == 1)
				p.setBackClick2(true);
		}
	}

	/**
	 * 检查订单 接入是否有权限
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean CheckPermissions(Integer customer_id, List<Menu> menuList) throws Exception {
		for (Menu menu : menuList) {
			if (menu.getCustomer() != null && menu.getCustomer().getId() == customer_id)
				return true;
		}
		return false;
	}

	/**
	 * 订单导出按钮、
	 * 
	 * @return
	 */
	private Map<String, Object> payOrderBut(String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		Menu menu = getNameMenu(name);
		if (menu != null) {
			map.put("payOrderExport", new ViewButton(menu.getId(), true, "订单导出"));
		}

		return map;
	}

	private void conditionAdd(StringBuffer sb) {
		if (!sb.toString().equals("")) {
			sb.append(" and ");
		} else {
			sb.append(" where ");
		}
	}

	private void queryparameter(String orderNumber, String userName, String uid, Integer customer_id, String orderType,
			Integer flag, String gameOrderNumber, String start, String end, Double minPrice, Double maxPrice,
			String priceType, StringBuffer sb, Map<String, Object> params, boolean isFlag, String remarks)
			throws Exception {

		if (!StringUtils.isBlank(orderNumber)) {
			conditionAdd(sb);
			sb.append(" p.orderNumber=").append(":orderNumber ");
			params.put("orderNumber", orderNumber);
		}

		if (!StringUtils.isBlank(userName)) {
			conditionAdd(sb);
			sb.append(" p.userName=").append(":userName ");
			params.put("userName", userName);
		}

		if (!StringUtils.isBlank(uid)) {
			conditionAdd(sb);
			sb.append(" p.payName=").append(":payName ");
			if (uid.length() == 32) {
				Business b = new Business();
				b.setCode(uid);
				params.put("payName", businessService.getBusiness(b).getName());
			} else {
				params.put("payName", PayType.getType(uid).getText());
			}
		}

		if (!StringUtils.isBlank(orderType)) {
			conditionAdd(sb);
			sb.append(" p.orderType=").append(":orderType ");
			params.put("orderType", orderType);
		}

		if (!StringUtils.isBlank(gameOrderNumber)) {
			conditionAdd(sb);
			sb.append(" p.gameOrderNumber=").append(":gameOrderNumber ");
			params.put("gameOrderNumber", gameOrderNumber);
		}

		if (customer_id != null && customer_id > 0) {
			conditionAdd(sb);
			sb.append(" p.customer_id =:customer_id ");
			params.put("customer_id", customer_id);
		} else {
			conditionAdd(sb);
			sb.append(" p.customer_id in (").append(request.getSession().getAttribute("userCustomerID")).append(") ");
		}

		if (!StringUtils.isBlank(start)) {
			conditionAdd(sb);
			sb.append(" p.addTime>=").append(":start ");
			params.put("start", start);
		}

		if (!StringUtils.isBlank(end)) {
			conditionAdd(sb);
			sb.append(" p.addTime<=").append(":end ");
			params.put("end", end);
		}
		if (!StringUtils.isBlank(remarks)) {
			conditionAdd(sb);
			remarks = "%" + remarks + "%";
			sb.append(" p.remarks like ").append(":remarks");
			params.put("remarks", remarks);
		}

		if (minPrice != null && minPrice > 0) {
			conditionAdd(sb);
			sb.append("p." + priceType).append(">=").append(":minPrice ");
			params.put("minPrice", minPrice);
		}
		if (maxPrice != null && maxPrice > 0) {
			conditionAdd(sb);
			sb.append("p." + priceType).append("<=").append(":maxPrice ");
			params.put("maxPrice", maxPrice);
		}

		if (isFlag) {
			if (flag != null && flag > 0) {
				conditionAdd(sb);
				sb.append(" p.flag=").append(":flag ");
				params.put("flag", flag);
			}
			sb.append(" order by p.addTime desc ");
		} else {
			if (flag != null && flag > 0) {
				conditionAdd(sb);
				sb.append(" p.flag=").append(":flag2 ");
				params.put("flag2", flag);
			}
			sb.append(" order by p.addTime desc ");
		}

	}

	private Object[] Assemble(List<Groups> list, Object[] obj) {
		if (list.size() > 0) {
			obj = new Object[list.size()];
			for (int i = 0; i < list.size(); i++) {
				obj[i] = list.get(i).getCustomerid();
			}
		}
		return obj;
	}

	@RequestMapping(value = "/download_excel")
	@ResponseBody
	public void dowm(HttpServletResponse response, int pageNo, String orderNumber, String userName, String uid,
			Integer customer_id, String orderType, Integer flag, String gameOrderNumber, String start, String end) {
		try {
			response.setContentType("application/binary;charset=UTF-8");

			ServletOutputStream out = response.getOutputStream();
			try {
				// 设置文件头：最后一个参数是设置下载文件名(这里我们叫：张三.pdf)
				response.setHeader("Content-Disposition", "attachment;fileName="
						+ URLEncoder.encode("订单" + StringUtils.SDF.format(new Date()) + ".xls", "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
			Users user = getLonginName();
			if (user.getType() == 3 || user.getType() == 2) {// 如果是平台用户账户,客服账户,只能查询自己平台订单
				customer_id = user.getCustomer_id();
			}

			if (!StringUtils.isBlank(orderNumber))
				dc.add(Restrictions.eq("orderNumber", orderNumber));
			if (!StringUtils.isBlank(userName))
				dc.add(Restrictions.eq("userName", userName));
			if (!StringUtils.isBlank(uid))
				dc.add(Restrictions.eq("payName", PayType.getType(uid).getText()));
			if (!StringUtils.isBlank(orderType))
				dc.add(Restrictions.eq("orderType", orderType));
			if (!StringUtils.isBlank(gameOrderNumber))
				dc.add(Restrictions.eq("gameOrderNumber", gameOrderNumber));
			if (customer_id != null && customer_id > 0)
				dc.add(Restrictions.eq("customer_id", customer_id));
			if (flag != null && flag > 0)
				dc.add(Restrictions.eq("flag", flag));
			if (!StringUtils.isBlank(start))
				dc.add(Restrictions.ge("addTime", StringUtils.SDF.parse(start)));
			if (!StringUtils.isBlank(end))
				dc.add(Restrictions.le("addTime", StringUtils.SDF.parse(end)));
			dc.addOrder(Order.desc("addTime"));

			List<PayOrder> list = payOrderDao.findList(dc);
			ExcleImpl.export(list, payOrderDao.payCount(dc), out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getDate")
	@ResponseBody
	public String getDate() {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
			dc.addOrder(Order.desc("id"));
			List<Business> list = businessDao.findList(dc);
			Set<Member> member = new HashSet<Member>();
			Map<String, Object> map = new HashMap<String, Object>();
			for (Business b : list) {
				member.add(new Member(b.getCode(), b.getName()));
			}
			map.put("business", member);

			dc = DetachedCriteria.forClass(Customer.class);
			dc.addOrder(Order.desc("id"));
			List<Customer> list2 = customerDao.findList(dc);
			member = new HashSet<Member>();
			for (Customer c : list2) {
				member.add(new Member(Integer.toString(c.getId()), c.getName()));
			}

			map.put("customer", member);
			map.put("payTran", PayTran.getPerText());

			return toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	@RequestMapping(value = "/getOrderDate")
	@ResponseBody
	public String getOrderDate() {
		try {
			Set<Customer> idSet = new HashSet<Customer>();
			Set<Menu> set = getMenu();
			for (Menu menu : set) {
				if (menu.getCustomer() != null) {
					// 去掉无关隐秘数据
					menu.getCustomer().setKey("");
					menu.getCustomer().setPrivate_key1("");
					menu.getCustomer().setPublic_key1("");
					menu.getCustomer().setPrivate_key_2("");
					menu.getCustomer().setPublic_key_2("");
					menu.getCustomer().setUid("");
					menu.getCustomer().setSymmetric("");
					idSet.add(menu.getCustomer());
				}
			}

			return JSONArray.fromObject(idSet).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	@RequestMapping(value = "/manualCallBackGame")
	@ResponseBody
	public ReturnBan manualCallBackGame(String orderNumber) {
		try {
			PayOrder payOrder = payOrderService
					.payOrder(new PayOrder(orderNumber, PayState.SUCCESS.getCode(), CallBackGameStage.MOVE.getCode()));
			if (payOrder == null) {
				return new ReturnBan("该订单状已被改变", false);
			}
			log.info(getLonginName().getName() + "手动回调游戏api:" + payOrder.toString());
			boolean flag = gameApi.callBackGame(payOrder.getId(), false, true);
			if (flag) {
				return new ReturnBan("回调成功", true);
			} else {
				return new ReturnBan("回调失败", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/manualCallBackGame2")
	@ResponseBody
	public ReturnBan manualCallBackGame2(int id, double money) {
		try {
			return payOrderService.manualCallBackGame2(id, money, getLonginName().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("上分失败。", false);
	}

	public static void main(String[] args) throws Exception {
		Date date1 = StringUtils.SDF.parse("2019-02-02 03:00:00");
		Date date2 = StringUtils.SDF.parse("2019-02-02 03:59:59");
		if (date1.getTime() > date2.getTime()) {
			System.out.println("a");
		}
	}

}
