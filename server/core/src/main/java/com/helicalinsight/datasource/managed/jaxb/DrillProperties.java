package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "drillProperties")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrillProperties {

    private Group group;
}


@Component
@Scope("prototype")
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
class Group {

    private String name;


}

@Component
@Scope("prototype")
@XmlRootElement(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
class Properties {
    private String key;
    private String value;

}




