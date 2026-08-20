package com.helicalinsight.instant.ai.payload;

public class RecommendAnalystPayload implements IInstantBIPayload {

    private final String model;
    private final String domain;

    public RecommendAnalystPayload(String model, String domain) {
        this.model = model;
        this.domain = domain;
    }

    public String getModel() {
        return model;
    }

    public String getDomain() {
        return domain;
    }
}
