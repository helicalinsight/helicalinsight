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
import java.util.List;

/**
 * @author Rajesh
 * Created by author on 4/9/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "DataMaps")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataMaps {
    @XmlElement(name = "DataMap")
    private List<DataMap> dataMapList;

    @XmlElement(name = "Parameters")
    private Parameters parameters;

    public List<DataMap> getDataMapList() {
        return dataMapList;
    }

    public void setDataMapList(List<DataMap> dataMapList) {
        this.dataMapList = dataMapList;
    }


}
