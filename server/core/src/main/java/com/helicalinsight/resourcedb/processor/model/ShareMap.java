package com.helicalinsight.resourcedb.processor.model;

import java.util.List;
import java.util.Map;

public class ShareMap {
    private RoleMap roleMap;

    private Map<String, List<Pair>> securityMap;

    public RoleMap getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(RoleMap roleMap) {
        this.roleMap = roleMap;
    }

    public Map<String, List<Pair>> getSecurityMap() {
        return securityMap;
    }

    public void setSecurityMap(Map<String, List<Pair>> securityMap) {
        this.securityMap = securityMap;
    }
}
