package com.pay.controller;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.auto.AutoLoad;
import com.pay.auto.AutoMain;
import com.pay.auto.Business;
import com.pay.auto.Parameter;
import com.pay.auto.enums.ParameType;
import com.pay.dao.BaseDao;
import com.pay.dao.RedisModel;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.model.Auto;
import com.pay.model.Customer;
import com.pay.model.PayOrder;
import com.pay.model.SystConfig;
import com.pay.payAssist.ToolKit;
import com.pay.service.AuToService;
import com.pay.service.SystemService;
import com.pay.util.Encryption;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.vo.Vla;

import net.sf.json.JSONObject;

/**
*@author star
*@version 创建时间：2019年6月26日下午3:00:32
*/
@Controller
@RequestMapping("/auto")
public class AuaoController extends BaseController{
	private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("busi1");
	@Autowired
	private SystemService systemService;
	@Autowired
	private BaseDao<Auto> autoDao;
	@Autowired
	private BaseDao<com.pay.model.Business> businessDao;
	@Autowired
	private BaseDao<com.pay.auto.Business> autobusinessDao;
	@Autowired
	private AuToService auToService;
	@Autowired
	private RedisModel redisModel;
	@RequestMapping("/auto")
	public String auto(Model model){
		return "autoList";
	}
	
	@RequestMapping("/autoBusinessDetail")
	public String autoBusinessDetail(Model model){
		return "autoBusinessDetail";
	}
	
	@RequestMapping("/autoUpdatePage")
	public String autoUpdatePage(int id,Model model){
		return autoDetail(id,model);
	}
	
	@RequestMapping("/autoAddPage")
	public String autoAddPage(Model model){
		return autoDetail(0,model);
	}
	
	@RequestMapping("/showParam")
	public String showParam(String number, Model model){
		String returnError = redisModel.redisTemplate.opsForValue().get("returnStr"+number);
		returnError+="</br>"+redisModel.redisTemplate.opsForValue().get("TESTparamStr"+number);
		returnError+="</br>"+redisModel.redisTemplate.opsForValue().get("TESTsign"+number);
		PayOrder payOrder =new PayOrder();
		payOrder.setReturnError(returnError);
		model.addAttribute("payorder", payOrder);
		return "apiError";
	}
	
	private String autoDetail(int id,Model model){
		model.addAttribute("id",id);
		return "autoDetail";
	}
	
	@RequestMapping("/autoModelLoad")
	@ResponseBody
	public String autoModelLoad(Integer id){
		AutoMain autoMain = null;
		if(id!=null && id>0) {
			Auto auto = autoDao.get(Auto.class, id);
			Map<String,Class<?>> classMap = new HashMap<String,Class<?>>();
			classMap.put("layerList", Vla.class);
			autoMain = (AutoMain) JSONObject.toBean(JSONObject.fromObject(auto.getDatas()), AutoMain.class,classMap);
			autoMain.setAutoLoad(new AutoLoad(getBusinessList()));
		}else {
			autoMain = new AutoMain(new AutoLoad(getBusinessList()));
		}
			
		return JSONObject.fromObject(autoMain).toString();
	}
	
