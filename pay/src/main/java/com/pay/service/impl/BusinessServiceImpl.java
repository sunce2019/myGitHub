package com.pay.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.enums.ConfigCode;
import com.pay.enums.PayTran;
import com.pay.model.AliBusiness;
import com.pay.model.AliCode;
import com.pay.model.Bank;
import com.pay.model.BankBase;
import com.pay.model.BankBusiness;
import com.pay.model.Business;
import com.pay.model.CloudBusiness;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.SystConfig;
import com.pay.model.WechatBusiness;
import com.pay.model.WechatCode;
import com.pay.service.BusinessService;
import com.pay.service.SystemService;
import com.pay.util.StringUtils;

/**
 * @author star
 * @version 创建时间：2019年3月29日下午4:31:42
 */
@Service("businessService")
public class BusinessServiceImpl implements BusinessService {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");

	@Autowired
	private BaseDao<Business> businessDao;
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<BankBase> bankBaseDao;
	@Autowired
	private BaseDao<BankBusiness> bankBusinessDao;
	@Autowired
	private BaseDao<CloudBusiness> cloudBusinessDao;
	@Autowired
	private BaseDao<WechatBusiness> wechatBusinessDao;
	@Autowired
	private BaseDao<AliBusiness> aliBusinessDao;
	@Autowired
	private SystemService systemService;

	@Override
	public Business getBusiness(Business business) throws Exception {
		if (business == null)
			return null;
		DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
		if (!StringUtils.isBlank(business.getUid()))
			dc.add(Restrictions.eq("uid", business.getUid()));
		if (!StringUtils.isBlank(business.getCode()))
			dc.add(Restrictions.eq("code", business.getCode()));
		if (!StringUtils.isBlank(business.getPayType()))
			dc.add(Restrictions.eq("payType", business.getPayType()));
		if (business.getCustomer_id() != null && business.getCustomer_id() > 0)
			dc.add(Restrictions.eq("customer_id", business.getCustomer_id()));
		return businessDao.findObj(dc);
	}

