package com.helicalinsight.admin.dto;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by helical021 on 6/3/2021.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<Pair<Integer, Integer>> userPermission;
    private List<Pair<Integer, Integer>> orgPermission;
    private List<Pair<Integer, Integer>> rolePermission;

    public List<Pair<Integer, Integer>> getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(List<Pair<Integer, Integer>> userPermission) {
        this.userPermission = userPermission;
    }

    public List<Pair<Integer, Integer>> getOrgPermission() {
        return orgPermission;
    }

    public void setOrgPermission(List<Pair<Integer, Integer>> orgPermission) {
        this.orgPermission = orgPermission;
    }

    public List<Pair<Integer, Integer>> getRolePermission() {
        return rolePermission;
    }

    public void setRolePermission(List<Pair<Integer, Integer>> rolePermission) {
        this.rolePermission = rolePermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShareDTO shareDTO = (ShareDTO) o;

        if (orgPermission != null ? !orgPermission.equals(shareDTO.orgPermission) : shareDTO.orgPermission != null)
            return false;
        if (rolePermission != null ? !rolePermission.equals(shareDTO.rolePermission) : shareDTO.rolePermission != null)
            return false;
        if (userPermission != null ? !userPermission.equals(shareDTO.userPermission) : shareDTO.userPermission != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userPermission != null ? userPermission.hashCode() : 0;
        result = 31 * result + (orgPermission != null ? orgPermission.hashCode() : 0);
        result = 31 * result + (rolePermission != null ? rolePermission.hashCode() : 0);
        return result;
    }
}
