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
import org.hibernate.query.MutationQuery;
import org.hibernate.query.SelectionQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class LlmUsageAuditDaoImpl implements LlmUsageAuditDao {

    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "createdAt", "a.createdAt",
            "username", "u.username",
            "endpoint", "a.endpoint",
            "modelName", "a.modelName",
            "totalTokens", "a.totalTokens",
            "totalCost", "a.totalCost",
            "requestStatus", "a.requestStatus"
    );

    private final SessionFactory sessionFactory;

    @Override
    public void save(HILlmUsageAudit audit) {
        Session session = sessionFactory.getCurrentSession();
        session.save(audit);
    }

    @Override
    public LlmUsageSummaryDTO fetchSummary(LlmUsageFilterDTO filter) {
        LlmUsageFilterDTO summaryFilter = resolveDateFilter(filter);
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(a), COALESCE(SUM(a.totalTokens), 0), COALESCE(SUM(a.inputTokens), 0), "
                        + "COALESCE(SUM(a.outputTokens), 0), COALESCE(SUM(a.totalCost), 0), "
                        + "COALESCE(SUM(a.inputCost), 0), COALESCE(SUM(a.outputCost), 0) "
                        + "FROM HILlmUsageAudit a WHERE 1=1");
        appendCommonFilters(hql, summaryFilter, "a");
        SelectionQuery<Object[]> query = session.createSelectionQuery(hql.toString(), Object[].class);
        bindCommonFilters(query, summaryFilter);
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
        summary.setSuccessCount(countByStatus(summaryFilter, "SUCCESS"));
        summary.setFailureCount(countByStatus(summaryFilter, "FAILURE"));
        return summary;
    }

    @Override
    public long countByStatus(LlmUsageFilterDTO filter, String requestStatus) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(a) FROM HILlmUsageAudit a WHERE UPPER(a.requestStatus) = UPPER(:requestStatus)");
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
                        + "a.id, u.username, a.endpoint, a.userQuery, a.inputTokens, a.outputTokens, "
                        + "a.totalTokens, a.inputCost, a.outputCost, a.totalCost, a.modelName, "
                        + "a.requestStatus, a.errorMessage, a.chatId, a.chatSeqId, a.createdAt) "
                        + "FROM HILlmUsageAudit a LEFT JOIN User u ON u.id = a.userId WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        hql.append(" ORDER BY ")
                .append(resolveSortField(filter.getSortField()))
                .append(" ")
                .append(resolveSortOrder(filter.getSortOrder()));
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
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder(
                "SELECT COALESCE(u.username, 'unknown'), COUNT(a), COALESCE(SUM(a.totalTokens), 0), "
                        + "COALESCE(SUM(a.inputTokens), 0), COALESCE(SUM(a.outputTokens), 0), COALESCE(SUM(a.totalCost), 0) "
                        + "FROM HILlmUsageAudit a LEFT JOIN User u ON u.id = a.userId WHERE 1=1");
        appendCommonFilters(hql, filter, "a");
        hql.append(" GROUP BY u.username ORDER BY COALESCE(SUM(a.totalTokens), 0) DESC");
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

    @Override
    public List<HILlmUsageAudit> findOlderThan(Date cutoffDate, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        SelectionQuery<HILlmUsageAudit> query = session.createSelectionQuery(
                "FROM HILlmUsageAudit a WHERE a.createdAt < :cutoffDate ORDER BY a.createdAt ASC",
                HILlmUsageAudit.class);
        query.setParameter("cutoffDate", cutoffDate);
        query.setFirstResult(Math.max(offset, 0));
        query.setMaxResults(Math.max(limit, 1));
        return query.list();
    }

    @Override
    public int deleteOlderThan(Date cutoffDate) {
        Session session = sessionFactory.getCurrentSession();
        MutationQuery deleteQuery = session.createMutationQuery(
                "DELETE FROM HILlmUsageAudit a WHERE a.createdAt < :cutoffDate");
        deleteQuery.setParameter("cutoffDate", cutoffDate);
        return deleteQuery.executeUpdate();
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
            hql.append(" AND EXISTS (SELECT 1 FROM User u WHERE u.id = ")
                    .append(alias)
                    .append(".userId AND u.username = :username)");
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
        if (filter.getOrganizationId() != null) {
            hql.append(" AND ").append(alias).append(".organizationId = :organizationId");
        }
        if (filter.getRestrictedUserId() != null) {
            hql.append(" AND ").append(alias).append(".userId = :restrictedUserId");
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
        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getRestrictedUserId() != null) {
            query.setParameter("restrictedUserId", filter.getRestrictedUserId());
        }
    }

    private static LlmUsageFilterDTO resolveDateFilter(LlmUsageFilterDTO filter) {
        if (filter.getFromDate() != null) {
            return filter;
        }
        LlmUsageFilterDTO copy = copyFilter(filter);
        copy.setFromDate(defaultFromDate(copy.getDays()));
        return copy;
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
        copy.setSortField(filter.getSortField());
        copy.setSortOrder(filter.getSortOrder());
        copy.setOrganizationId(filter.getOrganizationId());
        copy.setRestrictedUserId(filter.getRestrictedUserId());
        return copy;
    }

    private static String resolveSortField(String sortField) {
        if (!StringUtils.hasText(sortField)) {
            return SORT_FIELD_MAP.get("createdAt");
        }
        return SORT_FIELD_MAP.getOrDefault(sortField, SORT_FIELD_MAP.get("createdAt"));
    }

    private static String resolveSortOrder(String sortOrder) {
        return "asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
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
