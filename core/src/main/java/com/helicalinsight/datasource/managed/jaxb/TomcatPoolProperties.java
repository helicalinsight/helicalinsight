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


    public TomcatPoolProperties() {
    }

    public TomcatPoolProperties(int id, String baseType, String dataSourcePoolId, String name,
                                String dataSourceProvider, boolean forceAlternateUsername, String username,
                                String password, String driverClassName, String url, boolean testWhileIdle,
                                boolean testOnBorrow, boolean testOnReturn, String validationQuery,
                                int validationInterval, int timeBetweenEvictionRunsMillis, int maxActive,
                                int minIdle, int maxWait, int initialSize, int removeAbandonedTimeout,
                                boolean removeAbandoned, boolean logAbandoned, int minEvictableIdleTimeMillis,
                                boolean jmxEnabled, String jdbcInterceptors) {
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
        this.validationQuery = validationQuery;
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
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (dataSourcePoolId != null ? !dataSourcePoolId.equals(that.dataSourcePoolId) : that.dataSourcePoolId != null)
            return false;
        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that
                .dataSourceProvider != null)
            return false;
        if (driverClassName != null ? !driverClassName.equals(that.driverClassName) : that.driverClassName != null)
            return false;
        if (jdbcInterceptors != null ? !jdbcInterceptors.equals(that.jdbcInterceptors) : that.jdbcInterceptors != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (validationQuery != null ? !validationQuery.equals(that.validationQuery) : that.validationQuery != null)
            return false;
        if (visible != null ? !visible.equals(that.visible) : that.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        result = 31 * result + (dataSourcePoolId != null ? dataSourcePoolId.hashCode() : 0);
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (forceAlternateUsername ? 1 : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (driverClassName != null ? driverClassName.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (testWhileIdle ? 1 : 0);
        result = 31 * result + (testOnBorrow ? 1 : 0);
        result = 31 * result + (testOnReturn ? 1 : 0);
        result = 31 * result + (validationQuery != null ? validationQuery.hashCode() : 0);
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
        result = 31 * result + (jdbcInterceptors != null ? jdbcInterceptors.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TomcatPoolProperties{" +
                "id=" + id +
                ", visible='" + visible + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
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
                '}';
    }
}