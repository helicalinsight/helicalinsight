/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.datasource.RunningQueryDetails;
import com.helicalinsight.efw.ApplicationProperties;
import net.sf.json.JSONObject;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
public class JdbcDaoImpl implements IJdbcDao {
    private static final Logger logger = LoggerFactory.getLogger(JdbcDaoImpl.class);

    public JsonObject query(Connection connection, String sql) {
        if (connection == null || sql == null) {
            throw new IllegalArgumentException("Nopes! The connection object or sql is null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JDBC query executor has started.");
        }


        long now = System.currentTimeMillis();
        Statement statement = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JsonObject> future = null;
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        try {
            statement = connection.createStatement();
            int jdbcQueryCancellationTime = ApplicationProperties.getInstance().getJdbcQueryCancellationTime();
            statement.getQueryTimeout();

            JSONObject dbDetails = this.setDbDetails(connection, sql, currentThreadName);

            RunningQueryDetails runningQueryDetails = new RunningQueryDetails(statement, dbDetails, thread);

            registry.registerStatements(currentThreadName, runningQueryDetails);
            JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(statement, sql);

            //Submit the callable to the executor. Start the thread
            future = executor.submit(jdbcQueryExecutor);

            //Wait for specified time limit. Otherwise cancel statement
            JsonObject queryResult = future.get(jdbcQueryCancellationTime, TimeUnit.SECONDS);
            executor.shutdown();

            long time = System.currentTimeMillis();

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Execution time taken for the query %s is %s milliseconds", sql,
                        (time - now)));
            }

            return queryResult;
        } catch (SQLException | InterruptedException | ExecutionException ex) {
            cancelStatement(statement, ex);
            throw new QueryException("Failed to query the database.", ex);
        } catch (TimeoutException ex) {
            cancelStatement(statement, ex);
            future.cancel(true);
            throw new JdbcQueryTimeoutException("The time taken for the execution of the query is longer than the " +
                    "default value of timeout. Timeout exception occurred. Increasing the timeout in the " +
                    "settings may help retrieve the results. Contact your administrator.");
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
            if (!executor.isTerminated()) {
                logger.error("Forcefully shutting down the executor");
            }

            executor.shutdownNow();

            if (logger.isDebugEnabled()) {
                logger.debug("Successfully shutdown the jdbc query executor service");
            }

            registry.deRegisterStatement(currentThreadName);
        }
    }

    private JSONObject setDbDetails(Connection connection, String sql, String threadName) throws SQLException {

        JSONObject dbDetails = new JSONObject();
        dbDetails.put("query", sql);
        try {
            dbDetails.put("clientInfo", connection.getClientInfo());
            dbDetails.put("threadName", threadName);
            dbDetails.put("userName", AuthenticationUtils.getUserName());
        } catch (Exception ignore) {

        }
        return dbDetails;
    }

    private void cancelStatement(Statement statement, Exception ex) {
        logger.error("Cancelling the current query statement. Timeout occurred.", ex);
        try {
            if (!statement.isClosed()) {
                statement.cancel();
            }
        } catch (SQLException exception) {
            logger.error("Calling cancel() on the Statement issued exception. Details are: ", exception);
        }
    }
}
