package com.helicalinsight.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LlmUsageSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long requestCount;
    private long successCount;
    private long failureCount;
    private long totalTokens;
    private long inputTokens;
    private long outputTokens;
    private BigDecimal totalCost;
    private BigDecimal inputCost;
    private BigDecimal outputCost;

    public LlmUsageSummaryDTO() {
        this.totalCost = BigDecimal.ZERO;
        this.inputCost = BigDecimal.ZERO;
        this.outputCost = BigDecimal.ZERO;
    }

    public LlmUsageSummaryDTO(
            Long requestCount,
            Long totalTokens,
            Long inputTokens,
            Long outputTokens,
            BigDecimal totalCost,
            BigDecimal inputCost,
            BigDecimal outputCost
    ) {
        this.requestCount = requestCount != null ? requestCount : 0L;
        this.totalTokens = totalTokens != null ? totalTokens : 0L;
        this.inputTokens = inputTokens != null ? inputTokens : 0L;
        this.outputTokens = outputTokens != null ? outputTokens : 0L;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.inputCost = inputCost != null ? inputCost : BigDecimal.ZERO;
        this.outputCost = outputCost != null ? outputCost : BigDecimal.ZERO;
    }
}
