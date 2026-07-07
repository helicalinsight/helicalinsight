package org.apache.tomcat.jdbc.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;

import com.helicalinsight.datasource.DriverShim;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;

public class PooledConnection {
  private static final Log log = LogFactory.getLog(PooledConnection.class);
  
  public static final String PROP_USER = "user";
  
  public static final String PROP_PASSWORD = "password";
  
  public static final int VALIDATE_BORROW = 1;
  
  public static final int VALIDATE_RETURN = 2;
  
  public static final int VALIDATE_IDLE = 3;
  
  public static final int VALIDATE_INIT = 4;
  
  protected PoolConfiguration poolProperties;
  
  private volatile Connection connection;
  
  protected volatile XAConnection xaConnection;
  
  private String abandonTrace = null;
  
  private volatile long timestamp;
  
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
  
  private volatile boolean discarded = false;
  
  private volatile long lastConnected = -1L;
  
  private volatile long lastValidated = System.currentTimeMillis();
  
  protected ConnectionPool parent;
  
  private HashMap<Object, Object> attributes = new HashMap<>();
  
  private volatile long connectionVersion = 0L;
  
  private volatile JdbcInterceptor handler = null;
  
  private AtomicBoolean released = new AtomicBoolean(false);
  
  private volatile boolean suspect = false;
  
  private Driver driver = null;
  
  public PooledConnection(PoolConfiguration prop, ConnectionPool parent) {
    this.poolProperties = prop;
    this.parent = parent;
    this.connectionVersion = parent.getPoolVersion();
  }
  
  public long getConnectionVersion() {
    return this.connectionVersion;
  }
  
  @Deprecated
  public boolean checkUser(String username, String password) {
    return !shouldForceReconnect(username, password);
  }
  
  public boolean shouldForceReconnect(String username, String password) {
    if (!getPoolProperties().isAlternateUsernameAllowed())
      return false; 
    if (username == null)
      username = this.poolProperties.getUsername(); 
    if (password == null)
      password = this.poolProperties.getPassword(); 
    String storedUsr = (String)getAttributes().get("user");
    String storedPwd = (String)getAttributes().get("password");
    boolean noChangeInCredentials = (username == null && storedUsr == null);
    noChangeInCredentials = (noChangeInCredentials || (username != null && username.equals(storedUsr)));
    noChangeInCredentials = (noChangeInCredentials && ((password == null && storedPwd == null) || (password != null && password.equals(storedPwd))));
    if (username == null) {
      getAttributes().remove("user");
    } else {
      getAttributes().put("user", username);
    } 
    if (password == null) {
      getAttributes().remove("password");
    } else {
      getAttributes().put("password", password);
    } 
    return !noChangeInCredentials;
  }
  
  public void connect() throws SQLException {
    if (this.released.get())
      throw new SQLException("A connection once released, can't be reestablished."); 
    if (this.connection != null)
      try {
        disconnect(false);
      } catch (Exception x) {
        log.debug("Unable to disconnect previous connection.", x);
      }  
    if (this.poolProperties.getDataSource() != null || this.poolProperties.getDataSourceJNDI() != null);
    if (this.poolProperties.getDataSource() != null) {
      connectUsingDataSource();
    } else {
      connectUsingDriver();
    } 
    if (this.poolProperties.getJdbcInterceptors() == null || this.poolProperties.getJdbcInterceptors().indexOf(ConnectionState.class.getName()) < 0 || this.poolProperties.getJdbcInterceptors().indexOf(ConnectionState.class.getSimpleName()) < 0) {
      if (this.poolProperties.getDefaultTransactionIsolation() != -1)
        this.connection.setTransactionIsolation(this.poolProperties.getDefaultTransactionIsolation()); 
      if (this.poolProperties.getDefaultReadOnly() != null)
        this.connection.setReadOnly(this.poolProperties.getDefaultReadOnly().booleanValue()); 
      if (this.poolProperties.getDefaultAutoCommit() != null)
        this.connection.setAutoCommit(this.poolProperties.getDefaultAutoCommit().booleanValue()); 
      if (this.poolProperties.getDefaultCatalog() != null)
        this.connection.setCatalog(this.poolProperties.getDefaultCatalog()); 
    } 
    this.discarded = false;
    this.lastConnected = System.currentTimeMillis();
  }
  
