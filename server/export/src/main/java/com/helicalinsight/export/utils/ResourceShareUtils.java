package com.helicalinsight.export.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.ProfileDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.service.RecycleBinHandler;
import com.helicalinsight.resourcedb.Deleted;
/**
 * Utility class for managing resource sharing, including user, role, organization, and profile operations.
 */
@Component
public class ResourceShareUtils {

	@Autowired
	@Qualifier("userDetailsService")
	protected UserService userService;
	@Autowired
	protected OrganizationService orgService;
	@Autowired
	protected RoleService roleService;
	@Autowired
	protected ProfileService profileService;
	@Autowired
	protected ResourceDTOMapper mapper;

	@Autowired
	@Lazy
	private RecycleBinHandler recycleBinHandler;


    /**
     * Gets an existing role or inserts a new one into the database.
     * If the role with the same name exists, the existing role is returned; otherwise, a new role is added.
     *
     * @param role 		role to retrieve or insert.
     * @return The existing or newly inserted role.
     */
	public Role getOrInsertRole(RoleDTO role) {
		
		OrganizationDTO orgDto = role.getOrganization();
		Organization org = orgDto != null ? getOrInsertOrganiation(orgDto) : null;
		Role dbRole = org == null ?roleService.findRoleByNameNullOrg(role.getRole_name()):roleService.findRoleByNameNOrgId(role.getRole_name(),org.getId());
		
		if (dbRole != null) {
			return dbRole;
		} else {
			dbRole = new Role();
			if(org != null) dbRole.setOrg_id(org.getId());
			Organization organization =  getOrInsertOrganiation(role.getOrganization());
			dbRole.setOrganization(organization);
			dbRole.setRole_name(role.getRole_name());
			roleService.add(dbRole);
			return dbRole;
		}
	}
	/**
     * Retrieves an existing organization or inserts a new one into the database.
     * If the organization with the same name exists, the existing organization is returned; otherwise, a new organization is added.
     *
     * @param organization 		 organization to retrieve or insert.
     * @return The existing or newly inserted organization.
     */
	public Organization getOrInsertOrganiation(OrganizationDTO organization) {
		return getOrInsertOrganiation(organization, null);
	}

	public Organization getOrInsertOrganiation(OrganizationDTO organization, ImportManagerContext context) {
		if (organization == null) {
			return null;
		}
		Organization dbOrg = orgService.getOrganizationForRecycleBinCondition(organization.getOrg_name());
		if (dbOrg != null) {
			if (Boolean.TRUE.equals(dbOrg.isDeleted()) && context == null) {
				return null;
			}
			applyOrganizationRecycleBin(organization.getId(), dbOrg, context);
			return dbOrg;
		}

		Organization entity = new Organization();
		entity.setOrg_name(organization.getOrg_name());
		entity.setOrg_desc(organization.getOrg_desc());
		entity.setDeleted(false);
		orgService.add(entity);
		applyOrganizationRecycleBin(organization.getId(), entity, context);
		return entity;
	}
	/**
     * Retrieves an existing user or inserts a new one into the database.
     * If the user with the same username and organization exists, the existing user is returned; otherwise, a new user is added.
     *
     * @param user 		 user to retrieve or insert.
     * @return The existing or newly inserted user.
     */
	public User getOrInsertUser(UserDTO user) {
		return getOrInsertUser(user, null);
	}

	public User getOrInsertUser(UserDTO user, ImportManagerContext context) {
		if (user == null) {
			return null;
		}
		Organization dbOrg = resolveOrganizationForUser(user.getOrganization(), context);
		User dbUser = findExistingUser(user.getUsername(), dbOrg, context != null);

		if (dbUser != null) {
			if (!Boolean.TRUE.equals(dbUser.isDeleted())) {
				updateUser(user, dbUser);
				upsertProfiles(user.getProfile(), dbUser);
			}
		} else {
			dbUser = createUser(user, dbOrg);
		}
		applyUserRecycleBin(user.getId(), dbUser, context);
		return dbUser;
	}

	private Organization resolveOrganizationForUser(OrganizationDTO fileOrg, ImportManagerContext context) {
		if (fileOrg == null) {
			return null;
		}
		Organization dbOrg = orgService.getOrganizationForRecycleBinCondition(fileOrg.getOrg_name());
		if (dbOrg == null) {
			dbOrg = new Organization();
			dbOrg.setOrg_name(fileOrg.getOrg_name());
			dbOrg.setOrg_desc(fileOrg.getOrg_desc());
			dbOrg.setDeleted(false);
			orgService.add(dbOrg);
		}
		applyOrganizationRecycleBin(fileOrg.getId(), dbOrg, context);
		return dbOrg;
	}

