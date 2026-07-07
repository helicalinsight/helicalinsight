package com.helicalinsight.scheduling.model;


import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Rajesh
 *         Created by author on 2/17/2020.
 */
@Entity
@Table(name = "hi_resource")
public class HiResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "name")
    private String name;

    @Column(name = "folder")
    private Boolean isFolder;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "resource_type")
    private ResourceType resourceType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "visible")
    private Boolean visible;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Column(name = "resource_path")
    private String resourcePath;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "resourceId",cascade = CascadeType.REMOVE)
    private List<Schedules> resourceIdSchedules;

    @Column(name = "is_migrated")
    private Boolean isMigrated;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public List<Schedules> getResourceIdSchedules() {
        return resourceIdSchedules;
    }

    public void setResourceIdSchedules(List<Schedules> resourceIdSchedules) {
        this.resourceIdSchedules = resourceIdSchedules;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiResource that = (HiResource) o;
        return Objects.equals(resourceId, that.resourceId) && Objects.equals(name, that.name) && Objects.equals(isFolder, that.isFolder) && Objects.equals(description, that.description) && Objects.equals(resourceType, that.resourceType) && Objects.equals(parentId, that.parentId) && Objects.equals(visible, that.visible) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(lastModifiedDate, that.lastModifiedDate) && Objects.equals(resourcePath, that.resourcePath) && Objects.equals(resourceIdSchedules, that.resourceIdSchedules) && Objects.equals(isMigrated, that.isMigrated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, name, isFolder, description, resourceType, parentId, visible, createdBy, createdDate, lastModifiedDate, resourcePath, resourceIdSchedules, isMigrated);
    }

    @Override
    public String toString() {
        return "HiResource{" +
                "resourceId=" + resourceId +
                ", name='" + name + '\'' +
                ", isFolder=" + isFolder +
                ", description='" + description + '\'' +
                ", resourceType=" + resourceType +
                ", parentId=" + parentId +
                ", visible=" + visible +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", resourcePath='" + resourcePath + '\'' +
                ", resourceIdSchedules=" + resourceIdSchedules +
                ", isMigrated=" + isMigrated +
                '}';
    }
}
