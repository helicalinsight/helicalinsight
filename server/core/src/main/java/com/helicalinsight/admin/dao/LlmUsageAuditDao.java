package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.HILlmUsageAudit;

import java.util.List;

public interface LlmUsageAuditDao {

    void save(HILlmUsageAudit audit);

    LlmUsageSummaryDTO fetchSummary(LlmUsageFilterDTO filter);

    long countByStatus(LlmUsageFilterDTO filter, String requestStatus);

    long countDetails(LlmUsageFilterDTO filter);

    List<LlmUsageAuditListDTO> fetchDetails(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> fetchGroupedByEndpoint(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> fetchGroupedByUser(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> fetchGroupedByModel(LlmUsageFilterDTO filter);

    List<LlmUsageGroupDTO> fetchDailyTrend(LlmUsageFilterDTO filter);
}
