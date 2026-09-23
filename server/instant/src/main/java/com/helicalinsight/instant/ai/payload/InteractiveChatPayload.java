package com.helicalinsight.instant.ai.payload;

/**
 * Interactive-chat request. Optional {@code mode}: fast | think | auto.
 * Optional {@code removeColsInFilter} is forwarded to InstantBI as
 * {@code rm_cols_in_filter}; omit it so InstantBI can use application_config.yaml.
 */
public class InteractiveChatPayload implements IInstantBIPayload {

    private final String input;
    private final String chatid;
    private final String chatSeqId;
    private final String subject;
    private final String mode;
    private final Boolean removeColsInFilter;

    public InteractiveChatPayload(String input, String chatid, String chatSeqId, String subject) {
        this(input, chatid, chatSeqId, subject, null, null);
    }

    public InteractiveChatPayload(String input, String chatid, String chatSeqId, String subject, String mode) {
        this(input, chatid, chatSeqId, subject, mode, null);
    }

    public InteractiveChatPayload(String input, String chatid, String chatSeqId, String subject, String mode,
            Boolean removeColsInFilter) {
        this.input = input;
        this.chatid = chatid;
        this.chatSeqId = chatSeqId;
        this.subject = subject;
        this.mode = mode;
        this.removeColsInFilter = removeColsInFilter;
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

    public String getMode() {
        return mode;
    }

    public Boolean getRemoveColsInFilter() {
        return removeColsInFilter;
    }
}
