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

