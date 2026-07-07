package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by author on 14-Dec-14.
 *
 * @author Rajasekhar
 */

public class DSTypeNoSQLDTO implements Serializable,DsTypeDTO {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private Integer globalConnections;
    private Boolean visible;
    private String dataSourcePoolId;
    private String dataSourceProvider;
    private boolean forceAlternateUsername;
    private String username;
    private String password;
    private String driverClassName;
    private String url;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private String validationQuery;
    private Integer validationInterval;
    private Integer timeBetweenEvictionRunsMillis;
    private Integer maxActive;
    private Integer minIdle;
    private Integer maxWait;
    private Integer initialSize;
    private Integer removeAbandonedTimeout;
    private boolean removeAbandoned;
    private boolean logAbandoned;
    private Integer minEvictableIdleTimeMillis;
    private boolean jmxEnabled;
    private String jdbcInterceptors;
    private String databaseDialect;
    private String databaseName;
    private Integer hiveReferenceId;
    private String subType;
    private String collection;
    private Date lastUpdatedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(Integer globalConnections) {
        this.globalConnections = globalConnections;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
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

    public Integer getValidationInterval() {
        return validationInterval;
    }

    public void setValidationInterval(Integer validationInterval) {
        this.validationInterval = validationInterval;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
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

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
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

    public String getDatabaseDialect() {
        return databaseDialect;
    }

    public void setDatabaseDialect(String databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }


    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Integer getHiveReferenceId() {
        return hiveReferenceId;
    }

    public void setHiveReferenceId(Integer hiveReferenceId) {
        this.hiveReferenceId = hiveReferenceId;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "DSTypeNoSQLDTO{" +
                "id=" + id +
                ", globalConnections=" + globalConnections +
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
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", hiveReferenceId=" + hiveReferenceId +
                ", subType='" + subType + '\'' +
                ", collection='" + collection + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
    public Integer globalConnections() {
        return globalConnections;
    }
}