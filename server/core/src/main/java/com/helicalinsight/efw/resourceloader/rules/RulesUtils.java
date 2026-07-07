package com.helicalinsight.efw.resourceloader.rules;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

/**
 * Consists of a set of utility methods related to json processing
 *
 * @author Rajasekhar
 * @since 1.1
 */
public class RulesUtils {

    /**
     * Returns the xml template of the security tag
     *
     * @return The template json of the security tag
     */

    private static ResourcePermissionFactory factory = new ResourcePermissionFactory();

    /**
	 * newGetSecurityJsonTemplate
	 * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link RulesUtils#newGetSecurityJsonTemplate()} instead.</p>
	 * @return JsonObject 
	 */
	@Deprecated
    @NotNull
    public static JSONObject getSecurityJsonTemplate() {
        final Principal principal = AuthenticationUtils.getUserDetails();
        JSONObject security = new JSONObject();
        final User loggedInUser = principal.getLoggedInUser();
        security.accumulate("createdBy", loggedInUser.getId());
        Integer hisOrganization = loggedInUser.getOrg_id();
        security.accumulate("organization", ((hisOrganization == null) ? "" : hisOrganization));
        return security;
    }
	/**
	 * newGetSecurityJsonTemplate using gson
	 * @return JsonObject
	 */
	 @NotNull
	 public static JsonObject newGetSecurityJsonTemplate() {
	    final Principal principal = AuthenticationUtils.getUserDetails();
	    JsonObject security = new JsonObject();
	    final User loggedInUser = principal.getLoggedInUser();
	    security.addProperty("createdBy", loggedInUser.getId());
	    Integer hisOrganization = loggedInUser.getOrg_id();
	    //security.accumulate("organization", ((hisOrganization == null) ? "" : hisOrganization));
	    if(hisOrganization == null) {
	        security.addProperty("organization", "");
	    }else {
	        security.addProperty("organization", hisOrganization);
	    }
	    return security;
	 }

    public static String permissionLevelForNonVisibleFiles() {
        final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class);
        return resourcePermissionLevelsHolder.publicResourceAccessLevel() + "";
    }

    /**
     * Verifies whether the currently logged in user has access to the file
     * represented by the json parameter.
     *
     * @param principal The currently logged in users details
     * @param security  The json of security tag of the file under concern
     * @return true if validated
     */
    public static boolean validateUser(final Principal principal, String security) {
        //Get the integer as string
        String owner = security.replace("[", "").replace("]", "").replace("\"", "");
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String loggedInUser = activeUser.getId() + "";
        return loggedInUser.equals(owner);
    }

    /**
     * Verifies whether the currently logged in user has access to the file
     * represented by the json parameter.
     *
     * @param principal The currently logged in users details
     * @param security  The json of security tag of the file under concern
     * @return true if validated
     */
    /**
	 * validateUser
	 * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link RulesUtils#validateUser(final Principal principal,JsonObject security)} instead.</p>
	 * @param final Principal principal
	 * @param JsonObject security
	 * @return boolean 
	 */
	@Deprecated
    public static boolean validateUser(@NotNull final Principal principal, @NotNull JSONObject security) {
        //Get the integer as string
        String owner = security.getString("createdBy");
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String loggedInUser = activeUser.getId() + "";
        if (loggedInUser.equals(owner)) {
            boolean matchingOrganization = false;
            String ownersOrganization = security.getString("organization");
            if (ownersOrganization == null || "".equals(ownersOrganization)) {
                ownersOrganization = "[]";
            }
            Integer loggedInUserOrganization = activeUser.getOrg_id();
            if (loggedInUserOrganization == null) {
                if ("[]".equals(ownersOrganization)) {
                    matchingOrganization = true;
                }
            } else {
                if (loggedInUserOrganization.toString().equals(ownersOrganization)) {
                    matchingOrganization = true;
                }
            }
            return matchingOrganization;
        }
        return false;
    }

	/**
	 * validateUser using gson
	 * @param final Principal principal
	 * @param JsonObject security
	 * @return boolean
	 */
	public static boolean validateUser(@NotNull final Principal principal, @NotNull JsonObject security) {
        //Get the integer as string
        String owner = security.get("createdBy").getAsString();
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String loggedInUser = activeUser.getId() + "";
        if (loggedInUser.equals(owner)) {
            boolean matchingOrganization = false;
            String ownersOrganization = security.get("organization").getAsString();
            if (ownersOrganization == null || "".equals(ownersOrganization)) {
                ownersOrganization = "[]";
            }
            Integer loggedInUserOrganization = activeUser.getOrg_id();
            if (loggedInUserOrganization == null) {
                if ("[]".equals(ownersOrganization)) {
                    matchingOrganization = true;
                }
            } else {
                if (loggedInUserOrganization.toString().equals(ownersOrganization)) {
                    matchingOrganization = true;
                }
            }
            return matchingOrganization;
        }
        return false;
    }
	
    public static boolean validateUser(@NotNull final Principal principal, @NotNull User user) {
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String owner = user.getId() + "";
        String loggedInUser = activeUser.getId() + "";
        if (loggedInUser.equals(owner)) {
            boolean matchingOrganization = false;
            Integer ownersOrganization = user.getOrg_id();
            Integer loggedInUserOrganization = activeUser.getOrg_id();
            if (loggedInUserOrganization == null && ownersOrganization == null) {
                matchingOrganization = true;
            } else {
                if (loggedInUserOrganization.toString().equals(ownersOrganization.toString())) {
                    matchingOrganization = true;
                }
            }
            return matchingOrganization;
        }
        return false;
    }
}
