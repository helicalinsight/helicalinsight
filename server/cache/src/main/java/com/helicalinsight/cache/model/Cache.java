package com.helicalinsight.cache.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "cache")
public class Cache implements Serializable {

    private static final int maximumQueryLengthCharacters = 10000;

    private static final long serialVersionUID = 1L;

    @Column(name = "cache_file_path")
    private String cacheFilePath;

    @Column(name = "cache_file_size")
    private Long cacheFileSize;

    @Column(name = "cache_file_time_stamp")
    private Date cacheFileTimeStamp;

    @Id
    @Column(name = "cache_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cacheId;

    @Column(name = "connection_file_path")
    private String connectionFilePath;

    @Lob
    @Column(name = "report_parameters", length = Integer.MAX_VALUE)
    private String parameters;

    @Column(name = "no_of_records")
    private Integer noOfRecords;

    @Column(name = "connection_id")
    private Long connectionId;

    @Column(name = "connection_type")
    private String connectionType;

    private Integer priority;

    @Lob
    @Column(length = Integer.MAX_VALUE)
    private String query;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cache", cascade = CascadeType.REMOVE)
    private List<CacheReport> reportList;

    private String status;

    @Column(name = "map_id")
    private Integer mapId;
    @Column(name = "cache_file_expiry")
    private Date cacheExpiryTime;
    
    @Column(name = "query_lookup")
    private String queryLookup;

    public Cache() {
    }

    public Cache(List<CacheReport> reportList) {
        this.reportList = reportList;
    }

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public Date getCacheExpiryTime() {
        return cacheExpiryTime;
    }

    public void setCacheExpiryTime(Date cacheExpiryTime) {
        this.cacheExpiryTime = cacheExpiryTime;
    }

    public String getCacheFilePath() {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
    }

    public Long getCacheFileSize() {
        return cacheFileSize;
    }

    public void setCacheFileSize(Long cacheFileSize) {
        this.cacheFileSize = cacheFileSize;
    }

    public Date getCacheFileTimeStamp() {
        return cacheFileTimeStamp;
    }

    public void setCacheFileTimeStamp(Date cacheFileTimeStamp) {
        this.cacheFileTimeStamp = cacheFileTimeStamp;
    }

    public Long getCacheId() {
        return cacheId;
    }

    public void setCacheId(Long cacheId) {
        this.cacheId = cacheId;
    }

    public String getConnectionFilePath() {
        return connectionFilePath;
    }

    public void setConnectionFilePath(String connectionFilePath) {
        this.connectionFilePath = connectionFilePath;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<CacheReport> getReportList() {
        return reportList;
    }

    public void setReportList(List<CacheReport> reportList) {
        this.reportList = reportList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Integer getNoOfRecords() {
        return noOfRecords;
    }

    public void setNoOfRecords(Integer noOfRecords) {
        this.noOfRecords = noOfRecords;
    }
    
    public String getQueryLookup() {
		return queryLookup;
	}

	public void setQueryLookup(String queryLookup) {
		this.queryLookup = queryLookup;
	}

    @Override
    public String toString() {
        return "Cache{" +
                "cacheFilePath='" + cacheFilePath + '\'' +
                ", cacheFileSize=" + cacheFileSize +
                ", cacheFileTimeStamp=" + cacheFileTimeStamp +
                ", cacheId=" + cacheId +
                ", connectionFilePath='" + connectionFilePath + '\'' +
                ", parameters='" + parameters + '\'' +
                ", noOfRecords=" + noOfRecords +
                ", connectionId=" + connectionId +
                ", connectionType='" + connectionType + '\'' +
                ", priority=" + priority +
                ", query='" + query + '\'' +
                ", reportList=" + reportList +
                ", status='" + status + '\'' +
                ", mapId=" + mapId +
                ", cacheExpiryTime=" + cacheExpiryTime +
                '}';
    }
}