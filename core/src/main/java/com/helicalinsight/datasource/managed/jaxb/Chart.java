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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * @author Rajesh
 *         Created by author on 4/17/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Chart")
@XmlAccessorType(XmlAccessType.FIELD)
public class Chart {
    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "prop")
    private CustomProp property;

    @XmlElement(name = "prop")
    private GeneralProp generalProp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustomProp getProperty() {
        return property;
    }


    public void setProperty(CustomProp property) {
        this.property = property;
    }

    public GeneralProp getGeneralProp() {
        return generalProp;
    }

    public void setGeneralProp(GeneralProp generalProp) {
        this.generalProp = generalProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chart chart = (Chart) o;

        if (id != null ? !id.equals(chart.id) : chart.id != null) return false;
        if (property != null ? !property.equals(chart.property) : chart.property != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "id='" + id + '\'' +
                ", property=" + property +
                '}';
    }
}
