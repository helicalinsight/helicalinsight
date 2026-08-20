package com.helicalinsight.instant.ai.payload;

public class ChatContextPayload implements IInstantBIPayload {

    private final String input;

    public ChatContextPayload(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