	private User findExistingUser(String username, Organization dbOrg, boolean includeDeleted) {
		User dbUser;
		if (dbOrg != null) {
			dbUser = userService.findUserByNameNOrgId(username, dbOrg.getId(), Deleted.FALSE);
			if (dbUser == null && includeDeleted) {
				dbUser = userService.findUserByNameNOrgId(username, dbOrg.getId(), Deleted.TRUE);
			}
		} else {
			dbUser = userService.findUserByNameNorgNull(username, Deleted.FALSE);
			if (dbUser == null && includeDeleted) {
				dbUser = userService.findUserByNameNorgNull(username, Deleted.TRUE);
			}
		}
		return dbUser;
	}

	private User createUser(UserDTO user, Organization dbOrg) {
		User dbUser = new User();
		List<RoleDTO> roles = user.getRoles();
		List<Role> dbRoles = new ArrayList<>();
		for (RoleDTO role : roles) {
			dbRoles.add(getOrInsertRole(role));
		}
		dbUser.setRoles(dbRoles);
		if (dbOrg != null) {
			dbUser.setOrganization(dbOrg);
			dbUser.setOrg_id(dbOrg.getId());
		}
		dbUser.setEmailAddress(user.getEmailAddress());
		dbUser.setEnabled(user.isEnabled());
		dbUser.setIsExternallyAuthenticated(user.getIsExternallyAuthenticated());
		dbUser.setPassword(user.getPassword());
		dbUser.setUsername(user.getUsername());
		dbUser.setDeleted(false);
		userService.addUser(dbUser);
		upsertProfiles(user.getProfile(), dbUser);
		return dbUser;
	}

	private void applyUserRecycleBin(int exportUserId, User user, ImportManagerContext context) {
		if (context != null) {
			recycleBinHandler.importUser(exportUserId, user, context);
		}
	}

	private void applyOrganizationRecycleBin(int exportOrgId, Organization organization, ImportManagerContext context) {
		if (context != null) {
			recycleBinHandler.importOrganization(exportOrgId, organization, context);
		}
	}
	/**
     * Updates or insert the profiles associated with a user.
     *
     * @param profiles 		list of profiles to upsert.
     * @param user     		user for whom the profiles are upserted.
     */
	public void upsertProfiles(List<ProfileDTO> profiles , User user) {
		for(ProfileDTO profile : profiles) {
			final Profile existing = profileService.getProfileByNameAndUserId(profile.getProfile_name(),user.getId());
			if(existing == null) {
				Profile dbProfile = mapper.map(profile);
				dbProfile.setUser_id(user.getId());
				profileService.add(dbProfile);
			}
			else {
				existing.setProfile_value(profile.getProfile_value());
				profileService.edit(existing);
			}
		}
	}
	/**
     * Updates an existing user with the information from another user.
     *
     * @param fromFile The user with updated information.
     * @param fromDb   The existing user to be updated.
     */
	public void updateUser(UserDTO fromFile , User fromDb) {
		fromDb.setEmailAddress(fromFile.getEmailAddress());
		fromDb.setIsExternallyAuthenticated(fromFile.getIsExternallyAuthenticated());
		List<Role> roles =  Optional.ofNullable(fromFile.getRoles())
					.orElse(Collections.emptyList())
					.stream()
					.map(this::getOrInsertRole)
					.toList();
					
		fromDb.setRoles(roles);
		fromDb.setEnabled(fromFile.isEnabled());
		userService.editUser(fromDb);
	}
	
	public User resolveUser(Object createdByObj) {
		return resolveUser(createdByObj, null);
	}

	public User resolveUser(Object createdByObj, ImportManagerContext context) {
		User createdBy = null;
		boolean shouldFallBack = true;

		if (createdByObj instanceof User user) {
			if (user.getUsername() != null) {
				createdBy = getOrInsertUser(mapper.map(user), context);
				shouldFallBack = false;
			}
		}
		
		else if (createdByObj instanceof String idStr && StringUtils.isNotBlank(idStr) && !"null".equalsIgnoreCase(idStr)
				&& StringUtils.isNumeric(idStr)) {
			
			User existing = userService.findUser(Integer.parseInt(idStr));
			if (existing != null) {
				createdBy = existing;
				shouldFallBack = false;
			}
		}
		
		if (shouldFallBack) {
			JsonObject settingsJson = JsonUtils.newGetSettingsJson();
			String defaultOwnerId = GsonUtility.optString(settingsJson, "defaultOwnerId");
			if (StringUtils.isNotBlank(defaultOwnerId) && !"null".equalsIgnoreCase(defaultOwnerId)) {
				User user = userService.findUser(Integer.parseInt(defaultOwnerId));
				if (user != null) {
					createdBy = user;
					if (context != null && StringUtils.isNotBlank(context.getCurrentResourceUrl())) {
						context.addWarning(context.getCurrentResourceUrl(),
								"Resource owner not found; assigned default owner (id=" + defaultOwnerId + ").");
					}
				}
			}
		}
		return createdBy;
	}

}
