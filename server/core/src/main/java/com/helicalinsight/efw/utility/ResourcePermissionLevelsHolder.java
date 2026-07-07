package com.helicalinsight.efw.utility;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by author on 07-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("singleton")
@SuppressWarnings("unused")
public class ResourcePermissionLevelsHolder {

    private static final Logger logger = LoggerFactory.getLogger(ResourcePermissionLevelsHolder.class);

    private int noAccessLevel;

    private int executeAccessLevel;

    private int readAccessLevel;

    private int readWriteAccessLevel;

    private int readWriteDeleteAccessLevel;

    private int ownerAccessLevel;

    private int publicResourceAccessLevel;

    public ResourcePermissionLevelsHolder() {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        try {
            JSONObject permissions = settingsJson.getJSONObject("permissions");

            String noAccessLevel = permissions.getString("noAccessLevel");
            String executeAccessLevel = permissions.getString("executeAccessLevel");
            String readAccessLevel = permissions.getString("readAccessLevel");
            String readWriteAccessLevel = permissions.getString("readWriteAccessLevel");
            String readWriteDeleteAccessLevel = permissions.getString("readWriteDeleteAccessLevel");
            String ownerAccessLevel = permissions.getString("ownerAccessLevel");

            this.noAccessLevel = Integer.valueOf(noAccessLevel);
            this.executeAccessLevel = Integer.parseInt(executeAccessLevel);
            this.readAccessLevel = Integer.valueOf(readAccessLevel);
            this.readWriteAccessLevel = Integer.parseInt(readWriteAccessLevel);
            this.readWriteDeleteAccessLevel = Integer.parseInt(readWriteDeleteAccessLevel);
            this.ownerAccessLevel = Integer.valueOf(ownerAccessLevel);

            JSONObject defaultPermissions = settingsJson.optJSONObject("defaultPermissions");

            this.publicResourceAccessLevel = Integer.parseInt(defaultPermissions.getString
                    ("publicResourceAccessLevel"));
        } catch (Exception ignore) {
            logger.error("Exception occurred. ", ignore);
            if (logger.isWarnEnabled()) {
                logger.warn("DefaultPermissionLevelsHolder message: Default permission levels " + "configuration is " +
                        "missing. Using standard values for permission levels.");
            }

            this.noAccessLevel = 0;
            this.executeAccessLevel = 1;
            this.readAccessLevel = 2;
            this.readWriteAccessLevel = 3;
            this.readWriteDeleteAccessLevel = 4;
            this.ownerAccessLevel = 5;

            this.publicResourceAccessLevel = 2;
        }
    }

    public String getAccessName(Integer access){
        if(access==this.noAccessLevel) {
            return "noAccessLevel";
        }else if(access==this.executeAccessLevel){
            return "executeAccessLevel";
        }else if(access==this.readAccessLevel){
            return "readAccessLevel";
        }else if(access==this.readWriteAccessLevel){
            return "readWriteAccessLevel";
        }else if(access==this.readWriteDeleteAccessLevel){
            return "readWriteDeleteAccessLevel";
        }else if(access==this.ownerAccessLevel()){
            return "ownerAccessLevel";
        }else if(access==this.publicResourceAccessLevel) {
            return "publicAccessLevel";
        }
        return "noAccessLevel";
    }

    public int noAccessLevel() {
        return this.noAccessLevel;
    }

    public int executeAccessLevel() {
        return this.executeAccessLevel;
    }

    public int readAccessLevel() {
        return this.readAccessLevel;
    }

    public int readWriteAccessLevel() {
        return this.readWriteAccessLevel;
    }

    public int readWriteDeleteAccessLevel() {
        return this.readWriteDeleteAccessLevel;
    }

    public int ownerAccessLevel() {
        return this.ownerAccessLevel;
    }

    public int publicResourceAccessLevel() {
        return this.publicResourceAccessLevel;
    }
}
