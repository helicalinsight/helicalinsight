package com.helicalinsight.admin.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consists of a set of utility methods used by various rules related classes
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
public final class AuthenticationUtils {

	public static Integer getLoggedInUserOrganizationId() {
		RolesAccessComponent rolesAccessComponent = ApplicationContextAccessor.getBean(RolesAccessComponent.class);
		// Get logged in user organization
		return rolesAccessComponent.getLoggedInUserOrgId();
	}

	@Nullable
	public static String getOrganizationId() {
		final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Principal activeUser;
		if (principal instanceof Principal) {
			activeUser = (Principal) principal;
		} else {
			throw new EfwException("User session has expired. Please login.");
		}
		User loggedInUser = activeUser.getLoggedInUser();
		final Integer orgId = loggedInUser.getOrg_id();
		if (orgId == null) {
			return null;
		}
		return String.valueOf(orgId);
	}


    @NotNull
    public static String getUserId() {
        try {
            final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Principal activeUser;
            if (principal instanceof Principal) {
                activeUser = (Principal) principal;
            } else {
                throw new EfwException("User session has expired. Please login.");
            }
            return String.valueOf(activeUser.getLoggedInUser().getId());
        }catch(NullPointerException ne){
            throw new UsernameNotFoundException("User session has expired. Please login.");
        }
    }

	/**
	 * Obtains the currently logged in <code>User</code>'s <code>Role</code> ids
	 * from the <code>Principal</code>
	 *
	 * @return Returns a list of <code>Role</code> ids held by the currently logged
	 *         in user
	 */
	@NotNull
	public static List<String> getUserRolesIds() {
		List<String> idList = new ArrayList<>();
		Principal activeUser;
		try {
			activeUser = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception ex) {
			throw new EfwException("User session has expired. Please login.", ex);
		}
		List<Role> list = activeUser.getLoggedInUser().getRoles();
		int counter = 0;
		for (Role role : list) {
			if (role == null) {
				idList.add(counter, null);
				continue;
			}
			idList.add(counter, role.getId() + "");
			counter++;
		}
		return idList;
	}

