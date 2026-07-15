package com.helicalinsight.admin.service;

import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.HILlmUsageAudit;

import java.util.List;
import java.util.Map;

public interface LlmUsageAuditService {

    void save(HILlmUsageAudit audit);

    LlmUsageSummaryDTO getSummary(LlmUsageFilterDTO filter);

    Map<String, Object> getDetails(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> getUsageByEndpoint(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> getUsageByUser(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> getUsageByModel(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> getDailyTrend(LlmUsageFilterDTO filter);
}
