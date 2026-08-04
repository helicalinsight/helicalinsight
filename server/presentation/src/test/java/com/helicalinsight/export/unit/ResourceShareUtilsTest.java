package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.resourcedb.Deleted;

public class ResourceShareUtilsTest extends ExportUnitTestBase {

	private ResourceShareUtils createUtilsWithMocks() throws Exception {
		ResourceShareUtils utils = new ResourceShareUtils();
		Field f1 = ResourceShareUtils.class.getDeclaredField("userService");
		f1.setAccessible(true);
		f1.set(utils, mock(UserService.class));
		Field f2 = ResourceShareUtils.class.getDeclaredField("orgService");
		f2.setAccessible(true);
		f2.set(utils, mock(OrganizationService.class));
		Field f3 = ResourceShareUtils.class.getDeclaredField("roleService");
		f3.setAccessible(true);
		f3.set(utils, mock(RoleService.class));
		Field f4 = ResourceShareUtils.class.getDeclaredField("profileService");
		f4.setAccessible(true);
		f4.set(utils, mock(ProfileService.class));
		Field f5 = ResourceShareUtils.class.getDeclaredField("mapper");
		f5.setAccessible(true);
		f5.set(utils, mock(ResourceDTOMapper.class));
		return utils;
	}

	@Test
	public void ut_a1_testGetOrInsertRoleExisting() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		RoleService roleService = getField(utils, "roleService", RoleService.class);
		Role existing = new Role();
		existing.setRole_name("admin");
		when(roleService.findRoleByNameNullOrg("admin")).thenReturn(existing);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRole_name("admin");
		Assert.assertEquals(existing, utils.getOrInsertRole(roleDTO));
	}

	@Test
	public void ut_a2_testGetOrInsertRoleNew() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		RoleService roleService = getField(utils, "roleService", RoleService.class);
		when(roleService.findRoleByNameNullOrg("newrole")).thenReturn(null);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRole_name("newrole");
		Assert.assertNotNull(utils.getOrInsertRole(roleDTO));
	}

	@Test
	public void ut_a3_testGetOrInsertOrganiationExisting() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		OrganizationService orgService = getField(utils, "orgService", OrganizationService.class);
		Organization existing = new Organization();
		existing.setOrg_name("org");
		existing.setDeleted(false);
		when(orgService.getOrganizationForRecycleBinCondition("org")).thenReturn(existing);

		OrganizationDTO dto = new OrganizationDTO();
		dto.setOrg_name("org");
		Assert.assertEquals(existing, utils.getOrInsertOrganiation(dto));
	}

	@Test
	public void ut_a4_testGetOrInsertOrganiationDeleted() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		OrganizationService orgService = getField(utils, "orgService", OrganizationService.class);
		Organization existing = new Organization();
		existing.setDeleted(true);
		when(orgService.getOrganizationForRecycleBinCondition("del")).thenReturn(existing);

		OrganizationDTO dto = new OrganizationDTO();
		dto.setOrg_name("del");
		Assert.assertNull(utils.getOrInsertOrganiation(dto));
	}

	@Test
	public void ut_a5_testGetOrInsertOrganiationNew() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		OrganizationService orgService = getField(utils, "orgService", OrganizationService.class);
		when(orgService.getOrganizationForRecycleBinCondition("neworg")).thenReturn(null);

		OrganizationDTO dto = new OrganizationDTO();
		dto.setOrg_name("neworg");
		dto.setOrg_desc("desc");
		dto.setDeleted(false);
		Assert.assertNotNull(utils.getOrInsertOrganiation(dto));
	}

	@Test
	public void ut_a6_testGetOrInsertUserExisting() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		UserService userService = getField(utils, "userService", UserService.class);
		User existing = mock(User.class);
		existing.setDeleted(false);
		when(userService.findUserByNameNorgNull("user", Deleted.FALSE)).thenReturn(existing);
		
		UserDTO dto = new UserDTO();
		dto.setUsername("user");
		dto.setProfile(Collections.emptyList());
		Assert.assertEquals(existing, utils.getOrInsertUser(dto));
	}

	@Test
	public void ut_a7_testGetOrInsertUserNew() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		UserService userService = getField(utils, "userService", UserService.class);
		when(userService.findUserByNameNorgNull("newuser", Deleted.FALSE)).thenReturn(null);

		UserDTO dto = new UserDTO();
		dto.setUsername("newuser");
		dto.setRoles(Collections.emptyList());
		dto.setProfile(Collections.emptyList());
		Assert.assertNotNull(utils.getOrInsertUser(dto));
	}

	@Test
	public void ut_a8_testUpsertProfiles() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		ProfileService profileService = getField(utils, "profileService", ProfileService.class);
		ResourceDTOMapper mapper = getField(utils, "mapper", ResourceDTOMapper.class);
		when(profileService.getProfileByNameAndUserId("p", 1)).thenReturn(null);
		Profile profile = new Profile();
		when(mapper.map(any(ProfileDTO.class))).thenReturn(profile);

		ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setProfile_name("p");
		User user = new User();
		user.setId(1);
		utils.upsertProfiles(Arrays.asList(profileDTO), user);
	}

	@Test
	public void ut_a9_testUpsertProfilesExisting() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		ProfileService profileService = getField(utils, "profileService", ProfileService.class);
		Profile existing = new Profile();
		when(profileService.getProfileByNameAndUserId("p", 1)).thenReturn(existing);

		ProfileDTO profileDTO = new ProfileDTO();
		profileDTO.setProfile_name("p");
		profileDTO.setProfile_value("val");
		User user = new User();
		user.setId(1);
		utils.upsertProfiles(Arrays.asList(profileDTO), user);
	}

	@Test
	public void ut_b1_testUpdateUser() throws Exception {
		ResourceShareUtils utils = createUtilsWithMocks();
		UserDTO fromFile = new UserDTO();
		fromFile.setEmailAddress("a@b.com");
		fromFile.setIsExternallyAuthenticated(true);
		fromFile.setEnabled(true);
		fromFile.setRoles(Collections.emptyList());
		User fromDb = new User();
		utils.updateUser(fromFile, fromDb);
		Assert.assertEquals("a@b.com", fromDb.getEmailAddress());
	}

	@SuppressWarnings("unchecked")
	private <T> T getField(Object target, String name, Class<T> type) throws Exception {
		Field field = ResourceShareUtils.class.getDeclaredField(name);
		field.setAccessible(true);
		return (T) field.get(target);
	}

}