package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is service layer of application and this class implements the
 * OrganizationService interface and responsible for organization transactional
 * activities like add, edit, delete, list all organization etc
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);
    private static final String orgNameRegex = "[A-Za-z0-9._@$-]+";
    /**
     * Springs' dependency injection facilities which instantiate the
     * OrganizationDao bean from spring container
     */

    @Autowired
    private OrganizationDao organizationDao;

    /**
     * this is overloaded method responsible for transaction of saving
     * organization and return the id of newly added organization
     *
     * @return organization id
     */

    @Transactional
    public int add(Organization organization) {
    	if(AdminUtils.isValidName(organization.getOrg_name(), orgNameRegex)) {
    		return organizationDao.add(organization);
    	}
		throw new EfwServiceException("Organization name can only use A-Z, a-z, 0-9, ., @, $, - and _ and cannot have spaces");
    }

    /**
     * this is overloaded method responsible for transaction of updating
     * organization
     */

    @Transactional
    public void edit(Organization organization) {
    	if(AdminUtils.isValidName(organization.getOrg_name(), orgNameRegex)) {
    		organizationDao.edit(organization);
    	} else {
    		throw new EfwServiceException("Organization name can only use A-Z, a-z, 0-9, ., @, $, - and _ and cannot have spaces");
    	}
    }

    /**
     * this is overloaded method responsible for transaction of deleting
     * organization by organization id
     */

    @Transactional
    public void delete(int organizationId) {
        organizationDao.delete(organizationId);
    }

    /**
     * this is overloaded method responsible for transaction of loading
     * organization by organization id
     *
     * @param organizationId id
     */

    @Transactional
    public Organization getOrganization(int organizationId) {
        return organizationDao.getOrganization(organizationId);
    }

    /**
     * this is overloaded method responsible for transaction of loading list of
     * all existing organization and return list of existing organization
     * objects
     *
     * @return list of all existing organization objects
     */

    @Transactional
    public List<Organization> getAllOrganization(LimitOffsetModel pageCount) {
        return organizationDao.getAllOrganization(pageCount.getOffset(), pageCount.getLimit(), pageCount);
    }

    @Transactional
    public List<Organization> findAllOrganization(LimitOffsetModel pageCount) {
        return organizationDao.findAllOrganization(pageCount.getOffset(), pageCount.getLimit(),
                pageCount.getSearchPhrase(), pageCount.getSearchOn(), pageCount);
    }

    @Transactional
    public Organization findOrganization(Integer orgId, LimitOffsetModel pageCount) {
        return organizationDao.findOrganization(orgId, pageCount.getOffset(), pageCount.getLimit(),
                pageCount.getSearchPhrase(), pageCount.getSearchOn());
    }

    /**
     * this is overloaded method responsible for transaction of loading
     * organization by name
     *
     * @return organization object
     */

    @Transactional
    public Organization getOrganization(String organizationName) {
        logger.debug("Returning organization");
        return organizationDao.getOrganization(organizationName);
    }

    @Transactional
    public List<Organization> searchOrganization(String organizationName, Integer orgId, LimitOffsetModel pageCount) {
        return organizationDao.searchOrganization(organizationName, orgId, pageCount);
    }

	@Override
	@Transactional
	public Organization getOrganizationForRecycleBinCondition(String organizationName) {
		return organizationDao.getOrganizationForRecycleBinCondition(organizationName);
	}

	@Transactional
	@Override
	public void restoreOrganization(Integer id) {
		organizationDao.restoreOrganization(id);
	}
    
}
