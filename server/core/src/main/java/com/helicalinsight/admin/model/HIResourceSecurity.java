package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="hi_resource_security")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIResourceSecurity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "resource_id",nullable = false)
    private HIResource hiResource;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="org_id")
    private Organization orgId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User userId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="role_id")
    private Role roleId;

    @Column(name="permission")
    private Integer permission;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;

    @Column(name="created_by")
    private String createdBy;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HIResource getHiResource() {
        return hiResource;
    }

    public void setHiResource(HIResource hiResource) {
        this.hiResource = hiResource;
    }

    public Organization getOrgId() {
        return orgId;
    }

    public void setOrgId(Organization orgId) {
        this.orgId = orgId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Role getRoleId() {
        return roleId;
    }

    public void setRoleId(Role roleId) {
        this.roleId = roleId;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HIResourceSecurity that = (HIResourceSecurity) o;
        return Objects.equals(id, that.id) && Objects.equals(hiResource, that.hiResource) && Objects.equals(orgId, that.orgId) && Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId) && Objects.equals(permission, that.permission) && Objects.equals(lastUpdatedTime, that.lastUpdatedTime) && Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hiResource, orgId, userId, roleId, permission, lastUpdatedTime, createdBy);
    }

    @Override
    public String toString() {
        return "HIResourceSecurity{" +
                "id=" + id +
                ", hiResource=" + hiResource +
                ", orgId=" + orgId +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", permission=" + permission +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
