package com.pay.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dao.BaseDao;
import com.pay.dao.RedisModel;
import com.pay.enums.ButClick;
import com.pay.enums.ConfigCode;
import com.pay.model.Groups;
import com.pay.model.GroupsMenu;
import com.pay.model.Menu;
import com.pay.model.SystConfig;
import com.pay.service.SystemService;
import com.pay.util.ReturnBan;

import net.sf.json.JSONArray;

/**
 * @author star
 * @version 创建时间：2019年6月18日下午3:15:30
 */
@Controller
@RequestMapping("/system")
public class SystemController extends BaseController {

	@Autowired
	private BaseDao<Groups> groupsDao;
	@Autowired
	private BaseDao<Menu> menuDao;
	@Autowired
	private SystemService systemService;
	@Autowired
	private BaseDao<GroupsMenu> groupsmenuDao;
	@Autowired
	private BaseDao<SystConfig> systConfigDao;
	@Autowired
	private RedisModel redisModel;

	private String groupsDetail(Integer id, Model model) {
		if (id != null && id > 0) {
			model.addAttribute("groups", groupsDao.get(Groups.class, id));
		}
		return "groupsDetail";
	}

	@RequestMapping(value = "/groupsDetails")
	@ResponseBody
	private ReturnBan groupsDetails(Integer id, Model model) {
		Groups groups = null;
		if (id != null && id > 0) {
			groups = groupsDao.get(Groups.class, id);
		}
		return new ReturnBan("", true, groups);
	}

	@RequestMapping("/systconfigUpdatePage")
	public String systconfigUpdatePage(Integer id, Model model) {
		model.addAttribute("systConfig", systConfigDao.get(SystConfig.class, id));
		return "systconfigDetail";
	}

	@RequestMapping("/groupsUpdatePage")
	public String groupsUpdatePage(Integer id, Model model) {
		return groupsDetail(id, model);
	}

	@RequestMapping("/groupsAddPage")
	public String groupsAddPage(Model model) {
		return groupsDetail(null, model);
	}

	@RequestMapping("/groups")
	public String groups(Model model) {
		return "groupsList";
	}

	@RequestMapping("/systconfig")
	public String systconfig(Model model) {
		return "systconfigList";
	}

	@RequestMapping("/systemPermiDetail")
	public String systemPermiDetail(Model model) {
		return "systemPermiDetail";
	}

