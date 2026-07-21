package com.helicalinsight.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class LlmUsageAuditListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String endpoint;
    private String userQuery;
    private Integer inputTokens;
    private Integer outputTokens;
    private Integer totalTokens;
    private BigDecimal inputCost;
    private BigDecimal outputCost;
    private BigDecimal totalCost;
    private String modelName;
    private String requestStatus;
    private String errorMessage;
    private String chatId;
    private String chatSeqId;
    private Date createdAt;

    public LlmUsageAuditListDTO() {
    }

    public LlmUsageAuditListDTO(
            Long id,
            String username,
            String endpoint,
            String userQuery,
            Integer inputTokens,
            Integer outputTokens,
            Integer totalTokens,
            BigDecimal inputCost,
            BigDecimal outputCost,
            BigDecimal totalCost,
            String modelName,
            String requestStatus,
            String errorMessage,
            String chatId,
            String chatSeqId,
            Date createdAt
    ) {
        this.id = id;
        this.username = username;
        this.endpoint = endpoint;
        this.userQuery = userQuery;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.totalTokens = totalTokens;
        this.inputCost = inputCost;
        this.outputCost = outputCost;
        this.totalCost = totalCost;
        this.modelName = modelName;
        this.requestStatus = requestStatus;
        this.errorMessage = errorMessage;
        this.chatId = chatId;
        this.chatSeqId = chatSeqId;
        this.createdAt = createdAt;
    }
}
