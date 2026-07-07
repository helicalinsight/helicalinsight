package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.ResultSetConfigDTO;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.datasource.ActiveQueryRegistry;
import com.helicalinsight.datasource.RunningQueryDetails;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
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

    private static final ApplicationProperties applicationProperties  = ApplicationProperties.getInstance();
    
    @Autowired
    private TaskExecutorService taskExecutorService;
    
    private Integer limit;

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @SuppressWarnings("unchecked")
	@Override
    public ResultSet getResultSet(@Nullable Connection connection, String sql) {
        if (connection == null || sql == null) {
            throw new IllegalArgumentException("Nopes! The connection object or sql is null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JDBC query executor has started.");
        }

        logger.debug("sql Query :" + sql);
        long now = System.currentTimeMillis();
        Statement statement = null;
        Future<ResultSet> future = null;
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        String requestId = RequestContext.get();
        try {
            statement = connection.createStatement();
            int jdbcQueryCancellationTime = ApplicationProperties.getInstance().getJdbcQueryCancellationTime();
            statement.getQueryTimeout();

            JsonObject dbDetails = this.setDbDetails(connection, sql, currentThreadName);

            RunningQueryDetails runningQueryDetails = new RunningQueryDetails(statement, dbDetails, thread);

            registry.registerStatements(currentThreadName, runningQueryDetails);
            ResultSetJdbcQueryExecutor jdbcQueryExecutor = new ResultSetJdbcQueryExecutor(statement, sql);
            //Submit the callable to the executor. Start the thread
            future = (Future<ResultSet>) taskExecutorService.submit(jdbcQueryExecutor, requestId);
            //Wait for specified time limit. Otherwise cancel statement
            ResultSet queryResult = future.get(jdbcQueryCancellationTime, TimeUnit.SECONDS);
            long time = System.currentTimeMillis();

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Execution time taken for the query %s is %s milliseconds", sql,
                        (time - now)));
            }

            return queryResult;
        } catch (SQLException | InterruptedException | CancellationException | ExecutionException ex) {
        	logger.debug("Request interrupted, canceeling the statement");
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
            registry.deRegisterStatement(currentThreadName);
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully shutdown the jdbc query executor service");
            }
        }

    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void streamResult(Connection connection, String sql, CallBack<ResultSet> callBack) {
    	
    	ResultSetConfigDTO config = applicationProperties.getResultSetConfig();
    	
    	int fetchSize = config.fetchSize();
    	int batchSize = config.batchSize();
    	
    	logger.debug("Batch Size : {} , fetchSize : {}", batchSize, fetchSize);
    	
    	if (connection == null || sql == null) {
            throw new IllegalArgumentException("Nopes! The connection object or sql is null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JDBC query executor has started.");
        }
        
        logger.debug("sql Query :" + sql);
        Statement statement = null;
        Future<ResultSet> future = null;
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        String requestId = RequestContext.get();
        try {
        	DatabaseMetaData metadata =  connection.getMetaData();
        	String dbName = metadata.getDatabaseProductName();
        	
        	statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        	connection.setAutoCommit(config.autoCommit());
        	
        	if ( config.dbConfigOverrides().containsKey(dbName)) {
        		ResultSetConfigDTO.ConfigOverrides overrides = config.dbConfigOverrides().get(dbName);
        		statement.setFetchSize(overrides.fetchSize());
        		connection.setAutoCommit(overrides.autoCommit());
        	}
        	else {
        		statement.setFetchSize(fetchSize);
        	}
        	
        	 int jdbcQueryCancellationTime = ApplicationProperties.getInstance().getJdbcQueryCancellationTime();
             statement.getQueryTimeout();

             JsonObject dbDetails = this.setDbDetails(connection, sql, currentThreadName);

             RunningQueryDetails runningQueryDetails = new RunningQueryDetails(statement, dbDetails, thread);

             registry.registerStatements(currentThreadName, runningQueryDetails);
             
             ResultSetJdbcQueryStreamer jdbcQueryStreamer = new ResultSetJdbcQueryStreamer(statement, sql, batchSize,callBack);
             future = (Future<ResultSet>) taskExecutorService.submit(jdbcQueryStreamer, requestId);
             
             // FIXME : Blocking call
             long start = System.currentTimeMillis();
             future.get(jdbcQueryCancellationTime, TimeUnit.SECONDS);
             long end = System.currentTimeMillis();
             logger.debug("Time taken to execute Query : " + (end-start)/1000 + " s");
             
        	
	    } catch (SQLException | InterruptedException | CancellationException | ExecutionException ex) {
        	logger.debug("Request interrupted, cancelling the statement");
            cancelStatement(statement, ex);
            throw new QueryException("Failed to query the database.", ex);
        } catch (TimeoutException ex) {
            cancelStatement(statement, ex);
            future.cancel(true);
            throw new JdbcQueryTimeoutException("The time taken for the execution of the query is longer than the " +
                    "default value of timeout. Timeout exception occurred. Increasing the timeout in the " +
                    "settings may help retrieve the results. Contact your administrator.");
        } finally {
	        cleanup(connection, statement, currentThreadName, registry);
	    }
    }
    
    
    @SuppressWarnings("unchecked")
	public JsonObject query(@Nullable Connection connection, String sql) {
        if (connection == null || sql == null) {
            throw new IllegalArgumentException("Nopes! The connection object or sql is null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JDBC query executor has started.");
        }

        logger.debug("sql Query :" + sql);
        long now = System.currentTimeMillis();
        Statement statement = null;
        Future<JsonObject> future = null;
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();
        ActiveQueryRegistry registry = ActiveQueryRegistry.getRegistry();
        try {
            statement = connection.createStatement();
            int jdbcQueryCancellationTime = ApplicationProperties.getInstance().getJdbcQueryCancellationTime();
            statement.getQueryTimeout();

            JsonObject dbDetails = this.setDbDetails(connection, sql, currentThreadName);

            RunningQueryDetails runningQueryDetails = new RunningQueryDetails(statement, dbDetails, thread);
            String requestId = RequestContext.get();
            registry.registerStatements(currentThreadName, runningQueryDetails);
            JdbcQueryExecutor jdbcQueryExecutor;
            logger.debug("limit :" + limit);
            if (limit != null) {
                //statement.setMaxRows(limit);
                jdbcQueryExecutor = new HCRJdbcQueryExecutor(statement, sql);
            } else {
                jdbcQueryExecutor = new JdbcQueryExecutor(statement, sql);
            }

            //Submit the callable to the executor. Start the thread
            future =  (Future<JsonObject>) taskExecutorService.submit(jdbcQueryExecutor, requestId);    //executor.submit(jdbcQueryExecutor);
            //Wait for specified time limit. Otherwise cancel statement
            JsonObject queryResult = future.get(jdbcQueryCancellationTime, TimeUnit.SECONDS);

            long time = System.currentTimeMillis();

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Execution time taken for the query %s is %s milliseconds", sql,
                        (time - now)));
            }

            return queryResult;
        } catch (SQLException | InterruptedException| CancellationException| ExecutionException ex) {
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
            registry.deRegisterStatement(currentThreadName);

            if (logger.isDebugEnabled()) {
                logger.debug("Successfully shutdown the jdbc query executor service");
            }
        }
    }

    private JsonObject setDbDetails(Connection connection, String sql, String threadName) {

        JsonObject dbDetails = new JsonObject();
        dbDetails.addProperty("query", sql);
        try {
            dbDetails.addProperty("clientInfo", connection.getClientInfo().toString());
            dbDetails.addProperty("threadName", threadName);
            dbDetails.addProperty("userName", AuthenticationUtils.getUserName());
        } catch (Throwable ignore) {
            logger.error("Cant obtain dbDetails ");

        }
        return dbDetails;
    }

    private void cancelStatement(Statement statement, Exception ex) {
        logger.error("Cancelling the current query statement. Timeout occurred.", ex);
        try {
            if (!statement.isClosed() && statement.getConnection() != null) {
                statement.cancel();
            }
        } catch (SQLFeatureNotSupportedException e) {
        	try {
				if(!statement.isClosed() && statement.getConnection() != null) {
					statement.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
        catch (SQLException exception) {
            logger.error("Calling cancel() on the Statement issued exception. Details are: ", exception);
        }
    }

    public void setLimit() {

    }
    
    private final void cleanup(Connection connection, Statement statement,  
    		String currentThreadName,  ActiveQueryRegistry registry) {
    	
    	 DbUtils.closeQuietly(connection);
         DbUtils.closeQuietly(statement);
         registry.deRegisterStatement(currentThreadName);
         if (logger.isDebugEnabled()) {
             logger.debug("Successfully shutdown the jdbc query executor service");
         }
    }
}
