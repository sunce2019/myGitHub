package com.pay.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pay.dao.BaseDao;
import com.pay.dao.RedisModel;
import com.pay.enums.ButClick;
import com.pay.enums.PayTran;
import com.pay.enums.RedisKey;
import com.pay.model.BankBase;
import com.pay.model.CloudBusiness;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.OssSetup;
import com.pay.model.PayOrder;
import com.pay.service.PayOrderService;
import com.pay.util.Encryption;
import com.pay.util.QRCode;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
 * @author star
 * @version 创建时间：2019年5月30日下午3:13:49
 */
@Controller
@RequestMapping("/cloudFlasHover")
public class CloudFlasHoverController extends BaseController {
	@Autowired
	private BaseDao<OssSetup> ossSetupDao;
	@Autowired
	private BaseDao<CloudFlasHover> cloudDao;
	@Autowired
	private BaseDao<CloudBusiness> cloudbusinessDao;
	@Autowired
	private BaseDao<PayOrder> payOrderDao;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private RedisModel redisModel;
	@Autowired
	private BaseDao<Customer> customerDao;

	@RequestMapping("/cloud")
	public String cloud(Model model) {
		return "cloudList";
	}

	@RequestMapping("/cloudUpdatePage")
	public String cloudUpdatePage(Integer id, Model model) {
		return cloudDetail(id, model);
	}

	@RequestMapping("/cloudAddPage")
	public String cloudAddPage(Model model) {
		return cloudDetail(null, model);
	}

	private String cloudDetail(Integer id, Model model) {
		if (id != null && id > 0) {
			model.addAttribute("cloud", cloudDao.get(CloudFlasHover.class, id));
		}
		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		List<Customer> list = customerDao.findList(dc);
		model.addAttribute("list", list);
		return "cloudDetail";
	}

	@RequestMapping("/cloudDetails")
	@ResponseBody
	public ReturnBan cloudDetails(Integer id, Model model) {
		CloudFlasHover cloud = null;
		if (id != null && id > 0) {
			cloud = cloudDao.get(CloudFlasHover.class, id);
		}
		return new ReturnBan("", true, cloud);
	}

	@RequestMapping("/uploadYSF")
	@ResponseBody
	public String uploadYSF(@RequestParam(value = "file", required = false) MultipartFile file, Model model, int payId,
			String date, String sing) {
		try {
			String thisSing = Encryption.sign((payId + Long.parseLong(date)) + "rk8Yu2cWrLmE335yI7INQph4Gfw9yIrL",
					"UTF-8");
			if (thisSing.equals(sing)) {
				redisModel.redisTemplate.opsForValue().set(
						RedisKey.YSF_OSS.getCode() + payOrderDao.get(PayOrder.class, payId).getOrderNumber(),
						gerUrl(file), 10 * 60 * 1000, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "cloudDetail";
	}

	private String gerUrl(MultipartFile file) throws Exception {
		File file1 = new File(StringUtils.PATH);
		isFiles(file1);

		String filename = UUID.randomUUID().toString().replace("-", "");
		filename = StringUtils.PATH + "\\" + filename + getExtensionName(file.getOriginalFilename());
		file.transferTo(new File(filename));
		String url = "";
		try {
			url = QRCode.decode(new File(filename));
			System.out.println("获取的URL：" + url);
			delFile(filename);
		} catch (Exception e) {
			delFile(filename);
		}
		return url;
	}

	private static boolean delFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			return false;
		}

		if (file.isFile()) {
			return file.delete();
		} else {
			String[] filenames = file.list();
			for (String f : filenames) {
				delFile(f);
			}
			return file.delete();
		}
	}

	private void isFiles(File file) {

		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		} else {
		}
	}

	@RequestMapping("/getCardNo")
	@ResponseBody
	public ReturnBan getCardNo(Model model, String cardNo) {
		List<String> list = redisModel.redisTemplate.opsForList().range(RedisKey.YSF_ADD.getCode() + cardNo, 0, 1);
		if (list != null && list.size() > 0) {
			JSONObject jsonObject = JSONObject.fromObject(list.get(0));
			PayOrder payOrder = (PayOrder) JSONObject.toBean(jsonObject, PayOrder.class);
			// redisModel.redisTemplate.delete(RedisKey.YSF_ADD.getCode() + cardNo);
			redisModel.redisTemplate.opsForList().remove(RedisKey.YSF_ADD.getCode() + cardNo, 0, list.get(0));
			return new ReturnBan("", true, payOrder.getId(), payOrder.getRealprice());
		}
		return new ReturnBan("", false, 0, 0);
	}

