package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
    public void getScheduleEntriesReadsWrappedSystemSchedulesNode() {
        JSONObject root = new JSONObject();
        JSONObject systemSchedules = new JSONObject();
        JSONObject schedule = new JSONObject();
        schedule.put("@id", "llmUsageAuditPurge");
        systemSchedules.put("schedule", schedule);
        root.put("systemSchedules", systemSchedules);

        JSONArray entries = SystemScheduleLoader.getScheduleEntries(root);

        assertEquals(1, entries.size());
        assertEquals("llmUsageAuditPurge", entries.getJSONObject(0).getString("@id"));
    }
}
