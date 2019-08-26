package com.pay.controller;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dao.BaseDao;
import com.pay.dao.MemberDao;
import com.pay.dao.RedisModel;
import com.pay.enums.ConfigCode;
import com.pay.model.Groups;
import com.pay.model.Menu;
import com.pay.model.SystConfig;
import com.pay.model.Users;
import com.pay.service.SystemService;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;
import com.pay.vo.Member;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@CrossOrigin(allowCredentials="true",origins = "http://127.0.0.1",maxAge = 3600)
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
	//private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("busi1");
	
	@Autowired
	private  HttpServletRequest request;
	@Autowired
	private BaseDao<Users> usersDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private SystemService systemService;
	@Autowired
	private RedisModel redisModel;
	@Resource(name="sessionRepository") 
	private SessionRepository<ExpiringSession> sessionRepository;
	
	@RequestMapping(value="/login") 
	public @ResponseBody ReturnBan login(String loginname,String pwd,String Vcode){
		try {
			DetachedCriteria dc2 = DetachedCriteria.forClass(Users.class);
			dc2.add(Restrictions.eq("loginname", loginname));
			dc2.add(Restrictions.eq("password", pwd));
			Users proxy =  (Users) usersDao.findObj(dc2);
			if(proxy!=null &&proxy.getFlag()==1) {		
				if(!authcode(Vcode, proxy.getKey(),proxy.getLoginname()))return new ReturnBan("谷歌验证码错误",false);
				 
				HttpSession session = request.getSession();  
				
				//user groupsList 集合采用懒加载方式，在拦截器当中获取 groupsList session关闭
				//采用存储json数据，不进行多次查询
				jurisdiction(session, proxy);
				session.setMaxInactiveInterval(3600);
				memberDao.delete("sessionid"+proxy.getLoginname());
				
				if(memberDao.add(new Member("sessionid"+proxy.getLoginname(), session.getId()))) {	//保存session,只能登录一个地方，比如 谷歌 IE 
					return new ReturnBan("登录成功", true);
				}
			}else {
				return new ReturnBan("用户名或密码错误", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统繁忙,请稍后重试!", false);
	}
	
	/**
	 * 校验谷歌验证
	 */
	private boolean authcode(String code,String key,String loginname) throws Exception {
		SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.AUTHCODE.getCode(),1));
		if(systConfig==null)return true;
		if(GoogleAuthenticator.authcode(code,key)) {
			systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.GOOGLETIME.getCode(),1));
			if(systConfig==null)return true;
			int time = Integer.parseInt(systConfig.getContent());
			String namekey = loginname+"Google";
			//设置谷歌验证器有效期位10分钟
			redisModel.redisTemplate.opsForValue().set(namekey,"",1000*60*time,TimeUnit.MILLISECONDS);
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户权限并将用户保存到session
	 */
	private void jurisdiction(HttpSession session,Users proxy) {
		getMenuMap(session, proxy);
		session.setAttribute("user",JSONObject.fromObject(proxy).toString());
	}
	/**
	 * 将用户所有权限放入一个集合
	 */
	private void getMenuMap(HttpSession session,Users users) {
		String customerID = "";	//登录用户所属 id，用来 订单查询 用户相关 
		Set<Menu> list = new TreeSet<Menu>();
		for(Groups groups : users.getGroupsList()) {
			for(Menu m1 : groups.getMenuList()) {
				//if(m1.getId()==184||m1.getId()==211) {
				//	System.out.println("");
				//}
			if(list.add(m1) && m1.getCustomer()!=null)customerID += m1.getCustomer().getId()+",";
			}
		} 
		if(!customerID.equals(""))
			session.setAttribute("userCustomerID",customerID.substring(0, customerID.length()-1));
		//这里吧 子类模块全部移除了。这个集合只保存 用户登录有哪些功能权限。
		session.setAttribute("userMenu",JSONArray.fromObject(list,StringUtils.getJsonConfig(new String[]{"menuList"})).toString());
	}
	
	
	
	public static void main(String[] args) {
		System.out.println("a59c300feb06dc24673cabe87d99ccc6".toUpperCase());
	}
	
	public String index(){
		return "index";
	}
}
