package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LlmUsageAuditDaoImpl implements LlmUsageAuditDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(HILlmUsageAudit audit) {
        Session session = sessionFactory.getCurrentSession();
        session.save(audit);
    }
}
