package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
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
