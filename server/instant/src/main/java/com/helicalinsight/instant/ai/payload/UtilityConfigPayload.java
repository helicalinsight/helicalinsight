package com.helicalinsight.instant.ai.payload;

public class UtilityConfigPayload implements IInstantBIPayload {

    private final String utilityPath;

    public UtilityConfigPayload(String utilityPath) {
        this.utilityPath = utilityPath;
    }

    public String getUtilityPath() {
        return utilityPath;
    }
}
