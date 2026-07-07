package com.helicalinsight.datasource.managed;

import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 13-Dec-14.
 *
 * @author Rajasekhar
 */
public class PoolUtils {

    @NotNull
    public static HashMapKey newMapKey(String json) {
        String dataSourceProvider = JsonUtils.getKeyFromJson(json, "dataSourceProvider");
        String jdbcUrl = JsonUtils.extractUrl(json);
        String userName = JsonUtils.extractOptionalUserName(json);
        String password = JsonUtils.extractOptionalPassword(json);
        String driverName = JsonUtils.extractDriverName(json);
        return new HashMapKey(jdbcUrl, dataSourceProvider, userName, password, driverName);
    }
}
