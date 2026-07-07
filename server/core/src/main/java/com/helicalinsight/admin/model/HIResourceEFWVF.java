package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="hi_resource_efwvf")
@SecondaryTables(
        {
                @SecondaryTable(name = "hi_resource_efwvf_custom", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_area", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_area_spline", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_area_step", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_bar", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_donut", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_gauge", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_line", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_pie", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_scatter", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_spline", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_cross_tab", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_table", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
                @SecondaryTable(name = "hi_resource_efwvf_step", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
        }
)

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceEFWVF implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    private HIResource hiResource;

    @Column(name="chart_id")
    private Integer chartId;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="prop_name")
    private String propName;

    @Column(name="prop_type")
    private String propType;

    @Column(name="prop_datasource")
    private Integer propDataSource;

    @Column(name="prop_script", length = Integer.MAX_VALUE)
    @Lob
    private String propScript;

    @Column(name="created_by")
    private Integer createdBy;

    @Embedded
    private HIResourceEFWFCustom hiResourceEFWFCustom;

    @Embedded
    private HIResourceEFWVFArea hiResourceEFWVFArea;

    @Embedded
    private HIResourceEfwvfAreaSpline hiResourceEfwvfAreaSpline;

    @Embedded
    private HIResourceEfwvfAreaStep hiResourceEfwvfAreaStep;

    @Embedded
    private HIResourceEfwvfBar hiResourceEfwvfBar;

    @Embedded
    private HIResourceEfwvfCrossTab hiResourceEfwvfCrossTab;

    @Embedded
    private HIResourceEfwvfDonut hiResourceEfwvfDonut;

    @Embedded
    private HIResourceEfwvfGauge hiResourceEfwvfGauge;

    @Embedded
    private HIResourceEfwvfLine hiResourceEfwvfLine;

    @Embedded
    private HIResourceEfwvfPie hiResourceEfwvfPie;

    @Embedded
    private HIResourceEfwvfScatter hiResourceEfwvfScatter;

    @Embedded
    private HIResourceEfwvfSpline hiResourceEfwvfSpline;

    @Embedded
    private HIResourceEfwvfStep hiResourceEfwvfStep;

    @Embedded
    private HIResourceEfwvfTable hiResourceEfwvfTable;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropType() {
        return propType;
    }

    public void setPropType(String propType) {
        this.propType = propType;
    }

    public Integer getPropDataSource() {
        return propDataSource;
    }

    public void setPropDataSource(Integer propDataSource) {
        this.propDataSource = propDataSource;
    }

    public String getPropScript() {
        return propScript;
    }

    public void setPropScript(String propScript) {
        this.propScript = propScript;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public HIResourceEFWFCustom getHiResourceEFWFCustom() {
        return hiResourceEFWFCustom;
    }

    public void setHiResourceEFWFCustom(HIResourceEFWFCustom hiResourceEFWFCustom) {
        this.hiResourceEFWFCustom = hiResourceEFWFCustom;
    }

    public HIResourceEFWVFArea getHiResourceEFWVFArea() {
        return hiResourceEFWVFArea;
    }

    public void setHiResourceEFWVFArea(HIResourceEFWVFArea hiResourceEFWVFArea) {
        this.hiResourceEFWVFArea = hiResourceEFWVFArea;
    }

    public HIResourceEfwvfAreaSpline getHiResourceEfwvfAreaSpline() {
        return hiResourceEfwvfAreaSpline;
    }

    public void setHiResourceEfwvfAreaSpline(HIResourceEfwvfAreaSpline hiResourceEfwvfAreaSpline) {
        this.hiResourceEfwvfAreaSpline = hiResourceEfwvfAreaSpline;
    }

    public HIResourceEfwvfAreaStep getHiResourceEfwvfAreaStep() {
        return hiResourceEfwvfAreaStep;
    }

    public void setHiResourceEfwvfAreaStep(HIResourceEfwvfAreaStep hiResourceEfwvfAreaStep) {
        this.hiResourceEfwvfAreaStep = hiResourceEfwvfAreaStep;
    }

    public HIResourceEfwvfBar getHiResourceEfwvfBar() {
        return hiResourceEfwvfBar;
    }

    public void setHiResourceEfwvfBar(HIResourceEfwvfBar hiResourceEfwvfBar) {
        this.hiResourceEfwvfBar = hiResourceEfwvfBar;
    }

    public HIResourceEfwvfCrossTab getHiResourceEfwvfCrossTab() {
        return hiResourceEfwvfCrossTab;
    }

    public void setHiResourceEfwvfCrossTab(HIResourceEfwvfCrossTab hiResourceEfwvfCrossTab) {
        this.hiResourceEfwvfCrossTab = hiResourceEfwvfCrossTab;
    }

    public HIResourceEfwvfDonut getHiResourceEfwvfDonut() {
        return hiResourceEfwvfDonut;
    }

    public void setHiResourceEfwvfDonut(HIResourceEfwvfDonut hiResourceEfwvfDonut) {
        this.hiResourceEfwvfDonut = hiResourceEfwvfDonut;
    }

    public HIResourceEfwvfGauge getHiResourceEfwvfGauge() {
        return hiResourceEfwvfGauge;
    }

    public void setHiResourceEfwvfGauge(HIResourceEfwvfGauge hiResourceEfwvfGauge) {
        this.hiResourceEfwvfGauge = hiResourceEfwvfGauge;
    }

    public HIResourceEfwvfLine getHiResourceEfwvfLine() {
        return hiResourceEfwvfLine;
    }

    public void setHiResourceEfwvfLine(HIResourceEfwvfLine hiResourceEfwvfLine) {
        this.hiResourceEfwvfLine = hiResourceEfwvfLine;
    }

    public HIResourceEfwvfPie getHiResourceEfwvfPie() {
        return hiResourceEfwvfPie;
    }

    public void setHiResourceEfwvfPie(HIResourceEfwvfPie hiResourceEfwvfPie) {
        this.hiResourceEfwvfPie = hiResourceEfwvfPie;
    }

    public HIResourceEfwvfScatter getHiResourceEfwvfScatter() {
        return hiResourceEfwvfScatter;
    }

    public void setHiResourceEfwvfScatter(HIResourceEfwvfScatter hiResourceEfwvfScatter) {
        this.hiResourceEfwvfScatter = hiResourceEfwvfScatter;
    }

    public HIResourceEfwvfSpline getHiResourceEfwvfSpline() {
        return hiResourceEfwvfSpline;
    }

    public void setHiResourceEfwvfSpline(HIResourceEfwvfSpline hiResourceEfwvfSpline) {
        this.hiResourceEfwvfSpline = hiResourceEfwvfSpline;
    }

    public HIResourceEfwvfStep getHiResourceEfwvfStep() {
        return hiResourceEfwvfStep;
    }

    public void setHiResourceEfwvfStep(HIResourceEfwvfStep hiResourceEfwvfStep) {
        this.hiResourceEfwvfStep = hiResourceEfwvfStep;
    }

    public HIResourceEfwvfTable getHiResourceEfwvfTable() {
        return hiResourceEfwvfTable;
    }

    public void setHiResourceEfwvfTable(HIResourceEfwvfTable hiResourceEfwvfTable) {
        this.hiResourceEfwvfTable = hiResourceEfwvfTable;
    }

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }
}

