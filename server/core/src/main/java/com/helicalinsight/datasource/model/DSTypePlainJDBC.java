package com.helicalinsight.datasource.model;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.DsTypeDTO;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ds_type_adhoc_jdbc")
@Cacheable(true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DSTypePlainJDBC implements Serializable, DsTypeDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(orphanRemoval = true)
    @Cascade(CascadeType.DELETE)
    @JoinColumn(name = "global_id")
    private GlobalConnections globalConnections;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "jdbc_url",length =HibernateConfig.COLUMN_LENGTH)
    private String jdbcUrl;

    @Column(name = "data_source_provider")
    private String dataSourceProvider;

    @Column(name = "database_dialect")
    private String databaseDialect;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "last_update_time")
    private Date lastUpdatedTime;

    @Column(name="driver_name")
    private String driverName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GlobalConnections getGlobalConnections() {
        return globalConnections;
    }

    public void setGlobalConnections(GlobalConnections globalConnections) {
        this.globalConnections = globalConnections;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DSTypePlainJDBC that = (DSTypePlainJDBC) o;

        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that.dataSourceProvider != null)
            return false;
        if (databaseDialect != null ? !databaseDialect.equals(that.databaseDialect) : that.databaseDialect != null)
            return false;
        if (databaseName != null ? !databaseName.equals(that.databaseName) : that.databaseName != null) return false;
        if (driverName != null ? !driverName.equals(that.driverName) : that.driverName != null) return false;
        if (globalConnections != null ? !globalConnections.equals(that.globalConnections) : that.globalConnections != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (jdbcUrl != null ? !jdbcUrl.equals(that.jdbcUrl) : that.jdbcUrl != null) return false;
        if (lastUpdatedTime != null ? !lastUpdatedTime.equals(that.lastUpdatedTime) : that.lastUpdatedTime != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (visible != null ? !visible.equals(that.visible) : that.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (globalConnections != null ? globalConnections.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (jdbcUrl != null ? jdbcUrl.hashCode() : 0);
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (databaseDialect != null ? databaseDialect.hashCode() : 0);
        result = 31 * result + (databaseName != null ? databaseName.hashCode() : 0);
        result = 31 * result + (lastUpdatedTime != null ? lastUpdatedTime.hashCode() : 0);
        result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DSTypePlainJDBC{" +
                "id=" + id +
                ", globalConnections=" + globalConnections +
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

