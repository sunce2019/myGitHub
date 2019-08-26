package com.pay.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.pay.dao.BaseDao;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.enums.PayTran;
import com.pay.model.BankBase;
import com.pay.model.CloudBusiness;
import com.pay.model.CloudFlasHover;
import com.pay.model.Customer;
import com.pay.model.OssSetup;
import com.pay.model.SystConfig;
import com.pay.model.WechatBusiness;
import com.pay.model.WechatCode;
import com.pay.service.SystemService;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;

/**
*@author star
*@version 创建时间：2019年5月30日下午3:13:49
*/
@Controller
@RequestMapping("/wechat")
public class WechatCodeController extends BaseController {
	@Autowired
	private SystemService systemService;
	@Autowired
	private BaseDao<OssSetup> ossSetupDao;
	@Autowired
	private BaseDao<WechatCode> wechatDao;
	@Autowired
	private BaseDao<WechatBusiness> wechatbusinessDao;
	@Autowired
	private BaseDao<Customer> customerDao;
	@RequestMapping("/wechat")
	public String wechat(Model model){
		return "wechatList";
	}
	public String wechatDetail(Integer id, Model model){
		if(id!=null && id>0) {
			model.addAttribute("cloud", wechatDao.get(WechatCode.class, id));
		}
		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		List<Customer> list = customerDao.findList(dc);
		model.addAttribute("list", list);
		return "wechatDetail";
	}
	
	@RequestMapping("/wechatAddPage")
	public String wechatAddPage(Integer id, Model model){
		return wechatDetail(null, model);
	}
	
	@RequestMapping("/wechatUpdatePage")
	public String wechatUpdatePage(Integer id, Model model){
		return wechatDetail(id, model);
	}
	
	@RequestMapping("/wechatDetails")
	@ResponseBody
	public ReturnBan wechatDetails(Integer id, Model model){
		WechatCode wechatCode = null;
		if(id!=null && id>0) {
			wechatCode =  wechatDao.get(WechatCode.class, id);
		}
		return new ReturnBan("", true, wechatCode);
	}
	
	@RequestMapping(value="/addWechat")  
	@ResponseBody
    public ReturnBan addWechat(@RequestParam(value="file", required = false) MultipartFile file,HttpServletRequest request,Integer id,String cardNo, String bankAccount,String bankName,String bankMark,Integer flag,String remarks,Double maxLimit,String cardIndex,String useTime,String tail,String code,Integer customerid){
		return addWechatOrUpdate(file, request, id, cardNo, bankAccount, bankName, bankMark, flag, remarks, maxLimit, cardIndex, useTime, tail, code,customerid);
	}
	
	@RequestMapping(value="/updateWechat")  
	@ResponseBody
    public ReturnBan updateWechat(@RequestParam(value="file", required = false) MultipartFile file,HttpServletRequest request,Integer id,String cardNo, String bankAccount,String bankName,String bankMark,Integer flag,String remarks,Double maxLimit,String cardIndex,String useTime,String tail,String code,Integer customerid){
		return addWechatOrUpdate(file, request, id, cardNo, bankAccount, bankName, bankMark, flag, remarks, maxLimit, cardIndex, useTime, tail, code,customerid);
	}
	
