package com.helicalinsight.cache.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by helical019 on 12/31/2018.
 */
@Entity
@Table(name = "connection_details")
public class ConnectionDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "connection_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectionId;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "connectionDetails", cascade = CascadeType.ALL)
    private List<ConnectionCatalog> connectionCatalog;

    @Column(name = "dir")
    private Long dir;

    @Column(name = "file")
    private String file;

    @Column(name = "connection_type")
    private String connectionType;


    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public List<ConnectionCatalog> getConnectionCatalog() {
        return connectionCatalog;
    }

    public void setConnectionCatalog(List<ConnectionCatalog> connectionCatalog) {
        this.connectionCatalog = connectionCatalog;
    }

    public Long getDir() {
        return dir;
    }

    public void setDir(Long dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
}
