package com.helicalinsight.instant.ai.payload;

public class InteractiveChatPayload implements IInstantBIPayload {

    private final String input;
    private final String chatid;
    private final String chatSeqId;
    private final String subject;

    public InteractiveChatPayload(String input, String chatid, String chatSeqId, String subject) {
        this.input = input;
        this.chatid = chatid;
        this.chatSeqId = chatSeqId;
        this.subject = subject;
    }

    public String getInput() {
        return input;
    }

    public String getChatid() {
        return chatid;
    }

    public String getChatSeqId() {
        return chatSeqId;
    }

    public String getSubject() {
        return subject;
    }
}
