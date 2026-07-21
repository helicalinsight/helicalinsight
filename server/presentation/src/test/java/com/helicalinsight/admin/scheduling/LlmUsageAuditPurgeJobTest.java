package com.helicalinsight.admin.scheduling;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class LlmUsageAuditPurgeJobTest {

    @Test
    public void calculateCutoffDateSubtractsRetentionDays() {
        Date cutoff = LlmUsageAuditPurgeJob.calculateCutoffDate(30);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date expected = calendar.getTime();

        long difference = Math.abs(cutoff.getTime() - expected.getTime());
        assertTrue(difference < 2000L);
    }
}
