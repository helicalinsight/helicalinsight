package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
@lombok.extern.slf4j.Slf4j
public class DrillConfigUrlContext {

    private static final Logger logger = LoggerFactory.getLogger(DrillConfigUrlContext.class);
    public static JSONObject getUrlConfig() {
        JSONObject drillJSONObject = getDrill();
        return drillJSONObject.getJSONObject("urlConfig");
    }

    public static String getHost() {
        return getUrlConfig().getString("host");
    }

    public static String getPort() {
        return getUrlConfig().getString("port");
    }

    public static String getDbPort() {
        return getUrlConfig().getString("dbPort");
    }

    public static String getExtraParam() {
        return getUrlConfig().getString("extraParam");
    }

    public static String getUsername() {
        return checkEmptyArrayForString(getUrlConfig().get("username"));
    }

    public static String getPassword() {
        return checkEmptyArrayForString(getUrlConfig().get("password"));
    }

    public static String getHttpsState() {
        return getUrlConfig().getString("httpsState");
    }

    public static Boolean isDistributedMode() {
        return getUrlConfig().optBoolean("distributedMode");
    }

    public static String getHttps() {
        return getUrlConfig().getString("https");
    }

    public static String getExtractHeaders() {
        return getDrill().getString("extractHeaders");
    }


    public static String getDrillConfigURL() {

        return getHttps() + "://" + getHost() + ":" + getPort();
    }

    public static String provideDrillStorageDirectoryPath(String directoryPath) {
        try {
            JSONArray drillStorageLocationArray = getDrill().getJSONArray("drillStorageLocation");
            JSONObject firstStorage = drillStorageLocationArray.getJSONObject(0);
            directoryPath = firstStorage.getString("@path");
        } catch (JSONException je) {
            throw new OperationFailedException(
                    "Selected file path has no storage configuration in drillConfig.xml " + ". Aborting the process.");
        }
        return directoryPath;
    }
    /**
     * getDrill()
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link DrillConfigUrlContext#newGetDrill() } instead</p>
     * @return
     */
    @Deprecated
    public static JSONObject getDrill() {
        String path=JsonUtils.getDrillConfigPath();
       JSONObject drillConfig = JsonUtils.getXmlAsJson(path);
       return JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
    }
    /**
     * using gson
     * newGetDrill()
     * @return
     */
    public static JsonObject newGetDrill() {
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        return drillConfig;
     }
    public static String getJdbcUrl() {
        final String zkOrDrillBit = isDistributedMode() ? "zk" : "drillbit";
        final String port = isDistributedMode() ? getZookeperPort() : getDbPort();
        String host = getHost();
        if (host == null || host.isEmpty() || host.equals("[]") || port == null || port.isEmpty() || port.equals("[]")) {
            throw new EfwServiceException("Please configure the host/port in admin page for middleware connection");
        }
        return "jdbc:drill:" + zkOrDrillBit + "=" + host + ":" + port;
    }

    private static String getZookeperPort() {

        return getUrlConfig().getString("zookeeperPort");
    }

    public static Boolean isEnabled() {
        return getDrill().getBoolean("enabled");
    }

    private static String checkEmptyArrayForString(Object obj) {
        if (obj instanceof JSONArray) {
            return "";
        } else
            return (String) obj;

    }
}
