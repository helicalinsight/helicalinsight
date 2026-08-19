package com.helicalinsight.instant.ai.payload;

public class LoadChatPayload implements IInstantBIPayload {

    private final String chatSeqId;
    private final String formData;

    public LoadChatPayload(String chatSeqId, String formData) {
        this.chatSeqId = chatSeqId;
        this.formData = formData;
    }

    public String getChatSeqId() {
        return chatSeqId;
    }

    public String getFormData() {
        return formData;
    }
}
