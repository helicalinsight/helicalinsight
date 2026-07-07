package com.helicalinsight.export.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.export.utils.ResourceShareUtils;
/**
 * DatasourceShareHandler class do the sharing of permissions for both EFWD and global connections.
 * Manages the import and retrieval of security details for these connections.
 */
@Component
public class DatasourceShareHandler {

	@Autowired
	@Qualifier("userDetailsService")
	protected UserService userService;
	@Autowired
	protected OrganizationService orgService;
	@Autowired
	protected RoleService roleService;
	@Autowired
	private GlobalConnectionService globalConnectionService;
	
	@Autowired
	private EFWDConnectionService efwdConnectionService;
	
	@Autowired
	private ResourceShareUtils shareUtils;
	
	@Autowired
	private ResourceDTOMapper mapper;
	
	/**
	 * Returns the security details for a global connection and maps them to the corresponding connectionId.
	 * @param globalId          used to get list of Global connection security details.
	 * @param connectionId		id used as key for sharing purpose.
	 * @return {@code null} if list of security is null or empty, otherwise it returns the {@code MAP} of security list.
	 */
	public Map<Integer, List<GlobalConnectionSecurity>> getGlobalConnectionShare(int globalId, int connectionId) {
		Map<Integer, List<GlobalConnectionSecurity>> shareList = new HashMap<>();
		List<GlobalConnectionSecurity> permissions = globalConnectionService.findPermissionByConnectionId(globalId);
		if (permissions != null && !permissions.isEmpty()) {
			shareList.put(connectionId, permissions);
			return shareList;
		}
		return null;
	}
	/**
	 * 
	 * @param efwdConnId      id for fetching efwd connection security details.
	 * @return A list of HIEfwdConnSecurity containing the security details for the specified connection.
     *         Returns {@code null} if the list of security is null or empty.
	 */
	public List<HIEfwdConnSecurityDTO> getAdvancedConnectionShare(int efwdConnId){
		List<HIEfwdConnSecurityDTO> permissions = efwdConnectionService.findEFWDConnectionSecurityByConnectionId(efwdConnId);
		if(permissions != null && !permissions.isEmpty()) {
			return permissions;
		}
		return null;
	} 
	/**
	 * Imports the EFWD connection permissions to the database.
	 * @param connectionId        id for importing connection permission
	 * @param securities          List of HIEfwdConnSecurity containing the security details to be imported.
	 */
	public void importEFWDConnectionPermissions(int connectionId, List<HIEfwdConnSecurityDTO> securities) {
		
		for (HIEfwdConnSecurityDTO security : securities) {

			UserDTO user = security.getUserId();
			RoleDTO role = security.getRoleId();
			OrganizationDTO org = security.getOrgId();
			
			User dbUser = null;
			Role dbRole = null;
			Organization dbOrg = null;

			if (user != null) {
				dbUser = shareUtils.getOrInsertUser(user);
			}
			if (role != null) {
				dbRole = shareUtils.getOrInsertRole(role);
			}
			if (org != null) {
				dbOrg = shareUtils.getOrInsertOrganiation(org);
			}
			efwdConnectionService.updateOrInsert(connectionId, dbUser != null ? dbUser.getId() : null,
					dbRole != null ? dbRole.getId() : null, dbOrg != null ? dbOrg.getId() : null, security.getPermission());
		}
	}
	/**
     * Imports the global connection permissions to the database.
     *
     * @param globalId         id used to delete connection and set the connection
     * @param permissions      List of GlobalConnectionSecurity containing the security details to be imported.
     */
	public void importGlobalConnectionPermissions(int globalId, List<GlobalConnectionSecurity> permissions) {
		
		globalConnectionService.deleteGlobalConnectionSecurityById(globalId);
		if (permissions != null && !permissions.isEmpty()) {
			permissions.forEach(permission -> {
				permission.getGlobalConnections().setGlobalId(globalId);
				createNewSecurity(permission);
			});
		}
	}
	/**
	 * Creates new security connection using provided details.
	 * @param security     provides required data to add or create new security connection.
	 */
	private void createNewSecurity(GlobalConnectionSecurity security) {
		User user = security.getUserId();
		Role sharedRole = security.getRoleId();
		Organization org = security.getOrgId();
		Date date = new Date();
		security.setLastUpdatedTime(date);
		if(null != user && null == org) {
			user = shareUtils.getOrInsertUser(mapper.map(user));
			security.setUserId(user);
		}
		else if (null != sharedRole) {
			sharedRole = shareUtils.getOrInsertRole(mapper.map(sharedRole));
			security.setRoleId(sharedRole);
		} else {
			org = shareUtils.getOrInsertOrganiation(mapper.map(org));
			security.setOrgId(org);
		}
		globalConnectionService.addGlobalConnectionSecurity(security);
	}
	
	
	/**
	 * it compares the object of GlobalConnectionSecurity.
	 * @param o1	first Object of GlobalConnectionSecurity        
	 * @param o2	second object of GlobalConnectionSecurity
	 * @return `1` if condition matches or `-1` if condition failed
	 */
	private int compare(GlobalConnectionSecurity o1, GlobalConnectionSecurity o2) {

		if (o1.getUserId() != null && o2.getUserId() != null) {
			if (o1.getUserId().getUsername().equalsIgnoreCase(o2.getUserId().getUsername())
					&& o1.getPermission() == o2.getPermission()) {
				return 1;
			}
		}
		if (o1.getRoleId() != null && o2.getRoleId() != null) {
			if (o1.getRoleId().getRole_name().equalsIgnoreCase(o2.getRoleId().getRole_name())
					&& o1.getPermission() == o2.getPermission()) {
				return 1;
			}
		}
		if (o1.getOrgId() != null && o2.getOrgId() != null) {
			if (o1.getOrgId().getOrg_name().equalsIgnoreCase(o2.getOrgId().getOrg_name())
					&& o1.getPermission() == o2.getPermission()) {
				return 1;
			}
		}

		return -1;
	}
}