	@RequestMapping("/synAuto")
	@ResponseBody
	public ReturnBan synAuto(int id){
		try {
			Auto auto = autoDao.get(Auto.class, id);
			if(auto!=null) {
				com.pay.model.Business business = businessDao.get(com.pay.model.Business.class, auto.getBusiness().getId());
				if(business!=null) {
					String autoJson = URLEncoder.encode(JSONObject.fromObject(auto).toString());
					String businessJson = URLEncoder.encode(JSONObject.fromObject(business).toString());
					String s = ToolKit.request("http://149.129.79.167:8080/auto/collectSynAuto.do?autoJson="+autoJson+"&businessJson="+businessJson,"");
					String txt = "";
					if(!StringUtils.isBlank(s)) {
						ReturnBan rb = (ReturnBan) JSONObject.toBean(JSONObject.fromObject(URLDecoder.decode(s)),ReturnBan.class);
						if(rb.isState()) {
							txt = "测试环境同步成功，";
						}
					}
					business.setCustomer_id(1);
					business.setCustomer_name("YLC");
					businessJson = URLEncoder.encode(JSONObject.fromObject(business).toString());
					s = ToolKit.request("https://47.56.47.216:8786/auto/collectSynAuto.do?autoJson="+autoJson+"&businessJson="+businessJson,"");
					if(!StringUtils.isBlank(s)) {
						ReturnBan rb = (ReturnBan) JSONObject.toBean(JSONObject.fromObject(URLDecoder.decode(s)),ReturnBan.class);
						if(rb.isState()) {
							txt += "正式环境同步成功，";
						}
					}
					return new ReturnBan(txt, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("同步失败", false);
	}
	@RequestMapping("/collectSynAuto")
	@ResponseBody
	public ReturnBan collectSynAuto(String autoJson,String businessJson){
		try {
			Auto auto = (Auto) JSONObject.toBean(JSONObject.fromObject(URLDecoder.decode(autoJson)),Auto.class);
			com.pay.model.Business business = (com.pay.model.Business) JSONObject.toBean(JSONObject.fromObject(URLDecoder.decode(businessJson)),com.pay.model.Business.class);
			Auto loadAuto = auToService.getAuto(new Auto(auto.getSynNo()));
			if(loadAuto==null) {
				business.setId(0);
				business.setFlag(2);
				SystConfig systConfig1 = systemService.getSystConfig(new SystConfig(ConfigCode.NOTIFYURL.getCode()));
				SystConfig systConfig2 = systemService.getSystConfig(new SystConfig(ConfigCode.NOTIFYVIEWURL.getCode()));
				business.setNotifyUrl(systConfig1.getContent());
				business.setNotifyViewUrl(systConfig2.getContent());
				businessDao.save(business);
				auto.setId(0);
				auto.setBusiness(business);
				AutoMain autoMain = (AutoMain) JSONObject.toBean(JSONObject.fromObject(URLDecoder.decode(auto.getDatas())),AutoMain.class);
				autoMain.setBusinessid(business.getId());
				auto.setDatas(JSONObject.fromObject(autoMain).toString());
				autoDao.save(auto);
			}else{
				loadAuto.setDatas(auto.getDatas());
				autoDao.update(loadAuto);
			}
			return new ReturnBan("同步成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("同步失败", true);
	}
	
	@RequestMapping("/addAutoBusiness")
	@ResponseBody
	public ReturnBan addAutoBusiness(String name){
		if(StringUtils.isBlank(name))return new ReturnBan("请输入商户名字", false); 
		try {
			com.pay.model.Business business = new com.pay.model.Business();
			business.setName(name);
			business.setIsauto(2);
			business.setCode(UUID.randomUUID().toString().replace("-", ""));
			business.setVip(0);
			business.setFlag(2);
			business.setBankPattern(1);
			businessDao.save(business);
			return new ReturnBan("添加成功", true); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统错误", false); 
	}
	
	public static void main(String[] args) {
		System.out.println(URLDecoder.decode("2019-07-06+04%3A41%3A17"));
		System.out.println(Encryption.sign("orderAmt=200.00&payAmt=200&payStatus=0&reqOrderNo=1000001739321882&reqTime=2019-07-06 04:27:20&respTime=2019-07-06 04:41:17&key=rt7ebjsMW5hgPKBUqOZCHDAq2okUdKVg", "UTF-8"));
	}
	
	@RequestMapping("/addAuto")
	@ResponseBody
	public ReturnBan addAuto(@RequestBody AutoMain  autoMain,String code){
		return addOrAuto(0, autoMain,code);
	}
	
	@RequestMapping("/updateAuto")
	@ResponseBody
	public ReturnBan updateAuto(int id,@RequestBody AutoMain  autoMain,String code){
		return addOrAuto(id, autoMain,code);
	}
	
	private ReturnBan addOrAuto(int id,AutoMain autoMain,String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if(returnBan!=null)return returnBan;
			ReturnBan rb = checkAutoParam(autoMain);
			if(rb!=null)return rb;
			auToService.addOrUpdateAuto(id, autoMain);
			return new ReturnBan("添加成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}
	
	private ReturnBan checkAutoParam(AutoMain autoMain) {
		if(autoMain.getBusinessid()==0)return new ReturnBan("请选择商户号", false);
		if(!requestParam(autoMain.getParamList(), ParameType.UID))return new ReturnBan("请选择参数列表商户号", false);
		if(!requestParam(autoMain.getParamList(), ParameType.ORDERNO))return new ReturnBan("请选择参数列表订单号", false);
		if(!requestParam(autoMain.getParamList(), ParameType.AMOUNT))return new ReturnBan("请选择参数列表金额", false);
		if(!requestParam(autoMain.getParamList(), ParameType.SIGN))return new ReturnBan("请选择参数列表签名", false);
		if(autoMain.getKeySort()==1&&!requestParam(autoMain.getParamList(), ParameType.TOKEN))return new ReturnBan("请选择参数列表商户秘钥", false);
		if(!requestParam(autoMain.getBackParamList(), ParameType.ORDERNO))return new ReturnBan("请选择回调列表订单号", false);
		if(!requestParam(autoMain.getBackParamList(), ParameType.AMOUNT))return new ReturnBan("请选择回调列表金额", false);
		if(!requestParam(autoMain.getBackParamList(), ParameType.SIGN))return new ReturnBan("请选择回调列表签名", false);
		return null;
	}
	
	private boolean requestParam(List<Parameter> list,ParameType parameType) {
		for(Parameter parameter :list) {
			if(parameter.getType().equals(parameType.getCode()))return true;
		}
		return false;
	}
	
	/**
	 * 获取商户集合数据
	 * @return
	 */
	private List<Business> getBusinessList(){
		DetachedCriteria dc = DetachedCriteria.forClass(com.pay.model.Business.class);
		dc.add(Restrictions.eq("isauto",2));
		dc.add(Restrictions.sqlRestriction(" 1=1 group by code"));
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("id").as("id"));
		projectionList.add(Projections.property("name").as("name"));
		dc.setProjection(projectionList);
		dc.setResultTransformer(Transformers.aliasToBean(Business.class)); 
		return autobusinessDao.findList(dc);
	}
	
	@RequestMapping(value="/getAuto")  
	@ResponseBody
    public String getAuto(int id){
		try {
			Auto auto = autoDao.get(Auto.class, id);
			if(auto!=null) {
				AutoMain autoMain = (AutoMain) JSONObject.toBean(JSONObject.fromObject(auto.getDatas()), AutoMain.class);
				autoMain.setAutoLoad(new AutoLoad(getBusinessList()));
				return JSONObject.fromObject(autoMain).toString();
			}
			return "{}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
    }
	
	@RequestMapping(value="/autoList")  
	@ResponseBody
    public String autoList(int pageNo,String name){
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Auto.class,"a");
			DetachedCriteria b = dc.createAlias("business", "b");
			if(!StringUtils.isBlank(name))
				b.add(Restrictions.like("b.name","%"+ name+"%"));
			List<Auto> list = autoDao.findList(dc, pageNo, pageSize);
			
			setAutoBut(list);
			
			int count = autoDao.count(dc);
			return toListJson(list, count,button(new ButClick[] {ButClick.ADDAUTO}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
    }
	
	private void setAutoBut(List<Auto> list) {
		Map<String,Boolean> map = getDateBut(new ButClick[] {ButClick.UPDAUTO,ButClick.SYAUTO});//
		if(map.isEmpty())return;
		for(Auto c :list) {
			if(map.containsKey(ButClick.UPDAUTO.getCode()))c.setEditClick(true);
			if(map.containsKey(ButClick.SYAUTO.getCode()))c.setSyClick(true);
		}
	}
	
}
