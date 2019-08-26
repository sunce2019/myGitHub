 package com.pay.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dao.BaseDao;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.enums.CustomerState;
import com.pay.model.Bank;
import com.pay.model.BankBusiness;
import com.pay.model.Business;
import com.pay.model.Customer;
import com.pay.model.Groups;
import com.pay.model.GroupsMenu;
import com.pay.model.GroupsUser;
import com.pay.model.PayOrder;
import com.pay.model.SystConfig;
import com.pay.model.Users;
import com.pay.service.SystemService;
import com.pay.util.ReturnBan;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.IgnoreFieldProcessorImpl;
import com.pay.util.KeyPairGenUtil;
import com.pay.util.QRCode;
import com.pay.util.StringUtils;
import com.pay.vo.Member;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

/**
*@author star
*@version 创建时间：2019年4月9日下午6:34:51
*/
@Controller
@RequestMapping("/user")
public class UsersController extends BaseController{
	@Autowired
	private SystemService systemService;
	@Autowired
	private BaseDao<Users> usersDao;
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<Groups> groupsDao;
	@Autowired
	private BaseDao<GroupsUser> groupsuserDao;
	
	@RequestMapping("/user")
	public String user(){
		return "userList";
	}
	
	@RequestMapping(value="/saveUser")  
	@ResponseBody
    public ReturnBan saveUser(Integer id,String loginname,String password,String name,Integer flag,Integer[] groups,String code){
		return saveOrUpdateUser(id, loginname, password, name, flag, groups, code);
	}
	
