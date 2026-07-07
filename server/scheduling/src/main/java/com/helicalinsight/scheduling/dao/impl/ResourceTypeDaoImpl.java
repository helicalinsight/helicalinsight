package com.helicalinsight.scheduling.dao.impl;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.dao.ResourceTypeDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

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
public class ResourceTypeDaoImpl implements ResourceTypeDao {
	
    private static final Logger logger = LoggerFactory.getLogger(JobParametersDaoImpl.class);
    
    @Autowired
    SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;
    /**
     * addResourceType(ResourceType resourceType)
     * Stores {@link ResourceType} entity/object in database.
     * @param resourceType			object for adding.
     * @return resourceType id in Long format.
     */
    @Override
    public Long addResourceType(ResourceType resourceType) {
        try {
            session.getCurrentSession().save(resourceType);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return resourceType.getResourceTypeId();
    }
    /**
     * editResourceType(ResourceType resourceType)
     * Edits the resouseType object in a database.
     * @param resourceType     new object for editing old one.
     */
    @Override
    public void editResourceType(ResourceType resourceType) {
        try {
            session.getCurrentSession().update(resourceType);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * deleteResourceType(Long resourceTypeId)
     * This method is responsible for deleting resourceType using id.
     * @param resourceTypeId      id in long format
     */
    @Override
    public void deleteResourceType(Long resourceTypeId) {
        try {
            session.getCurrentSession().delete(getResourceType(resourceTypeId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getResourceType(Long resourceTypeId)
     * it returns the ResourceType object from database.
     * @param resourceTypeId    id for fetching object from database.
     * @return ResourceType object .
     */
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
    /**
     * findUniqueResourceType(ResourceType sampleResourceType)
     *  Returns {@code null} as this method is not implemented.
     *  @return {@code null}.
     */
    @Override
    public ResourceType findUniqueResourceType(ResourceType sampleResourceType) {
        return null;
    }
    /**
     * getResourceTypeByTypeAndExtension(String type, String extension)
     * Retrieves a {@link ResourceType} entity based on the provided resource type and extension.
     *
     * @param type      			type of the resource.
     * @param extension 			extension of the resource.
     * @return The retrieved {@link ResourceType} object or {@code null} if not found.
     */
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
    /**
     * deleteAllResourceType()
     * Deletes all ResourceType from the database.
     */
    @Override
    public void deleteAllResourceType() {
        try {
            session.getCurrentSession().createQuery("delete from ResourceType").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getAllResourceTypes()
     * Returns a list of all resource types.
     *
     * @return A list containing all available {@link ResourceType} objects , otherwise {@code null} if not found.
     */
    @Override
    public List<ResourceType> getAllResourceTypes() {
    	
        List<ResourceType> listOfResourceTypes = new ArrayList<>();
        try {
            listOfResourceTypes = ( List<ResourceType>)session.getCurrentSession().createQuery("From ResourceType ").list();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return listOfResourceTypes;
    }
}
