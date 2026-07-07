package com.helicalinsight.admin.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "hi_efwd_connection_security", indexes = {
        @Index(name = "idx_efwd_conn_sec_conn_id", columnList = "efwd_conn_id")
})
@BatchSize(size = 20)
@DynamicUpdate
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIEfwdConnSecurity implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name="created_by")
    private Integer createdBy;

    @Column(name="last_updated_time")
    private Date lastUpdatedTime;

    @Column(name="permission")
    private Integer permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "efwd_conn_id",referencedColumnName = "id")
    private HIEfwdConnection hiEfwdConnection;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="org_id",referencedColumnName = "id")
    private Organization orgId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User userId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="role_id",referencedColumnName = "id")
    private Role roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public HIEfwdConnection getHiEfwdConnection() {
        return hiEfwdConnection;
    }

    public void setHiEfwdConnection(HIEfwdConnection hiEfwdConnection) {
        this.hiEfwdConnection = hiEfwdConnection;
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

}
