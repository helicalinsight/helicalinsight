package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for Role activity which is implemented by concrete
 * RoleDaoImpl class
 *
 * @author Muqtar
 * @version 1.1
 * @since 1.0
 */
public interface RoleDao {

    /**
     * Implementation class method add the role persistent object in database
     *
     * @param role persistent object
     */
    public int add(Role role);

    /**
     * Implementation class method update the role persistent object in database
     *
     * @param role persistent object
     */

    public void edit(Role role);

    /**
     * Implementation class method delete the role persistent object from
     * database
     *
     * @param roleId role id
     */

    public void delete(int roleId);

    /**
     * Implementation class method return the role object from database by role
     * id
     *
     * @param roleId role id
     * @return role object
     */

    public Role getRole(int roleId);

    /**
     * Implementation class method load return the list of role object from
     * database
     *
     * @return list of role objects
     */
    public List<Role> getAllRole(LimitOffsetModel pageCount);

    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount);

    /**
     * Implementation class method return the role object from database by role
     * name
     *
     * @param role_name role name
     * @return role object
     */
    public Role findByName(String role_name);

    /**
     * Implementation class method load the role object from database by role
     * name and organization id
     *
     * @param role_name role name
     * @param org_id    role id
     * @return role object
     */

    public Role findRoleByNameNOrgId(String role_name, Integer org_id);

    /**
     * Implementation class method delete role object from database by
     * organization id
     *
     * @param org_id organization id
     */

    public void deleteOrganization(int org_id);

    /**
     * Implementation class method return the list of role object from database
     * by organization id
     *
     * @param org_id organization id
     * @return list of role objects
     */

    public List<Role> getOrganizationRoles(Integer org_id, LimitOffsetModel pageCount);

    public List<Role> searchUserRoles(String roleName, int offest, int limit, Integer orgId,
                                      LimitOffsetModel pageCount);

    public boolean isRoleExists(int roleId);

    public List<Role> findAllRoles(int offset, int limit, String searchPhrase, String searchOn,
                                   LimitOffsetModel pageCount);

    public Long getRoleCount();

    public List<Role> getAllRoles();

	public Role findRoleByNameNOrgName(String roleName, String orgName);

	public Role findRoleByNameNullOrg(String roleName);
}
