package com.helicalinsight.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "hi_llm_usage_audit")
public class HILlmUsageAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "endpoint")
    private String endpoint;

    @Lob
    @Column(name = "user_query")
    private String userQuery;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "input_cost")
    private BigDecimal inputCost;

    @Column(name = "output_cost")
    private BigDecimal outputCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "request_status")
    private String requestStatus;

    @Lob
    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "chat_seq_id")
    private String chatSeqId;

    @Column(name = "created_at")
    private Date createdAt;
}
