package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 13/10/2020
 *
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "connectionDatabase")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionDatabase {
    @XmlElement(name = "connection")
    private ConnectionDetails connectionDetails;

    @XmlElement
    private Database database;


    public ConnectionDatabase() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionDatabase that = (ConnectionDatabase) o;

        
        if (connectionDetails != null ? !connectionDetails.equals(that.connectionDetails) : that.connectionDetails != null)
            return false;
        if (database != null ? !database.equals(that.database) : that.database != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "ConnectionDatabase{" +
                "connectionDetails=" + connectionDetails +
                ", database=" + database +
                '}';
    }

    @Override
    public int hashCode() {
        int result = connectionDetails != null ? connectionDetails.hashCode() : 0;
        result = 31 * result + (database != null ? database.hashCode() : 0);
        return result;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
