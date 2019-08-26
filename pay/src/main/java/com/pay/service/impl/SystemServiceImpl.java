package com.pay.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.model.Groups;
import com.pay.model.GroupsMenu;
import com.pay.model.Menu;
import com.pay.model.SystConfig;
import com.pay.service.SystemService;
import com.pay.util.StringUtils;

/**
*@author star
*@version 创建时间：2019年6月18日下午3:27:38
*/
@Repository(value="systemService")
public class SystemServiceImpl implements SystemService {
	
	@Autowired
	private BaseDao<Groups> groupsDao;
	@Autowired
	private BaseDao<Menu> menuDao;
	@Autowired
	private BaseDao<GroupsMenu> groupsmenuDao;
	@Autowired
	private BaseDao<SystConfig> systConfigDao;
	
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addSystemPermiOrUpdate(Integer id, String menuid) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(GroupsMenu.class);
		dc.add(Restrictions.eq("groupsId",id));
		List<GroupsMenu> list = groupsmenuDao.findList(dc);
		for(GroupsMenu del: list) {
			groupsmenuDao.delete(del);//删除原来权限。
		}
		if(StringUtils.isBlank(menuid))return;
		String[] ids =  menuid.split(",");
		for(String s:ids)groupsmenuDao.save(new GroupsMenu(Integer.parseInt(s),id));//在添加权限
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteGroups(int id) throws Exception {
		Groups groups = groupsDao.get(Groups.class, id);
		groupsDao.delete(groups);
		
		DetachedCriteria dc = DetachedCriteria.forClass(GroupsMenu.class);
		dc.add(Restrictions.eq("groupsId",groups.getId()));
		List<GroupsMenu> list = groupsmenuDao.findList(dc);
		for(GroupsMenu gm :list)groupsmenuDao.delete(gm);
	}

	@Override
	public SystConfig getSystConfig(SystConfig systConfig) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(SystConfig.class);
		if(!StringUtils.isBlank(systConfig.getCode()))
			dc.add(Restrictions.eq("code",systConfig.getCode()));
		if(systConfig.getFlag()>0)
			dc.add(Restrictions.eq("flag",systConfig.getFlag()));
		List<SystConfig> list = systConfigDao.findList(dc);
		if(list!=null && list.size()>0)
			return list.get(0);
		return null;
	}
	

}
