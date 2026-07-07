package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;

import java.sql.ResultSet;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
public interface IJdbcService {

    JsonObject execute(String connectionsFile, Integer connectionId, String sql);

    default ResultSet executeForResultSet(String connectionsFile, Integer connectionId, String sql) {
        return null;
    }

    
    default void streamForResultSet(String connectionFile, Integer connectionId, String sql, CallBack<ResultSet> callBack) {
    	
    }
}
