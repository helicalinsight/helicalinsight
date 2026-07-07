package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by helical021 on 1/24/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "hierarchies")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hierarchies {

    public List<Hierarchy> getHierarchyList() {
        return hierarchyList;
    }


    public void setHierarchyList(List<Hierarchy> hierarchyList) {
        this.hierarchyList = hierarchyList;
    }

    @XmlElement(name = "hierarchy")
    private List<Hierarchy> hierarchyList;
}
