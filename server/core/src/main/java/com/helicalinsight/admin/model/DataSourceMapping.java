package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Somen
 *         Created by helical021 on 4/1/2019.
 */
@Entity
@Table(name = "cache_datasource")

@org.hibernate.annotations.Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
public class DataSourceMapping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "expected_records_count")
    private int maxPages;


    @Column(name = "catalog_name")
    private String catalog = "";


    @Column(name = "schema_name")
    private String schema = "";

    @Column(name = "connection_name")
    private String connectionName;


    @Lob
    @Column(name="table_id_index", length = Integer.MAX_VALUE)
    private String tableIdIndex;

    @Lob
    @Column(name="table_name_index", length = Integer.MAX_VALUE)
    private String tableNameIndex;
    @Column(name = "connection_id")
    private Integer connectionId;
    @Column(name = "cache_key")
    private String key;
    @Column(name = "hi_repo_dir")
    private String dir;
    @Column(name = "hi_repo_file")
    private String file;
    @Column(name = "type")
    private String type;//=catalog/schema/table/column/joins
    @Column(name = "created_time")
    private Date createDateTime;

    public String getTableNameIndex() {
        return tableNameIndex;
    }

    public void setTableNameIndex(String tableNameIndex) {
        this.tableNameIndex = tableNameIndex;
    }

    @Override
    public String toString() {
        return "DataSourceMapping{" +
                "id=" + id +
                ", maxPages=" + maxPages +
                ", catalog='" + catalog + '\'' +
                ", schema='" + schema + '\'' +
                ", connectionName='" + connectionName + '\'' +
                ", tableIdIndex='" + tableIdIndex + '\'' +
                ", tableNameIndex='" + tableNameIndex + '\'' +
                ", connectionId=" + connectionId +
                ", key='" + key + '\'' +
                ", dir='" + dir + '\'' +
                ", file='" + file + '\'' +
                ", type='" + type + '\'' +
                ", createDateTime=" + createDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSourceMapping that = (DataSourceMapping) o;

        if (id != that.id) return false;
        if (maxPages != that.maxPages) return false;
        if (catalog != null ? !catalog.equals(that.catalog) : that.catalog != null) return false;
        if (connectionId != null ? !connectionId.equals(that.connectionId) : that.connectionId != null) return false;
        if (connectionName != null ? !connectionName.equals(that.connectionName) : that.connectionName != null)
            return false;
        if (createDateTime != null ? !createDateTime.equals(that.createDateTime) : that.createDateTime != null)
            return false;
        if (dir != null ? !dir.equals(that.dir) : that.dir != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;
        if (tableIdIndex != null ? !tableIdIndex.equals(that.tableIdIndex) : that.tableIdIndex != null) return false;
        if (tableNameIndex != null ? !tableNameIndex.equals(that.tableNameIndex) : that.tableNameIndex != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + maxPages;
        result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (connectionName != null ? connectionName.hashCode() : 0);
        result = 31 * result + (tableIdIndex != null ? tableIdIndex.hashCode() : 0);
        result = 31 * result + (tableNameIndex != null ? tableNameIndex.hashCode() : 0);
        result = 31 * result + (connectionId != null ? connectionId.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (createDateTime != null ? createDateTime.hashCode() : 0);
        return result;
    }

    public String getTableIdIndex() {
        return tableIdIndex;
    }

    public void setTableIdIndex(String containedTables) {
        this.tableIdIndex = containedTables;
    }

    public String getConnectionName() {

        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        if (catalog == null) this.catalog = "";
        else this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        if (schema == null)
            this.schema = "";
        else
            this.schema = schema;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }


    public String getDir() {

        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }
}
