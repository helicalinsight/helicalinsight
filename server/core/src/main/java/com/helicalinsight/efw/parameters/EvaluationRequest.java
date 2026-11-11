package com.helicalinsight.efw.parameters;

import java.time.Clock;
import java.time.ZoneId;

/**
 * The inputs for one token evaluation: which clock, which zone, and how the result should be written.
 */
public final class EvaluationRequest {

    private final ZoneId zone;
    private final Clock clock;
    private final String pattern;
    private final boolean endOfRange;
    private final boolean singleValue;
    private final String anchorDate;

    private EvaluationRequest(ZoneId zone, Clock clock, String pattern, boolean endOfRange, boolean singleValue,
            String anchorDate) {
        this.zone = zone;
        this.clock = clock;
        this.pattern = pattern;
        this.endOfRange = endOfRange;
        this.singleValue = singleValue;
        this.anchorDate = anchorDate;
    }

    public static EvaluationRequest single(ZoneId zone, Clock clock, String pattern) {
        return new EvaluationRequest(zone, clock, pattern, false, true, null);
    }

    public static EvaluationRequest rangeStart(ZoneId zone, Clock clock, String pattern) {
        return new EvaluationRequest(zone, clock, pattern, false, false, null);
    }

    public static EvaluationRequest rangeEnd(ZoneId zone, Clock clock, String pattern) {
        return new EvaluationRequest(zone, clock, pattern, true, false, null);
    }

    public EvaluationRequest withAnchor(String anchorDate) {
        return new EvaluationRequest(zone, clock, pattern, endOfRange, singleValue, anchorDate);
    }

    public ZoneId zone() {
        return zone;
    }

    public Clock clock() {
        return clock;
    }

    public String pattern() {
        return pattern;
    }

    public boolean endOfRange() {
        return endOfRange;
    }

    public boolean singleValue() {
        return singleValue;
    }

    public String anchorDate() {
        return anchorDate;
    }
}