	@RequestMapping(value = "/groupsList")
	@ResponseBody
	public String groupsList(int pageNo) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Groups.class);
			List<Groups> list = groupsDao.findList(dc, pageNo, pageSize);
			int count = groupsDao.count(dc);
			setPayorderBut(list);// 设置用户是否有权限操作按钮
			return toListJson(list, count, button(new ButClick[] { ButClick.ADDGROUPS }));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	@RequestMapping(value = "/systconfigList")
	@ResponseBody
	public String systconfigList(int pageNo) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(SystConfig.class);
			List<SystConfig> list = systConfigDao.findList(dc, pageNo, pageSize);
			int count = systConfigDao.count(dc);
			setSystconfigBut(list);// 设置用户是否有权限操作按钮
			return toListJson(list, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	private void setSystconfigBut(List<SystConfig> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDSYSTCONFIG });//
		if (map.isEmpty())
			return;
		for (SystConfig s : list) {
			if (map.containsKey(ButClick.UPDSYSTCONFIG.getCode()))
				s.setEditClick(true);
		}
	}

	private void setPayorderBut(List<Groups> list) {
		Map<String, Boolean> map = getDateBut(new ButClick[] { ButClick.UPDGROUPS, ButClick.DELGROUPS });//
		if (map.isEmpty())
			return;
		for (Groups p : list) {
			if (map.containsKey(ButClick.UPDGROUPS.getCode()))
				p.setEditClick(true);
			if (map.containsKey(ButClick.DELGROUPS.getCode()))
				p.setDelClick(true);
		}
	}

	@RequestMapping(value = "/getSinglePrivilege")
	@ResponseBody
	public ReturnBan getSinglePrivilege(String name) {
		try {
			Set<Groups> set = getLonginName().getGroupsList();
			Iterator<Groups> it = set.iterator();
			boolean flag = false;
			while (it.hasNext()) {
				Set<Menu> list = it.next().getMenuList();
				if (list != null && list.size() > 0) {
					Iterator<Menu> its = list.iterator();
					while (its.hasNext()) {
						singlePrivilege(name, its.next().getMenuList());
					}
				}
			}
			return new ReturnBan("", flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("", false);
	}

	private boolean singlePrivilege(String name, List<Menu> menuList) {
		for (Menu m : menuList) {
			if (m.getName().equals("name"))
				return true;
			if (m.getMenuList() != null && m.getMenuList().size() > 0)
				singlePrivilege(name, m.getMenuList());
		}
		return false;
	}

	@RequestMapping(value = "/getUsersMenu")
	@ResponseBody
	public String getUsersMenu() {
		try {

			// ***********************测试中。。。。。。。。。。。。。
			List<Menu> list = getUserMenus();
			Collections.sort(list, new Comparator<Menu>() {
				@Override
				public int compare(Menu o1, Menu o2) {
					int high1 = (int) o1.getSortby();
					int high2 = (int) o2.getSortby();
					return high1 - high2;
				}
			});
			return JSONArray.fromObject(list).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	@RequestMapping(value = "/addSystemPermiOrUpdate")
	@ResponseBody
	public ReturnBan addSystemPermiOrUpdate(Integer id, String menuid, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			systemService.addSystemPermiOrUpdate(id, menuid);
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/menuList")
	@ResponseBody
	public String menuList(int groupsId) {
		try {
			DetachedCriteria dc = DetachedCriteria.forClass(Menu.class);
			dc.add(Restrictions.eq("parentid", 0));
			List<Menu> list = menuDao.findList(dc);
			dc = DetachedCriteria.forClass(GroupsMenu.class);
			dc.add(Restrictions.eq("groupsId", groupsId));
			List<GroupsMenu> groupsMenulist = groupsmenuDao.findList(dc);

			return JSONArray.fromObject(handleTee(list, groupsMenulist, null, "0")).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "未知错误";
	}

	/**
	 * 递归
	 * 
	 * @param list
	 * @param groupsMenulist 当前选中的角色已有权限
	 * @param parenMap       父级集合 如果子级有选中，父级打开折叠
	 * @param parentId       父级id
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> handleTee(List<Menu> list, List<GroupsMenu> groupsMenulist,
			Map<String, Object> parenMap, String parentId) throws Exception {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < list.size() + 1; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Menu menu = list.get(i - 1);
			String thisId = parentId != "0" ? parentId + i : Integer.toString(menu.getId()); // 父级ID
			map.put("id", thisId);
			map.put("dbid", menu.getId());
			map.put("text", menu.getCustomer() == null ? menu.getName() : menu.getCustomer().getName());
			if (parentId == "0")
				map.put("state", "closed");
			flagChecked(map, groupsMenulist, menu.getId(), parenMap);
			if (menu.getMenuList() != null && menu.getMenuList().size() > 0) {
				map.put("children", handleTee(menu.getMenuList(), groupsMenulist, map, thisId));
			} else {
				map.remove("state");
			}
			returnList.add(map);
		}
		return returnList;
	}

	/**
	 * 是否被选中，是否有当前目录权限
	 * 
	 * @param map
	 * @param groupsMenulist
	 * @param menuId
	 * @throws Exception
	 */
	private void flagChecked(Map<String, Object> map, List<GroupsMenu> groupsMenulist, int menuId,
			Map<String, Object> parenMap) throws Exception {
		for (GroupsMenu gm : groupsMenulist) {
			if (gm.getMenuid() == menuId) {
				map.put("checked", true);// 选中
				if (parenMap != null)
					parenMap.put("state", "open");// 父级折叠打开
			}
		}

	}

	@RequestMapping(value = "/addGroups")
	@ResponseBody
	public ReturnBan addGroups(Integer id, String name, String code, String remarks) {
		return addGroupsOrUpdate(id, name, code, remarks);
	}

	@RequestMapping(value = "/updateGroups")
	@ResponseBody
	public ReturnBan updateGroups(Integer id, String name, String code, String remarks) {
		return addGroupsOrUpdate(id, name, code, remarks);
	}

	@RequestMapping(value = "/updateSystconfig")
	@ResponseBody
	public ReturnBan updateSystconfig(Integer id, String content, Integer flag, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			SystConfig systConfig = systConfigDao.get(SystConfig.class, id);
			systConfig.setContent(content);
			systConfig.setFlag(flag);
			systConfigDao.update(systConfig);
			return new ReturnBan("修改成功", true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/checkGoogle")
	@ResponseBody
	public ReturnBan checkGoogle() {
		try {

			SystConfig systConfig = systemService.getSystConfig(new SystConfig(ConfigCode.AUTHCODE.getCode(), 1));
			if (systConfig == null)
				return new ReturnBan("", true);

			String key = getLonginName().getLoginname() + "Google";
			String v = redisModel.redisTemplate.opsForValue().get(key);
			if (v != null)
				return new ReturnBan("", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("", false);
	}

	private ReturnBan addGroupsOrUpdate(Integer id, String name, String code, String remarks) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;
			if (id == null || id == 0) {
				groupsDao.save(new Groups(name, remarks));
			} else {
				Groups groups = groupsDao.get(Groups.class, id);
				groups.setName(name);
				groups.setCustomerid(0);
				groups.setUserid(0);
				groups.setRemarks(remarks);
				groupsDao.update(groups);
			}
			return new ReturnBan("操作成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

	@RequestMapping(value = "/deleteGroups")
	@ResponseBody
	public ReturnBan deleteGroups(int id, String code) {
		try {
			ReturnBan returnBan = gugeyzq(code);
			if (returnBan != null)
				return returnBan;

			systemService.deleteGroups(id);
			return new ReturnBan("删除成功", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ReturnBan("系统异常", false);
	}

}