	/**
	 * 增加或者修改商户
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Business saveOrUpdateBusiness(Integer id, String uid, String orderType, Double maxLimit, Integer flag,
			Double range_min, Double range_max, String fixed_price, String uids, String token, Integer customer,
			String public_key, String private_key, String[] bankId, String callbackip, String apiUrl, Integer vip,
			Integer bankPattern, boolean openflag, String name, String payCode, String jumpType) throws Exception {
		if (id == null || id == 0) {
			Business businessNow = new Business();
			if (!StringUtils.isBlank(uid) && !uid.equals("0")) {
				DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
				dc.add(Restrictions.eq("code", uid));
				Business business = businessDao.findList(dc, -1, 1).get(0);
				BeanUtils.copyProperties(business, businessNow);
			}

			businessNow.setFixed_price("");
			businessNow.setRange_max(0d);
			businessNow.setRange_min(0d);
			businessNow.setCurrentLimit(0d);

			businessNow.setId(0);
			businessNow.setMaxLimit(maxLimit);
			businessNow.setFlag(2);
			businessNow.setPayType(orderType);
			businessNow.setRange_min(range_min);
			businessNow.setRange_max(range_max);
			businessNow.setFixed_price(fixed_price);
			businessNow.setCurrentLimit(0d);
			Customer c = customerDao.get(Customer.class, customer);
			businessNow.setCustomer_id(customer);
			businessNow.setCustomer_name(c.getName());
			businessNow.setUid(uids);
			businessNow.setToken(token);
			businessNow.setPublic_key(public_key);
			businessNow.setPrivate_key(private_key);
			businessNow.setPrivate_key(private_key);
			businessNow.setCallbackip(callbackip);
			businessNow.setApiUrl(apiUrl);
			businessNow.setVip(vip);
			businessNow.setPayCode(payCode);
			businessNow.setJumpType(Integer.parseInt(jumpType));

			if (bankPattern == null || bankPattern == 0) {
				businessNow.setBankPattern(0);
			} else {
				businessNow.setBankPattern(bankPattern);
			}

			if (!StringUtils.isBlank(name)) { // 如果是自动类型。
				businessNow.setName(name);
				businessNow.setIsauto(2);
				businessNow.setCode(UUID.randomUUID().toString().replace("-", "")); // 随机生产代码
				SystConfig systConfig1 = systemService.getSystConfig(new SystConfig(ConfigCode.NOTIFYURL.getCode()));
				SystConfig systConfig2 = systemService
						.getSystConfig(new SystConfig(ConfigCode.NOTIFYVIEWURL.getCode()));
				businessNow.setNotifyUrl(systConfig1.getContent());
				businessNow.setNotifyViewUrl(systConfig2.getContent());
			}

			businessDao.save(businessNow);

			if (bankId != null && bankId.length > 0) {
				for (String s : bankId) {
					if (businessNow.getPayType().equals(PayTran.ZFB_BANK.getText())
							|| businessNow.getPayType().equals(PayTran.WX_BANK.getText())) {
						bankBusinessDao.save(new BankBusiness(s, businessNow.getId()));
					} else if (businessNow.getPayType().equals(PayTran.YSF.getText())) {
						cloudBusinessDao.save(new CloudBusiness(s, businessNow.getId()));
					} else if (businessNow.getPayType().equals(PayTran.WX_MD.getText())) {
						wechatBusinessDao.save(new WechatBusiness(s, businessNow.getId()));
					} else if (businessNow.getPayType().equals(PayTran.ALI_FM.getText())) {
						aliBusinessDao.save(new AliBusiness(s, businessNow.getId()));
					}

				}
			}
		} else {
			Business business = businessDao.get(Business.class, id, LockMode.UPGRADE);
			if (openflag) {
				business.setFlag(flag);
				business.setMaxLimit(maxLimit);
				business.setRange_min(range_min);
				business.setRange_max(range_max);
				business.setFixed_price(fixed_price);
				business.setUid(uids);
				business.setToken(token);
				business.setCustomer_id(customer);
				business.setPublic_key(public_key);
				business.setPrivate_key(private_key);
				Customer c = customerDao.get(Customer.class, customer);
				business.setCustomer_id(customer);
				business.setCustomer_name(c.getName());
				business.setCallbackip(callbackip);
				business.setApiUrl(apiUrl);
				business.setVip(vip);
				business.setPayCode(payCode);
				business.setJumpType(Integer.parseInt(jumpType));
				if (bankPattern == null || bankPattern == 0) {
					business.setBankPattern(0);
				} else {
					business.setBankPattern(bankPattern);
				}
				businessDao.update(business);

				if (business.getPayType().equals(PayTran.ZFB_BANK.getText())
						|| business.getPayType().equals(PayTran.WX_BANK.getText())) {// 银行卡设置
					DetachedCriteria dc = DetachedCriteria.forClass(BankBusiness.class);
					dc.add(Restrictions.eq("businessId", business.getId()));
					List<BankBusiness> bbList = bankBusinessDao.findList(dc);
					for (BankBusiness b : bbList) {
						bankBusinessDao.delete(b);
					}

					for (String s : bankId) {
						bankBusinessDao.save(new BankBusiness(s, business.getId()));
						updateBankCashierCount(s, Bank.class);
					}
				}
				if (business.getPayType().equals(PayTran.YSF.getText())) {// 云闪付设置
					DetachedCriteria dc = DetachedCriteria.forClass(CloudBusiness.class);
					dc.add(Restrictions.eq("businessId", business.getId()));
					List<CloudBusiness> bbList = cloudBusinessDao.findList(dc);
					for (CloudBusiness b : bbList) {
						cloudBusinessDao.delete(b);
					}
					for (String s : bankId) {
						cloudBusinessDao.save(new CloudBusiness(s, business.getId()));
						updateBankCashierCount(s, CloudFlasHover.class);
					}
				}
				if (business.getPayType().equals(PayTran.WX_MD.getText())) {// 微信买单设置
					DetachedCriteria dc = DetachedCriteria.forClass(WechatBusiness.class);
					dc.add(Restrictions.eq("businessId", business.getId()));
					List<WechatBusiness> bbList = wechatBusinessDao.findList(dc);
					for (WechatBusiness b : bbList) {
						wechatBusinessDao.delete(b);
					}
					for (String s : bankId) {
						wechatBusinessDao.save(new WechatBusiness(s, business.getId()));
						updateBankCashierCount(s, WechatCode.class);
					}
				}
				if (business.getPayType().equals(PayTran.ALI_FM.getText())) {// 支付宝付码设置
					DetachedCriteria dc = DetachedCriteria.forClass(AliBusiness.class);
					dc.add(Restrictions.eq("businessId", business.getId()));
					List<AliBusiness> bbList = aliBusinessDao.findList(dc);
					for (AliBusiness b : bbList) {
						aliBusinessDao.delete(b);
					}
					for (String s : bankId) {
						aliBusinessDao.save(new AliBusiness(s, business.getId()));
						updateBankCashierCount(s, AliCode.class);
					}
				}
			} else {
				business.setFlag(flag);
				businessDao.update(business);
			}
		}
		return null;
	}

	/**
	 * 每次修改银行卡 修改收款次数为0
	 * 
	 * @throws Exception
	 */
	private void updateBankCashierCount(String cardNo, Class classs) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(classs);
		dc.add(Restrictions.eq("cardNo", cardNo));
		BankBase bank = bankBaseDao.findObj(dc);
		if (bank != null) {
			BankBase bankBase = bankBaseDao.get(classs, bank.getId(), LockMode.UPGRADE);
			bankBase.setCashierCount(0);
			bankBaseDao.update(bankBase);
		}
	}

	@Override
	public Business getBusiness(String orderType, Double price, Integer customer_id, int vip) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Business.class);

		dc.add(Restrictions.eq("flag", 1));
		dc.add(Restrictions.eq("customer_id", customer_id));
		dc.add(Restrictions.or(Restrictions.like("fixed_price", "%" + new Double(price).intValue() + "%"),
				Restrictions.and(Restrictions.le("range_min", price), Restrictions.ge("range_max", price))));
		// dc.addOrder(Order.desc("currentLimit"));

		// 第三方支付 微信 和支付包 采用模糊查询
		if (orderType.contains(PayTran.WX_BANK.getCode())) {
			orderType = PayTran.WX_BANK.getText();
		} else if (orderType.contains(PayTran.ZFB_BANK.getCode())) {
			orderType = PayTran.ZFB_BANK.getText();
		} else if (orderType.contains(PayTran.WX.getCode())) {
			orderType = "微信";
			dc.add(Restrictions.ne("payType", PayTran.WX_BANK.getText()));
		} else if (orderType.contains(PayTran.ZFB.getCode())) {
			orderType = "支付宝";
			dc.add(Restrictions.ne("payType", PayTran.ZFB_BANK.getText()));
		} else if (orderType.contains("UNION")) {
			orderType = "银联";
			// 微信转银行 支付宝转银行 云闪付 精准查询
		} else if (orderType.contains(PayTran.YSF.getCode())) {
			orderType = PayTran.YSF.getText();
		} else if (orderType.contains(PayTran.WX_MD.getCode())) {
			orderType = PayTran.WX_MD.getText();
		} else if (orderType.contains(PayTran.ALI_FM.getCode())) {
			orderType = PayTran.ALI_FM.getText();
		}

		dc.add(Restrictions.le("vip", vip));
		dc.add(Restrictions.like("payType", orderType + "%"));
		dc.addOrder(Order.desc("vip"));
		List<Business> list = businessDao.findList(dc);
		deleteObj(list, price);// 删除固定金额数据不匹配数据。 固定金额数据 无法使用sql语句进行匹配
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {// 获取的条数大于1 随机取一条通道
			return list.get(new Random().nextInt(list.size()));
		}
		return null;
	}

	public static void main(String[] args) {
		while (true) {
			System.out.println(new Random().nextInt(1));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 固定金额类商户。进行删除。 查询可能 比如 金额10 固定金额 100-500-1000 这类数据会查询出来。将删除
	 * 
	 * @param list
	 * @param price
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void deleteObj(List<Business> list, Double price) {
		List<Business> deleiteIndex = new ArrayList<Business>();
		for (int i = 0; i < list.size(); i++) {
			if (!StringUtils.isBlank(list.get(i).getFixed_price())) { // 固定金额类商户。进行删除。 查询可能 比如 金额10 固定金额 100-500-1000 删除
				String[] strList = list.get(i).getFixed_price().split("-");
				boolean flag = true; // true 删除
				for (String str : strList) {
					if (Double.parseDouble(str) == price) {
						flag = false;// 有价格相等，false, 不删除
						break;
					}
				}
				if (flag)
					deleiteIndex.add(list.get(i));
			}
		}
		for (Business b : deleiteIndex) {
			list.remove(b); // 删除价格没有的数据
		}
	}

	@Override
	public boolean getBusinessTypeFlag(int id, String orderType) throws Exception {
		if (orderType.contains("微信")) {
			orderType = "微信";
		} else if (orderType.contains("支付宝")) {
			orderType = "支付宝";
		} else if (orderType.contains("银联")) {
			orderType = "银联";
		}
		DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
		dc.add(Restrictions.ne("id", id));
		dc.add(Restrictions.eq("flag", 1));
		dc.add(Restrictions.like("payType", orderType + "%"));
		List<Business> list = businessDao.findList(dc, -1, 1);
		if (list.size() == 0)
			return true;
		return false;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOnekey(String code, int flag) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
		dc.add(Restrictions.eq("code", code));
		List<Business> list = businessDao.findList(dc);
		for (Business b : list) {
			b.setFlag(flag);
			businessDao.update(b);
		}
	}

}
