package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
public interface IJdbcDao {
    Logger logger = LoggerFactory.getLogger(IJdbcDao.class);

    JsonObject query(Connection connection, String sql);

    default ResultSet getResultSet(@Nullable Connection connection, String sql) {
        return null;

    }
    
    // This is the new implementation for streaming the resultset
    default void streamResult(Connection connection, String sql , CallBack<ResultSet> callBack) {
    	// NOOP 
    }

}
