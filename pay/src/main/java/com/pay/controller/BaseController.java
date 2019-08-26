package com.pay.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyun.oss.OSSClient;
import com.pay.dao.RedisModel;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.model.Active;
import com.pay.model.Groups;
import com.pay.model.Menu;
import com.pay.model.OssSetup;
import com.pay.model.SystConfig;
import com.pay.model.Users;
import com.pay.service.SystemService;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.IgnoreFieldProcessorImpl;
import com.pay.util.JsonTools;
import com.pay.util.ReturnBan;
import com.pay.util.VerifyCodeUtils;
import com.pay.util.ViewButton;
import com.pay.vo.PayOrderCount;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BaseController {
	
	public final int pageSize = 20;
	
	@Autowired
	public  HttpServletRequest request;
	@Autowired
	private SystemService systemService;
	@Autowired
	private RedisModel redisModel;
	/**
	 * 谷歌验证码验证
	 */
	public ReturnBan gugeyzq(String Vcode)throws Exception {
		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.AUTHCODE.getCode(),1));
		if(systConfig==null)return null;
		Users users = getLonginName();
		String key = users.getLoginname()+"Google";
		String v = redisModel.redisTemplate.opsForValue().get(key);
		if(v!=null)return null;
		if(StringUtils.isBlank(Vcode))return new ReturnBan("请输入验证码", false);
		if(GoogleAuthenticator.authcode(Vcode,users.getKey())) {
			systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.GOOGLETIME.getCode(),1));
			if(systConfig==null)return null;
			int time = Integer.parseInt(systConfig.getContent());
			redisModel.redisTemplate.opsForValue().set(key,"",1000*60*time,TimeUnit.MILLISECONDS);
			return null;
		}
		return new ReturnBan("谷歌验证码错误", false);
	}
	/*
	 * 获取当前时间日历
	 */
	public Date getStart() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		// 将小时至0
		c.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		c.set(Calendar.MINUTE, 0);
		// 将秒至0
		c.set(Calendar.SECOND, 0);
		// 将毫秒至0
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 获取用户登录权限树，包括子类权限  主页左边菜单树，  订单查询判断权限
	 */
	public List<Menu> getUserMenus() {
		Set<Menu> menuList = new TreeSet<Menu>();
		Users user= getLonginName();
		Set<Menu> haveSet = getMenu();	//获取已有权限
		for(Groups groups : user.getGroupsList()) {
			for(Menu menu :groups.getMenuList()) {
				//if(menu.getParentid()==0)System.out.println(menu.getName());
				menuList.add(menu);
				List<Menu> addList = new ArrayList<Menu>();
				removeSon(menu.getMenuList(), haveSet, addList);//查找子类是否有权限，没有权限剔除。
				menu.setMenuList(addList);
			}
		}
		return new ArrayList<Menu>(menuList);
	}
	
	private void removeSon(List<Menu> menuList,Set<Menu> haveSet,List<Menu> addList) {
		for(Menu menu :menuList) {
			for(Menu m :  haveSet) {
				if(menu.getId()==m.getId()) {
					List<Menu> add = new ArrayList<Menu>();
					if(m.getMenuList()!=null)removeSon(m.getMenuList(), haveSet, add);
					m.setMenuList(add);
					addList.add(m);
					break;
				}
			}
		}
	}
	
	public static void uploadOSS(OssSetup ossSetup,InputStream inputStream,String filename) {
    	OSSClient ossClient = new OSSClient(ossSetup.getEndpoint(), ossSetup.getAccessKeyId(), ossSetup.getAccessKeySecret());
    	ossClient.putObject(ossSetup.getBucketName(),filename,inputStream);
    	ossClient.shutdown();
	}
	
	public String getCode(){
		try {
			
			String path = request.getSession().getServletContext().getRealPath("/code") + "/";
			
        	File dir = new File(path);
    	    int w = 200, h = 80;
    		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
    		
    		request.getSession().setAttribute("code", verifyCode);
    		
    		String name= UUID.randomUUID() + ".jpg";
            File file = new File(dir, name);
			VerifyCodeUtils.outputImage(w, h, file, verifyCode);
			return name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ServletContext getServletContext(){
		ServletContext servletContext=request.getSession().getServletContext(); 
		return servletContext;
	}
	

	static Logger log = Logger.getLogger(BaseController.class);
	
	public static String toJson(Object obj){
		if(obj!=null){
			JSONObject jsonArray = IgnoreFieldProcessorImpl.JsonConfig(null,obj); 
			String json = jsonArray.toString();
			return json;
		}
		return null;
	}
	
	public static String toJson2(Object obj){
		if(obj!=null){
			JSONObject jsonArray = IgnoreFieldProcessorImpl.JsonConfig(null,obj); 
			String json = jsonArray.toString();
			StringBuffer retValue = new StringBuffer();
			retValue.append("{").append("\"totalCount\":").append(0).append(",")
            .append("\"obj\":").append(json).append(",")
            .append("\"totalPage\":").append(0).append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toListJson(List<Object> obj){
		if(obj!=null){
			JSONArray jsonArray = JSONArray.fromObject(obj); 
			String json = jsonArray.toString();
			return json;
		}
		return null;
	}
	
	/**
	 * 获取当前登录用户全部信息
	 */
	public Users getLonginName() {
		HttpSession session = request.getSession();
		Map<String,Class<?>> classMap = new HashMap<String,Class<?>>();
    	classMap.put("groupsList", Groups.class);
    	classMap.put("menuList", Menu.class);
		JSONObject jsonobject = JSONObject.fromObject(session.getAttribute("user"));
		return (Users)JSONObject.toBean(jsonobject,Users.class,classMap);
	}
	
	/**
	 * 获取登录的用户  有权限的模块
	 */
	public Set<Menu> getMenu() {
		HttpSession session = request.getSession();
		return toMenuList(session.getAttribute("userMenu").toString());
	} 
	
	private static Set<Menu> toMenuList(String s){
		Set<Menu> list = new TreeSet<Menu>();
		JSONArray jsonArray = JSONArray.fromObject(s);
		for(int i=0;i<jsonArray.size();i++) {
			JSONObject menu = JSONObject.fromObject(jsonArray.get(i));
			list.add((Menu) JSONObject.toBean(menu, Menu.class));
		}
		return list;
	}
	
	/**
	 * 获取指定权限
	 * @param name
	 * @return
	 */
	public Menu getNameMenu(String name) {
		Set<Menu> set = getMenu();
		for(Menu menu: set) {
			if(menu.getName().equals(name))return menu;
		}
		return null;
	}
	
	public String toListJson(Object obj,int count){
		if(obj!=null){
			JSONArray listj= IgnoreFieldProcessorImpl.ArrayJsonConfig(null, obj);  
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (count+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(count).append(",")
	                .append("\"obj\":").append(json).append(",")
	                .append("\"totalPage\":").append(pageNo).append(",")
	                .append("\"state\":").append(true).append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toListJsonIgnoreTime(Object obj,int count,Map<String,Object> map){
		if(obj!=null){
			JSONArray listj= IgnoreFieldProcessorImpl.ArrayJsonConfig(null, obj);  
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (count+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(count).append(",")
	                .append("\"obj\":").append(json).append(",")
	                .append("\"totalPage\":").append(pageNo).append(",");
	                if(map!=null) {
	                	for (Map.Entry<String, Object> m : map.entrySet()) {
	                		retValue.append("\""+m.getKey()+"\":").append(toJson(m.getValue())).append(",");
                	    }
	                }
	                retValue.append("\"state\":").append(true).append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toListJson(Object obj,int count,String[] str){
		if(obj!=null){
			JSONArray listj= JSONArray.fromObject(obj,com.pay.util.StringUtils.getJsonConfig(str))  ;
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (count+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(count).append(",")
	                .append("\"obj\":").append(json).append(",")
	                .append("\"totalPage\":").append(pageNo).append(",")
	                .append("\"state\":").append(true).append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toListJson(Object obj,int count,Map<String,Object> map){
		if(obj!=null){
			
			JSONArray listj= JSONArray.fromObject(obj);
			
			String json = listj.toString();
			
			 // String json=toListJson(obj,0);
			
			
			StringBuffer retValue = new StringBuffer();
			int pageNo = (count+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(count).append(",")
	                .append("\"obj\":").append(json).append(",");
	                retValue.append("\"totalPage\":").append(pageNo).append(",");
	                if(map!=null) {
	                	for (Map.Entry<String, Object> m : map.entrySet()) {
	                		retValue.append("\""+m.getKey()+"\":").append(toJson(m.getValue())).append(",");
                	    }
	                }
	                retValue.append("\"state\":").append(true).append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String list2json(List<Active> obj,int count,Map<String,Object> map){
		if(obj!=null){
			com.alibaba.fastjson.JSONArray listj= JsonTools.getJSONArrayByList(obj);
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (count+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(count).append(",")
	                .append("\"obj\":").append(json).append(",");
	                retValue.append("\"totalPage\":").append(pageNo).append(",");
	                if(map!=null) {
	                	for (Map.Entry<String, Object> m : map.entrySet()) {
	                		retValue.append("\""+m.getKey()+"\":").append(toJson(m.getValue())).append(",");
                	    }
	                }
	                retValue.append("\"state\":").append(true).append("}");
			return retValue.toString();
		}
		return null;
	}
	/**
	 * 查询用户是否有 列表数据按钮权限。
	 * @param list
	 */
	public Map<String,Boolean> getDateBut(ButClick[] vlas) {
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		Set<Menu> menuSet = getMenu();
		for(ButClick v :vlas) {
			for(Menu m : menuSet) {
				if(v.getText().equals(m.getName())) {
					map.put(v.getCode(),true);
					break;
				}
			}
		}
		return map;
	}
	
	/**
	 * 页面的功能按钮、
	 * @return
	 */
	public Map<String,Object> button(ButClick[] vlas){
		Map<String,Object> map = new HashMap<String, Object>();
		for(ButClick v :vlas) {
			Menu menu = getNameMenu(v.getText());
			if(menu!=null && v.getText().equals(menu.getName())) {
				map.put(v.getCode(), new ViewButton(menu.getId(), true,v.getText()));
			}
		}
		return map;
	}
	
	public String toListJson(Object obj,PayOrderCount poc){
		if(obj!=null){
			JSONArray listj= IgnoreFieldProcessorImpl.ArrayJsonConfig(null, obj);  
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (poc.getCount()+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(poc.getCount()).append(",")
	                .append("\"obj\":").append(json).append(",")
	                .append("\"totalPage\":").append(pageNo).append(",")
	                .append("\"state\":").append(true).append(",")
	                .append("\"PayOrderCount\":").append(toJson2(poc))
	                .append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toListJson(Object obj,PayOrderCount poc,Map<String,Object> map){
		if(obj!=null){
			JSONArray listj= IgnoreFieldProcessorImpl.ArrayJsonConfig(null, obj);  
			String json = listj.toString();
			StringBuffer retValue = new StringBuffer();
			int pageNo = (poc.getCount()+pageSize-1)/pageSize;
			pageNo = pageNo==0?1:pageNo;
	        retValue.append("{").append("\"totalCount\":").append(poc.getCount()).append(",")
	                .append("\"obj\":").append(json).append(",")
	                .append("\"totalPage\":").append(pageNo).append(",")
	                .append("\"state\":").append(true).append(",");
	                if(map!=null) {
	                	for (Map.Entry<String, Object> m : map.entrySet()) {
	                		retValue.append("\""+m.getKey()+"\":").append(toJson(m.getValue())).append(",");
                	    }
	                }
	                retValue.append("\"PayOrderCount\":").append(toJson2(poc))
	                .append("}");
			return retValue.toString();
		}
		return null;
	}
	
	public String toMsg(String msg,Boolean state){
		if(!StringUtils.isBlank(msg) && state!=null){
			JSONArray jsonArray = JSONArray.fromObject(new ReturnBan(msg,state)); 
			String json = jsonArray.toString();
			return json;
		}
		return null;
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

}
