package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.processor.model.Security;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Consists of a set of utility methods related to json processing
 *
 * @author Rajasekhar
 * @since 1.1
 */
public class RulesUtilsDB {

    /**
     * Returns the xml template of the security tag
     *
     * @return The template json of the security tag
     */

    private static ResourcePermissionFactory factory = new ResourcePermissionFactory();

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
    public static boolean validateUser(@NotNull final Principal principal, Security security) {
        //Get the integer as string
        String owner = ""+security.getCreatedBy();
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String loggedInUser = activeUser.getId() + "";
        if (loggedInUser.equals(owner)) {
            boolean matchingOrganization = false;
            List<Organization> ownersOrganization = security.getOrganizationList();
            if (ownersOrganization == null || ownersOrganization.size()==0) {
                ownersOrganization = new ArrayList<>();
            }
            Integer loggedInUserOrganization = activeUser.getOrg_id();
            if (loggedInUserOrganization == null) {
                if (ownersOrganization==null || ownersOrganization.size()==0) {
                    matchingOrganization = true;
                }
            } else {
                for(Organization organization : ownersOrganization){
                    if(loggedInUserOrganization == organization.getId()){
                        matchingOrganization = true;
                    }
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
