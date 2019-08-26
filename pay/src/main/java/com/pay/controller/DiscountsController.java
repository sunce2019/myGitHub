package com.pay.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.pay.dao.BaseDao;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.model.Active;
import com.pay.model.Discounts;
import com.pay.model.SpeildKV;
import com.pay.model.SystConfig;
import com.pay.service.SystemService;
import com.pay.util.CodeUtil;
import com.pay.util.GoogleAuthenticator;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * 优惠申请
 * @author acer
 *
 */
@Controller
public class DiscountsController extends BaseController{
	@Autowired
	private BaseDao<Discounts> discountsDao;
	@Autowired
	private SystemService systemService;
	
	/**
	 * 首面跳转
	 */
	@RequestMapping("/discounts")
	public String discounts() {		
		return "discountsList";
	}	
    /**
     * 获取列表
     */
	@RequestMapping("/discountsList")
	@ResponseBody
	public String discountsList(int pageNo,String theme,String vipAccount) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Discounts.class);
			  if(!StringUtils.isBlank(theme)) 
				  dc.add(Restrictions.eq("theme", theme));	
			  if(!StringUtils.isBlank(vipAccount)) 
				  dc.add(Restrictions.eq("vipAccount", vipAccount));	
			  List<Discounts> list = discountsDao.findList(dc, pageNo, pageSize);			  
			  setDiscountsBut(list); 
			  int count = discountsDao.count(dc); 
			  return toListJsonIgnoreTime(list, count,button(new ButClick[] {ButClick.UPDDISCOUNT,ButClick.DELDISCOUNT}));	
			/* return JSONArray.fromObject(list).toString(); */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";		
	}
	/**
	 * 设置按钮
	 */
	private void setDiscountsBut(List<Discounts> list) {
		Map<String,Boolean> map = getDateBut(new ButClick[] {ButClick.UPDDISCOUNT,ButClick.DELDISCOUNT});
		if(map.isEmpty())return;
		for(Discounts b :list) {
			if(map.containsKey(ButClick.UPDDISCOUNT.getCode()))b.setEditClick(true);
			if(map.containsKey(ButClick.DELDISCOUNT.getCode()))b.setDelClick(true);
		}
	}			
	/**
	 * 详情页
	 */
	@RequestMapping("/discountsInfoPage")
	public String discountsUpdatePage(Integer id,Model model){		
		if(id!=null && id>0) {
			Discounts discounts = discountsDao.get(Discounts.class, id);					 
			model.addAttribute("discounts", discounts);
		}
		return "discountsDetail";
	}
	
		/**
		 * 详情页
		 */
    @RequestMapping("/discountsInfo")
	@ResponseBody
	private String discountsInfo(Integer id,Model model){
		List<SpeildKV> list=new ArrayList<>();
		Map<String,Object> map=new HashMap<String,Object>();
		if(id!=null && id>0) {
			Discounts discounts = discountsDao.get(Discounts.class, id);				
			map.put("id", discounts.getId());
			map.put("theme", discounts.gettheme());
			map.put("disTime", discounts.getDisTime());				
			map.put("status", discounts.getstatus());
			map.put("vipAccount", discounts.getvipAccount());
			String sf=discounts.getspecialField();
			if(sf!=null) {
				String b=sf.substring(1, sf.length()-1);				
			    String s[] = b.split(",");				
				for(int i=0;i<s.length;i++) {				
					Map maps = (Map)JSON.parse(s[i]);
					 for (Object map1 :maps.entrySet()) {
						 SpeildKV skv=new SpeildKV();
						 skv.setKey(((Map.Entry)map1).getKey().toString());
						 skv.setValue(((Map.Entry)map1).getValue().toString());
						 list.add(skv);
					 }
				}				
			  }	
		    map.put("list", list);	       
		}
		 return JSON.toJSONString(map);
	}
	
	/**
	 * 删除优惠单
	 */
	@RequestMapping(value="/deletediscounts")  
	@ResponseBody
    public ReturnBan deleteactive(Integer id,String code){		
		try {
			if(StringUtils.isBlank(code))return new ReturnBan("请输入验证码", false);
			Discounts ds=new Discounts();
			ds.setId(id);	
			discountsDao.delete(ds);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}	
	/**
	 * 更新优惠单
	 */
	@RequestMapping(value="/updateDiscounts")  
	@ResponseBody
    public ReturnBan updateActive(Integer id,Integer status,String code) {
	     try {
	    	ReturnBan returnBan = gugeyzq(code);
			if(returnBan!=null)return returnBan;
			Discounts ds=discountsDao.get(Discounts.class, id);
			if(ds!=null) {  //更新											
				ds.setstatus(status);
				discountsDao.update(ds);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}


	/**
	 * 新增优惠申请
	 */	
	@RequestMapping(value="/addDiscounts") 
	@ResponseBody
   public ReturnBan addDiscounts(String inpuValues,String ztTitle,String memName,String yzmCode,HttpServletRequest req){	
		try {
			if(StringUtils.isBlank(yzmCode))return new ReturnBan("请输入验证码", false);
			if(!verifyDiscounts(yzmCode,req))return new ReturnBan("验证码错误", false);
			if(StringUtils.isBlank(memName))return new ReturnBan("请输入会员账号", false);
			//验证账号			
			//********************************
			Discounts ds=new Discounts();
			String sf="[";
			if(inpuValues!=null) {
				String[] strs=inpuValues.split(",");
				for(String s:strs) {
					String[] ss=s.split(":");
					sf+="{\""+ss[0]+"\":"+"\"";
				    sf+=ss[1];
				    sf+="\"},";
				}
			}
			String sf1=sf.substring(0, sf.length()-1);
			sf1+="]";
			ds.settheme(ztTitle);
			ds.setvipAccount(memName);
			//设置特殊字段
			ds.setspecialField(sf1);
			//设置状态
			ds.setstatus(0);
            //设置申请时间
            Date d=new Date(); 
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            ds.setDisTime(sdf.format(d));
			discountsDao.save(ds);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}


	/**
	 * 优惠申请进度查询
	 */
	@RequestMapping("/discountsPlan")
	@ResponseBody
	public String discountsPlan(String vipAccount,String theme) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Discounts.class);
			  if(!StringUtils.isBlank(vipAccount)) 
				  dc.add(Restrictions.eq("vipAccount", vipAccount));			      			  
			  if(!StringUtils.isBlank(theme)) 
				  dc.add(Restrictions.eq("theme", theme));
			  List<Discounts> list = discountsDao.findList(dc, 1, pageSize);			  
			  return JSONArray.fromObject(list).toString();	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";		
	}
	
	/**
	 * 特殊字段列表加载
	 */
	@RequestMapping("/discountsSpeldLoad")
	@ResponseBody
	public String discountsSpeldLoad(Integer id){
		List<SpeildKV> list = new ArrayList<SpeildKV>();		
		if(id!=null && id>0) {
			Discounts discounts = discountsDao.get(Discounts.class, id);
			String sf=discounts.getspecialField();
			if(sf!=null) {
			String b=sf.substring(1, sf.length()-1);				
		    String s[] = b.split(",");
			for(int i=0;i<s.length;i++) {				
				Map maps = (Map)JSON.parse(s[i]);
				 for (Object map :maps.entrySet()) {
				    Map<String,String> m=new HashMap<String,String>();
				    SpeildKV skv=new SpeildKV();
				    skv.setKey(((Map.Entry)map).getKey().toString());
				    skv.setValue(((Map.Entry)map).getValue().toString());
				    //m.put(((Map.Entry)map).getKey().toString(),((Map.Entry)map).getValue().toString()); 
				    list.add(skv);
				  }
			}			
	        return  JSONArray.fromObject(list).toString();
			}
		}			
		return "[]" ;
	}
	
	/**
	 * 验证码对比
	 */
	public boolean verifyDiscounts(String code,HttpServletRequest request) {
		//调用cookie比较验证码
		Cookie[] cookies=request.getCookies();
		for(Cookie c:cookies) {
			if("verifyCode".equals(c.getName())) {
				String codeValue=c.getValue();
				if((codeValue.toLowerCase()).equals(code.toLowerCase())) {
					c.setMaxAge(0);//验证成功删除
					return true;				
				}else {
					return false;
				}
			}
		}
		return false;
		
	}
			
	/***********
	 * 获取验证码
	 ***********/
	@RequestMapping("/getVerifyCode")
	public void getVerifyCode(HttpServletResponse response,HttpServletRequest request) {
		 try {
             int width=200;
             int height=69;
             //生成对应宽高的初始图片
             BufferedImage verifyImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
             //生成验证码字符并加上噪点，干扰线，返回值为验证码字符   
             String randomText =CodeUtil.drawRandomText(width,height,verifyImg);
             //将验证码保存在session中              
		     request.getSession().setAttribute("verifyCode", randomText);
		     Cookie c = new Cookie("verifyCode",randomText);// 新建一个Cookie对象
		     c.setMaxAge(60); // 设置过期时间1分钟，以秒为单位		     
		     //跨域
		    // c.setDomain(".agx.com");               // 设置域名
		    // c.setPath("/");                        // 设置路径
		    // c.setMaxAge(Integer.MAX_VALUE);        // 设置有效期为永久		     
		     response.addCookie(c); // 保存cookie到客户端		     
		     //必须设置响应内容类型为图片，否则前台不识别
		     response.setContentType("image/png");
		     //获取文件输出流    	
		     OutputStream os = response.getOutputStream(); 
		     //输出图片流		
		     ImageIO.write(verifyImg,"png",os);
		     os.flush();
		     os.close();//关闭流
       } catch (IOException e) {
             e.printStackTrace();
       }		
	}
	
	/**
	 * 特殊字段拼接
	 */
	public String replace(String sr) {			
			if(sr!=null) {
			String sf="[";
			String s[] = sr.split(",");			 
				for(int i=0;i<s.length;i++) {				
				    sf+="{\"a\":"+"\"";
				    sf+=s[i];
				    sf+="\"}";
				    if(i<s.length-1) {
				    	sf+=",";
				    }
				}	
				sf+="]";
				return sf;
			}
			return null;
		}

}
