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
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class JdbcConnection implements IResource {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private String type = GlobalJdbcType.NON_POOLED;

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String baseType = GlobalJdbcType.TYPE;

    @XmlElement
    private String visible;

    @XmlElement
    private String userName;

    @XmlElement
    private String password;
    @XmlElement
    private String jdbcUrl;
    @XmlElement
    private String driverName;
    @XmlElement
    private String dataSourceProvider;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public JdbcConnection() {
    }

    public JdbcConnection(int id, String type, String name, String baseType, String userName, String password,
                          String jdbcUrl, String driverName, String dataSourceProvider, Security security,
                          Security.Share share) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.baseType = baseType;
        this.userName = userName;
        this.password = password;
        this.jdbcUrl = jdbcUrl;
        this.driverName = driverName;
        this.dataSourceProvider = dataSourceProvider;
        this.security = security;
        this.share = share;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public Security getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public Security.Share getShare() {
        return share;
    }

    @Override
    public void setShare(Security.Share share) {
        this.share = share;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JdbcConnection that = (JdbcConnection) o;

        if (id != that.id) return false;
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that
                .dataSourceProvider != null)
            return false;
        if (driverName != null ? !driverName.equals(that.driverName) : that.driverName != null) return false;
        if (jdbcUrl != null ? !jdbcUrl.equals(that.jdbcUrl) : that.jdbcUrl != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
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
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (jdbcUrl != null ? jdbcUrl.hashCode() : 0);
        result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "JdbcConnection{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", baseType='" + baseType + '\'' +
                ", visible='" + visible + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", driverName='" + driverName + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}