    private ReturnBan addWechatOrUpdate(@RequestParam(value="file", required = false) MultipartFile file,HttpServletRequest request,Integer id,String cardNo, String bankAccount,String bankName,String bankMark,Integer flag,String remarks,Double maxLimit,String cardIndex,String useTime,String tail,String code,Integer customerid){
		try {
			ReturnBan returnBan = gugeyzq(code);
			if(returnBan!=null)return returnBan;
			String url = "";
			if(file!=null) {
				String filename = UUID.randomUUID().toString().replace("-", "");
				OssSetup ossSetup = ossSetupDao.get(OssSetup.class, 1);
				filename = filename+getExtensionName(file.getOriginalFilename());
				url = ossSetup.getEndpoint().replace("http://", "https://"+ossSetup.getBucketName()+".") +"/"+filename;
				uploadOSS(ossSetup, file.getInputStream(),filename);
			}
			
			if(id==null || id==0) {
				DetachedCriteria dc = DetachedCriteria.forClass(WechatCode.class);
				dc.add(Restrictions.eq("cardNo", cardNo));
				List<WechatCode> list = wechatDao.findList(dc);
				if(list.size()>0)
					return new ReturnBan("卡号已存在",false);
				WechatCode b =new WechatCode(cardNo, bankAccount, bankName, flag, remarks, maxLimit);
				b.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				b.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				b.setOssURL(url);
				b.setTail(tail);
				b.setCustomerid(customerid);
				wechatDao.save(b);
			}else {
				WechatCode bank = wechatDao.get(WechatCode.class, id);
				bank.setCardNo(cardNo);
				bank.setBankAccount(bankAccount);
				bank.setBankName(bankName);
				bank.setFlag(flag);
				bank.setRemarks(remarks);
				bank.setMaxLimit(maxLimit);
				bank.setUseStartTime(Integer.parseInt(useTime.split("-")[0]));
				bank.setUseEndTime(Integer.parseInt(useTime.split("-")[1]));
				bank.setTail(tail);
				bank.setCustomerid(customerid);
				if(!StringUtils.isBlank(url))
					bank.setOssURL(url);
				wechatDao.saveOrUpdate(bank);
			}
			return new ReturnBan("操作成功",true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常",false);
    }
	
	private String  getExtensionName(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length() - 1))) { 
                return "."+filename.substring(dot + 1); 
            } 
        } 
        return "."+filename; 
    }
	
	@RequestMapping(value="/deleteWechat")  
	@ResponseBody
    public ReturnBan deleteWechat(Integer id,String code){
		try {
			ReturnBan returnBan = gugeyzq(code);
			if(returnBan!=null)return returnBan;
			WechatCode bank = wechatDao.get(WechatCode.class, id);
			wechatDao.delete(bank);
			
			DetachedCriteria dc = DetachedCriteria.forClass(WechatBusiness.class);
			dc.add(Restrictions.eq("cardNo", bank.getCardNo()));
			List<WechatBusiness> list = wechatbusinessDao.findList(dc);
			for(WechatBusiness b :list)
				wechatbusinessDao.delete(b);
			return new ReturnBan("删除成功",true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常",false);
    }
	
	@RequestMapping(value="/wechatList")  
	@ResponseBody
    public String wechatList(int pageNo,String cardNo,String bankAccount,String bankName,Integer flag,Integer customer_id){
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(WechatCode.class);
			if(!StringUtils.isBlank(cardNo))
				dc.add(Restrictions.eq("cardNo", cardNo));
			if(!StringUtils.isBlank(bankAccount))
				dc.add(Restrictions.eq("bankAccount", bankAccount));
			if(!StringUtils.isBlank(bankName))
				dc.add(Restrictions.eq("bankName", bankName));
			if(flag!=null && flag>0)
				dc.add(Restrictions.eq("flag", flag));
			if(customer_id!=null && customer_id>0)
				dc.add(Restrictions.eq("customerid", customer_id));
			List<WechatCode> list = wechatDao.createSQLQuery(bankSql(pageNo, cardNo, bankAccount, bankName, flag,customer_id),WechatCode.class);
			int count = wechatDao.count(dc);
			
			setBankBut(list);
			return toListJson(list, count,button(new ButClick[] {ButClick.ADDBANK}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
    }
	
	public void setBankBut(List<WechatCode> list) {
		Map<String,Boolean> map = getDateBut(new ButClick[] {ButClick.UPDBANK,ButClick.DELBANK});//
		if(map.isEmpty())return;
		for(BankBase b :list) {
			if(map.containsKey(ButClick.UPDBANK.getCode()))b.setEditClick(true);
			if(map.containsKey(ButClick.DELBANK.getCode()))b.setDelClick(true);
		}
	}
	
	private String bankSql(int pageNo,String cardNo,String bankAccount,String bankName,Integer flag,Integer customer_id ) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT p.price as price,b.* FROM  wechatcode b  left  join (  ");
		sb.append(" SELECT sum(realprice) as price,cardNo  from payorder where addTime>='"+StringUtils.SDF.format(getStart())+"' and flag=2 ");
		sb.append(" and  orderType = '").append(PayTran.YSF.getText()).append("'");
		sb.append(" GROUP BY cardNo ) p  on b.cardNo=p.cardNo  ");
		if(!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName) ||(flag!=null && flag>0) || customer_id!=null && customer_id>0)sb.append(" where ");
		if(!StringUtils.isBlank(cardNo))
			sb.append("b.cardNo='").append(cardNo).append("'");
		if(!StringUtils.isBlank(bankAccount)) {
			if(!StringUtils.isBlank(cardNo))sb.append(" and ");
			sb.append("b.bankAccount='").append(bankAccount).append("'");
		}
		if(!StringUtils.isBlank(bankName)) {
			if(!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount))sb.append(" and ");
			sb.append(" b.bankName='").append(bankName).append("'");
		}
		if(flag!=null && flag>0) {
			if(!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName))sb.append(" and ");
			sb.append(" b.flag=").append(flag).append("");                                            
		}
		if(customer_id!=null && customer_id>0) {
			if(!StringUtils.isBlank(cardNo) || !StringUtils.isBlank(bankAccount) || !StringUtils.isBlank(bankName) || (flag!=null && flag>0))sb.append(" and ");
			sb.append(" b.customerid=").append(customer_id).append("");
		}
		//if(pageNo<1)pageNo=1;
		sb.append("  LIMIT ").append((pageNo-1)*pageSize).append(",").append(pageSize);
		return sb.toString();
	}
	
	
}
