package com.helicalinsight.export.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
			Organization organization = mapper.map(role.getOrganization());
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
		Organization dbOrg = orgService.getOrganizationForRecycleBinCondition(organization.getOrg_name());
		if (dbOrg != null) {
			if(Boolean.TRUE.equals(dbOrg.isDeleted())) {
				return null;
			}
			return dbOrg;
		}
		
		Organization entity = new Organization();
		entity.setOrg_name(organization.getOrg_name());
		entity.setOrg_desc(organization.getOrg_desc());
		entity.setDeleted(organization.getDeleted());
		
		orgService.add(entity);
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
		
		OrganizationDTO fileOrg = user.getOrganization();
		User dbUser = null;
		Organization dbOrg = null;
		if(fileOrg != null) {
			dbOrg =  orgService.getOrganizationForRecycleBinCondition(fileOrg.getOrg_name());
			if(dbOrg == null) {
				dbOrg = new Organization();
				dbOrg.setOrg_name(fileOrg.getOrg_name());
				dbOrg.setOrg_desc(fileOrg.getOrg_desc());
				dbOrg.setDeleted(fileOrg.getDeleted());
				orgService.add(dbOrg);
			}
			dbUser = userService.findUserByNameNOrgId(user.getUsername(),dbOrg.getId(),Deleted.FALSE);
		}
		else {
			dbUser = userService.findUserByNameNorgNull(user.getUsername(),Deleted.FALSE);
		}
		if (dbUser != null ) {
			if(Boolean.TRUE.equals(dbUser.isDeleted())) {
				return dbUser;
			}
			updateUser(user,dbUser);
			upsertProfiles(user.getProfile(),dbUser);
			return dbUser;
		}
		
		dbUser = new User();
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
		userService.addUser(dbUser);
		List<ProfileDTO> profiles = user.getProfile();
		upsertProfiles(profiles, dbUser);
		return dbUser;
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
				dbProfile.setUser(user);
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

}
