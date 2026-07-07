package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinOrganization;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Query;

import com.helicalinsight.resourcesecurity.SecurityUtils;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;

import java.util.List;

/**
 * This class implements the OrganizationDao interface and responsible for
 * organization activities like add, edit, delete, list all organization.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
@SuppressWarnings("unchecked")
@Repository
public class OrganizationDaoImpl implements OrganizationDao {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationDaoImpl.class);
    private static final String IS_DELETED_FILTER = "isDeletedFilter";

    /**
     * @Autowired Spring's dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */

    @Autowired
    private SessionFactory session;

    @Autowired
    private HIRecycleBinService hiRecycleBinService;
    
    @Autowired
    private UserDao userDao;
    

    /**
     * this is override method get the current session from session factory
     * object and save the organization object
     *
     * @param organization organization object
     * @return return the id of currently added organization
     */

    @Override
    public int add(Organization organization) {

        try {
            if(organization!=null){
            	organization.setDeleted(false);
                getSession().save(organization);
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return organization.getId();
    }

    /**
     * this is override method get the current session from session factory
     * object and update the organization
     *
     * @param organization organization object
     */

    @Override
    public void edit(Organization organization) {
        try {
            getSession().update(organization);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    
    /**
     * This  method implements the soft delete for Organization,
     * and inserts a new record in 2 tables </br>
     * <b> 1.HIRecycleBinOrganization </b> </br>
     * <b> 2.HIRecycleBin </b> 
     * 
     *  @see HIRecycleBinOrganization 
     *  @see HIRecycleBin
     * 
     * @param organization
     * @since 6.0 
     * @author HDEV053 
     */
    
	private final void softDelete(Organization organization) {
		organization.setDeleted(true);
		HIRecycleBin bin = new HIRecycleBin();
		bin.setRecycleBinType(RecycleBinType.ORGANIZATION);
		bin.setDeletedOn(new Date());
		User deletedBy = userDao.findUser(Integer.valueOf(SecurityUtils.securityObject().getCreatedBy())); 
		bin.setDeletedBy(deletedBy);
		HIRecycleBinOrganization binItem = new HIRecycleBinOrganization();
		binItem.setOrganization(organization);
		binItem.setRecycleBin(bin);
		bin.setHiRecycleBinOrganization(binItem);
		hiRecycleBinService.save(bin);
		edit(organization);
	}

    /**
     * this is override method get the current session from session factory
     * object and delete the organization by organization id
     *
     * @param organizationId organization id
     */

    @Override
    public void delete(int organizationId) {
        try {
            Session currentSession = getSession();
            Organization organization = getOrganization(organizationId);
            if(!organization.isDeleted()) {
            	softDelete(organization);
            	return ;
            }
            currentSession.delete(organization);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the organization object by organization id
     *
     * @param organizationId organization id
     * @return organization object
     */

    @Override
    public Organization getOrganization(int organizationId) {
        Organization org = null;
        try {
        	Session currentSession = getSession();
        	currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            org = (Organization) currentSession.get(Organization.class, organizationId);
        } catch (Exception ex) {
            logger.error("Exception", ex);
        }
        return org;
    }

    @Override
    public List<Organization> findAllOrganization(int offset, int limit, String searchPhrase, String searchOn,
                                                  LimitOffsetModel pageCount) {
    	List<Organization> orgList=null;
    	try {
    		
			if ((searchOn == null) || ("".equalsIgnoreCase(searchOn)) || searchOn.trim().length() <= 0) {
				return getAllOrganization(offset, limit, pageCount);
			} else {

				Session currentSession = getSession();
				currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
				
				/*CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Organization> cr = cb.createQuery(Organization.class);
				Root<Organization> resource = cr.from(Organization.class);
				if ("name".equalsIgnoreCase(searchOn)) {
					searchPhrase = "%" + searchPhrase + "%";
					cr.select(resource).where(cb.equal(resource.get("org_name"), searchPhrase))
							.orderBy(cb.asc(resource.get("org_name")));
				}
				orgList = em.createQuery(cr).setFirstResult(offset).setMaxResults(limit).getResultList();*/
				
				if ("name".equalsIgnoreCase(searchOn)) {
					searchPhrase = "%" + searchPhrase + "%";
				}
				Query query=currentSession.createQuery("FROM Organization where org_name like :searchPhrase ORDER BY org_name ASC LIMIT :limit OFFSET :offset");
				query.setParameter("searchPhrase", searchPhrase);
				orgList = query.getResultList();
			}
		} catch (Exception e) {
			if(e instanceof NoResultException)
        		return null;
		}
    	return orgList;
        
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing organization objects
     *
     * @return list of organization objects
     */

    @Override
    public List<Organization> getAllOrganization(int offset, int limit, LimitOffsetModel limitOffsetModel) {
        List<Organization> organizationList = null;
        try {

        	Session currentSession = getSession();
        	currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
            Query query = currentSession.createQuery("from Organization");
            organizationList = ApplicationUtilities.castList(Organization.class,
                    query.setFirstResult(offset).setMaxResults(limit).getResultList());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        if (organizationList != null) {
            limitOffsetModel.setTotalCount(organizationList.size());
        }

        return organizationList;
    }

    @Override
    public Organization findOrganization(Integer orgId, int offset, int limit, String searchPhrase, String searchOn) {
        return getOrganization(orgId);
    }


    /**
     * this is override method get the current session from session factory
     * object and return the organization object by organization name
     *
     * @param organizationName organization name
     * @return organization object
     */

    @Override
    public Organization getOrganization(String organizationName) {

        Organization org = null;
        try {

        	Session currentSession = getSession();
        	currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        	Query query=currentSession.createQuery("FROM Organization where org_name=:organizationName");
        	query.setParameter("organizationName", organizationName);
            org = (Organization)query.getSingleResult();
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return org;
    }


    @Override
    public List<Organization> searchOrganization(String organizationName, Integer orgId,
                                                 LimitOffsetModel limitOffsetModel) {

        List<Organization> org = null;
        try {
        	Session currentSession = getSession();
        	currentSession.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
        	organizationName = "%" + organizationName + "%";
        	Query query=currentSession.createQuery("FROM Organization where org_name like :searchPhrase AND id=:orgId");
			query.setParameter("searchPhrase", organizationName);
			query.setParameter("id", orgId);
			org = query.getResultList();
        	
        } catch (Exception e) {
        	if(e instanceof NoResultException)
        		return null;
            logger.error("Exception", e);
        }
        return org;
    }
    
    
    private Session getSession() {
		Session currentSession = null;
		try {
			currentSession =  session.getCurrentSession();
		}
		catch (Exception e) {
			currentSession = session.openSession();
		}
		currentSession.disableFilter(IS_DELETED_FILTER);
		return currentSession;
	}

	@Override
	public Organization getOrganizationForRecycleBinCondition(String organizationName) {
		  Organization org = null;
	        try {
	        	Session currentSession = getSession();
	            SelectionQuery<Organization> organization = currentSession.createSelectionQuery("FROM Organization where org_name =:orgName",Organization.class);
	        	organization.setParameter("orgName", organizationName);
	            org = organization.uniqueResult();
	        } catch (Exception e) {
	            logger.error("Exception", e);
	        }
	        return org;
	}

	@Override
	public void restoreOrganization(Integer id) {
		try {
			String hql = """
					update Organization
					set deleted = false
					where id = :id
					  and deleted = true
					""";
			Session session = getSession();
			MutationQuery mutationQuery = session.createMutationQuery(hql);
			mutationQuery.setParameter("id", id);
			int count = mutationQuery.executeUpdate();
			logger.debug("No of updated Org(s) : {}", count);
		}
		catch (Exception e) {
			logger.error("Error occurred while restoring EFWD connection : {}" , id , e);
		}
	}
}
