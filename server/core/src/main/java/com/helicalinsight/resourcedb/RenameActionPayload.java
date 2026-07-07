package com.helicalinsight.resourcedb;

import com.helicalinsight.core.request.PayLoad;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RenameActionPayload implements PayLoad {

    @NotEmpty(message = "SourceArray Cannot Be Empty")
    private List<List<String>> sourceArray;

    @NotEmpty(message = "action Cannot Be Empty")
    private String action;


    public List<List<String>> getSourceArray() {
        return sourceArray;
    }

    public void setSourceArray(List<List<String>> sourceArray) {
        this.sourceArray = sourceArray;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
