package com.helicalinsight.instant.ai.payload;

public class ConvertChartPayload implements IInstantBIPayload {

    private final String vfTemplate;
    private final String selectedChart;
    private final String chatId;
    private final String chatSequence;

    public ConvertChartPayload(String vfTemplate, String selectedChart, String chatId, String chatSequence) {
        this.vfTemplate = vfTemplate;
        this.selectedChart = selectedChart;
        this.chatId = chatId;
        this.chatSequence = chatSequence;
    }

    public String getVfTemplate() {
        return vfTemplate;
    }

    public String getSelectedChart() {
        return selectedChart;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatSequence() {
        return chatSequence;
    }
}