	@RequestMapping(value = "/imgApi")
	@ResponseBody
	public ReturnBan imgApi(String orderNo) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(PayOrder.class);
			dc.add(Restrictions.eq("orderNumber", orderNo));
			List<PayOrder> list = payOrderDao.findList(dc);
			if (list.size() > 0) {
				dc = DetachedCriteria.forClass(CloudFlasHover.class);
				dc.add(Restrictions.eq("cardNo", list.get(0).getCardNo()));
				CloudFlasHover cloudFlasHover = cloudDao.findObj(dc);
				if (cloudFlasHover != null) {
					// 如果是固定二维码，直接返回
					if (cloudFlasHover.getPattern().intValue() == 2)
						return new ReturnBan(cloudFlasHover.getOssURL(), true);

					if (cloudFlasHover.getPattern().intValue() == 1) {// 固定加动态
						String url = redisModel.redisTemplate.opsForValue().get(RedisKey.YSF_OSS.getCode() + orderNo);
						if (url != null) {
							// 删除备注
							if (list.get(0).getRemarks().contains("未获取二维码"))
								payOrderService.updatePayOrder(list.get(0));
							return new ReturnBan("data:image/jpg;base64," + QRCode.generalQRCode(url), true);
						}

						Long addTime = list.get(0).getAddTime().getTime();
						// 固定加动态 超过30秒脚本没有设置动态二维码，使用。固定二维码
						if (((new Date().getTime() - addTime) / 1000) >= 30) {
							if (!list.get(0).getRemarks().contains("脚本出错，使用固定二维码"))
								payOrderService.updatePayOrder(list.get(0), "脚本出错，使用固定二维码");
							return new ReturnBan(cloudFlasHover.getOssURL(), true);
						}

					} else {
						return new ReturnBan(cloudFlasHover.getOssURL(), true);// 固定二维码，直接返回
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("", false);
	}

	@RequestMapping(value = "/addCloud")
	@ResponseBody
	public ReturnBan addCloud(@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer pattern, Integer customerid) {
		return addCloudflashoverOrUpdate(file, request, id, cardNo, bankAccount, bankName, bankMark, flag, remarks,
				maxLimit, cardIndex, useTime, tail, code, pattern, customerid);
	}

	@RequestMapping(value = "/updateCloud")
	@ResponseBody
	public ReturnBan updateCloud(@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer pattern, Integer customerid) {
		return addCloudflashoverOrUpdate(file, request, id, cardNo, bankAccount, bankName, bankMark, flag, remarks,
				maxLimit, cardIndex, useTime, tail, code, pattern, customerid);
	}

	private ReturnBan addCloudflashoverOrUpdate(@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, Integer id, String cardNo, String bankAccount, String bankName, String bankMark,
			Integer flag, String remarks, Double maxLimit, String cardIndex, String useTime, String tail, String code,
			Integer pattern, Integer customerid) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			String url = "";
			if (file != null) {
				String filename = UUID.randomUUID().toString().replace("-", "");
				OssSetup ossSetup = ossSetupDao.get(OssSetup.class, 1);
				filename = filename + getExtensionName(file.getOriginalFilename());
				url = ossSetup.getEndpoint().replace("http://", "https://" + ossSetup.getBucketName() + ".") + "/"
						+ filename;
				uploadOSS(ossSetup, file.getInputStream(), filename);
			}

			if (id == null || id == 0) {
				DetachedCriteria dc = DetachedCriteria.forClass(CloudFlasHover.class);
				dc.add(Restrictions.eq("cardNo", cardNo));
				List<CloudFlasHover> list = cloudDao.findList(dc);
				if (list.size() > 0)
					return new ReturnBan("卡号已存在", false);
				CloudFlasHover b = new CloudFlasHover(cardNo, bankAccount, bankName, flag, remarks, maxLimit);
				b.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				b.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				b.setOssURL(url);
				b.setTail(tail);
				b.setPattern(pattern);
				b.setCustomerid(customerid);
				cloudDao.save(b);
			} else {
				CloudFlasHover bank = cloudDao.get(CloudFlasHover.class, id);
				bank.setCardNo(cardNo);
				bank.setBankAccount(bankAccount);
				bank.setBankName(bankName);
				bank.setFlag(flag);
				bank.setRemarks(remarks);
				bank.setMaxLimit(maxLimit);
				bank.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				bank.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				bank.setTail(tail);
				bank.setPattern(pattern);
				bank.setCustomerid(customerid);
				if (!StringUtils.isBlank(url))
					bank.setOssURL(url);
				cloudDao.saveOrUpdate(bank);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	private String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return "." + filename.substring(dot + 1);
			}
		}
		return "." + filename;
	}

	@RequestMapping(value = "/cloudList")
	@ResponseBody
	public String cloudList(int pageNo, String cardNo, String bankAccount, String bankName, Integer flag,
			Integer customer_id) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(CloudFlasHover.class);
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
			List<CloudFlasHover> list = cloudDao.createSQLQuery(
					bankSql(pageNo, cardNo, bankAccount, bankName, flag, customer_id), CloudFlasHover.class);
			int count = cloudDao.count(dc);

			setBankBut(list);
			return toListJson(list, count, button(new ButClick[] { ButClick.ADDBANK }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	@RequestMapping(value = "/deleteCloud")
	@ResponseBody
	public ReturnBan deleteCloud(Integer id, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			CloudFlasHover bank = cloudDao.get(CloudFlasHover.class, id);
			cloudDao.delete(bank);

			DetachedCriteria dc = DetachedCriteria.forClass(CloudBusiness.class);
			dc.add(Restrictions.eq("cardNo", bank.getCardNo()));
			List<CloudBusiness> list = cloudbusinessDao.findList(dc);
			for (CloudBusiness b : list)
				cloudbusinessDao.delete(b);
			return new ReturnBan("删除成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	public void setBankBut(List<CloudFlasHover> list) {
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

	private String bankSql(int pageNo, String cardNo, String bankAccount, String bankName, Integer flag,
			Integer customer_id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT p.price as price,b.* FROM  cloudflashover b  left  join (  ");
		sb.append(" SELECT sum(realprice) as price,cardNo  from payorder where addTime>='"
				+ StringUtils.SDF.format(getStart()) + "' and flag=2 ");

		sb.append(" and  orderType = '").append(PayTran.YSF.getText()).append("'");

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
}
