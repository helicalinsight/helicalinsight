package com.helicalinsight.instant.ai.payload;

public class ConvertDashboardPayload implements IInstantBIPayload {

    private final String chatId;
    private final String items;
    private final String subjectString;
    private final String formData;
    private final String inputParam;

    public ConvertDashboardPayload(String chatId, String items, String subjectString, String formData,
                                   String inputParam) {
        this.chatId = chatId;
        this.items = items;
        this.subjectString = subjectString;
        this.formData = formData;
        this.inputParam = inputParam;
    }

    public String getChatId() {
        return chatId;
    }

    public String getItems() {
        return items;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public String getFormData() {
        return formData;
    }

    public String getInputParam() {
        return inputParam;
    }
}
