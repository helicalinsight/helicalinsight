package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.resourcesecurity.maxims.AbstractMaxim;
import com.helicalinsight.resourcesecurity.maxims.MaximFactoryProducer;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 16-01-2015.
 * Modified on 20-05-2015
 *
 * @author Rajasekhar
 */
@Deprecated
public final class ShareRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(ShareRule.class);

    private ShareRule() {
    }

    @NotNull
    public static IResourceSecurityRule getInstance() {
        return ShareRuleHolder.INSTANCE;
    }

    /**
     * Determines whether a file can be shown to the user based on the file
     * content
     *
     * @param fileAsJson The file under concern that is to be served to the view
     * @return <code>true</code> if the file can be shown to the user
     */
    public boolean validate(@NotNull JsonObject fileAsJson) {
        if (!ShareRuleHelper.isShareTagPresent(fileAsJson)) {
            return false;
        }

        final JsonObject shareJson;
        try {
            shareJson = fileAsJson.getAsJsonObject("share");
        } catch (JsonIOException badFileException) {
            if (logger.isWarnEnabled()) {
                logger.warn("Malformed Xml: ", badFileException);
            }
            return false;
        }

        try {


            final boolean isResourceSharedWithOtherRoles = ShareRuleHelper.isResourceSharedWithOtherRoles(shareJson);
            final boolean isResourceSharedWithUsers = ShareRuleHelper.isResourceSharedWithUsers(shareJson);
            final boolean isResourceSharedWithOrganizations = ShareRuleHelper.isResourceSharedWithOrganizations
                    (shareJson);

            if (!isResourceSharedWithOtherRoles && !isResourceSharedWithUsers &&
                    !isResourceSharedWithOrganizations) {
                return false;
            }

            MaximFactoryProducer factoryProducer = new MaximFactoryProducer(shareJson);
            final AbstractMaxim abstractMaxim = factoryProducer.getFactory();

            if (isResourceSharedWithOtherRoles) {
                if (abstractMaxim.maxim("roles").inspect()) {
                    return true;
                }
            }

            if (isResourceSharedWithUsers) {
                if (abstractMaxim.maxim("users").inspect()) {
                    return true;
                }
            }

            //The resource may be shared based on user or role but not on organization level.
            //So, boolean check is required.
            if (isResourceSharedWithOrganizations) {
                return abstractMaxim.maxim("organizations").inspect();
            }
        } catch (JsonIOException ex) {
            logger.error("The resource is malformed as it does not adhere to the " + "configuration in setting.xml.",
                    ex);
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
    private static class ShareRuleHolder {
        public static final IResourceSecurityRule INSTANCE = new ShareRule();
    }
}