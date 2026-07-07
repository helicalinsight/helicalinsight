package com.zaxxer.hikari.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.datasource.DriverShim;

public final class DriverDataSource implements DataSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
  
  private static final String PASSWORD = "password";
  
  private static final String USER = "user";
  
  private final String jdbcUrl;
  
  private final Properties driverProperties;
  
  private Driver driver;
  
  public DriverDataSource(String jdbcUrl, String driverClassName, Properties properties, String username, String password) {
    this.jdbcUrl = jdbcUrl;
    this.driverProperties = new Properties();
    this.driverProperties.putAll(properties);
    if (username != null)
      this.driverProperties.put("user", this.driverProperties.getProperty("user", username)); 
    if (password != null)
      this.driverProperties.put("password", this.driverProperties.getProperty("password", password)); 
    if (driverClassName != null) {
      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while (drivers.hasMoreElements()) {
        Driver d = drivers.nextElement();
        if ( d instanceof DriverShim ) {
			if (d.getClass().getName().equals(driverClassName)) {
				this.driver = d;
				break;
			}
			continue;
		}
        if (d.getClass().getName().equals(driverClassName)) {
            this.driver = d;
            break;
          } 
      } 
      if (this.driver == null) {
        LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", driverClassName);
        Class<?> driverClass = null;
        ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
          if (threadContextClassLoader != null)
            try {
              driverClass = threadContextClassLoader.loadClass(driverClassName);
              LOGGER.debug("Driver class {} found in Thread context class loader {}", driverClassName, threadContextClassLoader);
            } catch (ClassNotFoundException e) {
              LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", new Object[] { driverClassName, threadContextClassLoader, 
                    getClass().getClassLoader() });
            }  
          if (driverClass == null) {
            driverClass = getClass().getClassLoader().loadClass(driverClassName);
            LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
          } 
        } catch (ClassNotFoundException e) {
          LOGGER.debug("Failed to load driver class {} from HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
          try {
              ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
              driverClass = Class.forName(driverClassName, true, contextClassLoader);
              this.driver = (Driver)new DriverShim((Driver)driverClass.getDeclaredConstructor().newInstance());
              DriverManager.registerDriver(this.driver);
            } catch (Exception ex) {
              throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + jdbcUrl, ex);
            } 
        
        } 
        if (driverClass != null)
          try {
            this.driver = (Driver)new DriverShim((Driver) driverClass.getDeclaredConstructor().newInstance());
          } catch (Exception e) {
            LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", driverClassName, e);
          }  
      } 
    } 
    String sanitizedUrl = UtilityElf.maskPasswordInJdbcUrl(jdbcUrl);
    try {
      if (this.driver == null) {
        this.driver = DriverManager.getDriver(jdbcUrl);
        LOGGER.debug("Loaded driver with class name {} for jdbcUrl={}", this.driver.getClass().getName(), sanitizedUrl);
      } else if (!this.driver.acceptsURL(jdbcUrl)) {
        throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
      } 
    } catch (SQLException e) {
      throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
    } 
  }
  
  public Connection getConnection() throws SQLException {
    return this.driver.connect(this.jdbcUrl, this.driverProperties);
  }
  
  public Connection getConnection(String username, String password) throws SQLException {
    Properties cloned = (Properties)this.driverProperties.clone();
    if (username != null) {
      cloned.put("user", username);
      if (cloned.containsKey("username"))
        cloned.put("username", username); 
    } 
    if (password != null)
      cloned.put("password", password); 
    return this.driver.connect(this.jdbcUrl, cloned);
  }
  
  public PrintWriter getLogWriter() throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public void setLogWriter(PrintWriter logWriter) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public void setLoginTimeout(int seconds) throws SQLException {
    DriverManager.setLoginTimeout(seconds);
  }
  
  public int getLoginTimeout() throws SQLException {
    return DriverManager.getLoginTimeout();
  }
  
  public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return this.driver.getParentLogger();
  }
  
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
