package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 04-12-2014.
 * <p/>
 * <p/>
 * An instance of this class is obtained using setting.xml configuration(resourceSecurity).
 * Validates the
 * security tag details are matching or not in the file that is passed as a parameter to the
 * interface method.
 *
 * @author Rajasekhar
 * @version 1.0
 * @see com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule
 * @since 1.3
 */
@Deprecated
public final class ResourceSecurityRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSecurityRule.class);

    /**
     * No need to create multiple instances. So private constructor
     */
    private ResourceSecurityRule() {
    }

    /**
     * The singleton instance of the class is returned
     *
     * @return An instance of <code>IResourceSecurityRule</code>
     */
    @NotNull
    public static IResourceSecurityRule getInstance() {
        return ResourceSecurityRuleHolder.INSTANCE;
    }

    /**
     * Returns <code>true</code> if the file has matching security details. The credentials are
     * compared with the currently logged in user's credentials.
     *
     * @param fileAsJson The file under concern with extension from setting.xml
     * @return <code>true</code> if the file has matching security details.
     */
    @Override
    public boolean validate(@NotNull JsonObject fileAsJson) {
        try {
            if (!"true".equalsIgnoreCase(fileAsJson.get("visible").getAsString())) {
                return false;
            }
            Object unDeterminedObject = fileAsJson.get("security");
            if (unDeterminedObject instanceof JsonObject) {
                return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), (JsonObject) unDeterminedObject);
            } else {
                String security = fileAsJson.get("security").getAsString();
                return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), security);
            }
        } catch (JsonSyntaxException ex) {
            logger.error("The resource is malformed. Setting.xml is configured to apply rules. " +
                    "But the resource does not support " + "the rules.", ex);
        }
        return false;
    }



    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * A private static class for lazy initialization
     */
    private static class ResourceSecurityRuleHolder {
        private static final IResourceSecurityRule INSTANCE = new ResourceSecurityRule();
    }
}