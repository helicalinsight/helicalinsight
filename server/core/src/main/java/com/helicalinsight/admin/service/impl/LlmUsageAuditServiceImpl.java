package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LlmUsageAuditServiceImpl implements LlmUsageAuditService {

    private final LlmUsageAuditDao llmUsageAuditDao;

    @Override
    @Transactional
    public void save(HILlmUsageAudit audit) {
        llmUsageAuditDao.save(audit);
    }
}
