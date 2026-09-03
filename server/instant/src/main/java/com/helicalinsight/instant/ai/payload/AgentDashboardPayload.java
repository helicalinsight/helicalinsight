package com.helicalinsight.instant.ai.payload;

/**
 * Agent-dashboard request. Interactive envelope with {@code dashboardid} and
 * {@code dashboard_sequence_id} instead of {@code chatid} / {@code chat_sequence_id}.
 * Optional {@code mode}: fast | balanced | research.
 */
public class AgentDashboardPayload implements IInstantBIPayload {

    private final String input;
    private final String dashboardid;
    private final String dashboardSeqId;
    private final String subject;
    private final String mode;

    public AgentDashboardPayload(String input, String dashboardid, String dashboardSeqId, String subject) {
        this(input, dashboardid, dashboardSeqId, subject, null);
    }

    public AgentDashboardPayload(String input, String dashboardid, String dashboardSeqId, String subject, String mode) {
        this.input = input;
        this.dashboardid = dashboardid;
        this.dashboardSeqId = dashboardSeqId;
        this.subject = subject;
        this.mode = mode;
    }

    public String getInput() {
        return input;
    }

    public String getDashboardid() {
        return dashboardid;
    }

    public String getDashboardSeqId() {
        return dashboardSeqId;
    }

    public String getSubject() {
        return subject;
    }

    public String getMode() {
        return mode;
    }
}
