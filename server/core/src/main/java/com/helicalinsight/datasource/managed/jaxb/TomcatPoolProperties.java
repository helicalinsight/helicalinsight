package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 14-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "tomcatJdbcDataSource")
@XmlAccessorType(XmlAccessType.FIELD)
public class TomcatPoolProperties {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String type = GlobalJdbcType.DYNAMIC_DATASOURCE;

    @XmlAttribute
    private String baseType = GlobalJdbcType.TYPE;

    @XmlElement
    private String visible;

    @XmlElement
    private String dataSourcePoolId;
    @XmlElement
    private String dataSourceProvider;
    @XmlElement
    private boolean forceAlternateUsername;
    @XmlElement
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private String driverClassName;
    @XmlElement
    private String url;
    @XmlElement
    private boolean testWhileIdle;
    @XmlElement
    private boolean testOnBorrow;
    @XmlElement
    private boolean testOnReturn;
    @XmlElement
    private String validationQuery;
    @XmlElement
    private int validationInterval;
    @XmlElement
    private int timeBetweenEvictionRunsMillis;
    @XmlElement
    private int maxActive;
    @XmlElement
    private int minIdle;
    @XmlElement
    private int maxWait;
    @XmlElement
    private int initialSize;
    @XmlElement
    private int removeAbandonedTimeout;
    @XmlElement
    private boolean removeAbandoned;
    @XmlElement
    private boolean logAbandoned;
    @XmlElement
    private int minEvictableIdleTimeMillis;
    @XmlElement
    private boolean jmxEnabled;
    @XmlElement
    private String jdbcInterceptors;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    @XmlElement
    private String databaseDialect;

    @XmlElement
    private String databaseName;
    
    @XmlElement
    private String connectionProperties;


    public TomcatPoolProperties() {
    }

