package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "hi_resource_cube")
public class HIMetadataCube implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column
    private String name;

    @Column(name="domain")
    private String domainName;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Column
    private Boolean visible;


    @Column(name="cube_id")
    private String cubeId;

    //A string being displayed instead of the Dimension's name.
    @Column
    private String caption;

    @Column(name = "default_measure")
    private String defaultMeasure;


    @Column
    private String description;


    @Column
    private Boolean cache;

    @Column
    private Boolean enabled;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hiMetadataCube", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HICubeDimension> dimensions;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hiMetadataCube", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HICubeMeasure> measures;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata_id")
    private HIResourceMetadata hiResourceMetadata;

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "resource_id")
    private HIResource hiResource;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getCubeId() {
        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDefaultMeasure() {
        return defaultMeasure;
    }

    public void setDefaultMeasure(String defaultMeasure) {
        this.defaultMeasure = defaultMeasure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<HICubeDimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<HICubeDimension> dimensions) {
        this.dimensions = dimensions;
    }

    public List<HICubeMeasure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<HICubeMeasure> measures) {
        this.measures = measures;
    }

    public HIResourceMetadata getHiResourceMetadata() {
        return hiResourceMetadata;
    }

    public void setHiResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        this.hiResourceMetadata = hiResourceMetadata;
    }
}
