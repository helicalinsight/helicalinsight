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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 30-09-2016
 *
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "EFWD")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class EFWD {

    @XmlElement(name = "DataSources")
    private DataSources dataSources;

    public EFWD() {
    }

    public DataSources getDataSources() {
        return dataSources;
    }

    public void setDataSources(DataSources dataSources) {
        this.dataSources = dataSources;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        EFWD efwd = (EFWD) object;

        if (dataSources != null ? !dataSources.equals(efwd.dataSources) : efwd.dataSources != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return dataSources != null ? dataSources.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EFWD{" +
                "dataSources=" + dataSources +
                '}';
    }
}

