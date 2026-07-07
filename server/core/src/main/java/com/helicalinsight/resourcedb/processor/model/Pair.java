package com.helicalinsight.resourcedb.processor.model;

public class Pair {
    Integer id;
    Integer permission;
    Boolean isMandatory;

    public Pair(Integer id,Integer permission){
        this.id = id;
        this.permission = permission;
    }

    public Boolean getMandatory() {
        return isMandatory;
    }

    public void setMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPermission() {
        return permission;
    }
}
