package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
@Component
public class RolesAccessComponent {

    public JSONObject allRoles(String orgId, LimitOffsetModel limitOffsetModel) {
        //Get logged in user
        Integer loggedInUserOrganizationId = getLoggedInUserOrgId();

        Integer toBePassed = null;

        if (loggedInUserOrganizationId != null) {//Not super user
            if (orgId != null && orgId.trim().length() != 0) {
                int requested = Integer.parseInt(orgId);
                if (requested != loggedInUserOrganizationId) {//Not authorized
                    //toBePassed = loggedInUserOrganizationId;
                    // Send only his organization roles
                    throw new AccessDeniedException("The user is not authenticated to view " +
                            "organization " + orgId + " roles.");
                } else {
                    toBePassed = requested;//User is asking for his org only. Allow.
                }
            } else {
                toBePassed = loggedInUserOrganizationId;
            }
        } else {
            if (orgId != null && orgId.trim().length() != 0) {
                //Super user requesting a particular organization id roles
                toBePassed = Integer.parseInt(orgId);
            }
        }

        RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
        return AdminUtils.getOrganizationRoles(roleService, toBePassed, limitOffsetModel);
    }

    /**
     * @return logged-in users org id or logged-in user user-id depending on the type argument
     */
    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new EfwServiceException("User is not logged in");
        }
        Principal principal = (Principal) auth.getPrincipal();
        if (principal != null) {
            return (User) principal.getLoggedInUser();

        } else {
            throw new EfwServiceException("User is not logged in");
        }
    }

    public Integer getLoggedInUserId() {
        return getLoggedInUser().getId();
    }


    public Integer getLoggedInUserOrgId() {
        return getLoggedInUser().getOrg_id();
    }

}