	@RequestMapping(value="/updateUser")  
	@ResponseBody
    public ReturnBan updateUser(Integer id,String loginname,String password,String name,Integer flag,Integer[] groups,String code){
		return saveOrUpdateUser(id, loginname, password, name, flag, groups, code);
	}
    private ReturnBan saveOrUpdateUser(Integer id,String loginname,String password,String name,Integer flag,Integer[] groups,String code){
		try {
			ReturnBan returnBan = gugeyzq(code);
			if(returnBan!=null)return returnBan;
			
			if(StringUtils.isBlank(name))
				return new ReturnBan("请输入小名", false);
			if(flag==null||flag==0)
				return new ReturnBan("请选择状态", false);
			if(id==null||id==0) {
				if(StringUtils.isBlank(loginname))
					return new ReturnBan("请输入账号", false);
				
				DetachedCriteria dc = DetachedCriteria.forClass(Users.class);
				dc.add(Restrictions.eq("name",name));
				Users users = usersDao.findObj(dc);
				if(users!=null)
					return new ReturnBan("小名已存在", false);
				
				String key = "UYSHQELNXLO4WAZL";
				String url = "https://www.google.com/chart?chs=200x200&chld=M%7C0&cht=qr&chl=otpauth://totp/KF@HeChengPay%3Fsecret%3DUYSHQELNXLO4WAZL";
//				if(type==3) {
//					Map<String,String> map = GoogleAuthenticator.genSecret(loginname);
//					key = map.get("key");
//					url = map.get("url");
//				}
				String customer_name = "";
				Serializable serializable = usersDao.save(new Users(loginname,"123456",name,99,flag,key,url,0,customer_name));
				for(int ids : groups) {
					groupsuserDao.save(new GroupsUser((Integer)serializable,ids));
				}
			}else {
				Users users = usersDao.get(Users.class, id);
				users.setName(name);
				users.setFlag(flag);
				users.setCustomer_id(0);
				
				DetachedCriteria dc = DetachedCriteria.forClass(GroupsUser.class);
				dc.add(Restrictions.eq("userid",users.getId()));
				List<GroupsUser> list = groupsuserDao.findList(dc);
				for(GroupsUser g :list) {
					groupsuserDao.delete(g);
				}
				for(int ids : groups) {
					groupsuserDao.save(new GroupsUser(users.getId(),ids));
				}
				
				if(!StringUtils.isBlank(password) && password.length()<6)
					return new ReturnBan("密码最少6位", false);
				if(!StringUtils.isBlank(password))
					users.setPassword(password);
				usersDao.update(users);
			}
			
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
    }
	
	@RequestMapping(value="/groupsLists")  
	@ResponseBody
    public String groupsList(Integer id){
		try {
			
			DetachedCriteria dc = DetachedCriteria.forClass(Groups.class);
			List<Groups> list = groupsDao.findList(dc);
			JSONArray jsonArray = JSONArray.fromObject(list,StringUtils.getJsonConfig(new String[] {"menuList"}));
			if(id!=null && id>0) {	//编辑 用户已赋予的角色 选中
				Users user = usersDao.get(Users.class, id);
				for(int i=0;i<jsonArray.size();i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					jsonObject.put("state",false);
					for(Groups groups: user.getGroupsList()) {
						if(jsonObject.getString("id").equals(Integer.toString(groups.getId()))) {
							jsonObject.put("state",true);
						}
					}
				}
			}
			
			return jsonArray.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
    }
	
	
	@RequestMapping(value="/updatepassword")  
	@ResponseBody
    public ReturnBan updatepassword(String password1,String password2,String password3){
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Users.class);
			dc.add(Restrictions.eq("password",password1));
			dc.add(Restrictions.eq("loginname",getLonginName().getLoginname()));
			Users users = usersDao.findObj(dc);
			if(users!=null) {
				if(!StringUtils.equals(password2, password3))
					return new ReturnBan("新密码不相同", false);
				users.setPassword(password2);
				usersDao.update(users);
				return new ReturnBan("修改密码成功", true);
			}else {
				return new ReturnBan("原密码错误", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
    }
	
	private String userDetail(Integer id,Model model){
		if(id!=null && id>0) {
			Users users = usersDao.get(Users.class, id);
			model.addAttribute("users", users);
		}
		getGroups(id, model,null);
		return "userDetail";
	}
	
	@RequestMapping("/userUpdatePage")
	public String userUpdatePage(Integer id,Model model){
		return userDetail(id, model);
	}
	
	@RequestMapping("/userAddPage")
	public String userAddPage(Integer id,Model model){
		return userDetail(null, model);
	}
	
	@RequestMapping("/userDetails")
	@ResponseBody
	public ReturnBan userDetails(Integer id,Model model){
		Map<String,Object> map = new HashMap<String, Object>();
		if(id!=null && id>0) 
			map.put("users", usersDao.get(Users.class, id));
		getGroups(id, model,map);
		return new ReturnBan("", true, map);
	}
	
	@RequestMapping("/getLoginNames")
	@ResponseBody
	public ReturnBan getLoginNames(Model model){
		Users user = getLonginName();
		user.setPassword("");
		user.setKey("");
		user.setUrl("");
		return new ReturnBan("", true, user);
	}
	
	private void getGroups(Integer id,Model model,Map<String,Object> map) {
		DetachedCriteria dc = DetachedCriteria.forClass(Groups.class);
		dc.add(Restrictions.eq("userid", id));
		List<Groups> groupsList = groupsDao.findList(dc);
		
		DetachedCriteria dc2 = DetachedCriteria.forClass(Customer.class);
		List<Customer> customerList = customerDao.findList(dc2);
		Set<Member> member = new HashSet<Member>();
		for(Customer c :customerList) {
			boolean flag = false;
			if(id!=null && id>0) {
				for(Groups groups: groupsList) {
					if(groups.getCustomerid()==c.getId()) {
						flag=true;break;
					}
				}
			}
			member.add(new Member(Integer.toString(c.getId()),c.getName(),flag));
		}
		
		model.addAttribute("customerList", member);
		if(map!=null)map.put("", member);
	}
	
	@RequestMapping("/updateDetail")
	public String userDetail2(Model model){
		model.addAttribute("users", getLonginName());
		return "updateDetail";
	}
	
	@RequestMapping("/updateDetails")
	@ResponseBody
	public ReturnBan userDetail2s(Model model){
		return new ReturnBan("", true, getLonginName());
	}
	
	@RequestMapping(value="/userList")  
	@ResponseBody
    public String userList(int pageNo,String loginname,String name,Integer type,Integer customer_id){
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Users.class);
			if(!StringUtils.isBlank(name))
				dc.add(Restrictions.eq("name", name));
			if(!StringUtils.isBlank(loginname))
				dc.add(Restrictions.eq("loginname", loginname));
			if(type!=null && type>0)
				dc.add(Restrictions.eq("type", type));
			if(customer_id!=null && customer_id>0)
				dc.add(Restrictions.eq("customer_id", customer_id));
			List<Users> list = usersDao.findList(dc, pageNo, pageSize);
			
			
			setUsersBut(list);
			int count = usersDao.count(dc);
			return toListJson(list, count,button(new ButClick[] {ButClick.ADDUSER}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
    }
	
	private void setUsersBut(List<Users> list) {
		Map<String,Boolean> map = getDateBut(new ButClick[] {ButClick.UPDUSER});//
		if(map.isEmpty())return;
		for(Users b :list) {
			if(map.containsKey(ButClick.UPDUSER.getCode()))b.setEditClick(true);
		}
	}
	
	@RequestMapping("/out")
	public String out(Model model){
		request.getSession().removeAttribute("user");
		request.getSession().removeAttribute("userCustomerID");
		return "redirect:/hilogin.jsp";
	}
}
