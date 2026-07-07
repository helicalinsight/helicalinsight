package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by author on 16-01-2015.
 * Modified on 20-05-2015
 *
 * @author Rajasekhar
 */
@Deprecated
public final class InheritShareRuleDB implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(InheritShareRuleDB.class);
    private static final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
    private static String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
    ;
    /**
     * Determines whether a file can be shown to the user based on the file
     * content
     *
     * @param fileAsJson The file under concern that is to be served to the view
     * @return <code>true</code> if the file can be shown to the user
     */

    private ResourcePermissionFactory factory = new ResourcePermissionFactory();

    private InheritShareRuleDB() {
    }

    @NotNull
    public static IResourceSecurityRule getInstance() {
        return InheritShareRuleHolder.INSTANCE;
    }

    public boolean validate(@NotNull JsonObject fileAsJson) {
        String absolutePath = fileAsJson.get("absolutePath").getAsString();
        HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();
        Map<String, Object> resourcePermission = allResources.getResourcePermission();
        Map<String, Object> resourceSecurity = allResources.getResourceSecurity();
        Object o = resourcePermission.get(absolutePath);
        int parentPermission = (int) resourcePermission.get(absolutePath);
        if (parentPermission == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        Object shareTagPresent = resourceSecurity.get(absolutePath);

        /*if (!shareTagPresent && parentPermission > resourcePermissionLevelsHolder.noAccessLevel()) {
            fileAsJson.put("permissionLevel", parentPermission);
            fileAsJson.put("parentPermission", parentPermission);
            fileAsJson.put("inherit", "true");
            return true;
        } else {
            int myPermission = SecurityUtils.whatIsMyPermission(fileAsJson);
            if (myPermission == -1 && parentPermission >= resourcePermissionLevelsHolder.readAccessLevel()) {

                fileAsJson.put("permissionLevel", myPermission);
                fileAsJson.put("parentPermission", parentPermission);
                fileAsJson.put("inherit", "true");
                return true;
            }

            if (myPermission < resourcePermissionLevelsHolder.noAccessLevel()) {

                //fileAsJson.put("parentPermission", myPermission);
                return false;
            } else {
                fileAsJson.put("permissionLevel", myPermission);
                fileAsJson.put("parentPermission", parentPermission);
                fileAsJson.put("inherit", "true");

                return true;
            }
        }*/
        return false;
    }



    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    /**
     * A private static class for lazy initialization
     */

    private static class InheritShareRuleHolder {
        public static final IResourceSecurityRule INSTANCE = new InheritShareRuleDB();
    }
}