package com.helicalinsight.scheduling.dao.impl;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.HiResourceDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class implements the {@link HiResourceDao} interface and provides methods to interact with the database
 * for managing HiResource entities.
 * Created by author on 3/13/2020.
 * @author Rajesh
 */
@Repository
public class HiResourceDaoImpl implements HiResourceDao {
    private static final Logger logger = LoggerFactory.getLogger(HiResourceDaoImpl.class);

    @Autowired
    SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;
    /**
     * addHiResource(HiResource hiResource)
     * It adds hiResource entity in database
     * @param hiResource        hiResource properties
     * @return resource id in Long format
     */
    @Override
    public Long addHiResource(HiResource hiResource) {
        try {
            session.getCurrentSession().save(hiResource);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return hiResource.getResourceId();
    }
    /**
     *  editHiResource(HiResource hiResource)
     *  it deletes the resource entity from database
     *  @param hiResource        hiResource properties
     */
    @Override
    public void editHiResource(HiResource hiResource) {
        try {
            session.getCurrentSession().delete(hiResource);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * deleteHiResource(Long hiResourceId)
     * this method deletes the hi resouce using given id.
     * @param hiResourceId      id 
     */
    @Override
    public void deleteHiResource(Long hiResourceId) {
        try {
            String hql = "delete from HiResource where resourceId=:id";
            session.getCurrentSession().createQuery(hql).setParameter("id", hiResourceId).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getHiResource(Long hiResourceId)
     * Returns HiResource from database
     * @param hiResourceId      id in long format
     * @return HiResource from database.
     */
    @Override
    public HiResource getHiResource(Long hiResourceId) {
        HiResource hiResource = null;
        try {
            hiResource = (HiResource) session.getCurrentSession().get(HiResource.class, hiResourceId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return hiResource;
    }
    /**
     * findUniqueHiResource(JobParameters sampleJobParameter)
     * Returns {@code null} as this method for finding unique HiResource is not implemented.
     *
     * @param sampleJobParameter          object to search for.
     * @return {@code null} since the method is not implemented.
     */
    @Override
    public HiResource findUniqueHiResource(HiResource sampleHiResource) {
        return null;
    }
    /**
     * deleteAllHiResource()
     * Method deletes all HiReource from database.
     */
    @Override
    public void deleteAllHiResource() {
        try {
            session.getCurrentSession().createQuery("delete from HiResource").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getResourceTypeById(Long hiResourceId)
     * Returns {@link ResourceType} entity using resource id.
     * @param hiResourceId      id in long format
     * @return ResourceType entity
     */
    @Override
    public ResourceType getResourceTypeById(Long hiResourceId) {
        ResourceType resourceType = null;
        try {
            resourceType = getHiResource(hiResourceId).getResourceType();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return resourceType;
    }
    /**
     * getUserById(Long hiResourceId)
     * Returns user object who created hiResource.
     * @param hiResourceId      id in long format
     * @return user object.
     */
    @Override
    public User getUserById(Long hiResourceId) {
        User user = null;
        try {
            user = getHiResource(hiResourceId).getCreatedBy();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user;
    }
    /**
     * getAllSchedulesById(Long hiResourceId)
     * @param hiResourceId      id in long format
     * @return List of schedules .
     */
    @Override
    public List<Schedules> getAllSchedulesById(Long hiResourceId) {
        List<Schedules> listOfSchedules = null;
        try {
            listOfSchedules = getHiResource(hiResourceId).getResourceIdSchedules();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return listOfSchedules;
    }
    /**
     * getHiResourceByPath(String path, Long parentId)
     * Returns HiResouce entity
     * @param path            Hiresource path 
     * @param parentId        id
     * @return {@link HiResource} object. otherwise returns {@code null} if any exception occurs.
     */
    @Override
    public HiResource getHiResourceByPath(String path, Long parentId) {

		HiResource hiResource = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<HiResource> cr = cb.createQuery(HiResource.class);
		Root<HiResource> resource = cr.from(HiResource.class);
		Predicate p1 = cb.equal(resource.get("resourcePath"), path);
		try {
			if (parentId == null) {
				try {
					cr.select(resource).where(p1);
					hiResource = em.createQuery(cr).getSingleResult();
				} catch (Exception e) {
					if (e instanceof NoResultException)
						return null;
					logger.error("Exception", e);
				}
			} else {
				try {
					cr.select(resource).where(cb.and(p1, cb.equal(resource.get("parentId"), parentId)));
					hiResource = em.createQuery(cr).getSingleResult();
				} catch (Exception e) {
					if (e instanceof NoResultException)
						return null;
					logger.error("Exception", e);
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return hiResource;
    }
    /**
     * deleteAllMigratedEntries()
     * This method deletes the migration related entries.
     */
    @Override
    public void deleteAllMigratedEntries() {
        try {
            String hql = "delete from HiResource where isMigrated=:id";
            session.getCurrentSession().createQuery(hql).setParameter("id", true).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getHiResourceByPath(String path) 
     * @param path        resource Path 
     * @return {@link HiResource} object from specified path. otherwise returns {@code null} if any exception occurs.
     */
	@Override
	public HiResource getHiResourceByPath(String path) {
		try {
			String hql = "FROM HiResource where resourcePath = :path";
			return (HiResource) session.getCurrentSession().createQuery(hql)
										.setParameter("path", path)
										.uniqueResult();
		}
		catch (Exception e) {
			logger.error("Exception",e);
		}
		return null;
	}
}
