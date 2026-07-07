package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="hi_resource_hwf")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceHWF implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;

    @Column(name="created_by")
    private int createdBy;

    @OneToOne
    private HIResource hiResource;

    @OneToMany(fetch = FetchType.LAZY)
    private List<HWFExecution> hwfExecution;

    @OneToMany(fetch = FetchType.LAZY)
    private List<HIHwfInput> inputList;

    @OneToMany(fetch = FetchType.LAZY)
    private List<HIHwfOutput> outputList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public List<HWFExecution> getHwfExecution() {
        return hwfExecution;
    }

    public void setHwfExecution(List<HWFExecution> hwfExecution) {
        this.hwfExecution = hwfExecution;
    }

    public List<HIHwfInput> getInputList() {
        return inputList;
    }

    public void setInputList(List<HIHwfInput> inputList) {
        this.inputList = inputList;
    }

    public List<HIHwfOutput> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<HIHwfOutput> outputList) {
        this.outputList = outputList;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }
}
