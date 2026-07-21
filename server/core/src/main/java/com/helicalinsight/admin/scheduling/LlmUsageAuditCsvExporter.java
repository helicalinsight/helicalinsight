package com.helicalinsight.admin.scheduling;

import au.com.bytecode.opencsv.CSVWriter;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LlmUsageAuditCsvExporter {

    private static final Logger logger = LoggerFactory.getLogger(LlmUsageAuditCsvExporter.class);

    private static final String[] HEADERS = {
            "id", "userId", "endpoint", "userQuery", "inputTokens", "outputTokens", "totalTokens",
            "inputCost", "outputCost", "totalCost", "modelName", "requestStatus", "errorMessage",
            "chatId", "chatSeqId", "createdAt"
    };

    public File export(List<HILlmUsageAudit> records, File exportDirectory) throws IOException {
        if (!exportDirectory.exists() && !exportDirectory.mkdirs()) {
            throw new IOException("Unable to create export directory: " + exportDirectory.getAbsolutePath());
        }
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        File exportFile = new File(exportDirectory, "llm_usage_audit_" + timestamp + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(exportFile))) {
            writer.writeNext(HEADERS);
            for (HILlmUsageAudit record : records) {
                writer.writeNext(toRow(record));
            }
        }
        logger.info("Exported {} LLM audit records to {}", records.size(), exportFile.getAbsolutePath());
        return exportFile;
    }

    static String[] toRow(HILlmUsageAudit record) {
        List<String> row = new ArrayList<>();
        row.add(stringValue(record.getId()));
        row.add(stringValue(record.getUserId()));
        row.add(stringValue(record.getEndpoint()));
        row.add(stringValue(record.getUserQuery()));
        row.add(stringValue(record.getInputTokens()));
        row.add(stringValue(record.getOutputTokens()));
        row.add(stringValue(record.getTotalTokens()));
        row.add(stringValue(record.getInputCost()));
        row.add(stringValue(record.getOutputCost()));
        row.add(stringValue(record.getTotalCost()));
        row.add(stringValue(record.getModelName()));
        row.add(stringValue(record.getRequestStatus()));
        row.add(stringValue(record.getErrorMessage()));
        row.add(stringValue(record.getChatId()));
        row.add(stringValue(record.getChatSeqId()));
        row.add(stringValue(record.getCreatedAt()));
        return row.toArray(new String[0]);
    }

    private static String stringValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (value instanceof Date date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        return String.valueOf(value);
    }
}