	@SuppressWarnings("ALL")
	public static boolean isSuperAdmin(@Nullable Integer loggedInUserOrganizationId) {
		boolean isSuperAdmin = false;
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor
				.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class);
		if (loggedInUserOrganizationId == null) {// Super user organization
			List<String> userRoles = AuthenticationUtils.getUserRoles();
			if (userRoles.contains(namesConfigurer.getRoleAdmin())) {
				isSuperAdmin = true;
			}
		}
		return isSuperAdmin;
	}

	@NotNull
	public static List<String> getUserRoles() {
		List<String> idList = new ArrayList<>();
		Principal activeUser;
		try {
			activeUser = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception ex) {
			throw new EfwException("User session has expired. Please login.", ex);
		}

		List<Role> list = activeUser.getLoggedInUser().getRoles();
		int counter = 0;
		for (Role role : list) {
			if (role == null) {
				idList.add(counter, null);
				continue;
			}
			String roleId = role.getRole_name();
			idList.add(counter, roleId);
			counter++;
		}
		return idList;
	}

	/**
	 * Obtains the currently logged in <code>User</code> credentials as a
	 * <code>Principal</code> object.
	 * <p>
	 * Organization name and organization id can be null.
	 *
	 * @return Principal
	 */
	@NotNull
	public static Principal getUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof Principal) {
				return (Principal) principal;
			} else {
				throw new EfwException("User session has expired. Please login.");
			}
		} else {
			throw new EfwException("Could not obtain the Principal object. The authentication is null");
		}
	}

	/**
	 * Returns the currently logged in user's name
	 *
	 * @return A string
	 */
	public static String getUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Principal activeUser;
		if (principal instanceof Principal) {
			activeUser = (Principal) principal;
		} else {
			throw new EfwException("User session has expired. Please login.");
		}
		return activeUser.getLoggedInUser().getUsername();
	}

	/**
	 * Returns the currently logged in user's password
	 *
	 * @return A string
	 */
	public static String getPassword() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Principal activeUser;
		if (principal instanceof Principal) {
			activeUser = (Principal) principal;
		} else {
			throw new EfwException("User session has expired. Please login.");
		}
		return CipherUtils.decrypt(activeUser.getLoggedInUser().getPassword());
	}

	/**
	 * Returns the currently logged in user's organization name
	 *
	 * @return Logged in user's organization name or null if there is no
	 *         organization
	 */
	@Nullable
	public static String getOrganization() {
		Object securityPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Principal principal;
		if (securityPrincipal instanceof Principal) {
			principal = (Principal) securityPrincipal;
		} else {
			throw new EfwException("User session has expired. Please login.");
		}
		Organization userOrganization = principal.getLoggedInUser().getOrganization();
		String organization = null;
		if (userOrganization != null) {
			organization = userOrganization.getOrg_name();
		}
		return organization;
	}
	 /**
     * getRealNames
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link AuthenticationUtils#getRealNames(JsonObject fileContent)} instead.</p>
     *
     * @param JsonObject fileContent 
     * @return Map<String, String>
     */
	@Deprecated
	@NotNull
	public static Map<String, String> getRealNames(@Nullable JSONObject fileContent) {
		Map<String, String> realNames = new HashMap<>();
		if (fileContent == null) {
			throw new IllegalArgumentException("Json can't be null");
		}

		String userId = getUserId(fileContent);
		String organizationId = fileContent.getJSONObject("security").getString("organization");

		String organization;
		realNames.put("userId", userId);
		UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
		User userObject = userService.findUser(Integer.parseInt(userId));
		if (userObject == null) {
			throw new RuntimeException("The user id is not found");
		}

		userId = userObject.getUsername();

		if (organizationId == null || "[]".equals(organizationId.trim())) {
			organization = null;
		} else {
			organization = userObject.getOrganization().getOrg_name();
		}

		String password = CipherUtils.decrypt(userObject.getPassword());

		realNames.put("username", userId);
		realNames.put("organization", organization);
		realNames.put("password", password);
		return realNames;
	}
	/**
	 * getRealNames using gson
	 * @param JsonObject fileContent
	 * @return Map<String, String>
	 */
	@NotNull
	public static Map<String, String> getRealNames(@Nullable JsonObject fileContent) {
		Map<String, String> realNames = new HashMap<>();
		if (fileContent == null) {
			throw new IllegalArgumentException("Json can't be null");
		}

		String userId = getUserId(fileContent);
		String organizationId = fileContent.getAsJsonObject("security").get("organization").getAsString();

		String organization;
		realNames.put("userId", userId);
		UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
		User userObject = userService.findUser(Integer.parseInt(userId));
		if (userObject == null) {
			throw new RuntimeException("The user id is not found");
		}

		userId = userObject.getUsername();

		if (organizationId == null || "[]".equals(organizationId.trim())) {
			organization = null;
		} else {
			organization = userObject.getOrganization().getOrg_name();
		}

		String password = CipherUtils.decrypt(userObject.getPassword());

		realNames.put("username", userId);
		realNames.put("organization", organization);
		realNames.put("password", password);
		return realNames;
	}

	@NotNull
	public static String getDownloadManager() {
		Map<String, String> realNames = new HashMap<>();
		UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
		User userObject = userService.findUserByNameNorgNull("downloadManager");
		if (userObject == null) {
			throw new RuntimeException("The user id is not found");
		}

		String password = CipherUtils.decrypt(userObject.getPassword());
		return  password;
	}



	public static void allowSuperAdminOnly() {
		if (!isSuperOrganizationUser()) {
			throw new EfwException(
					"Access denied, you may not have sufficient privilege to access this " + "resource" + ".");
		}
	}

	public static boolean isSuperOrganizationUser() {
		RolesAccessComponent rolesAccessComponent = ApplicationContextAccessor.getBean(RolesAccessComponent.class);
		Integer organizationId = rolesAccessComponent.getLoggedInUserOrgId();
		return (organizationId == null);
	}
	/**
     * getUserId
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link AuthenticationUtils#getUserId(JsonObject fileContent)} instead.</p>
     *
     * @param JsonObject getUserId 
     * @return String
     */
	@Deprecated
	public static String getUserId(JSONObject fileContent) {

		Object unDeterminedObject = fileContent.opt("security");
		if (null == unDeterminedObject) {
			return null;
		}
		if (unDeterminedObject instanceof JSONObject) {
			if (null != ((JSONObject) unDeterminedObject).optString("createdBy")) {
				return ((JSONObject) unDeterminedObject).optString("createdBy");
			}
		} else {
			return fileContent.getString("security").replace("[", "").replace("]", "").replace("\"", "");
		}
		return null;
	}
	/**
	 * getUserId using gson
	 * @param JsonObject fileContent
	 * @return String
	 */
	public static String getUserId(JsonObject fileContent) {

		Object unDeterminedObject = fileContent.get("security");
		if (null == unDeterminedObject) {
			return null;
		}
		if (unDeterminedObject instanceof JsonObject) {
			if (null != optString((JsonObject) unDeterminedObject, "createdBy")) {
				return optString((JsonObject) unDeterminedObject, "createdBy");
			}
		} else {
			return fileContent.get("security").getAsString().replace("[", "").replace("]", "").replace("\"", "");
		}
		return null;
	}

	private static String optString(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					return jsonPrimitive.getAsString();
				}
			}
		}
		return null;
	}


}
