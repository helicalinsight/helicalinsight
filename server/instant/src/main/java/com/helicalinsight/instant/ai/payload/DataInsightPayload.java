package com.helicalinsight.instant.ai.payload;

public class DataInsightPayload implements IInstantBIPayload {

    private final String chatSeqId;
    private final String chatid;
    private final String inputParam;
    private final String formData;
    private final String subjectString;
    private final String downstreamEndpoint;

    public DataInsightPayload(String chatSeqId, String chatid, String inputParam, String formData,
                              String subjectString, String downstreamEndpoint) {
        this.chatSeqId = chatSeqId;
        this.chatid = chatid;
        this.inputParam = inputParam;
        this.formData = formData;
        this.subjectString = subjectString;
        this.downstreamEndpoint = downstreamEndpoint;
    }

    public String getChatSeqId() {
        return chatSeqId;
    }

    public String getChatid() {
        return chatid;
    }

    public String getInputParam() {
        return inputParam;
    }

    public String getFormData() {
        return formData;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public String getDownstreamEndpoint() {
        return downstreamEndpoint;
    }
}
