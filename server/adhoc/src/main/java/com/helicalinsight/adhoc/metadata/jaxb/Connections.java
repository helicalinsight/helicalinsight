package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by author on 13/10/2020
 *
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "connections")
@XmlAccessorType(XmlAccessType.FIELD)
public class Connections {
    @XmlElement(name = "connectionDatabase")
    private List<ConnectionDatabase> connectionDatabase;

    public List<ConnectionDatabase> getConnectionDatabase() {
        return connectionDatabase;
    }

    public void setConnectionDatabase(List<ConnectionDatabase> connectionDatabase) {
        this.connectionDatabase = connectionDatabase;
    }

    @Override
    public String toString() {
        return "Connections{" +
                "connectionDatabase=" + connectionDatabase +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connections that = (Connections) o;

        if (connectionDatabase != null ? !connectionDatabase.equals(that.connectionDatabase) : that.connectionDatabase != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return connectionDatabase != null ? connectionDatabase.hashCode() : 0;
    }


}
