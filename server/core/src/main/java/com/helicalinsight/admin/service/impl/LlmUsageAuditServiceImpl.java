package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;
import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LlmUsageAuditServiceImpl implements LlmUsageAuditService {

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
}
