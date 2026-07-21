package com.helicalinsight.admin.management;

import com.helicalinsight.admin.dto.LlmUsageAuditListDTO;
import com.helicalinsight.admin.dto.LlmUsageFilterDTO;
import com.helicalinsight.admin.dto.LlmUsageGroupDTO;
import com.helicalinsight.admin.dto.LlmUsageSummaryDTO;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class LlmUsageAuditProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        String action = formData.optString("action");
        LlmUsageFilterDTO filter = applyAccessControl(buildFilter(formData));
        LlmUsageAuditService llmUsageAuditService = ApplicationContextAccessor.getBean(LlmUsageAuditService.class);

        return switch (action.toLowerCase()) {
            case "summary" -> toSummaryJson(llmUsageAuditService.getSummary(filter)).toString();
            case "details" -> toDetailsJson(llmUsageAuditService.getDetails(filter)).toString();
            case "byendpoint" -> wrapGroupJson(llmUsageAuditService.getUsageByEndpoint(filter)).toString();
            case "byuser" -> wrapGroupJson(llmUsageAuditService.getUsageByUser(filter)).toString();
            case "bymodel" -> wrapGroupJson(llmUsageAuditService.getUsageByModel(filter)).toString();
            case "dailytrend" -> wrapGroupJson(llmUsageAuditService.getDailyTrend(filter)).toString();
            default -> throw new EfwServiceException("The requested LLM usage action is not valid: " + action);
        };
    }

    private static LlmUsageFilterDTO buildFilter(JSONObject formData) {
        LlmUsageFilterDTO filter = new LlmUsageFilterDTO();
        if (formData.has("fromDate") && !formData.get("fromDate").equals(null)) {
            filter.setFromDate(new Date(formData.getLong("fromDate")));
        }
        if (formData.has("toDate") && !formData.get("toDate").equals(null)) {
            filter.setToDate(new Date(formData.getLong("toDate")));
        }
        if (StringUtils.hasText(formData.optString("username"))) {
            filter.setUsername(formData.getString("username"));
        }
        if (StringUtils.hasText(formData.optString("endpoint"))) {
            filter.setEndpoint(formData.getString("endpoint"));
        }
        if (StringUtils.hasText(formData.optString("modelName"))) {
            filter.setModelName(formData.getString("modelName"));
        }
        if (StringUtils.hasText(formData.optString("requestStatus"))) {
            filter.setRequestStatus(formData.getString("requestStatus"));
        }
        if (formData.has("page")) {
            filter.setPage(formData.getInt("page"));
        }
        if (formData.has("pageSize")) {
            filter.setPageSize(formData.getInt("pageSize"));
        }
        if (formData.has("days")) {
            filter.setDays(formData.getInt("days"));
        }
        if (formData.has("limit")) {
            filter.setLimit(formData.getInt("limit"));
        }
        if (StringUtils.hasText(formData.optString("sortField"))) {
            filter.setSortField(formData.getString("sortField"));
        }
        if (StringUtils.hasText(formData.optString("sortOrder"))) {
            filter.setSortOrder(formData.getString("sortOrder"));
        }
        return filter;
    }

    private static LlmUsageFilterDTO applyAccessControl(LlmUsageFilterDTO filter) {
        Principal activeUser = AuthenticationUtils.getUserDetails();
        User currentUser = activeUser.getLoggedInUser();
        Integer orgId = AuthenticationUtils.getLoggedInUserOrganizationId();
        if (AuthenticationUtils.isSuperAdmin(orgId)) {
            return filter;
        }
        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor
                .getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class);
        boolean isAdmin = AuthenticationUtils.getUserRoles().contains(namesConfigurer.getRoleAdmin());
        if (isAdmin) {
            filter.setOrganizationId(currentUser.getOrganization().getId());
            return filter;
        }
        filter.setRestrictedUserId(currentUser.getId());
        return filter;
    }

    private static JSONObject toSummaryJson(LlmUsageSummaryDTO summary) {
        JSONObject response = new JSONObject();
        response.put("requestCount", summary.getRequestCount());
        response.put("successCount", summary.getSuccessCount());
        response.put("failureCount", summary.getFailureCount());
        response.put("totalTokens", summary.getTotalTokens());
        response.put("inputTokens", summary.getInputTokens());
        response.put("outputTokens", summary.getOutputTokens());
        response.put("totalCost", toNumber(summary.getTotalCost()));
        response.put("inputCost", toNumber(summary.getInputCost()));
        response.put("outputCost", toNumber(summary.getOutputCost()));
        return response;
    }

    private static JSONObject toDetailsJson(Map<String, Object> details) {
        JSONObject response = new JSONObject();
        response.put("total", details.get("total"));
        response.put("page", details.get("page"));
        response.put("pageSize", details.get("pageSize"));
        @SuppressWarnings("unchecked")
        List<LlmUsageAuditListDTO> records = (List<LlmUsageAuditListDTO>) details.get("records");
        response.put("records", toRecordArray(records));
        return response;
    }

    private static JSONObject wrapGroupJson(List<LlmUsageGroupDTO> groups) {
        JSONObject response = new JSONObject();
        response.put("items", toGroupJson(groups));
        return response;
    }

    private static JSONArray toGroupJson(List<LlmUsageGroupDTO> groups) {
        JSONArray array = new JSONArray();
        for (LlmUsageGroupDTO group : groups) {
            JSONObject item = new JSONObject();
            item.put("groupKey", group.getGroupKey());
            item.put("requestCount", group.getRequestCount());
            item.put("totalTokens", group.getTotalTokens());
            item.put("inputTokens", group.getInputTokens());
            item.put("outputTokens", group.getOutputTokens());
            item.put("totalCost", toNumber(group.getTotalCost()));
            array.add(item);
        }
        return array;
    }

    private static JSONArray toRecordArray(List<LlmUsageAuditListDTO> records) {
        JSONArray array = new JSONArray();
        for (LlmUsageAuditListDTO record : records) {
            JSONObject item = new JSONObject();
            item.put("id", record.getId());
            item.put("username", record.getUsername());
            item.put("endpoint", record.getEndpoint());
            item.put("userQuery", truncate(record.getUserQuery(), 500));
            item.put("inputTokens", record.getInputTokens());
            item.put("outputTokens", record.getOutputTokens());
            item.put("totalTokens", record.getTotalTokens());
            item.put("inputCost", toNumber(record.getInputCost()));
            item.put("outputCost", toNumber(record.getOutputCost()));
            item.put("totalCost", toNumber(record.getTotalCost()));
            item.put("modelName", record.getModelName());
            item.put("requestStatus", record.getRequestStatus());
            item.put("errorMessage", truncate(record.getErrorMessage(), 500));
            item.put("chatId", record.getChatId());
            item.put("chatSeqId", record.getChatSeqId());
            item.put("createdAt", record.getCreatedAt() != null ? record.getCreatedAt().getTime() : null);
            array.add(item);
        }
        return array;
    }

    private static double toNumber(BigDecimal value) {
        return value != null ? value.doubleValue() : 0D;
    }

    private static String truncate(String value, int maxLength) {
        if (!StringUtils.hasText(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}