    @Override
    public String toString() {
        return "TomcatPoolProperties{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", visible='" + visible + '\'' +
                ", dataSourcePoolId='" + dataSourcePoolId + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", forceAlternateUsername=" + forceAlternateUsername +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", testWhileIdle=" + testWhileIdle +
                ", testOnBorrow=" + testOnBorrow +
                ", testOnReturn=" + testOnReturn +
                ", validationQuery='" + validationQuery + '\'' +
                ", validationInterval=" + validationInterval +
                ", timeBetweenEvictionRunsMillis=" + timeBetweenEvictionRunsMillis +
                ", maxActive=" + maxActive +
                ", minIdle=" + minIdle +
                ", maxWait=" + maxWait +
                ", initialSize=" + initialSize +
                ", removeAbandonedTimeout=" + removeAbandonedTimeout +
                ", removeAbandoned=" + removeAbandoned +
                ", logAbandoned=" + logAbandoned +
                ", minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis +
                ", jmxEnabled=" + jmxEnabled +
                ", jdbcInterceptors='" + jdbcInterceptors + '\'' +
                ", security=" + security +
                ", share=" + share +
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TomcatPoolProperties that = (TomcatPoolProperties) o;

        if (forceAlternateUsername != that.forceAlternateUsername) return false;
        if (id != that.id) return false;
        if (initialSize != that.initialSize) return false;
        if (jmxEnabled != that.jmxEnabled) return false;
        if (logAbandoned != that.logAbandoned) return false;
        if (maxActive != that.maxActive) return false;
        if (maxWait != that.maxWait) return false;
        if (minEvictableIdleTimeMillis != that.minEvictableIdleTimeMillis) return false;
        if (minIdle != that.minIdle) return false;
        if (removeAbandoned != that.removeAbandoned) return false;
        if (removeAbandonedTimeout != that.removeAbandonedTimeout) return false;
        if (testOnBorrow != that.testOnBorrow) return false;
        if (testOnReturn != that.testOnReturn) return false;
        if (testWhileIdle != that.testWhileIdle) return false;
        if (timeBetweenEvictionRunsMillis != that.timeBetweenEvictionRunsMillis) return false;
        if (validationInterval != that.validationInterval) return false;
        if (!baseType.equals(that.baseType)) return false;
        if (!dataSourcePoolId.equals(that.dataSourcePoolId)) return false;
        if (!dataSourceProvider.equals(that.dataSourceProvider)) return false;
        if (!databaseDialect.equals(that.databaseDialect)) return false;
        if (!databaseName.equals(that.databaseName)) return false;
        if (!driverClassName.equals(that.driverClassName)) return false;
        if (!jdbcInterceptors.equals(that.jdbcInterceptors)) return false;
        if (!name.equals(that.name)) return false;
        if (!password.equals(that.password)) return false;
        if (!security.equals(that.security)) return false;
        if (!share.equals(that.share)) return false;
        if (!type.equals(that.type)) return false;
        if (!url.equals(that.url)) return false;
        if (!username.equals(that.username)) return false;
        if (!validationQuery.equals(that.validationQuery)) return false;
        if (!visible.equals(that.visible)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + baseType.hashCode();
       // result = 31 * result + visible.hashCode();
        result = 31 * result + dataSourcePoolId.hashCode();
        result = 31 * result + dataSourceProvider.hashCode();
        result = 31 * result + (forceAlternateUsername ? 1 : 0);
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + driverClassName.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + (testWhileIdle ? 1 : 0);
        result = 31 * result + (testOnBorrow ? 1 : 0);
        result = 31 * result + (testOnReturn ? 1 : 0);
        result = 31 * result + validationQuery.hashCode();
        result = 31 * result + validationInterval;
        result = 31 * result + timeBetweenEvictionRunsMillis;
        result = 31 * result + maxActive;
        result = 31 * result + minIdle;
        result = 31 * result + maxWait;
        result = 31 * result + initialSize;
        result = 31 * result + removeAbandonedTimeout;
        result = 31 * result + (removeAbandoned ? 1 : 0);
        result = 31 * result + (logAbandoned ? 1 : 0);
        result = 31 * result + minEvictableIdleTimeMillis;
        result = 31 * result + (jmxEnabled ? 1 : 0);
        result = 31 * result + jdbcInterceptors.hashCode();
       // result = 31 * result + security.hashCode();
        //result = 31 * result + share.hashCode();
        result = 31 * result + databaseDialect.hashCode();
        result = 31 * result + databaseName.hashCode();
        return result;
    }

    public String getDatabaseName() {

        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public TomcatPoolProperties(int id, String baseType, String dataSourcePoolId, String name,
                                String dataSourceProvider, boolean forceAlternateUsername, String username,
                                String password, String driverClassName, String url, boolean testWhileIdle,
                                boolean testOnBorrow, boolean testOnReturn, String validationQuery,
                                int validationInterval, int timeBetweenEvictionRunsMillis, int maxActive,
                                int minIdle, int maxWait, int initialSize, int removeAbandonedTimeout,
                                boolean removeAbandoned, boolean logAbandoned, int minEvictableIdleTimeMillis,
                                boolean jmxEnabled, String jdbcInterceptors, String databaseDialect, String databaseName) {
        this.id = id;
        this.baseType = baseType;
        this.name = name;
        this.dataSourcePoolId = dataSourcePoolId;
        this.dataSourceProvider = dataSourceProvider;
        this.forceAlternateUsername = forceAlternateUsername;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.url = url;
        this.testWhileIdle = testWhileIdle;
        this.testOnBorrow = testOnBorrow;
        this.testOnReturn = testOnReturn;
        this.validationQuery = validationQuery;
        this.validationInterval = validationInterval;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.maxActive = maxActive;
        this.minIdle = minIdle;
        this.maxWait = maxWait;
        this.initialSize = initialSize;
        this.removeAbandonedTimeout = removeAbandonedTimeout;
        this.removeAbandoned = removeAbandoned;
        this.logAbandoned = logAbandoned;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this.jmxEnabled = jmxEnabled;
        this.jdbcInterceptors = jdbcInterceptors;
        this.databaseDialect = databaseDialect;
        this.databaseName = databaseName;
    }

    public String getDatabaseDialect() {
        return this.databaseDialect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getDataSourcePoolId() {
        return dataSourcePoolId;
    }

    public void setDataSourcePoolId(String dataSourcePoolId) {
        this.dataSourcePoolId = dataSourcePoolId;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public boolean isForceAlternateUsername() {
        return forceAlternateUsername;
    }

    public void setForceAlternateUsername(boolean forceAlternateUsername) {
        this.forceAlternateUsername = forceAlternateUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setDatabaseDialect(String databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return CipherUtils.decrypt(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        String vaQuery=com.helicalinsight.efw.utility.JsonUtils.getQueryMapping(this.driverClassName);
        this.validationQuery = vaQuery!=null?vaQuery:validationQuery;
    }

    public int getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(int validationInterval) {
        this.validationInterval = validationInterval;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Security.Share getShare() {
        return share;
    }

    public void setShare(Security.Share share) {
        this.share = share;
    }

	public String getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

}