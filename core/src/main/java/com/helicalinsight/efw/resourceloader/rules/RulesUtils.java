/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourceloader.rules;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import net.sf.json.JSONObject;


/**
 * Consists of a set of utility methods related to json processing
 *
 * @author Rajasekhar
 */
public class RulesUtils {

    /**
     * Returns the xml template of the security tag
     *
     * @return The template json of the security tag
     */

    public static JSONObject getSecurityJsonTemplate() {
        final Principal principal = AuthenticationUtils.getUserDetails();
        JSONObject security = new JSONObject();
        final User loggedInUser = principal.getLoggedInUser();
        security.accumulate("createdBy", loggedInUser.getId());
        security.accumulate("organization", "");
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
    public static boolean validateUser(final Principal principal, JSONObject security) {
        //Get the integer as string
        String owner = security.getString("createdBy");
        final User activeUser = principal.getLoggedInUser();
        //Convert into string
        String loggedInUser = activeUser.getId() + "";
        return loggedInUser.equals(owner);
    }


}
