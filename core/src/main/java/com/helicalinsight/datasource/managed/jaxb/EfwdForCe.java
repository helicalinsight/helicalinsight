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
 * @author Rajesh
 * Created by author on 4/17/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "EFWD")
@XmlAccessorType(XmlAccessType.FIELD)
public class EfwdForCe {

    @XmlElement(name = "DataSources", required = false)
    private DataSourcesForCe dataSources;

    @XmlElement(name = "DataMaps", required = false)
    private DataMaps dataMaps;

    public DataSourcesForCe getDataSources() {
        return dataSources;
    }

    public void setDataSources(DataSourcesForCe dataSources) {
        this.dataSources = dataSources;
    }

    public DataMaps getDataMaps() {
        return dataMaps;
    }

    public void setDataMaps(DataMaps dataMaps) {
        this.dataMaps = dataMaps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EfwdForCe efwdForCe = (EfwdForCe) o;

        if (dataMaps != null ? !dataMaps.equals(efwdForCe.dataMaps) : efwdForCe.dataMaps != null) return false;
        if (dataSources != null ? !dataSources.equals(efwdForCe.dataSources) : efwdForCe.dataSources != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataSources != null ? dataSources.hashCode() : 0;
        result = 31 * result + (dataMaps != null ? dataMaps.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EfwdForCe{" +
                "dataSources=" + dataSources +
                ", dataMaps=" + dataMaps +
                '}';
    }
}