  protected void connectUsingDataSource() throws SQLException {
    String usr = null;
    String pwd = null;
    if (getAttributes().containsKey("user")) {
      usr = (String)getAttributes().get("user");
    } else {
      usr = this.poolProperties.getUsername();
      getAttributes().put("user", usr);
    } 
    if (getAttributes().containsKey("password")) {
      pwd = (String)getAttributes().get("password");
    } else {
      pwd = this.poolProperties.getPassword();
      getAttributes().put("password", pwd);
    } 
    if (this.poolProperties.getDataSource() instanceof XADataSource) {
      XADataSource xds = (XADataSource)this.poolProperties.getDataSource();
      if (usr != null && pwd != null) {
        this.xaConnection = xds.getXAConnection(usr, pwd);
        this.connection = this.xaConnection.getConnection();
      } else {
        this.xaConnection = xds.getXAConnection();
        this.connection = this.xaConnection.getConnection();
      } 
    } else if (this.poolProperties.getDataSource() instanceof DataSource) {
      DataSource ds = (DataSource)this.poolProperties.getDataSource();
      if (usr != null && pwd != null) {
        this.connection = ds.getConnection(usr, pwd);
      } else {
        this.connection = ds.getConnection();
      } 
    } else if (this.poolProperties.getDataSource() instanceof ConnectionPoolDataSource) {
      ConnectionPoolDataSource ds = (ConnectionPoolDataSource)this.poolProperties.getDataSource();
      if (usr != null && pwd != null) {
        this.connection = ds.getPooledConnection(usr, pwd).getConnection();
      } else {
        this.connection = ds.getPooledConnection().getConnection();
      } 
    } else {
      throw new SQLException("DataSource is of unknown class:" + ((this.poolProperties.getDataSource() != null) ? this.poolProperties.getDataSource().getClass() : "null"));
    } 
  }
  
