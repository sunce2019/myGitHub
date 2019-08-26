package com.pay.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pay.model.BankBase;
import com.pay.vo.PayOrderCount;

@Repository
@Transactional
public class BaseDaoImpl<T> implements BaseDao<T> {

	@Autowired
	private SessionFactory sessionFactory;

	public Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	public Serializable save(T o) {
		return this.getCurrentSession().save(o);
	}

	public void delete(T o) {
		this.getCurrentSession().delete(o);

	}

	public void update(T o) {
		this.getCurrentSession().update(o);
		this.getCurrentSession().flush();
	}

	public void saveOrUpdate(T o) {
		this.getCurrentSession().saveOrUpdate(o);

	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql) {

		return this.getCurrentSession().createQuery(hql).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, Object[] param, Integer page, Integer rows) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (rows == null || rows < 1) {
			rows = 10;
		}

		Query q = this.getCurrentSession().createQuery(hql);

		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> find(String hql, List<Object> param, Integer page, Integer rows) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (rows == null || rows < 1) {
			rows = 10;
		}
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
	}

	@SuppressWarnings("unchecked")
	public T get(Class<T> c, Serializable id) {
		return (T) this.getCurrentSession().get(c, id);
	}

	public T get(Class<T> c, Serializable id, LockMode lockMode) {
		return (T) this.getCurrentSession().get(c, id, lockMode);
	}

	public T load(Class<T> c, Serializable id, LockMode lockMode) {
		return (T) this.getCurrentSession().load(c, id, lockMode);
	}

	public T load(Class<T> c, Serializable id) {
		return (T) this.getCurrentSession().load(c, id);
	}

	public T get(String hql, Object[] param) {
		List<T> l = this.find(hql, param);
		if (l != null && l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}

	public T get(String hql, List<Object> param) {
		List<T> l = this.find(hql, param);
		if (l != null && l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}

	public Long count(String hql) {
		return (Long) this.getCurrentSession().createQuery(hql).uniqueResult();
	}

	public Long count(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return (Long) q.uniqueResult();
	}

	public Long count(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return (Long) q.uniqueResult();
	}

	public Integer executeHql(String hql) {
		return this.getCurrentSession().createQuery(hql).executeUpdate();
	}

	public Integer executeHql(String hql, Object[] param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.length > 0) {
			for (int i = 0; i < param.length; i++) {
				q.setParameter(i, param[i]);
			}
		}
		return q.executeUpdate();
	}

	public Integer executeHql(String hql, List<Object> param) {
		Query q = this.getCurrentSession().createQuery(hql);
		if (param != null && param.size() > 0) {
			for (int i = 0; i < param.size(); i++) {
				q.setParameter(i, param.get(i));
			}
		}
		return q.executeUpdate();
	}

	@Override
	public List<T> findList(Class<T> t) {
		return (List<T>) getCurrentSession().createCriteria(t);
	}

	@Override
	public T findObj(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		List list = c.list();
		if (list != null && list.size() > 0) {
			return (T) list.get(0);
		}
		return null;
	}

	@Override
	public List<T> findList(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		return c.list();
	}

	@Override
	public List<BankBase> findBankBaseList(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		return c.list();
	}

	@Override
	public Object findObject(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		return c.list();
	}

	public List<T> findList(DetachedCriteria dc, int pageNo, int pageSize) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		if (pageNo > 0)
			c.setFirstResult((pageNo - 1) * pageSize);
		if (pageSize > 0)
			c.setMaxResults(pageSize);
		return c.list();
	}

	public List<T> findList(DetachedCriteria dc, T t) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		return c.list();
	}

	public Long findCountByCondition(DetachedCriteria criteria) {
		Long totalCount = (Long) criteria.setProjection(Projections.rowCount())
				.getExecutableCriteria(this.getCurrentSession()).uniqueResult();
		return totalCount;
	}

	@Override
	public int count(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		c.setProjection(Projections.rowCount());
		c.setMaxResults(0);
		c.setFirstResult(0);
		return Integer.parseInt(c.uniqueResult().toString());
	}

	@Override
	public List<T> createSQLQuery(String sql, Class c) {
		SQLQuery query = this.getCurrentSession().createSQLQuery(sql);
		query.setResultTransformer(Transformers.aliasToBean(c));
		return query.list();
	}

	public List<Map<String, Object>> findMapBySql(String sql, Map<String, Object> params) {
		SQLQuery sqlQuery = this.getCurrentSession().createSQLQuery(sql);
		sqlQuery = getSqlQueryByMap(sqlQuery, params);
		return sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<T> findObjBySql(String sql, Map<String, Object> params, Class c) {
		SQLQuery sqlQuery = this.getCurrentSession().createSQLQuery(sql);
		sqlQuery = getSqlQueryByMap(sqlQuery, params);
		return sqlQuery.setResultTransformer(Transformers.aliasToBean(c)).list();
	}

	public SQLQuery getSqlQueryByMap(SQLQuery sqlQuery, Map<String, Object> params) {
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				Object obj = params.get(key);
				if (obj instanceof Collection<?>)
					sqlQuery.setParameterList(key, (Collection<?>) obj);
				else if (obj instanceof Object[])
					sqlQuery.setParameterList(key, (Object[]) obj);
				else
					sqlQuery.setParameter(key, obj);

			}
		}
		return sqlQuery;
	}

	@Override
	public PayOrderCount payCount(DetachedCriteria dc) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		c.setFirstResult(0);
		c.setMaxResults(0);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.rowCount());
		proList.add(Projections.sum("price"));
		proList.add(Projections.sum("realprice"));
		c.setProjection(proList);
		List list = c.list();
		Object[] obj = (Object[]) list.get(0);

		PayOrderCount pc = new PayOrderCount(obj[0] != null ? Integer.parseInt(obj[0].toString()) : 0,
				obj[1] != null ? Double.parseDouble(obj[1].toString()) : 0d,
				obj[2] != null ? Double.parseDouble(obj[2].toString()) : 0d);

		session = this.getCurrentSession();
		dc.add(Restrictions.eq("flag", 2));
		c = dc.getExecutableCriteria(session);
		c.setFirstResult(0);
		c.setMaxResults(0);
		proList = Projections.projectionList();
		proList.add(Projections.rowCount());
		proList.add(Projections.sum("realprice"));

		c.setProjection(proList);
		list = c.list();
		obj = (Object[]) list.get(0);
		pc.setRealpriceSum(obj[1] != null ? Double.parseDouble(obj[1].toString()) : 0);
		return pc;
	}

	@Override
	public int successRateCount(DetachedCriteria dc) {
		dc.add(Restrictions.eq("flag", 2));
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		// c.setFirstResult(0);
		// c.setMaxResults(0);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.rowCount());
		c.setProjection(proList);
		List list = c.list();
		return list.get(0) != null ? Integer.parseInt(list.get(0).toString()) : 0;
	}

	@Override
	public List<Object> findList1(DetachedCriteria dc, int pageNo, int pageSize) {
		Session session = this.getCurrentSession();
		Criteria c = dc.getExecutableCriteria(session);
		if (pageNo > 0)
			c.setFirstResult((pageNo - 1) * pageSize);
		if (pageSize > 0)
			c.setMaxResults(pageSize);
		return c.list();
	}

}
