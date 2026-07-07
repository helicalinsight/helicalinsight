package com.helicalinsight.resourcedb.processor.model;

import com.helicalinsight.admin.model.Organization;

import java.util.List;

public class Security {
    public Integer createdBy;
    public List<Organization> organizationList;

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }
}
