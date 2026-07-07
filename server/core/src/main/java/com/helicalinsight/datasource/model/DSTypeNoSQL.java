package com.helicalinsight.datasource.model;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.DsTypeDTO;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by author on 14-Dec-14.
 *
 * @author Rajasekhar
 */
@Entity
@Table(name="ds_type_nosql")
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DSTypeNoSQL implements Serializable, DsTypeDTO {

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

    @Column(name="force_alternate_username")
    private boolean forceAlternateUsername;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name="driver_class_name")
    private String driverClassName;

    @Column(name="url",length =HibernateConfig.COLUMN_LENGTH)
    private String url;

    @Column(name = "test_while_idle")
    private boolean testWhileIdle;

    @Column(name="test_on_borrow")
    private boolean testOnBorrow;

    @Column(name = "test_on_return")
    private boolean testOnReturn;

    @Column(name = "validation_query")
    private String validationQuery;

    @Column(name = "validation_interval")
    private Integer validationInterval;

    @Column(name = "time_between_eviction_runs_millis")
    private Integer timeBetweenEvictionRunsMillis;

    @Column(name = "max_active")
    private Integer maxActive;

    @Column(name = "min_idle")
    private Integer minIdle;

    @Column(name = "max_wait")
    private Integer maxWait;

    @Column(name = "initial_size")
    private Integer initialSize;

    @Column(name = "remove_abandoned_timeout")
    private Integer removeAbandonedTimeout;

    @Column(name = "remove_abandoned")
    private boolean removeAbandoned;

    @Column(name = "log_abandoned")
    private boolean logAbandoned;

    @Column(name = "min_evictable_idle_time_millis")
    private Integer minEvictableIdleTimeMillis;

    @Column(name = "jmx_enabled")
    private boolean jmxEnabled;

    @Column(name = "jdbc_interceptors")
    private String jdbcInterceptors;

    @Column(name = "database_dialect")
    private String databaseDialect;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "hive_reference_id")
    private Integer hiveReferenceId;

    @Column(name = "sub_type")
    private String subType;

    @Column(name = "collection")
    private String collection;

    @Column(name = "last_update_time")
    private Date lastUpdatedTime;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseDialect() {
        return this.databaseDialect;
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
        return (password!=null ? CipherUtils.decrypt(password):password);
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


    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
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
    public Integer globalConnections() {
        return this.globalConnections.getGlobalId();
    }
}