package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by helical021 on 1/24/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "measures")
@XmlAccessorType(XmlAccessType.FIELD)
public class Measures {
    @Override
    public String toString() {
        return "Measures{" +
                "measureList=" + measureList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measures measures = (Measures) o;

        if (measureList != null ? !measureList.equals(measures.measureList) : measures.measureList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return measureList != null ? measureList.hashCode() : 0;
    }

    public List<Measure> getMeasureList() {

        return measureList;
    }

    public void setMeasureList(List<Measure> measureList) {
        this.measureList = measureList;
    }

    @XmlElement(name = "measure")
    private List<Measure> measureList;
}
