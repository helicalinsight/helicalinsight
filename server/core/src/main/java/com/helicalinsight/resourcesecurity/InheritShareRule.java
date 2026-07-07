package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by author on 16-01-2015.
 * Modified on 20-05-2015
 *
 * @author Rajasekhar
 */
@Deprecated
public final class InheritShareRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(InheritShareRule.class);
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

    private InheritShareRule() {
    }

    @NotNull
    public static IResourceSecurityRule getInstance() {
        return InheritShareRuleHolder.INSTANCE;
    }

    public boolean validate(@NotNull JsonObject fileAsJson) {
        String absolutePath = fileAsJson.get("absolutePath").getAsString();
        File file = new File(absolutePath);

        int parentPermission = SecurityUtils.maxInheritPermission(file);
        if (parentPermission == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        boolean shareTagPresent = ShareRuleHelper.isShareTagPresent(fileAsJson);
        if (!shareTagPresent && parentPermission > resourcePermissionLevelsHolder.noAccessLevel()) {
            fileAsJson.addProperty("permissionLevel", parentPermission);
            fileAsJson.addProperty("parentPermission", parentPermission);
            fileAsJson.addProperty("inherit", "true");
            return true;
        } else {
            int myPermission = SecurityUtils.whatIsMyPermission(fileAsJson);
            if (myPermission == -1 && parentPermission >= resourcePermissionLevelsHolder.readAccessLevel()) {

                fileAsJson.addProperty("permissionLevel", myPermission);
                fileAsJson.addProperty("parentPermission", parentPermission);
                fileAsJson.addProperty("inherit", "true");
                return true;
            }

            if (myPermission < resourcePermissionLevelsHolder.noAccessLevel()) {

                //fileAsJson.put("parentPermission", myPermission);
                return false;
            } else {
                fileAsJson.addProperty("permissionLevel", myPermission);
                fileAsJson.addProperty("parentPermission", parentPermission);
                fileAsJson.addProperty("inherit", "true");

                return true;
            }
        }

    }



    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    /**
     * A private static class for lazy initialization
     */
    private static class InheritShareRuleHolder {
        public static final IResourceSecurityRule INSTANCE = new InheritShareRule();
    }
}