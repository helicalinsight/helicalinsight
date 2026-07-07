package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

public class DSTypeHikariDTO implements Serializable,DsTypeDTO {
   
	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private Integer globalConnections;
    private Boolean visible;
    private String dataSourcePoolId;
    private String dataSourceProvider;
    private Integer maximumPoolSize;
    private Integer minimumIdle;
    private Integer maxLifetime;
    private String userName;
    private String password;
    private String poolName;
    private String jdbcUrl;
    private String connectionTimeout;
    private String leakDetectionThreshold;
    private String idleTimeout;
    private String databaseDialect;
    private String databaseName;
    private Date lastUpdatedTime;
    private String driverClassName;
    private String validationQuery;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getGlobalConnections() {
        return globalConnections;
    }
    public Integer globalConnections() {
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

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(Integer minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public Integer getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(Integer maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(String leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }

    public String getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(String idleTimeout) {
        this.idleTimeout = idleTimeout;
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

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        String vaQuery=com.helicalinsight.efw.utility.JsonUtils.getQueryMapping(this.driverClassName);
        this.validationQuery = vaQuery!=null?vaQuery:validationQuery;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "DSTypeHikariDTO{" +
                "id=" + id +
                ", globalConnections=" + globalConnections +
                ", visible='" + visible + '\'' +
                ", dataSourcePoolId='" + dataSourcePoolId + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", maximumPoolSize=" + maximumPoolSize +
                ", minimumIdle=" + minimumIdle +
                ", maxLifetime=" + maxLifetime +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", poolName='" + poolName + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", connectionTimeout='" + connectionTimeout + '\'' +
                ", leakDetectionThreshold='" + leakDetectionThreshold + '\'' +
                ", idleTimeout='" + idleTimeout + '\'' +
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", driverClassName='" + driverClassName + '\'' +
                ", validationQuery='" + validationQuery + '\'' +
                '}';
    }
}
