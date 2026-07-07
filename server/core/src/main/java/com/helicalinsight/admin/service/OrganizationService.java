package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for Organization service layer which is implemented by
 * concrete OrganizationServiceImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public interface OrganizationService {

    /**
     * Implementation service class method add the organization persistent
     * object in database
     *
     * @param organization organization to be aded
     * @return id of newly added organization
     */

    int add(Organization organization);

    /**
     * Implementation service class method update the organization persistent
     * object
     *
     * @param organization persistent object
     */

    void edit(Organization organization);

    /**
     * Implementation service class method delete persistent object organization
     * by organization id from database
     *
     * @param organizationId organization id
     */

    void delete(int organizationId);

    /**
     * Implementation service class method return the organization object by
     * organization id from the database
     *
     * @param organizationId organization id
     * @return organization object
     */

    Organization getOrganization(int organizationId);

    /**
     * Implementation service class method return the organization object by
     * organization name from the database
     *
     * @param organizationName organization name
     * @return organization object
     */

    Organization getOrganization(String organizationName);

    /**
     * Implementation service class method return the list of all existing
     * organizations from the database
     *
     * @return list of all existing organization
     */

    List<Organization> getAllOrganization(LimitOffsetModel limitOffsetModel);

    List<Organization> findAllOrganization(LimitOffsetModel limitOffsetModel);

    Organization findOrganization(Integer orgId, LimitOffsetModel limitOffsetModel);

    List<Organization> searchOrganization(String organizationName, Integer orgId, LimitOffsetModel limitOffsetModel);
	
	Organization getOrganizationForRecycleBinCondition(String organizationName);

	void restoreOrganization(Integer id);

}
