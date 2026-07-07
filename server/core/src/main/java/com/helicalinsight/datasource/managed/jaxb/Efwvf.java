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
 * Created by author on 4/17/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Charts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Efwvf {

    @XmlElement(name = "Chart")
    private List<Chart> charts;

    public List<Chart> getCharts() {
        return charts;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Efwvf efwvf = (Efwvf) o;

        if (charts != null ? !charts.equals(efwvf.charts) : efwvf.charts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return charts != null ? charts.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Efwvf{" +
                "charts=" + charts +
                '}';
    }
}
