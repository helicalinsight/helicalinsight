package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.scheduling.annotation.Schedules;
import com.helicalinsight.admin.model.ResourceTypeDB;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Rajesh
 *         Created by author on 2/17/2020.
 */
@Entity
@Table(name = "resource_type_db")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ResourceTypeDB implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_type_id")
    private Long resourceTypeId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "extension")
    private String extension;

    @OneToOne
    private HIResource hiResource;

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceTypeDB that = (ResourceTypeDB) o;
        return Objects.equals(resourceTypeId, that.resourceTypeId) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(extension, that.extension) && Objects.equals(hiResource, that.hiResource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceTypeId, name, description, extension, hiResource);
    }
}
