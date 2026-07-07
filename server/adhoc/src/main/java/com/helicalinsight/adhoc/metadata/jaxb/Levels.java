package com.helicalinsight.adhoc.metadata.jaxb;

import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by helical021 on 1/24/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "levels")
@XmlAccessorType(XmlAccessType.FIELD)
public class Levels {

    @XmlElement(name = "level")
    private List<Level> levelList;

    public List<Level> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<Level> levelList) {
        this.levelList = levelList;
    }
}
