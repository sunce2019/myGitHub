package com.pay.controller;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pay.model.Active;
import com.pay.util.ReturnBan;
import com.pay.util.StringUtils;

import net.sf.json.JSONArray;

/**
 *    活动管理
 * @author acer
 *
 */
@Controller
public class ActiveController extends BaseController{
	
	@Autowired
	private BaseDao<Active> activeDao;
	@Autowired
	private Active active;
	/**
	 * 页面跳转
	 */
	@RequestMapping("active")
	public String toActiveJsp() {	
		return "activeList";
	}
	/**
	 * 获取活动列表
	 */
	@RequestMapping("/activeList")
	@ResponseBody
	public String activeList(int pageNo,String title,String startTime,String endTime,String status) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Active.class);
			  if(!StringUtils.isBlank(title)) 
				  dc.add(Restrictions.eq("title", title));			  
			  //将字符串转Date类型
			  DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");          			  
			  if(!StringUtils.isBlank(startTime)) 
				  dc.add(Restrictions.ge("startTime", format.parse(startTime)));
			  if(!StringUtils.isBlank(endTime)) 
				  dc.add(Restrictions.le("endTime",  format.parse(endTime)));
			  if(!"-1".equals(status)) {
				  if("0".equals(status))
				     dc.add(Restrictions.eq("status", false));
				  if("1".equals(status))
					 dc.add(Restrictions.eq("status", true));
			  }
			  List<Active> list = activeDao.findList(dc, pageNo, pageSize);			  
			  setActiveBut(list); 
			  int count = activeDao.count(dc); 
			  return toListJsonIgnoreTime(list, count,button(new ButClick[] {ButClick.ADDUSER}));			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";		
	}
	
	/**
	 * 获取活动列表
	 */
	@RequestMapping("/pageActiveList")
	@ResponseBody
	public String pageActiveList(Integer pageNo,Integer pageSize) {
		try {
			  DetachedCriteria dc = DetachedCriteria.forClass(Active.class);	
			  List<Active> list = activeDao.findList(dc);	
			  if(list!=null) {
				  return JSONArray.fromObject(list).toString();	
			  }
			  	  return null;	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";		
	}
	
	
	/**
	 * 设置按钮
	 * @param list
	 */
	private void setActiveBut(List<Active> list) {
		Map<String,Boolean> map = getDateBut(new ButClick[] {ButClick.UPDACTIVE,ButClick.DELACTIVE});
		if(map.isEmpty())return;
		for(Active b :list) {
			if(map.containsKey(ButClick.UPDACTIVE.getCode()))b.setEditClick(true);
			if(map.containsKey(ButClick.DELACTIVE.getCode()))b.setDelClick(true);
		}
	}	
	
	/**
	 * 编辑页面，获取数据详细
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/activeAddPage")
	public String userAddPage(Integer id,Model model){
		return activeDetail(null, model);
	}
	
	
	@RequestMapping("/activeUpdatePage")
	public String activeUpdatePage(Integer id,Model model){		
		return activeDetail(id, model);
	}
	private String activeDetail(Integer id,Model model){
		List<Map<String,String>> list=new ArrayList<>();
		if(id!=null && id>0) {
			Active active = activeDao.get(Active.class, id);
			String sf=active.getSpecialField();
			if(sf!=null&&!"".equals(sf)) {
				String b=sf.substring(1, sf.length()-1);				
			    String s[] = b.split(",");
				Map<String,String> m=new HashMap<String,String>(); 
				for(int i=0;i<s.length;i++) {				
					Map maps = (Map)JSON.parse(s[i]);
					 for (Object map :maps.entrySet()) {
						  m.put(((Map.Entry)map).getKey().toString(),((Map.Entry)map).getValue().toString());
					      list.add(m);
					 }
				}
				active.setspecialList(list);
			}						 
			model.addAttribute("active", active);
		}
		return "activeDetail";
	}
	

	//获取活动详情
	@RequestMapping("/activeDetailInfo")
	@ResponseBody
	private String activeDetailInfo(Integer id,Model model){
		List<Map<String,String>> list=new ArrayList<>();
		Map<String,Object> map=new HashMap<String,Object>();
		if(id!=null && id>0) {
			Active active = activeDao.get(Active.class, id);			
			map.put("content", active.getcontent());
			map.put("details", active.getdetails());
			map.put("title", active.gettitle());
			map.put("endTime", active.getendTime().toString());
			map.put("startTime", active.getstartTime().toString());
			map.put("img",active.getImg());
			map.put("sort",active.getsort());
			map.put("status",active.getStatus());
			String sf=active.getSpecialField();
			if(sf!=null) {
				String b=sf.substring(1, sf.length()-1);				
			    String s[] = b.split(",");				
				for(int i=0;i<s.length;i++) {				
					Map maps = (Map)JSON.parse(s[i]);
					 for (Object map1 :maps.entrySet()) {
						 Map<String,String> m=new HashMap<String,String>(); 
						  m.put(((Map.Entry)map1).getKey().toString(),((Map.Entry)map1).getValue().toString());
						  list.add(m);
					 }
				}				
			   }	
		    map.put("list", list);	       
		}
		 return JSON.toJSONString(map);
	}
	
	
	@RequestMapping("/activeModelLoad")
	@ResponseBody
	public String activeModelLoad(Integer id){
		List<Map<String,String>>list = new ArrayList<Map<String,String>>();
		
		if(id!=null && id>0) {
			Active active = activeDao.get(Active.class, id);
			String sf=active.getSpecialField();
			if(sf!=null&&!"".equals(sf)) {
			String b=sf.substring(1, sf.length()-1);				
		    String s[] = b.split(",");
			for(int i=0;i<s.length;i++) {				
				Map maps = (Map)JSON.parse(s[i]);
				 for (Object map :maps.entrySet()) {
				    Map<String,String> m=new HashMap<String,String>();
				    m.put(((Map.Entry)map).getKey().toString(),((Map.Entry)map).getValue().toString()); 
				    list.add(m);
				  }
			}
	        return  JSONArray.fromObject(list).toString();
			}
		}			
		return "[]" ;
	}
	

	/**
	 * 新增活动
	 */
	@RequestMapping(value="/addActive")  
	@ResponseBody
    public ReturnBan addActive(Integer id,String title,String content,String details,String startTime,String endTime,Integer sort,String code,String  list,String img,String status){
		String sf=replace(list);
		return saveOrUpdateActive(id, title, content,details, startTime, endTime, sort,code,sf,img,status);
	}
	
	/**
	 * 修改活动
	 * @return
	 */
	@RequestMapping(value="/updateActive")  
	@ResponseBody
    public ReturnBan updateActive(Integer id,String title,String content,String details,String startTime,String endTime,Integer sort,String code,String list,String img,String status){
		String sf=replace(list);
		return saveOrUpdateActive(id, title, content,details, startTime, endTime, sort,code,sf,img,status);
	}

	/**
	  * 新增或更改活动信息
	 * @return
	 */
	private ReturnBan saveOrUpdateActive(Integer id,String title,String content,String details,String startTime,String endTime,Integer sort,String code,String sf,String img,String status){
		try {
			ReturnBan rb = gugeyzq(code);
			if(rb!=null)return rb;
			
			if(StringUtils.isBlank(title))
					return new ReturnBan("请输入标题", false);
			if(id==null) {
				DetachedCriteria dc = DetachedCriteria.forClass(Active.class);
				dc.add(Restrictions.eq("title",title));
				active = activeDao.findObj(dc);
				if(active!=null) {
					return new ReturnBan("标题已存在", false);
				}else {
					active=new Active();
				}
			}
			active.settitle(title);
			active.setcontent(content);
			active.setdetails(details);
			active.setstartTime(strToDateLong(startTime));
			active.setendTime(strToDateLong(endTime));
			active.setsort(sort);
			active.setSpecialField(sf);	
			active.setImg(img);
			if("0".equals(status)) {
				active.setStatus(false);
			}else {
				active.setStatus(true);
			}
			if(id!=null) {
				active.setId(id);
				activeDao.update(active);
			}else {
				activeDao.save(active);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
    }
	
	 /**
	     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	  */
	 public static Date strToDateLong(String strDate) {
	     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     ParsePosition pos = new ParsePosition(0);
	     Date strtodate = formatter.parse(strDate, pos);
	     return strtodate;
	  }
	
	/**
	 * 删除
	 * @param active
	 */
	@RequestMapping(value="/deleteactive")  
	@ResponseBody
    public ReturnBan deleteactive(Integer id,String code){		
		try {
			ReturnBan rb = gugeyzq(code);
			if(rb!=null)return rb;
			active.setId(id);	
			activeDao.delete(active);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}
	
	public String replace(String sr) {
		if(sr!=null&&!"".equals(sr)) {
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
	
	/**
	 * 测试文本框
	 */
	@RequestMapping("/testUeditor")
	public String testUeditor(String content,Model model){
		model.addAttribute("content", content);
		return "testUeditorPage";
	}

}
