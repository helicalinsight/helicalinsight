package com.helicalinsight.adhoc.dao.impl;

import com.helicalinsight.adhoc.dao.HICubeDAO;
import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.model.*;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HICubeDAOImpl implements HICubeDAO {
    private static final Logger logger = LoggerFactory.getLogger(HICubeDAOImpl.class);
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RoleDao roleDao;

    @Override
    public void addCube(HIMetadataCube cube) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the HIMetadataView " + cube);
            }
            sessionFactory.getCurrentSession().save(cube);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public void add(Object obj) {
        try {

            sessionFactory.getCurrentSession().save(obj);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void edit(Object obj) {
        try {
            sessionFactory.getCurrentSession().update(obj);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public HIMetadataCube findCube(Integer metadataId, Integer cubeId, String uuid) {

        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HIMetadataCube cube where cube.hiResourceMetadata.id=:id and cube.hiResource.resourceId=:cubeRes and cubeId=:cubeId ");
        query.setParameter("id", metadataId);
        query.setParameter("cubeId", uuid);
        query.setParameter("cubeRes", cubeId);
        query.setCacheable(true);
        return (HIMetadataCube) query.uniqueResult();

    }

    @Override
    public HICubeDimension findHICubeDimension(String cubeId, String id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HICubeDimension cd where cd.hiMetadataCube.id=:cubeId and cd.dimId=:id ");
        query.setParameter("cubeId", Integer.valueOf(cubeId));
        query.setParameter("id", id);
        return (HICubeDimension) query.uniqueResult();

    }

    @Override
    public HIDimensionHierarchy findDimensionHierarchy(Integer id, String id1) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HIDimensionHierarchy dh where dh.hiCubeDimension.id=:id and dh.hierarchyId =:id1");
        query.setParameter("id", id);
        query.setParameter("id1", id1);
        return (HIDimensionHierarchy) query.uniqueResult();

    }

    @Override
    public List<HIMetadataCube> findAllCubeWithResourceId(Integer resourceId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HIMetadataCube cube where  cube.hiResource.resourceId=:cubeRes ");

        query.setParameter("cubeRes", resourceId);
        query.setCacheable(true);
        return (List<HIMetadataCube>) query.list();
    }

    @Override
    public HICubeMeasure findCubeMeasure(Integer cubeId, String measureId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HICubeMeasure dh where dh. hiMetadataCube.id=:id and dh.measureId =:id1");
        query.setParameter("id", cubeId);
        query.setParameter("id1", measureId);
        return (HICubeMeasure) query.uniqueResult();
    }

    @Override
    public HICubeHierarchyLevel findHICubeHierarchyLevel(Integer hiecId, String levelId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HICubeHierarchyLevel dh where dh.hiDimensionHierarchy.id=:id and dh.levelId =:id1");
        query.setParameter("id", hiecId);
        query.setParameter("id1", levelId);
        return (HICubeHierarchyLevel) query.uniqueResult();
    }

    @Override
    public void delete(Object anyObject) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.delete(anyObject);
    }


	@Override
	public HIMetadataCube findCubeByResourceId(Integer resourceId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "FROM HIMetadataCube where hiResource.resourceId = :resourceId";
			Query query = session.createQuery(hql);
			query.setParameter("resourceId", resourceId);
			return (HIMetadataCube) query.uniqueResult();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching cube.");
			return null;
		}
	}


	@Override
	public List<HICubeDimension> findAllCubeDimentions(Integer cubeId) {
		List<HICubeDimension> cubeDimensions=null;
		try {
	        Session currentSession = sessionFactory.getCurrentSession();
	        Query query = currentSession.createQuery("FROM HICubeDimension dim WHERE dim.hiMetadataCube.id=:cubeId");
	        query.setParameter("cubeId", cubeId);
	        cubeDimensions=query.list();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
        return cubeDimensions;
	}


	@Override
	public List<HIDimensionHierarchy> findAllDimHierarchy(Integer dimId) {
		List<HIDimensionHierarchy> hiDimensionHierarchies=null;
		try {
	        Session currentSession = sessionFactory.getCurrentSession();
	        Query query = currentSession.createQuery("FROM HIDimensionHierarchy dim WHERE dim.hiCubeDimension.id=:dimId");
	        query.setParameter("dimId", dimId);
	        hiDimensionHierarchies=query.list();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
        return hiDimensionHierarchies;
	}


	@Override
	public List<HICubeHierarchyLevel> findAllHierarchyLevels(Integer hirId) {
		List<HICubeHierarchyLevel> hiCubeHierarchyLevels=null;
		try {
	        Session currentSession = sessionFactory.getCurrentSession();
	        Query query = currentSession.createQuery("FROM HICubeHierarchyLevel dim WHERE dim.hiDimensionHierarchy.id=:hirId");
	        query.setParameter("hirId", hirId);
	        hiCubeHierarchyLevels=query.list();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
        return hiCubeHierarchyLevels;
	}


	@Override
	public List<HICubeMeasure> findAllCubeMeasuresByCubeId(Integer cubeId) {
		List<HICubeMeasure> hiCubeMeasures=null;
		try {
	        Session currentSession = sessionFactory.getCurrentSession();
	        Query query = currentSession.createQuery("FROM HICubeMeasure dim WHERE dim.hiMetadataCube.id=:cubeId");
	        query.setParameter("cubeId", cubeId);
	        hiCubeMeasures=query.list();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
        return hiCubeMeasures;
	}
}
