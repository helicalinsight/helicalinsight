package com.helicalinsight.instant.ai.payload;

public class RecommendDomainPayload implements IInstantBIPayload {

    private final String model;

    public RecommendDomainPayload(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
