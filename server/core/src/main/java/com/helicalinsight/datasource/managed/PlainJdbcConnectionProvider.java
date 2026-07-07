package com.helicalinsight.datasource.managed;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.CustomWatcherUtils;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.PluginFinder;
import com.helicalinsight.efw.framework.PluginsRegistry;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by author on 13-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
public class PlainJdbcConnectionProvider implements IConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(PlainJdbcConnectionProvider.class);
    private final PluginFinder pluginFinder = new PluginFinder();

    @Autowired
    private CalciteConnectionProvider calciteConnectionProvider;

    public Connection newConnection(String json) {
        json = ConnectionProviderUtility.checkIfInternalConnection(json);
        long time1 = System.currentTimeMillis();
        Connection connection = getConnection(json);
        long time2 = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("An ad-hoc Jdbc connection is obtained in %s milli seconds." +
                            " " + "The registered drivers count is %s ", (time2 - time1),
                    ConnectionProviderUtility.getDriverCount()));
        }
        return connection;
    }

    private Connection getConnection(String json) {
        String model = JsonUtils.getKeyFromJson(json, "model");
        if (model != null) {
            return this.calciteConnectionProvider.getConnection(model);
        }
        String connectionId = JsonUtils.getKeyFromJson(json, "id");
        String jdbcUrl = JsonUtils.getKeyFromJson(json, "jdbcUrl");
        String userName = JsonUtils.getKeyFromJson(json, "userName");
        String password = CipherUtils.decrypt(JsonUtils.getKeyFromJson(json, "password"));
        Map<String,String> extraOptions = DataSourceUtils.getExtraOptions(json , connectionId);
        if (jdbcUrl == null) {
            throw new MalformedJsonException("One or more of the parameters jdbcUrl are null. Can't obtain the connection to database" + ".");
        }

        String driverName = JsonUtils.extractDriverName(json);
        Connection connection = null;

        try {
            if (ConnectionProviderUtility.isDriverRegistered(driverName)) {
            	logger.debug("Driver registered: Reference Data "+json);
            	if(extraOptions ==  null || extraOptions.isEmpty() )  {
            		connection =  ConnectionProviderUtility.getConnection(jdbcUrl, userName, password);
            	}
            	else {
            		String name = JsonUtils.getKeyFromJson(json, "fileName");
            		String fileName = DataSourceUtils.getFileName(name, extraOptions, connectionId);
            		if ( StringUtils.isNotBlank(fileName) ) {
            			jdbcUrl = DataSourceUtils.updateFlatFileUrl(jdbcUrl, fileName);
            		}
            		connection =  ConnectionProviderUtility.getConnection(jdbcUrl, userName, password, extraOptions);
            	}
            } else {
            	logger.debug("Driver Not registered: Reference Data "+json);
                return fallBack(json,extraOptions,jdbcUrl);
            }
        } catch (SQLException ex) {
            throw new EfwdServiceException(ex);
        } catch (LinkageError err) {
            logger.error("The linkage error ", err);
            CustomWatcherUtils.closeClassLoader(driverName);
            throw new EfwdServiceException("Unable to obtain connection. " + err.getMessage());

        }
        return connection;
    }

    private Connection fallBack(String json, Map<String, String> extraOptions, String jdbcUrl) throws SQLException {
    	String driverName = JsonUtils.extractDriverName(json);
        try {
            //Check if Driver is available in classpath
        	Class<?> loadedClass=FactoryMethodWrapper.forName(driverName);
            if(loadedClass!=null) {
            	logger.debug("Loading suitable class And Register as enry point: Reference Data "+loadedClass);
            	logger.debug("calling refister method");
            	this.pluginFinder.getPluginClassLoader(driverName);
            }
            //By now if the class is in classpath the Driver's static initializer code is run. Driver should have
            // registered by itself with the DriverManager
        } catch (ClassNotFoundException ignored) {
            ConnectionProviderUtility.registerDriver(driverName);
        }
        try {
            String userName = JsonUtils.getKeyFromJson(json, "userName");
            String password = CipherUtils.decrypt(JsonUtils.getKeyFromJson(json, "password"));

            if(extraOptions !=  null && !extraOptions.isEmpty() ) {
                String name = JsonUtils.getKeyFromJson(json, "fileName");
                String connectionId = JsonUtils.getKeyFromJson(json, "id");
                String fileName = DataSourceUtils.getFileName(name, extraOptions, connectionId);
                if ( StringUtils.isNotBlank(fileName) ) {
                    jdbcUrl = DataSourceUtils.updateFlatFileUrl(jdbcUrl, fileName);
                }
             return  ConnectionProviderUtility.getConnection(jdbcUrl, userName, password, extraOptions);
            }

            return ConnectionProviderUtility.getConnection(jdbcUrl,userName,password);
        }
        catch(JdbcConnectionException ex) {
        	PluginsRegistry.INSTANCE.deRegisterAndClosePlugin(driverName);
        	throw ex;
        }
    }
  
}