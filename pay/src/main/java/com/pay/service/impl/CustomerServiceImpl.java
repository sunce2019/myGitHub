package com.pay.service.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.dao.BaseDao;
import com.pay.enums.PayTran;
import com.pay.model.Customer;
import com.pay.model.Groups;
import com.pay.model.Menu;
import com.pay.service.CustomerService;
import com.pay.util.StringUtils;

/**
*@author star
*@version 创建时间：2019年3月29日下午6:21:09
*/
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	private BaseDao<Customer> customerDao;
	@Autowired
	private BaseDao<Groups> groupsDao;
	@Autowired
	private BaseDao<Menu> menuDao;
	@Override
	public Customer getCustomer(Customer customer) throws Exception {
		if(customer==null)return null;
		DetachedCriteria dc = DetachedCriteria.forClass(Customer.class);
		if(!StringUtils.isBlank(customer.getUid()))
			dc.add(Restrictions.eq("uid",customer.getUid()));
		if(customer.getFlag()!=null && customer.getFlag()>0)
			dc.add(Restrictions.eq("flag",customer.getFlag()));
		return customerDao.findObj(dc);
	}
	@Override
	public Customer getCustomer(PayTran payTran) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean userJurisdiction(int userid, int customerid) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Groups.class);
		dc.add(Restrictions.eq("userid",userid));
		dc.add(Restrictions.eq("customerid",customerid));
		List<Groups> list = groupsDao.findList(dc);
		return list.size()>0?true:false;
	}
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addCustomer(Customer customer) throws Exception {
		Menu menu = menuDao.get(Menu.class, 1);
		List<Menu> menuList = menu.getMenuList().get(0).getMenuList();
		customerDao.save(customer);
		Serializable serializable = menuDao.save(new Menu("",1,customer,"",null));//创建 接入权限
		for(Menu m :menuList) {
			Menu newMenu = new Menu();
			newMenu.setName(m.getName());
			newMenu.setParentid((int)serializable);
			newMenu.setUrl(m.getUrl());
			newMenu.setCustomer(null);
			newMenu.setSortby(null);
			menuDao.save(newMenu);	//功能权限
		}
		
	}

}
