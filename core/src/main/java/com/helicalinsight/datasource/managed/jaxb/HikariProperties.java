/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 29-Nov-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "hikariDataSource")
@XmlAccessorType(XmlAccessType.FIELD)
public class HikariProperties {

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
    private int maximumPoolSize;
    @XmlElement
    private int minimumIdle;
    @XmlElement
    private int maxLifetime;
    @XmlElement
    private String userName;
    @XmlElement
    private String password;
    @XmlElement
    private String poolName;
    @XmlElement
    private String connectionTestQuery;
    @XmlElement
    private String driverName;
    @XmlElement
    private String jdbcUrl;
    @XmlElement
    private String connectionTimeout;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;


    public HikariProperties() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
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

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(int maxLifetime) {
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

    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HikariProperties that = (HikariProperties) o;

        if (id != that.id) return false;
        if (maxLifetime != that.maxLifetime) return false;
        if (maximumPoolSize != that.maximumPoolSize) return false;
        if (minimumIdle != that.minimumIdle) return false;
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (connectionTestQuery != null ? !connectionTestQuery.equals(that.connectionTestQuery) : that
                .connectionTestQuery != null)
            return false;
        if (connectionTimeout != null ? !connectionTimeout.equals(that.connectionTimeout) : that.connectionTimeout !=
                null)
            return false;
        if (dataSourcePoolId != null ? !dataSourcePoolId.equals(that.dataSourcePoolId) : that.dataSourcePoolId != null)
            return false;
        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that
                .dataSourceProvider != null)
            return false;
        if (driverName != null ? !driverName.equals(that.driverName) : that.driverName != null) return false;
        if (jdbcUrl != null ? !jdbcUrl.equals(that.jdbcUrl) : that.jdbcUrl != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (poolName != null ? !poolName.equals(that.poolName) : that.poolName != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (visible != null ? !visible.equals(that.visible) : that.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (dataSourcePoolId != null ? dataSourcePoolId.hashCode() : 0);
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + maximumPoolSize;
        result = 31 * result + minimumIdle;
        result = 31 * result + maxLifetime;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (poolName != null ? poolName.hashCode() : 0);
        result = 31 * result + (connectionTestQuery != null ? connectionTestQuery.hashCode() : 0);
        result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
        result = 31 * result + (jdbcUrl != null ? jdbcUrl.hashCode() : 0);
        result = 31 * result + (connectionTimeout != null ? connectionTimeout.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HikariProperties{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", visible='" + visible + '\'' +
                ", dataSourcePoolId='" + dataSourcePoolId + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", maximumPoolSize=" + maximumPoolSize +
                ", minimumIdle=" + minimumIdle +
                ", maxLifetime=" + maxLifetime +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", poolName='" + poolName + '\'' +
                ", connectionTestQuery='" + connectionTestQuery + '\'' +
                ", driverName='" + driverName + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", connectionTimeout='" + connectionTimeout + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
