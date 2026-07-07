package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")

@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionDetails {

    @XmlAttribute
    String connectionType;
    @XmlElement
    String dialect;
    @XmlElement
    String connectionId;
    @XmlElement
    String directory;
    @XmlElement
    DriverClass driverClass;
    @XmlElement
    String dbId;
    
    @XmlElement
    String vendorName;

    @Override
    public String toString() {
        return "ConnectionDetails{" +
                "connectionType='" + connectionType + '\'' +
                ", dialect='" + dialect + '\'' +
                ", connectionId='" + connectionId + '\'' +
                ", directory='" + directory + '\'' +
                ", driverClass=" + driverClass +
                ", subType='" + subType + '\'' +
                ", fetchMode='" + fetchMode + '\'' +
                ", persist='" + persist + '\'' +
                ", cache='" + cache + '\'' +
                '}';
    }

    @XmlAttribute
    private String subType;

    @XmlElement
    String fetchMode;

    @XmlElement
    String persist;

    @XmlElement
    String cache;

    public String getPersist() {
        return persist;
    }

    public void setPersist(String persist) {
        this.persist = persist;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getFetchMode() {
        return fetchMode;
    }

    public void setFetchMode(String fetchMode) {
        this.fetchMode = fetchMode;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        if(dialect.equals("org.hibernate.dialect.DerbyTenSevenDialect")){
            dialect="org.hibernate.dialect.DerbyDialect";
        }
        this.dialect = dialect;
    }

    public DriverClass getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(DriverClass driverClass) {
        this.driverClass = driverClass;
    }

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
    
	


}
