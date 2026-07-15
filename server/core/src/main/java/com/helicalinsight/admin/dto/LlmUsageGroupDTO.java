package com.helicalinsight.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LlmUsageGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupKey;
    private long requestCount;
    private long totalTokens;
    private long inputTokens;
    private long outputTokens;
    private BigDecimal totalCost;

    public LlmUsageGroupDTO() {
        this.totalCost = BigDecimal.ZERO;
    }

    public LlmUsageGroupDTO(String groupKey, Long requestCount, Long totalTokens, BigDecimal totalCost) {
        this.groupKey = groupKey;
        this.requestCount = requestCount != null ? requestCount : 0L;
        this.totalTokens = totalTokens != null ? totalTokens : 0L;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
    }

    public LlmUsageGroupDTO(
            String groupKey,
            Long requestCount,
            Long totalTokens,
            Long inputTokens,
            Long outputTokens,
            BigDecimal totalCost
    ) {
        this.groupKey = groupKey;
        this.requestCount = requestCount != null ? requestCount : 0L;
        this.totalTokens = totalTokens != null ? totalTokens : 0L;
        this.inputTokens = inputTokens != null ? inputTokens : 0L;
        this.outputTokens = outputTokens != null ? outputTokens : 0L;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
    }
}
