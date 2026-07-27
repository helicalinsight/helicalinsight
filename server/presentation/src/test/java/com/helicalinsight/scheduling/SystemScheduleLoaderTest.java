package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SystemScheduleLoaderTest {

    @Test
    public void buildDailyCronUsesScheduledTime() {
        assertEquals("0 5 0 * * ?", SystemScheduleLoader.buildDailyCron("00:05:00"));
    }

    @Test
    public void buildDailyCronDefaultsMissingSeconds() {
        assertEquals("0 30 1 * * ?", SystemScheduleLoader.buildDailyCron("01:30"));
    }

    @Test
    public void resolveCronExpressionPrefersExplicitCron() {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("cron", "0 0/15 * * * ?");
        schedule.put("scheduledTime", "00:05:00");

        assertEquals("0 0/15 * * * ?", SystemScheduleLoader.resolveCronExpression(schedule));
    }

    @Test
    public void resolveCronExpressionFallsBackToScheduledTime() {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("scheduledTime", "00:05:00");

        assertEquals("0 5 0 * * ?", SystemScheduleLoader.resolveCronExpression(schedule));
    }

    @Test
    public void extractSchedulesReadsArrayEntries() {
        List<Object> schedules = new ArrayList<>();
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id", "llmUsageAuditPurge");
        schedules.add(schedule);

        List<Map<String, Object>> entries = SystemScheduleLoader.extractSchedules(schedules);

        assertEquals(1, entries.size());
        assertEquals("llmUsageAuditPurge", entries.get(0).get("id"));
    }

    @Test
    public void resolveExpireDateTreatsNeverAsNoExpiry() {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("expireDate", "never");
        assertNull(SystemScheduleLoader.resolveExpireDate(schedule));
    }

    @Test
    public void resolveExpireDateParsesDateString() {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("expireDate", "2099-12-31");
        assertTrue(SystemScheduleLoader.resolveExpireDate(schedule).getTime() > System.currentTimeMillis());
    }

    @Test
    public void invokeGroovyReceivesScheduleNode() throws Exception {
        String groovy = """
                def execute(Map schedule) {
                    return schedule.id
                }
                """;
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id", "groovySample");
        schedule.put("enabled", true);

        Object result = SystemGroovyScheduleJob.invokeGroovy(groovy, "execute", schedule);

        assertEquals("groovySample", result);
        assertTrue(schedule.containsKey("id"));
    }
}
