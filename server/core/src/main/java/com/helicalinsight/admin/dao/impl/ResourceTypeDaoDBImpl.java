package com.helicalinsight.admin.dao.impl;


import com.helicalinsight.admin.dao.ResourceTypeDaoDB;
import com.helicalinsight.admin.model.ResourceType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
@Repository
public class ResourceTypeDaoDBImpl implements ResourceTypeDaoDB {
    private static final Logger logger = LoggerFactory.getLogger(ResourceTypeDaoDBImpl.class);
    
    @Autowired
    SessionFactory session;

    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;
    
    @Override
    public Long addResourceType(ResourceType resourceType) {
        try {
            session.getCurrentSession().save(resourceType);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return resourceType.getResourceTypeId();
    }

    @Override
    public void editResourceType(ResourceType resourceType) {
        try {
            session.getCurrentSession().update(resourceType);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void deleteResourceType(Long resourceTypeId) {
        try {
            session.getCurrentSession().delete(getResourceType(resourceTypeId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public ResourceType getResourceType(Long resourceTypeId) {
        ResourceType resourceType = null;
        try {
            resourceType = (ResourceType) session.getCurrentSession().get(ResourceType.class, resourceTypeId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return resourceType;
    }

    @Override
    public ResourceType findUniqueResourceType(ResourceType sampleResourceType) {
        return null;
    }

    @Override
    public ResourceType getResourceTypeByTypeAndExtension(String type, String extension) {
        ResourceType resourceType = null;
        try {
            org.hibernate.query.Query query = session.getCurrentSession().createQuery("From ResourceType where name= :name and extension = :extension");
            query.setParameter("name", type);
            query.setParameter("extension", extension);
            resourceType = (ResourceType) query.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return resourceType;
    }

    @Override
    public void deleteAllResourceType() {
        try {
            session.getCurrentSession().createQuery("delete from ResourceType").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public List<ResourceType> getAllResourceTypes() {
        List<ResourceType> listOfResourceTypes = new ArrayList<>();

        try {
            listOfResourceTypes = ( List<ResourceType>) session.getCurrentSession().createQuery("From ResourceType").list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return listOfResourceTypes;
    }
}
