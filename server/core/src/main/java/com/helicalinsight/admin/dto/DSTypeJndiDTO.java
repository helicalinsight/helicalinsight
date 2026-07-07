package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

public class DSTypeJndiDTO implements Serializable,DsTypeDTO {
 
	private static final long serialVersionUID = 1L;

	private Integer id;
    private Integer globalConnections;
    private Boolean visible;
    private String dataSourceProvider;
    private String databaseDialect;
    private String databaseName;
    private String driverClassName;
    private String lookUpName;
    private Date lastUpdatedTime;
    public Integer globalConnections() {
        return globalConnections;
    }
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

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }



    public String getLookUpName() {
        return lookUpName;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "DSTypeJndiDTO{" +
                "id=" + id +
                ", globalConnections=" + globalConnections +
                ", visible='" + visible + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", lookUpName='" + lookUpName + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }


}

