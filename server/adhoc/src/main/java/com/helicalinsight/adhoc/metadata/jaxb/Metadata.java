package com.helicalinsight.adhoc.metadata.jaxb;

import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Security;
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
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class Metadata implements IResource {

    @XmlAttribute
    private Boolean isCached;

    @XmlAttribute
    private String metadataId;

    @XmlAttribute
    private String version;

    public Boolean getCached() {
        return isCached;
    }

    public void setCached(Boolean cached) {
        isCached = cached;
    }

    public String getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String databaseType;

    @XmlElement
    private String fileName;

    @XmlElement
    private String connectionType;

    @XmlElement
    private String visible;

    @XmlElement(name = "connection")
    private ConnectionDetails connectionDetails;

    @XmlElement
    private Connections connections;
    @XmlElement
    private Database database;
    @XmlElement
    private Security security;
    @XmlElement
    private Security.Share share;
    @XmlElement(name = "access")
    private MetadataSecurity metadataSecurity;

    @XmlElement
    private ExternalRelationships externalRelationships;

    public ExternalRelationships getExternalRelationships() {
        return externalRelationships;
    }

    public void setExternalRelationships(ExternalRelationships externalRelationships) {
        this.externalRelationships = externalRelationships;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "type='" + type + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", connectionType='" + connectionType + '\'' +
                ", visible='" + visible + '\'' +
                ", connectionDetails=" + connectionDetails +
                ", connections=" + connections +
                ", database=" + database +
                ", security=" + security +
                ", share=" + share +
                ", metadataSecurity=" + metadataSecurity +
                ", externalRelationships=" + externalRelationships +
                '}';
    }

    public Connections getConnections() {

        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
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

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Security.Share getShare() {
        return share;
    }

    public void setShare(Security.Share share) {
        this.share = share;
    }

    public MetadataSecurity getMetadataSecurity() {
        return metadataSecurity;
    }

    public void setMetadataSecurity(MetadataSecurity metadataSecurity) {
        this.metadataSecurity = metadataSecurity;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

}
