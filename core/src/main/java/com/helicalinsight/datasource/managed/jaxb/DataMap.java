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

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Rajesh
 * Created by author on 4/9/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "DataMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataMap {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String connection;
    @XmlAttribute
    private String type;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Query")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String query;

    @XmlElement(name = "Parameters")
    private Parameters parameters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataMap dataMap = (DataMap) o;

        if (connection != null ? !connection.equals(dataMap.connection) : dataMap.connection != null) return false;
        if (id != null ? !id.equals(dataMap.id) : dataMap.id != null) return false;
        if (name != null ? !name.equals(dataMap.name) : dataMap.name != null) return false;
        if (parameters != null ? !parameters.equals(dataMap.parameters) : dataMap.parameters != null) return false;
        if (query != null ? !query.equals(dataMap.query) : dataMap.query != null) return false;
        if (type != null ? !type.equals(dataMap.type) : dataMap.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (connection != null ? connection.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataMap{" +
                "id='" + id + '\'' +
                ", connection='" + connection + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", query='" + query + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
