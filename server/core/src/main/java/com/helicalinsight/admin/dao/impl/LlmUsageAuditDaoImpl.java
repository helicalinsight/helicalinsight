package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.LlmUsageAuditDao;
import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.HILlmUsageAudit;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class LlmUsageAuditDaoImpl implements LlmUsageAuditDao {

    private final SessionFactory sessionFactory;

    @Override
    public void save(HILlmUsageAudit audit) {
        Session session = sessionFactory.getCurrentSession();
        session.save(audit);
    }

    @Override
    public LlmUsageSummaryDTO fetchSummary(LlmUsageFilterDTO filter) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(a), COALESCE(SUM(a.totalTokens), 0), COALESCE(SUM(a.inputTokens), 0), "
                        + "COALESCE(SUM(a.outputTokens), 0), COALESCE(SUM(a.totalCost), 0), "
                        + "COALESCE(SUM(a.inputCost), 0), COALESCE(SUM(a.outputCost), 0) "
                        + "FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        SelectionQuery<Object[]> query = session.createSelectionQuery(hql.toString(), Object[].class);
        bindCommonFilters(query, filter);
        Object[] row = query.uniqueResult();
        LlmUsageSummaryDTO summary = new LlmUsageSummaryDTO();
        if (row != null) {
            summary.setRequestCount(toLong(row[0]));
            summary.setTotalTokens(toLong(row[1]));
            summary.setInputTokens(toLong(row[2]));
            summary.setOutputTokens(toLong(row[3]));
            summary.setTotalCost(toBigDecimal(row[4]));
            summary.setInputCost(toBigDecimal(row[5]));
            summary.setOutputCost(toBigDecimal(row[6]));
        }
        summary.setSuccessCount(countByStatus(filter, "success"));
        summary.setFailureCount(Math.max(0, summary.getRequestCount() - summary.getSuccessCount()));
        return summary;
    }

    @Override
    public long countByStatus(LlmUsageFilterDTO filter, String requestStatus) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("SELECT COUNT(a) FROM HILlmUsageAudit a WHERE a.requestStatus = :requestStatus");
        appendCommonFilters(hql, filter, "a");
        SelectionQuery<Long> query = session.createSelectionQuery(hql.toString(), Long.class);
        query.setParameter("requestStatus", requestStatus);
        bindCommonFilters(query, filter);
        Long count = query.uniqueResult();
        return count != null ? count : 0L;
    }

    @Override
    public long countDetails(LlmUsageFilterDTO filter) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("SELECT COUNT(a) FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        SelectionQuery<Long> query = session.createSelectionQuery(hql.toString(), Long.class);
        bindCommonFilters(query, filter);
        Long count = query.uniqueResult();
        return count != null ? count : 0L;
    }

    @Override
    public List<LlmUsageAuditListDTO> fetchDetails(LlmUsageFilterDTO filter) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT new com.helicalinsight.admin.dto.LlmUsageAuditListDTO("
                        + "a.id, a.username, a.endpoint, a.userQuery, a.inputTokens, a.outputTokens, "
                        + "a.totalTokens, a.inputCost, a.outputCost, a.totalCost, a.modelName, "
                        + "a.requestStatus, a.errorMessage, a.chatId, a.chatSeqId, a.createdAt) "
                        + "FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        hql.append(" ORDER BY a.createdAt DESC");
        SelectionQuery<LlmUsageAuditListDTO> query = session.createSelectionQuery(hql.toString(), LlmUsageAuditListDTO.class);
        bindCommonFilters(query, filter);
        query.setFirstResult(filter.getOffset());
        query.setMaxResults(Math.max(filter.getPageSize(), 1));
        return query.list();
    }

    @Override
    public List<LlmUsageGroupDTO> fetchGroupedByEndpoint(LlmUsageFilterDTO filter) {
        return fetchGrouped(
                filter,
                "a.endpoint",
                "COALESCE(SUM(a.totalTokens), 0) DESC"
        );
    }

    @Override
    public List<LlmUsageGroupDTO> fetchGroupedByUser(LlmUsageFilterDTO filter) {
        return fetchGrouped(
                filter,
                "a.username",
                "COALESCE(SUM(a.totalTokens), 0) DESC"
        );
    }

    @Override
    public List<LlmUsageGroupDTO> fetchGroupedByModel(LlmUsageFilterDTO filter) {
        return fetchGrouped(
                filter,
                "COALESCE(a.modelName, 'unknown')",
                "COALESCE(SUM(a.totalTokens), 0) DESC"
        );
    }

    @Override
    public List<LlmUsageGroupDTO> fetchDailyTrend(LlmUsageFilterDTO filter) {
        LlmUsageFilterDTO trendFilter = copyFilter(filter);
        if (trendFilter.getFromDate() == null) {
            trendFilter.setFromDate(defaultFromDate(trendFilter.getDays()));
        }
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT FUNCTION('DATE', a.createdAt), COUNT(a), COALESCE(SUM(a.totalTokens), 0), "
                        + "COALESCE(SUM(a.inputTokens), 0), COALESCE(SUM(a.outputTokens), 0), COALESCE(SUM(a.totalCost), 0) "
                        + "FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, trendFilter, "a");
        hql.append(" GROUP BY FUNCTION('DATE', a.createdAt) ORDER BY FUNCTION('DATE', a.createdAt) DESC");
        SelectionQuery<Object[]> query = session.createSelectionQuery(hql.toString(), Object[].class);
        bindCommonFilters(query, trendFilter);
        List<Object[]> rows = query.list();
        List<LlmUsageGroupDTO> groups = new ArrayList<>();
        for (Object[] row : rows) {
            String dayKey = row[0] != null ? String.valueOf(row[0]) : "unknown";
            groups.add(new LlmUsageGroupDTO(
                    dayKey,
                    toLong(row[1]),
                    toLong(row[2]),
                    toLong(row[3]),
                    toLong(row[4]),
                    toBigDecimal(row[5])
            ));
        }
        return groups;
    }

    private List<LlmUsageGroupDTO> fetchGrouped(LlmUsageFilterDTO filter, String groupExpression, String orderBy) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT ").append(groupExpression).append(", COUNT(a), COALESCE(SUM(a.totalTokens), 0), "
                        + "COALESCE(SUM(a.inputTokens), 0), COALESCE(SUM(a.outputTokens), 0), COALESCE(SUM(a.totalCost), 0) "
                        + "FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        hql.append(" GROUP BY ").append(groupExpression).append(" ORDER BY ").append(orderBy);
        SelectionQuery<Object[]> query = session.createSelectionQuery(hql.toString(), Object[].class);
        bindCommonFilters(query, filter);
        if (filter.getLimit() > 0) {
            query.setMaxResults(filter.getLimit());
        }
        List<Object[]> rows = query.list();
        List<LlmUsageGroupDTO> groups = new ArrayList<>();
        for (Object[] row : rows) {
            String key = row[0] != null ? String.valueOf(row[0]) : "unknown";
            groups.add(new LlmUsageGroupDTO(
                    key,
                    toLong(row[1]),
                    toLong(row[2]),
                    toLong(row[3]),
                    toLong(row[4]),
                    toBigDecimal(row[5])
            ));
        }
        return groups;
    }

    private void appendCommonFilters(StringBuilder hql, LlmUsageFilterDTO filter, String alias) {
        if (filter.getFromDate() != null) {
            hql.append(" AND ").append(alias).append(".createdAt >= :fromDate");
        }
        if (filter.getToDate() != null) {
            hql.append(" AND ").append(alias).append(".createdAt <= :toDate");
        }
        if (StringUtils.hasText(filter.getUsername())) {
            hql.append(" AND ").append(alias).append(".username = :username");
        }
        if (StringUtils.hasText(filter.getEndpoint())) {
            hql.append(" AND ").append(alias).append(".endpoint = :endpoint");
        }
        if (StringUtils.hasText(filter.getModelName())) {
            hql.append(" AND ").append(alias).append(".modelName = :modelName");
        }
        if (StringUtils.hasText(filter.getRequestStatus())) {
            hql.append(" AND ").append(alias).append(".requestStatus = :requestStatus");
        }
    }

    private void bindCommonFilters(SelectionQuery<?> query, LlmUsageFilterDTO filter) {
        if (filter.getFromDate() != null) {
            query.setParameter("fromDate", filter.getFromDate());
        }
        if (filter.getToDate() != null) {
            query.setParameter("toDate", filter.getToDate());
        }
        if (StringUtils.hasText(filter.getUsername())) {
            query.setParameter("username", filter.getUsername());
        }
        if (StringUtils.hasText(filter.getEndpoint())) {
            query.setParameter("endpoint", filter.getEndpoint());
        }
        if (StringUtils.hasText(filter.getModelName())) {
            query.setParameter("modelName", filter.getModelName());
        }
        if (StringUtils.hasText(filter.getRequestStatus())) {
            query.setParameter("requestStatus", filter.getRequestStatus());
        }
    }

    private static LlmUsageFilterDTO copyFilter(LlmUsageFilterDTO filter) {
        LlmUsageFilterDTO copy = new LlmUsageFilterDTO();
        copy.setFromDate(filter.getFromDate());
        copy.setToDate(filter.getToDate());
        copy.setUsername(filter.getUsername());
        copy.setEndpoint(filter.getEndpoint());
        copy.setModelName(filter.getModelName());
        copy.setRequestStatus(filter.getRequestStatus());
        copy.setPage(filter.getPage());
        copy.setPageSize(filter.getPageSize());
        copy.setDays(filter.getDays());
        copy.setLimit(filter.getLimit());
        return copy;
    }

    private static Date defaultFromDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -Math.max(days, 1));
        return calendar.getTime();
    }

    private static long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }
}
