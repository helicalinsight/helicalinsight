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
@XmlRootElement(name = "DataSources")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSourcesForCe {
    @XmlElement(name = "Connection")
    private List<ConnectionForCe> connectionList;

    public List<ConnectionForCe> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<ConnectionForCe> connectionList) {
        this.connectionList = connectionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSourcesForCe that = (DataSourcesForCe) o;

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
        return "DataSourcesForCe{" +
                "connectionList=" + connectionList +
                '}';
    }
}
