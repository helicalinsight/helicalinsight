package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for User activity which is implemented by concrete
 * OrganizationDaoImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
public interface OrganizationDao {

    /**
     * Implementation class method add the organization persistent object in
     * database
     *
     * @param organization persistent object
     * @return id of newly added organization
     */

    public int add(Organization organization);

    /**
     * Implementation class method edit the organization persistent object
     *
     * @param organization persistent object
     */

    public void edit(Organization organization);

    /**
     * Implementation class method delete persistent object organization by
     * organization id from database
     *
     * @param organizationId organization id
     */

    public void delete(int organizationId);

    /**
     * Implementation class method return the organization object by
     * organization id from the database
     *
     * @param organizationId organization id
     * @return organization object
     */

    public Organization getOrganization(int organizationId);

    /**
     * Implementation class method return the organization object by
     * organization name from the database
     *
     * @param organizationName organization name
     * @return organization object
     */

    public Organization getOrganization(String organizationName);

    /**
     * Implementation class method return the list of all existing organizations
     * from the database
     *
     * @return list of all existing organization
     */

    public List<Organization> getAllOrganization(int offset, int limit, LimitOffsetModel limitOffsetModel);

    public List<Organization> searchOrganization(String organizationName, Integer orgId,
                                                 LimitOffsetModel limitOffsetModel);

    public List<Organization> findAllOrganization(int offest, int limit, String searchPhrase, String searchOn,
                                                  LimitOffsetModel limitOffsetModel);

    public Organization findOrganization(Integer orgId, int offest, int limit, String searchPhrase, String searchOn);
    
    Organization getOrganizationForRecycleBinCondition(String organizationName);

	void restoreOrganization(Integer id);
}
