package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Somen
 */


@Entity
@Table(name = "cube_metadata")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CubeMetadataInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "partition_columns")
    private String partitionColumn;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "total_num_records")
    private Long totalRecords;

    @Column(name = "data_size_in_mb")
    private Long dataSize;

    public Long getDataSize() {
        return dataSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubeMetadataInformation that = (CubeMetadataInformation) o;

        if (cubeId != null ? !cubeId.equals(that.cubeId) : that.cubeId != null) return false;
        if (dataSize != null ? !dataSize.equals(that.dataSize) : that.dataSize != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastPhaseId != null ? !lastPhaseId.equals(that.lastPhaseId) : that.lastPhaseId != null) return false;
        if (partitionColumn != null ? !partitionColumn.equals(that.partitionColumn) : that.partitionColumn != null)
            return false;
        if (phaseName != null ? !phaseName.equals(that.phaseName) : that.phaseName != null) return false;
        if (storageLocation != null ? !storageLocation.equals(that.storageLocation) : that.storageLocation != null)
            return false;
        if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;
        if (totalRecords != null ? !totalRecords.equals(that.totalRecords) : that.totalRecords != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (partitionColumn != null ? partitionColumn.hashCode() : 0);
        result = 31 * result + (storageLocation != null ? storageLocation.hashCode() : 0);
        result = 31 * result + (totalRecords != null ? totalRecords.hashCode() : 0);
        result = 31 * result + (dataSize != null ? dataSize.hashCode() : 0);
        result = 31 * result + (cubeId != null ? cubeId.hashCode() : 0);
        result = 31 * result + (phaseName != null ? phaseName.hashCode() : 0);
        result = 31 * result + (lastPhaseId != null ? lastPhaseId.hashCode() : 0);
        return result;
    }

    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }

    @Column(name = "cube_id")
    private String cubeId;


    @Column(name = "current_phase")
    private String phaseName;


    @Column(name = "last_phase_id")
    private Long lastPhaseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPartitionColumn() {
        return partitionColumn;
    }

    public void setPartitionColumn(String partitionColumn) {
        this.partitionColumn = partitionColumn;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public Long getLastPhaseId() {
        return lastPhaseId;
    }

    public void setLastPhaseId(Long lastPhaseId) {
        this.lastPhaseId = lastPhaseId;
    }
}
