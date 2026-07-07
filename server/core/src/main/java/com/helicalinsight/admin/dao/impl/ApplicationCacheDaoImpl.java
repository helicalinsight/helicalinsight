package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.ApplicationCacheDao;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Rajesh
 *         Created by helical019 on 2/15/2019.
 */

@Repository
public class ApplicationCacheDaoImpl implements ApplicationCacheDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationCacheDaoImpl.class);

    @Autowired
    private SessionFactory session;
    

    @Override
    public String addApplicationCache(ApplicationCache applicationCache) {
        try {
            Session currentSession = session.getCurrentSession();
            currentSession.save(applicationCache);
            currentSession.flush();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return applicationCache.getKey();
    }

    @Override
    public void editApplicationCache(ApplicationCache applicationCache) {

        try {
            Session currentSession = session.getCurrentSession();
            List<Predicate> predicates=new ArrayList<>();
        	CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        	CriteriaQuery<ApplicationCache> cr=cb.createQuery(ApplicationCache.class);
        	Root<ApplicationCache> resource = cr.from(ApplicationCache.class);
        	predicates.add(cb.equal(resource.get("key"), applicationCache.getKey()));
        	cr.select(resource).where(cb.equal(resource.get("page"), applicationCache.getPage()));
        	ApplicationCache existing=(ApplicationCache)cr;
            existing.setValue(applicationCache.getValue());
            existing.setCreateDateTime(new Date());
            currentSession.update(existing);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteApplicationCache(String applicationCacheId) {
        ApplicationCache applicationCache = (ApplicationCache) session.getCurrentSession().get(ApplicationCache.class, applicationCacheId);
        if (applicationCache != null)
            session.getCurrentSession().delete(applicationCache);
    }

    @Override
    public List<ApplicationCache> findApplicationCache(String applicationCacheId) {
        List<ApplicationCache> listOfCache = new ArrayList<>();
        try {
        	Session currentSession = session.getCurrentSession();
        	CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        	CriteriaQuery<ApplicationCache> cr=cb.createQuery(ApplicationCache.class);
        	Root<ApplicationCache> resource = cr.from(ApplicationCache.class);
        	cr.select(resource).where(cb.equal(resource.get("key"), applicationCacheId));
            listOfCache =currentSession.createQuery(cr).getResultList();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return listOfCache;

    }

    @Override
    public List<ApplicationCache> readApplicationCache() {
        List<ApplicationCache> listOfCache = new ArrayList<>();
        try {
        	Session currentSession = session.getCurrentSession();
        	CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        	CriteriaQuery<ApplicationCache> cr=cb.createQuery(ApplicationCache.class);
            listOfCache = currentSession.createQuery(cr).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return listOfCache;
    }


    @Override
    public Boolean findByKeyAndPage(String key, Integer position) {
		Boolean st = false;
		try {
			org.hibernate.query.Query<ApplicationCache> query = session.getCurrentSession()
					.createQuery("select 1 from ApplicationCache t where t.key = :key and t.page = :position");
			query.setParameter("key", key);
			query.setParameter("position", position);
			query.setCacheable(true);
			st = (query.getSingleResult() != null);
		} catch (NoResultException e) {
			return st;
		}
		return st;
    }


    @Override
    public boolean deleteAllCache() {
        try {
            Session currentSession = session.getCurrentSession();
            currentSession.createQuery("delete ApplicationCache ").executeUpdate();
            currentSession.createQuery("delete DataSourceMapping ").executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("Exception", e);
            return false;
        }
    }


    private void mergeByCacheKey(ApplicationCache applicationCache) {
        List<ApplicationCache> listOfApplicationCache = findApplicationCache(applicationCache.getKey());
        Session currentSession = session.getCurrentSession();
        if (listOfApplicationCache != null) {
            currentSession.update(applicationCache);
        } else {
            currentSession.save(applicationCache);
            currentSession.flush();
        }
    }

    public List<ApplicationCache> findCacheByKey(String key) {
        try {
            Session currentSession = session.getCurrentSession();
            org.hibernate.query.Query<ApplicationCache> query = currentSession.createQuery("from ApplicationCache where key = :key" );
            //query.setCacheable(true);
            query.setParameter("key",key);
            List<ApplicationCache> allCaches = query.getResultList();
            return allCaches;

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public boolean deleteByKey(String key) {
        try {
            Query query =  session.getCurrentSession().createQuery("delete ApplicationCache  ac where  ac.key = :key");
            query.setParameter("key", key);
            query.executeUpdate();

            Query dsQuery =  session.getCurrentSession().createQuery("delete DataSourceMapping ds where ds.key = :key");
            dsQuery.setParameter("key", key);
            dsQuery.executeUpdate();
            return true;

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return false;
    }


}
