package com.helicalinsight.datasource.model;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.DsTypeDTO;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ds_type_hikari")
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DSTypeHikari implements Serializable, DsTypeDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "global_id")
    private GlobalConnections globalConnections;


    @Column(name="visible")
    private Boolean visible;

    @Column(name="data_source_pool_id")
    private String dataSourcePoolId;

    @Column(name="data_source_provider")
    private String dataSourceProvider;

    @Column(name="maximum_pool_size")
    private Integer maximumPoolSize;

    @Column(name="minimum_idle")
    private Integer minimumIdle;

    @Column(name="max_lifetime")
    private Integer maxLifetime;

    @Column(name="user_name")
    private String userName;

    @Column(name="password")
    private String password;

    @Column(name="pool_name")
    private String poolName;

    @Column(name="jdbc_url",length =HibernateConfig.COLUMN_LENGTH)
    private String jdbcUrl;

    @Column(name="connection_timeout")
    private String connectionTimeout;

    @Column(name="leak_detection_threshold")
    private String leakDetectionThreshold;

    @Column(name="idle_timeout")
    private String idleTimeout;

    @Column(name="database_dialect")
    private String databaseDialect;

    @Column(name="database_name")
    private String databaseName;

    @Column(name = "last_update_time")
    private Date lastUpdatedTime;

    @Column(name="driver_name")
    private String driverName;

    @Column(name="connection_test_query")
    private String validationQuery;

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

    public void setDatabaseName(String databaseName) {

        this.databaseName = databaseName;
    }

    public String getDatabaseName() {

        return databaseName;
    }

    public DSTypeHikari() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return (password!=null ? CipherUtils.decrypt(password):password);
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

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
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

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        String vaQuery=com.helicalinsight.efw.utility.JsonUtils.getQueryMapping(this.driverName);
        this.validationQuery = vaQuery!=null?vaQuery:validationQuery;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public Integer globalConnections() {
        return this.globalConnections.getGlobalId();
    }
}
