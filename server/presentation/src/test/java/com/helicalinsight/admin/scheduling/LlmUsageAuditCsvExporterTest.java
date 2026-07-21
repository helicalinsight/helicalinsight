package com.helicalinsight.admin.scheduling;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import com.helicalinsight.admin.model.HILlmUsageAudit;

import static org.junit.Assert.*;

public class LlmUsageAuditCsvExporterTest {

    @Test
    public void exportWritesHeaderAndEscapedValues() throws Exception {
        HILlmUsageAudit audit = new HILlmUsageAudit();
        audit.setId(1L);
        audit.setUserId(1);
        audit.setEndpoint("/interactive");
        audit.setUserQuery("line1\nline2");
        audit.setInputTokens(10);
        audit.setOutputTokens(5);
        audit.setTotalTokens(15);
        audit.setInputCost(new BigDecimal("0.10"));
        audit.setOutputCost(new BigDecimal("0.05"));
        audit.setTotalCost(new BigDecimal("0.15"));
        audit.setModelName("gpt-test");
        audit.setRequestStatus("success");
        audit.setErrorMessage("none");
        audit.setChatId("chat-1");
        audit.setChatSeqId("seq-1");
        audit.setCreatedAt(new Date(0L));

        File exportDir = new File(System.getProperty("java.io.tmpdir"), "llm-audit-export-test");
        exportDir.mkdirs();

        File exportFile = new LlmUsageAuditCsvExporter().export(Collections.singletonList(audit), exportDir);

        assertTrue(exportFile.exists());
        try {
            String content = new String(Files.readAllBytes(exportFile.toPath()), StandardCharsets.UTF_8);
            assertTrue(content.contains("\"id\""));
            assertTrue(content.contains("\"userId\""));
            assertTrue(content.contains("\"line1\nline2\""));
            assertTrue(content.contains("\"1\",\"1\",\"/interactive\""));
        } finally {
            exportFile.delete();
            exportDir.delete();
        }
    }

    @Test
    public void toRowMapsAllColumns() {
        HILlmUsageAudit audit = new HILlmUsageAudit();
        audit.setId(99L);
        audit.setUserId(1);
        String[] row = LlmUsageAuditCsvExporter.toRow(audit);
        assertEquals("99", row[0]);
        assertEquals("1", row[1]);
    }
}
