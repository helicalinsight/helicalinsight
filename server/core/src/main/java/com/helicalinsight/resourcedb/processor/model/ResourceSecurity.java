package com.helicalinsight.resourcedb.processor.model;

import java.util.HashMap;
import java.util.Map;

public class ResourceSecurity {
    private Map<String,Integer> resourcePermissionMap=new HashMap<>();

    public Map<String, Integer> getResourcePermissionMap() {
        return resourcePermissionMap;
    }

    public void setResourcePermissionMap(Map<String, Integer> resourcePermissionMap) {
        this.resourcePermissionMap = resourcePermissionMap;
    }
}
