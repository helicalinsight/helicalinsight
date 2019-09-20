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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Rajesh
 *         Created by author on 4/17/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "prop")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomProp {
    @XmlElement
    private String name;
    @XmlElement
    private String type;
    @XmlElement(name = "DataSource")
    private Integer dataSource;
    @XmlElement
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String script;

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

    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomProp customProp = (CustomProp) o;

        if (dataSource != null ? !dataSource.equals(customProp.dataSource) : customProp.dataSource != null) return false;
        if (name != null ? !name.equals(customProp.name) : customProp.name != null) return false;
        if (script != null ? !script.equals(customProp.script) : customProp.script != null) return false;
        if (type != null ? !type.equals(customProp.type) : customProp.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Prop{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dataSource=" + dataSource +
                ", script='" + script + '\'' +
                '}';
    }
}
