//package com.helicalinsight.admin.model;
//
//import jakarta.persistence.*;
//import java.io.Serializable;
//import java.util.Objects;
//@Entity
//@Table(name = "hi_resource_efwdd_hreport_id_mapping")
//public class HIResourceEfwddResource implements Serializable {
//    @Column(name="hi_resource_efwdd_id")
//    private Integer dashboardId;
//    @Column(name="hi_resource_hreport_id")
//    private Integer resourceId;
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;
//
//    public HIResourceEfwddResource() {
//    }
//
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        HIResourceEfwddResource that = (HIResourceEfwddResource) o;
//        return Objects.equals(dashboardId, that.dashboardId) && Objects.equals(resourceId, that.resourceId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(dashboardId, resourceId);
//    }
//
//    public Integer getDashboardId() {
//        return dashboardId;
//    }
//
//    public void setDashboardId(Integer dashboardId) {
//        this.dashboardId = dashboardId;
//    }
//
//    public Integer getResourceId() {
//        return resourceId;
//    }
//
//    public void setResourceId(Integer resourceId) {
//        this.resourceId = resourceId;
//    }
//}
