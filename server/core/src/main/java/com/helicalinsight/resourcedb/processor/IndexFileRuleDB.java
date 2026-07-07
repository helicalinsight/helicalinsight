package com.helicalinsight.resourcedb.processor;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.processor.iresource.IResourceRuleDB;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Some business specific rules (like whether to allow the currently logged in
 * user should view the folder in question in the view or not) are applied on
 * the folders in the solution directory. For the same purpose this class
 * instance can be used.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class IndexFileRuleDB extends AbstractResourceRuleDB implements IResourceRuleDB {

    private static final Logger logger = LoggerFactory.getLogger(IndexFileRuleDB.class);

    /**
     * For singleton structure
     */
    private IndexFileRuleDB() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRuleDB getInstance() {
        return EfwFolderRuleHolder.INSTANCE;
    }

    /**
     * Pretty toString() of the class
     *
     * @return The class name itself
     */
    @NotNull
    public String toString() {
        return "EFWFolderRuleValidator";
    }


    @Override
    public boolean validateMap(Map<String,Object> resoureMap) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        final JsonObject settings = JsonUtils.newGetSettingsJson();
        boolean isOwner = false;
        try {
            isOwner = AbstractResourceRuleDB.isSecurityMatching(settings,resoureMap);
        } catch (Exception ex) {
            logger.error("An exception has taken place. The stack trace is ", ex);
        }
        //Check if the folder is shared with other users, if it is shared it should be shown
        return isOwner || resultOfConfiguredRules(settings, resoureMap);
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name,
                                              String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        foldersMap.put("type", "folder");
        foldersMap.put("name", fileAsJson.getString("title"));

        foldersMap.put("options", new JSONObject().accumulate("selectable", "true").toString());
        if (fileAsJson.optBoolean("inherit")) {
            foldersMap.put("permissionLevel", fileAsJson.getString("permissionLevel"));
            foldersMap.put("inherit", "true");
        } else {
            foldersMap.put("permissionLevel", "" + SecurityUtilsDB.whatIsMyPermission(fileAsJson));
        }
        return foldersMap;

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys) {

    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwFolderRuleHolder {
        public static final IResourceRuleDB INSTANCE = new IndexFileRuleDB();
    }

}