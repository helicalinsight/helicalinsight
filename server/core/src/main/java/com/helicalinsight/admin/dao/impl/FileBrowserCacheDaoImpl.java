package com.helicalinsight.admin.dao.impl;

import com.google.gson.JsonArray;
import com.helicalinsight.admin.dao.FileBrowserCacheDao;
import com.helicalinsight.admin.model.FileBrowserCache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajesh on 3/25/2019.
 */
@Repository
public class FileBrowserCacheDaoImpl implements FileBrowserCacheDao {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileBrowserCacheDaoImpl.class);

    @Autowired
    private SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;

    @Override
    public int addFileBrowserCache(FileBrowserCache fileBrowserCache) {
        try {
            FileBrowserCache retrievedFileBrowserCache = findFileBrowserCache(fileBrowserCache.getFilePath());
            if (retrievedFileBrowserCache != null) {
                deleteFileBrowserCache(fileBrowserCache.getFilePath());
            }
            session.getCurrentSession().save(fileBrowserCache);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return fileBrowserCache.getId();
    }

    @Override
    public void editFileBrowserCache(FileBrowserCache fileBrowserCache) {
        try {
            session.getCurrentSession().update(fileBrowserCache);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteFileBrowserCache(String filePath) {
        try {
            FileBrowserCache fileBrowserCache = findFileBrowserCache(filePath);
            if (fileBrowserCache != null)
                session.getCurrentSession().delete(fileBrowserCache);
            else
                logger.error("File not found in database to delete.");
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public FileBrowserCache findFileBrowserCache(String filePath) {
        FileBrowserCache fileBrowserCache = null;
        try {
            List<FileBrowserCache> fileBrowserCacheList;
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	cr.select(resource).where(cb.equal(resource.get("filePath"), filePath));
        	fileBrowserCacheList =em.createQuery(cr).getResultList();
            if (fileBrowserCacheList != null && !fileBrowserCacheList.isEmpty())
                fileBrowserCache = fileBrowserCacheList.get(0);
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCache;
    }

    @Override
    public List<FileBrowserCache> getAllFileBrowserCache(int parentId) {
        List<FileBrowserCache> fileBrowserCacheList = new ArrayList<>();
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	cr.select(resource).where(cb.equal(resource.get("parentId"), parentId));
        	fileBrowserCacheList =em.createQuery(cr).getResultList();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCacheList;
    }

    @Override
    public FileBrowserCache getFileBrowserById(int parentId) {
        FileBrowserCache fileBrowserCache = null;
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	cr.select(resource).where(cb.equal(resource.get("id"), parentId));
        	fileBrowserCache =em.createQuery(cr).getSingleResult();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCache;
    }

    @Override
    public FileBrowserCache getFileBrowserByFileName(String fileName) {
        List<FileBrowserCache> listOfFileBrowserCache = null;
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	cr.select(resource).where(cb.equal(resource.get("fileName"), fileName));
        	listOfFileBrowserCache =em.createQuery(cr).getResultList();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        if (listOfFileBrowserCache != null && !listOfFileBrowserCache.isEmpty()) {
            return listOfFileBrowserCache.get(0);
        }
        return null;
    }


    @Override
    public int getId(String filePath) {
        FileBrowserCache fileBrowserCache;
        int id = 0;
        try {
            fileBrowserCache = (FileBrowserCache) session.getCurrentSession().get(FileBrowserCache.class, filePath);
            id = fileBrowserCache.getId();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return id;
    }

    @Override
    public void deleteAllFileBrowserCache() {

        try {
            session.getCurrentSession().createQuery("delete FileBrowserCache").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public List<FileBrowserCache> getResultByQuery(String searchString, String filterType) {
        return searchFileBrowserByType(searchString, filterType);
    }

    @Override
    public List<FileBrowserCache> getResultByQuery(String searchString, JsonArray filterType) {
        return searchFileBrowserByMultiTypes(searchString, filterType);
    }

    private List<FileBrowserCache> searchFileBrowserByMultiTypes(String searchString, JsonArray filterType) {
        List<FileBrowserCache> list = new ArrayList<>();
        if (searchString == null || searchString.isEmpty()) {
            list = onlyTypeSearch(filterType);
        } else {
            list = multiTypeSearchString(searchString, filterType);
        }
        return list;
    }

    private List<FileBrowserCache> multiTypeSearchString(String searchString, JsonArray filterType) {
        List<FileBrowserCache> fileBrowserCacheList = new ArrayList<>();
        try {
        	
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
            int size = filterType.size();
            Predicate[] prdicateList = new Predicate[size];
            for (int index = 0; index < size; index++) {
                String eachFilterType = filterType.get(index).getAsString();
                Predicate fileName = cb.like(resource.get("fileName"),"%." + eachFilterType);
                prdicateList[index] = fileName;
            }
            cr.select(resource).where(cb.like(resource.get("logicalPath"),  "%" + searchString + "%"),cb.or(prdicateList));
            fileBrowserCacheList = em.createQuery(cr).getResultList();
            // fileBrowserCacheList = session.getCurrentSession().createCriteria(FileBrowserCache.class).add(Restrictions.ilike("logicalPath", "%" + searchString + "%")).list();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCacheList;
    }

    private List<FileBrowserCache> onlyTypeSearch(JsonArray filterType) {
        List<FileBrowserCache> fileBrowserCacheList = new ArrayList<>();
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	
            int size = filterType.size();
            Predicate[] criterionList = new Predicate[size];
            for (int index = 0; index < size; index++) {
                String eachFilterType = filterType.get(index).getAsString();
                Predicate fileName =cb.like(resource.get("fileName"),"%." + eachFilterType);
                criterionList[index] = fileName;
            }
            cr.where(criterionList);
            fileBrowserCacheList = em.createQuery(cr).getResultList();
            // fileBrowserCacheList = session.getCurrentSession().createCriteria(FileBrowserCache.class).add(Restrictions.ilike("logicalPath", "%" + searchString + "%")).list();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCacheList;
    }

    private List<FileBrowserCache> searchFileBrowserByType(String searchString, String filterType) {
        List<FileBrowserCache> fileBrowserCacheList = new ArrayList<>();
        /*Query:
        * select * from cache_file_browser where logical_path like '%hi%' AND  (file_name like '%.report' or file_type='FOLDER')*/
        if (filterType != null && !filterType.isEmpty()) {
            if ("all".equalsIgnoreCase(filterType)) {
                fileBrowserCacheList = withOutTypeSearch(searchString, fileBrowserCacheList);
            } else {
                fileBrowserCacheList = withTypeSearch(searchString, filterType, fileBrowserCacheList);
            }
        } else {
            fileBrowserCacheList = withOutTypeSearch(searchString, fileBrowserCacheList);
        }
        return fileBrowserCacheList;
    }

    private List<FileBrowserCache> withTypeSearch(String searchString, String filterType, List<FileBrowserCache> fileBrowserCacheList) {
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	
            Predicate logicalPath = cb.like(resource.get("logicalPath"), "%" + searchString + "%");
            Predicate fileName = cb.like(resource.get("fileName"), "%." + filterType);
            cr.select(resource).where(cb.and(logicalPath,fileName));
            fileBrowserCacheList = em.createQuery(cr).getResultList();
            // fileBrowserCacheList = session.getCurrentSession().createCriteria(FileBrowserCache.class).add(Restrictions.ilike("logicalPath", "%" + searchString + "%")).list();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCacheList;
    }

    private List<FileBrowserCache> withOutTypeSearch(String searchString, List<FileBrowserCache> fileBrowserCacheList) {
        try {
        	
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	Root<FileBrowserCache> resource = cr.from(FileBrowserCache.class);
        	cr.select(resource).where(cb.like(resource.get("logicalPath"), "%" + searchString + "%"));
        	fileBrowserCacheList =em.createQuery(cr).getResultList();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return fileBrowserCacheList;
    }


    @Override
    public boolean emptyCheckFileBrowserCache() {
        boolean status = false;
        try {
        	CriteriaBuilder cb = em.getCriteriaBuilder();
        	CriteriaQuery<FileBrowserCache> cr=cb.createQuery(FileBrowserCache.class);
        	status=em.createQuery(cr).setMaxResults(1).getResultList().isEmpty();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return status;

    }
}