  protected void connectUsingDriver() throws SQLException {
    String driverClassName = this.poolProperties.getDriverClassName();
    
    if ( driverClassName != null ) {
    	Enumeration<Driver> drivers =  DriverManager.getDrivers();
    	while ( drivers.hasMoreElements()) {
    		Driver theDriver = drivers.nextElement();
    		if ( theDriver instanceof DriverShim driverShim ) {
    			if ( driverClassName.equals(driverShim.getDriverName())) {
    				this.driver = driverShim;
    				break;
    			}
    			continue;
    		}
    		
    		if ( theDriver.getClass().getName().equals(driverClassName)) {
    			this.driver = theDriver;
    			break;
    		}
    	}
    }
    
	 try {
      if (this.driver == null) {
        if (log.isDebugEnabled())
          log.debug("Instantiating driver using class: " + this.poolProperties.getDriverClassName() + " [url=" + this.poolProperties.getUrl() + "]"); 
        if (this.poolProperties.getDriverClassName() == null) {
          log.warn("Not loading a JDBC driver as driverClassName property is null.");
		} else {
			try {
				this.driver = (Driver) ClassLoaderUtil.loadClass(this.poolProperties.getDriverClassName(),
						new ClassLoader[] { PooledConnection.class.getClassLoader(),
								Thread.currentThread().getContextClassLoader() })
						.getDeclaredConstructor().newInstance();
			} catch (ClassNotFoundException e) {
				try {
					this.driver = new DriverShim((Driver)FactoryMethodWrapper.getUntypedInstance(driverClassName));
					DriverManager.registerDriver(this.driver);
				}
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
    } catch (Exception cn) {
      if (log.isDebugEnabled())
        log.debug("Unable to instantiate JDBC driver.", cn); 
      SQLException ex = new SQLException(cn.getMessage());
      ex.initCause(cn);
      throw ex;
    } 
    String driverURL = this.poolProperties.getUrl();
    String usr = null;
    String pwd = null;
    if (getAttributes().containsKey("user")) {
      usr = (String)getAttributes().get("user");
    } else {
      usr = this.poolProperties.getUsername();
      getAttributes().put("user", usr);
    } 
    if (getAttributes().containsKey("password")) {
      pwd = (String)getAttributes().get("password");
    } else {
      pwd = this.poolProperties.getPassword();
      getAttributes().put("password", pwd);
    } 
    Properties properties = PoolUtilities.clone(this.poolProperties.getDbProperties());
    if (usr != null)
      properties.setProperty("user", usr); 
    if (pwd != null)
      properties.setProperty("password", pwd); 
    try {
      if (this.driver == null) {
        this.connection = DriverManager.getConnection(driverURL, properties);
      } else {
        this.connection = this.driver.connect(driverURL, properties);
      } 
    } catch (Exception x) {
      if (log.isDebugEnabled())
        log.debug("Unable to connect to database.", x); 
      if (this.parent.jmxPool != null)
        this.parent.jmxPool.notify("CONNECTION FAILED", ConnectionPool.getStackTrace(x)); 
      if (x instanceof SQLException)
        throw (SQLException)x; 
      SQLException ex = new SQLException(x.getMessage());
      ex.initCause(x);
      throw ex;
    } 
    if (this.connection == null)
      throw new SQLException("Driver:" + this.driver + " returned null for URL:" + driverURL); 
  }
  
  public boolean isInitialized() {
    return (this.connection != null);
  }
  
  public boolean isMaxAgeExpired() {
    if (getPoolProperties().getMaxAge() > 0L)
      return (System.currentTimeMillis() - getLastConnected() > getPoolProperties().getMaxAge()); 
    return false;
  }
  
  public void reconnect() throws SQLException {
    disconnect(false);
    connect();
  }
  
  private void disconnect(boolean finalize) {
    if (isDiscarded() && this.connection == null)
      return; 
    setDiscarded(true);
    if (this.connection != null)
      try {
        this.parent.disconnectEvent(this, finalize);
        if (this.xaConnection == null) {
          this.connection.close();
        } else {
          this.xaConnection.close();
        } 
      } catch (Exception ignore) {
        if (log.isDebugEnabled())
          log.debug("Unable to close underlying SQL connection", ignore); 
      }  
    this.connection = null;
    this.xaConnection = null;
    this.lastConnected = -1L;
    if (finalize)
      this.parent.finalize(this); 
  }
  
  public long getAbandonTimeout() {
    if (this.poolProperties.getRemoveAbandonedTimeout() <= 0)
      return Long.MAX_VALUE; 
    return (this.poolProperties.getRemoveAbandonedTimeout() * 1000);
  }
  
  private boolean doValidate(int action) {
    if (action == 1 && this.poolProperties.isTestOnBorrow())
      return true; 
    if (action == 2 && this.poolProperties.isTestOnReturn())
      return true; 
    if (action == 3 && this.poolProperties.isTestWhileIdle())
      return true; 
    if (action == 4 && this.poolProperties.isTestOnConnect())
      return true; 
    if (action == 4 && this.poolProperties.getInitSQL() != null)
      return true; 
    return false;
  }
  
  public boolean validate(int validateAction) {
    return validate(validateAction, null);
  }
  
  public boolean validate(int validateAction, String sql) {
    if (isDiscarded())
      return false; 
    if (!doValidate(validateAction))
      return true; 
    long now = System.currentTimeMillis();
    if (validateAction != 4 && this.poolProperties.getValidationInterval() > 0L && now - this.lastValidated < this.poolProperties.getValidationInterval())
      return true; 
    if (this.poolProperties.getValidator() != null) {
      if (this.poolProperties.getValidator().validate(this.connection, validateAction)) {
        this.lastValidated = now;
        return true;
      } 
      if (getPoolProperties().getLogValidationErrors())
        log.error("Custom validation through " + this.poolProperties.getValidator() + " failed."); 
      return false;
    } 
    String query = sql;
    if (validateAction == 4 && this.poolProperties.getInitSQL() != null)
      query = this.poolProperties.getInitSQL(); 
    if (query == null)
      query = this.poolProperties.getValidationQuery(); 
    Statement stmt = null;
    try {
      stmt = this.connection.createStatement();
      int validationQueryTimeout = this.poolProperties.getValidationQueryTimeout();
      if (validationQueryTimeout > 0)
        stmt.setQueryTimeout(validationQueryTimeout); 
      stmt.execute(query);
      stmt.close();
      this.lastValidated = now;
      return true;
    } catch (Exception ex) {
      if (getPoolProperties().getLogValidationErrors()) {
        log.warn("SQL Validation error", ex);
      } else if (log.isDebugEnabled()) {
        log.debug("Unable to validate object:", ex);
      } 
      if (stmt != null)
        try {
          stmt.close();
        } catch (Exception ignore2) {} 
      return false;
    } 
  }
  
  public long getReleaseTime() {
    return this.poolProperties.getMinEvictableIdleTimeMillis();
  }
  
  public boolean release() {
    try {
      disconnect(true);
    } catch (Exception x) {
      if (log.isDebugEnabled())
        log.debug("Unable to close SQL connection", x); 
    } 
    return this.released.compareAndSet(false, true);
  }
  
  public void setStackTrace(String trace) {
    this.abandonTrace = trace;
  }
  
  public String getStackTrace() {
    return this.abandonTrace;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    setSuspect(false);
  }
  
  public boolean isSuspect() {
    return this.suspect;
  }
  
  public void setSuspect(boolean suspect) {
    this.suspect = suspect;
  }
  
  public void setDiscarded(boolean discarded) {
    if (this.discarded && !discarded)
      throw new IllegalStateException("Unable to change the state once the connection has been discarded"); 
    this.discarded = discarded;
  }
  
  public void setLastValidated(long lastValidated) {
    this.lastValidated = lastValidated;
  }
  
  public void setPoolProperties(PoolConfiguration poolProperties) {
    this.poolProperties = poolProperties;
  }
  
  public long getTimestamp() {
    return this.timestamp;
  }
  
  public boolean isDiscarded() {
    return this.discarded;
  }
  
  public long getLastValidated() {
    return this.lastValidated;
  }
  
  public PoolConfiguration getPoolProperties() {
    return this.poolProperties;
  }
  
  public void lock() {
    if (this.poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled())
      this.lock.writeLock().lock(); 
  }
  
  public void unlock() {
    if (this.poolProperties.getUseLock() || this.poolProperties.isPoolSweeperEnabled())
      this.lock.writeLock().unlock(); 
  }
  
  public Connection getConnection() {
    return this.connection;
  }
  
  public XAConnection getXAConnection() {
    return this.xaConnection;
  }
  
  public long getLastConnected() {
    return this.lastConnected;
  }
  
  public JdbcInterceptor getHandler() {
    return this.handler;
  }
  
  public void setHandler(JdbcInterceptor handler) {
    if (this.handler != null && this.handler != handler) {
      JdbcInterceptor interceptor = this.handler;
      while (interceptor != null) {
        interceptor.reset(null, null);
        interceptor = interceptor.getNext();
      } 
    } 
    this.handler = handler;
  }
  
  public String toString() {
    return "PooledConnection[" + ((this.connection != null) ? this.connection.toString() : "null") + "]";
  }
  
  public boolean isReleased() {
    return this.released.get();
  }
  
  public HashMap<Object, Object> getAttributes() {
    return this.attributes;
  }
}
