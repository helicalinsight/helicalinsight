package com.helicalinsight.admin.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;

@RunWith(MockitoJUnitRunner.class)
public class LlmUsageAuditServiceImplPurgeTest {

    @Mock
    private LlmUsageAuditDao llmUsageAuditDao;

    @InjectMocks
    private LlmUsageAuditServiceImpl service;

    @Test
    public void purgeOlderThanReturnsZeroWhenNoRecords() {
        when(llmUsageAuditDao.findOlderThan(any(Date.class), eq(0), anyInt())).thenReturn(Collections.emptyList());

        int purged = service.purgeOlderThan(new Date(), "Audit/LLM");

        assertEquals(0, purged);
        verify(llmUsageAuditDao, never()).deleteOlderThan(any(Date.class));
    }
}
