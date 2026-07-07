package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is service layer of application and this class implements the
 * RoleService interface and responsible for role transactional activities like
 * add, edit, delete, list all organization.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Service(value = "roleServiceImpl")
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private static final String roleNameRegex = "[A-Za-z0-9._\\s$@+!&-/]+";
    /**
     * Spring dependency injection facilities which instantiate the
     * roleDao bean from spring container
     */
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    /**
     * this is overloaded method responsible for transaction of saving role
     * object
     */

    @Transactional
    public int add(Role role) {
        logger.debug("adding role");
        if(AdminUtils.isValidName(role.getRole_name(), roleNameRegex)) {
        	return roleDao.add(role);
        }
        throw new EfwServiceException("Role name can only use A-Z, a-z, 0-9, ., @ , $, +, !, /, & and _ and can have spaces.");
    }

    /**
     * this is overloaded method responsible for transaction of updating role
     * object
     */

    @Transactional
    public void edit(Role role) {
    	if(AdminUtils.isValidName(role.getRole_name(), roleNameRegex)) {
        	 roleDao.edit(role);
        }
    	else {
    		throw new EfwServiceException("Role name can only use A-Z, a-z, 0-9, ., @ , $, +, !, /, & and _ and can have spaces.");
    	}
    }

    /**
     * this is overloaded method responsible for transaction of deleting role
     * object
     */

    @Transactional
    public void delete(int roleId) {
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
        List<User> allUsers = userDao.getAllUsers(0, 1, limitOffsetModel,true);
        allUsers = userDao.getAllUsers(0, limitOffsetModel.getTotalCount(), limitOffsetModel,true);
        Role role = roleDao.getRole(roleId);
        for(User user:allUsers){
            List<Role> roles = user.getRoles();
            if (roles.contains(role)) {
                roles.remove(role);
                userDao.editUser(user);
            }
        }
        roleDao.delete(roleId);
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by id and return role object
     *
     * @param roleId object
     */

    @Transactional
    public Role getRole(int roleId) {
        return roleDao.getRole(roleId);
    }

    /**
     * this is overloaded method responsible for transaction of loading all
     * existing roles and return the list of roles
     *
     * @return list of role objects
     */
    @Transactional
    public List<Role> getAllRole(LimitOffsetModel limitOffsetModel) {
        return roleDao.getAllRole(limitOffsetModel);
    }


    @Transactional
    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount) {
        return roleDao.getAllRole(offset, limit, pageCount);
    }

    @Transactional
    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by role name and return role object
     *
     * @param name name
     * @return role object
     */

    @Transactional
    public Role findByName(String name) {
        return roleDao.findByName(name);
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by role name, organization id and return role object
     *
     * @param role_name role name
     * @param org_id    organization id
     * @return role object
     */

    @Transactional
    public Role findRoleByNameNOrgId(String role_name, Integer org_id) {
        return roleDao.findRoleByNameNOrgId(role_name, org_id);
    }

    /**
     * this is overloaded method responsible for transaction of deleting role
     * object by organization id
     *
     * @param org_id organization id
     */

    @Transactional
    public void deleteOrganization(int org_id) {
        roleDao.deleteOrganization(org_id);
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by organization id and return list of role objects
     *
     * @param org_id organization id
     * @return list of role object
     */

    @Transactional
    public List<Role> getOrganizationRoles(Integer org_id, LimitOffsetModel limitOffsetModel) {
        return roleDao.getOrganizationRoles(org_id, limitOffsetModel);
    }

    @Transactional
    public List<Role> searchUserRoles(String roleName, int offset, int limit, Integer orgId,
                                      LimitOffsetModel pageCount) {
        return roleDao.searchUserRoles(roleName, offset, limit, orgId, pageCount);
    }

    @Transactional
    public List<Role> findAllRoles(LimitOffsetModel pageCount) {
        return roleDao.findAllRoles(pageCount.getOffset(), pageCount.getLimit(), pageCount.getSearchPhrase(),
                pageCount.getSearchOn(), pageCount);
    }

    @Override
    public boolean isRoleExists(int roleId) {
        return roleDao.isRoleExists(roleId);
    }

    @Transactional
	@Override
	public Role findRoleByNameNOrgName(String roleName, String orgName) {
		return roleDao.findRoleByNameNOrgName(roleName, orgName);
	}

    @Transactional
	@Override
	public Role findRoleByNameNullOrg(String roleName) {
		return roleDao.findRoleByNameNullOrg(roleName);
	}
}
