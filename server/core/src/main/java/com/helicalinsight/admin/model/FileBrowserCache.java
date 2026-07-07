package com.helicalinsight.admin.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Created by Rajesh on 3/25/2019.
 */
@Entity
@Table(name = "cache_file_browser")

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FileBrowserCache implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "last_modified")
    private Long lastModified;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "title", length = 2000)
    private String title;

    @Column(name = "logical_path", length = 2000)
    private String logicalPath;

    @Column(name = "folder_type")
    private String folderType;

    @Column(name = "is_file_large")
    private Boolean isFileLarge;

    @Lob
    @Column(name = "json_data", length = 2147483647)
    private String json;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getLogicalPath() {
        return logicalPath;
    }

    public void setLogicalPath(String logicalPath) {
        this.logicalPath = logicalPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getIsFileLarge() {
        return isFileLarge;
    }

    public void setIsFileLarge(Boolean isFileLarge) {
        this.isFileLarge = isFileLarge;
    }

    @Override
    public String toString() {
        return "FileBrowserCache{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", lastModified=" + lastModified +
                ", parentId='" + parentId + '\'' +
                ", title='" + title + '\'' +
                ", logicalPath='" + logicalPath + '\'' +
                ", folderType='" + folderType + '\'' +
                ", isFileLarge='" + isFileLarge + '\'' +
                ", json='" + json + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileBrowserCache that = (FileBrowserCache) o;

        if (id != that.id) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        if (filePath != null ? !filePath.equals(that.filePath) : that.filePath != null) return false;
        if (fileType != null ? !fileType.equals(that.fileType) : that.fileType != null) return false;
        if (folderType != null ? !folderType.equals(that.folderType) : that.folderType != null) return false;
        if (isFileLarge != null ? !isFileLarge.equals(that.isFileLarge) : that.isFileLarge != null) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null) return false;
        if (logicalPath != null ? !logicalPath.equals(that.logicalPath) : that.logicalPath != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (filePath != null ? filePath.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (logicalPath != null ? logicalPath.hashCode() : 0);
        result = 31 * result + (folderType != null ? folderType.hashCode() : 0);
        result = 31 * result + (isFileLarge != null ? isFileLarge.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }
}
