package com.pay.service.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.dao.BaseDao;
import com.pay.model.Ip;
import com.pay.service.IpService;

@Service("ipService")
public class IpServiceImpl implements IpService {
	
	@Autowired
	private BaseDao<Ip> ipDao;
	
	@Override
	public List<Ip> getIp() throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Ip.class);
		return ipDao.findList(dc);
	}
	
	
	
	
	
}
