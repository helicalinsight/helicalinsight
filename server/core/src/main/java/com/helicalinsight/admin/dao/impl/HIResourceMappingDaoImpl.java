package com.helicalinsight.admin.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.helicalinsight.admin.dao.HIResourceMappingDao;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;


@Repository
public class HIResourceMappingDaoImpl implements HIResourceMappingDao {
	
	
	private final SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger(HIResourceMappingDaoImpl.class);
	
	
	private static final String MAPPING_PARENT_PROJECTION = """
			new com.helicalinsight.admin.model.HIResource(
			    p.resourceId,
			    p.created_date,
			    p.isMigrated,
			    p.lastUpdatedTime,
			    p.parentId,
			    p.createdBy,
			    p.isFolder,
			    p.resourcePath,
			    p.resourceURL,
			    p.resourceTypeId,
			    p.title,
			    p.isVisible,
			    p.isDeleted
			)
			""";
	
	
	public HIResourceMappingDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Override
	public List<HIResourceMapping> findByParentId(Integer parentResourceId) {
		
		try {
			
			Session session = this.sessionFactory.getCurrentSession();
			SelectionQuery<HIResourceMapping> selectionQuery = 
					session.createSelectionQuery("FROM  HIResourceMapping mapping where mapping.parentResource.resourceId = :parentResourceId", HIResourceMapping.class);
	        selectionQuery.setParameter("parentResourceId", parentResourceId);

			return selectionQuery.getResultList();
		
		}
		catch (HibernateException e) {
			logger.error("Error fetching  HIResourceMapping with parentResourceId : {}", parentResourceId);
		}
		return Collections.emptyList();
	}

	@Override
	public void saveBatch(List<HIResourceMapping> mappingBatch) {
		
		try {
			Session session = this.sessionFactory.getCurrentSession();
			mappingBatch.forEach(session::persist);
		}
		catch (HibernateException e) {
			logger.error("Error saving mapping batch. Root cause : {}", e.getMessage());
		}
		
	}

	@Override
	public void save(HIResourceMapping mapping) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			session.persist(mapping);
		}
		catch (Exception e) {
			logger.error("Error saving mapping batch");
		}
	}

	@Override
	public void update(HIResourceMapping mapping) {
		
		try {
			Session session = this.sessionFactory.getCurrentSession();
			HIResourceMapping existingMapping =  session.get(HIResourceMapping.class, mapping.getId());
			if ( existingMapping != null ) {
				mapping.setParentResource(mapping.getParentResource());
				mapping.setChildResource(mapping.getChildResource());
				mapping.setId(mapping.getId());
				session.merge(mapping);
			}
			else {
				logger.error("Could not update HIResourceMapping");
			}
		}
		catch (HibernateException e) {
			logger.error("Error updating HIResourceMapping. Root cause : {}", e.getMessage());
		}
	}

	@Override
	public void deleteChildrenByParentId(Integer parentResourceId) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			findByParentId(parentResourceId).forEach(session::remove);
		}
		catch (HibernateException  e) {
			logger.error("Error deleting child resources with parent id : {} due to {}", parentResourceId, e.getMessage());
		}
	}
	
	@Override
	public List<HIResourceMapping> findMappingsByParentIdAndChildType(Integer parentId, Long typeId) {
		
		try {
			Session session = this.sessionFactory.getCurrentSession();
			SelectionQuery<HIResourceMapping> selectionQuery = session.createSelectionQuery("FROM HIResourceMapping mapping where mapping.parentResource.resourceId = :parentResourceId and mapping.childResource.resourceTypeId = :typeId",HIResourceMapping.class);
			selectionQuery.setParameter("parentResourceId", parentId);
			selectionQuery.setParameter("typeId", typeId);
			
			return selectionQuery.getResultList();
		}
		catch (HibernateException e) {
			logger.error("Error fetching HIResourceMapping  with parentId and child type. Root cause {} ", e.getMessage());
		}
		
		return Collections.emptyList();
	} 
	
	
	@Override
	public void deleteChildrenByParentIdAndType(Integer parentResourceId, Long typeId) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			findMappingsByParentIdAndChildType(parentResourceId, typeId)
			.forEach(session::remove);
		}
		catch (HibernateException e) {
			logger.error("Error deleting HIResourceMapping with parentId and childType");
		}
	}
	
	
	@Override
	public List<HIResource> findChildMappingsByChildResourceId(Integer childResourceId) {
		
		try {
			Session session = this.sessionFactory.getCurrentSession();
			SelectionQuery<HIResourceMapping> selectionQuery = session.createSelectionQuery("FROM HIResourceMapping mapping where mapping.childResource.resourceId = :childResourceId", HIResourceMapping.class);
			selectionQuery.setParameter("childResourceId", childResourceId);
			List<HIResourceMapping> mappings = selectionQuery.getResultList();
			List<HIResource> parentResources = new  ArrayList<>();
			for( HIResourceMapping mapping : mappings ) {
					parentResources.add(mapping.getParentResource());
			}
			return parentResources;
		}
		catch (HibernateException e) {
			logger.error("Error fetching child mappings by resource_id : {}", childResourceId);
			return Collections.emptyList();
		}
		
	}
	
	
	@Override
	public Map<Integer, List<HIResource>> findChildMappingsByChildResourceIds(List<Integer> childResourceIds) {
		Map<Integer, List<HIResource>> mappingsByChildId = new HashMap<>();
		if (childResourceIds == null || childResourceIds.isEmpty()) {
			return mappingsByChildId;
		}
		try {
			Session session = this.sessionFactory.getCurrentSession();
			String hql = "select mapping.childResource.resourceId, " + MAPPING_PARENT_PROJECTION
					+ " from HIResourceMapping mapping join mapping.parentResource p"
					+ " where mapping.childResource.resourceId in (:childResourceIds)";
			SelectionQuery<Object[]> selectionQuery = session.createSelectionQuery(hql, Object[].class);
			selectionQuery.setParameterList("childResourceIds", childResourceIds);
			for (Object[] row : selectionQuery.getResultList()) {
				Integer childId = (Integer) row[0];
				HIResource parentResource = (HIResource) row[1];
				mappingsByChildId.computeIfAbsent(childId, id -> new ArrayList<>()).add(parentResource);
			}
		} catch (HibernateException e) {
			logger.error("Error fetching child mappings by resource ids");
		}
		return mappingsByChildId;
	}
	
	/**
	@Override
	public Map<Integer, List<HIResource>> findChildMappingsByChildResourceIds(List<Integer> childResourceIds) {
		Map<Integer, List<HIResource>> mappingsByChildId = new HashMap<>();
		if (childResourceIds == null || childResourceIds.isEmpty()) {
			return mappingsByChildId;
		}
		try {
			Session session = this.sessionFactory.getCurrentSession();
			SelectionQuery<HIResourceMapping> selectionQuery = session.createSelectionQuery(
					"FROM HIResourceMapping mapping where mapping.childResource.resourceId in (:childResourceIds)",
					HIResourceMapping.class);
			selectionQuery.setParameterList("childResourceIds", childResourceIds);
			for (HIResourceMapping mapping : selectionQuery.getResultList()) {
				Integer childId = mapping.getChildResource().getResourceId();
				mappingsByChildId.computeIfAbsent(childId, id -> new ArrayList<>()).add(mapping.getParentResource());
			}
		} catch (HibernateException e) {
			logger.error("Error fetching child mappings by resource ids");
		}
		return mappingsByChildId;
	}
	**/
}
