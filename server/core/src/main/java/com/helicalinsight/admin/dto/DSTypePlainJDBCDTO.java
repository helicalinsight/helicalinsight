package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

public class DSTypePlainJDBCDTO implements Serializable,DsTypeDTO {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private Integer globalId;
    private Boolean visible;
    private String userName;
    private String password;
    private String jdbcUrl;
    private String dataSourceProvider;
    private String databaseDialect;
    private String databaseName;
    private Date lastUpdatedTime;
    private String driverName;
    public Integer globalConnections() {
        return globalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
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



    public Integer getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String toString() {
        return "DSTypePlainJDBCDTO{" +
                "id=" + id +
                ", globalId=" + globalId +
                ", visible='" + visible + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", driverName='" + driverName + '\'' +
                '}';
    }
}

