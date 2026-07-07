package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Component
@Scope("prototype")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSources {
    @XmlElement(name = "Connection")
    private List<Connection> connectionList;

    public DataSources() {

    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<Connection> connectionList) {
        this.connectionList = connectionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSources that = (DataSources) o;

        if (connectionList != null ? !connectionList.equals(that.connectionList) : that.connectionList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return connectionList != null ? connectionList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DataSources{" +
                "connectionList=" + connectionList +
                '}';
    }
}
