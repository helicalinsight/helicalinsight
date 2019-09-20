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

package com.helicalinsight.datasource.calcite;

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.resourcesecurity.jaxb.Security;

import javax.xml.bind.annotation.*;

/**
 * Created by user on 11/24/2015.
 *
 * @author Rajasekhar
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class CalciteConnection {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private String type = GlobalJdbcType.NON_POOLED;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String baseType = GlobalJdbcType.TYPE;

    @XmlElement
    private String driverName = "org.apache.calcite.jdbc.Driver";
    @XmlElement
    private String dataSourceProvider;
    @XmlElement
    private String model;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public CalciteConnection() {
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

        CalciteConnection that = (CalciteConnection) o;

        if (id != that.id) return false;
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (dataSourceProvider != null ? !dataSourceProvider.equals(that.dataSourceProvider) : that
                .dataSourceProvider != null)
            return false;
        if (driverName != null ? !driverName.equals(that.driverName) : that.driverName != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        result = 31 * result + (driverName != null ? driverName.hashCode() : 0);
        result = 31 * result + (dataSourceProvider != null ? dataSourceProvider.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CalciteConnection{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", baseType='" + baseType + '\'' +
                ", driverName='" + driverName + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", model='" + model + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
