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
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class JndiDataSource {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String type = GlobalJdbcType.STATIC_DATASOURCE;

    @XmlAttribute
    private String baseType = GlobalJdbcType.TYPE;

    @XmlElement
    private String visible;

    @XmlElement
    private String dataSourceProvider;

    @XmlElement
    private String lookUpName;

    @XmlElement
    private String driverClassName;


    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;


    public JndiDataSource() {
    }

    public JndiDataSource(int id, String name, String baseType, String dataSourceProvider, String lookUpName) {
        this.id = id;
        this.name = name;
        this.baseType = baseType;
        this.dataSourceProvider = dataSourceProvider;
        this.lookUpName = lookUpName;
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

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public String getLookUpName() {
        return lookUpName;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
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

        JndiDataSource that = (JndiDataSource) o;

        if (id != that.id) return false;
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that
                .dataSourceProvider != null)
            return false;
        if (driverClassName != null ? !driverClassName.equals(that.driverClassName) : that.driverClassName != null)
            return false;
        if (lookUpName != null ? !lookUpName.equals(that.lookUpName) : that.lookUpName != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
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
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (lookUpName != null ? lookUpName.hashCode() : 0);
        result = 31 * result + (driverClassName != null ? driverClassName.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JndiDataSource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", visible='" + visible + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", lookUpName='" + lookUpName + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
