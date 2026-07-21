package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;
import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import com.helicalinsight.admin.scheduling.LlmUsageAuditCsvExporter;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import com.helicalinsight.efw.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LlmUsageAuditServiceImpl implements LlmUsageAuditService {

    private static final int BATCH_SIZE = 5000;

    private final LlmUsageAuditDao llmUsageAuditDao;

    @Override
    @Transactional
    public void save(HILlmUsageAudit audit) {
        llmUsageAuditDao.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public LlmUsageSummaryDTO getSummary(LlmUsageFilterDTO filter) {
        return llmUsageAuditDao.fetchSummary(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDetails(LlmUsageFilterDTO filter) {
        long total = llmUsageAuditDao.countDetails(filter);
        List<LlmUsageAuditListDTO> records = llmUsageAuditDao.fetchDetails(filter);
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("page", filter.getPage());
        response.put("pageSize", filter.getPageSize());
        response.put("records", records);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LlmUsageGroupDTO> getUsageByEndpoint(LlmUsageFilterDTO filter) {
        return llmUsageAuditDao.fetchGroupedByEndpoint(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LlmUsageGroupDTO> getUsageByUser(LlmUsageFilterDTO filter) {
        return llmUsageAuditDao.fetchGroupedByUser(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LlmUsageGroupDTO> getUsageByModel(LlmUsageFilterDTO filter) {
        return llmUsageAuditDao.fetchGroupedByModel(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LlmUsageGroupDTO> getDailyTrend(LlmUsageFilterDTO filter) {
        return llmUsageAuditDao.fetchDailyTrend(filter);
    }

    @Override
    @Transactional
    public int purgeOlderThan(Date cutoffDate, String exportPath) {
        List<HILlmUsageAudit> records = fetchAllOlderThan(cutoffDate);
        if (records.isEmpty()) {
            return 0;
        }
        File exportDirectory = resolveExportDirectory(exportPath);
        try {
            File exportFile = new LlmUsageAuditCsvExporter().export(records, exportDirectory);
            if (!exportFile.exists() || exportFile.length() == 0) {
                throw new IllegalStateException("CSV export failed for LLM audit purge");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to export LLM audit records before purge", ex);
        }
        return llmUsageAuditDao.deleteOlderThan(cutoffDate);
    }

    private List<HILlmUsageAudit> fetchAllOlderThan(Date cutoffDate) {
        List<HILlmUsageAudit> records = new ArrayList<>();
        int offset = 0;
        List<HILlmUsageAudit> batch;
        do {
            batch = llmUsageAuditDao.findOlderThan(cutoffDate, offset, BATCH_SIZE);
            records.addAll(batch);
            offset += batch.size();
        } while (batch.size() == BATCH_SIZE);
        return records;
    }

    private File resolveExportDirectory(String exportPath) {
        String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        return new File(systemDirectory, exportPath);
    }
}
