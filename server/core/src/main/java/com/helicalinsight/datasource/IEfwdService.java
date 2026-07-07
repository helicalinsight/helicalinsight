package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.ApplicationProperties;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

/**
 * Created by author on 17-Jan-15.
 *
 * @author Rajasekhar
 */
public interface IEfwdService {

    @Nullable
    JsonObject execute(String httpRequestJson, String connectionDetails, String dataMapTagContent,
                       ApplicationProperties applicationProperties);

    @Nullable
    default ResultSet executeForResultSet(String httpRequestJson, String connectionDetails, String dataMapTagContent,
                                          ApplicationProperties applicationProperties) {
        return null;
    }

    
    @Nullable
    default void streamForResultSet(String httpRequestJson, String connectionDetails, String dataMapTagContent,
                                          ApplicationProperties applicationProperties, CallBack<ResultSet> callBack) {
    }
}
