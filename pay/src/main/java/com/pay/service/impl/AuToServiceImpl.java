package com.pay.service.impl;

import java.util.List;
import java.util.UUID;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.auto.AutoMain;
import com.pay.auto.enums.RequestMethod;
import com.pay.dao.BaseDao;
import com.pay.model.Auto;
import com.pay.model.Business;
import com.pay.service.AuToService;
import com.pay.util.StringUtils;

import net.sf.json.JSONObject;

/**
*@author star
*@version 创建时间：2019年6月27日下午3:08:01
*/
@Service("auToService")
public class AuToServiceImpl implements AuToService {
	
	private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("busi1");
	
	@Autowired
	private BaseDao<Auto> autoDao;
	@Autowired
	private BaseDao<Business> businessDaos;
	@Autowired
	private BaseDao<com.pay.model.Business> businessDao;
	
	@Override
	  @Transactional(rollbackFor=Exception.class)
	  public void addOrUpdateAuto(int id, AutoMain autoMain) throws Exception {
	    autoMain.setAutoLoad(null);//下拉框数据，不存储到数据库。修改的时候加载最新数据。
	    String json = JSONObject.fromObject(autoMain).toString();
	    com.pay.model.Business business =  businessDao.get(com.pay.model.Business.class, autoMain.getBusinessid(),LockMode.UPGRADE);
	    if(business==null || business.getIsauto()!=2)throw new Exception("商户不是自动类型。");
	    if(id>0) {
	      Auto auto = autoDao.get(Auto.class, id);
	      auto.setDatas(json);
	      updateBusiness(business,autoMain);
	      autoDao.update(auto);
	    } else {
	      DetachedCriteria dc = DetachedCriteria.forClass(Auto.class,"a");
	      dc.add(Restrictions.sqlRestriction("  business_id="+autoMain.getBusinessid()));
	      Auto auto =  autoDao.findObj(dc);
	      if(auto==null) {
	        updateBusiness(business,autoMain);
	        autoDao.save(new Auto(json,business,UUID.randomUUID().toString().replace("-", "")));
	      }
	    }
	  }
	  
	  /**
	   * 修改所有该类型 商户状态。
	   * @param autoMain
	   */
	  private void updateBusiness(Business business,AutoMain autoMain) {
	    DetachedCriteria dc = DetachedCriteria.forClass(Business.class);
	    dc.add(Restrictions.ne("id",business.getId()));
	    dc.add(Restrictions.eq("code",business.getCode()));
	    List<Business> list = businessDao.findList(dc);
	    for(Business b :list)updateAttribute(businessDao.get(Business.class, b.getId(),LockMode.UPGRADE), autoMain);
	    updateAttribute(business, autoMain);
	  }
	  
	  private void updateAttribute(Business business,AutoMain autoMain) {
	    business.setAutoType(autoMain.getSingType());  //加密方式。
	    businessDao.update(business);
	  }
	
	@Override
	public Auto getAuto(int business_id,String code) throws Exception {
		if(business_id>0 && code==null) {
			Business business = businessDaos.get(Business.class, business_id);
			if(business==null) {
				logger.info("没有找到商户号id:"+business_id);
				return null;
			}
			code = business.getCode();
		}
		
		DetachedCriteria dc = DetachedCriteria.forClass(Auto.class,"a");
		DetachedCriteria b = dc.createAlias("business", "b");
		b.add(Restrictions.eq("b.code",code));
		return autoDao.findObj(dc);
	}

	@Override
	public Auto getAuto(Auto auto) throws Exception {
		DetachedCriteria dc = DetachedCriteria.forClass(Auto.class);
		if(!StringUtils.isBlank(auto.getSynNo()))
			dc.add(Restrictions.eq("synNo",auto.getSynNo()));
		List<Auto> list = autoDao.findList(dc);
		if(list.size()==1)return list.get(0);
		return null;
	}

}
