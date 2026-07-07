package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for Role service which is implemented by concrete
 * RoleServiceImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public interface RoleService {

    /**
     * Implementation service class method add the role persistent object in
     * database
     *
     * @param role persistent object
     */

    public int add(Role role);

    /**
     * Implementation service class method update the role persistent object in
     * database
     *
     * @param role persistent object
     */

    public void edit(Role role);

    /**
     * Implementation service class method delete the role persistent object
     * from database
     *
     * @param roleId role id
     */

    public void delete(int roleId);

    /**
     * Implementation service class method return the role object from database
     * by role id
     *
     * @param roleId role id
     * @return role object
     */

    public Role getRole(int roleId);

    /**
     * Implementation service class method load return the list of role object
     * from database
     *
     * @return list of role objects
     */

    public List<Role> getAllRole(LimitOffsetModel limitOffsetModel);

    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount);

    public List<Role> getAllRoles();

    /**
     * Implementation service class method return the role object from database
     * by role name
     *
     * @param role_name role name
     * @return role object
     */

    public Role findByName(String role_name);

    /**
     * Implementation service class method load the role object from database by
     * role name and organization id
     *
     * @param role_name role name
     * @param org_id    role id
     * @return role object
     */

    public Role findRoleByNameNOrgId(String role_name, Integer org_id);
    
    Role findRoleByNameNOrgName(String roleName, String orgName);
    
    Role findRoleByNameNullOrg(String roleName);
    /**
     * Implementation service class method delete role object from database by
     * organization id
     *
     * @param org_id organization id
     */

    public void deleteOrganization(int org_id);

    /**
     * Implementation service class method return the list of role object from
     * database by organization id
     *
     * @param org_id organization id
     * @return list of role objects
     */

    public List<Role> getOrganizationRoles(Integer org_id, LimitOffsetModel pageCount);

    public List<Role> searchUserRoles(String roleName, int offset, int limit, Integer orgId,
                                      LimitOffsetModel pageCount);

    public boolean isRoleExists(int roleId);

    public List<Role> findAllRoles(LimitOffsetModel pageCount);
}
