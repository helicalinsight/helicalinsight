package com.helicalinsight.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LlmUsageFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date fromDate;
    private Date toDate;
    private String username;
    private String endpoint;
    private String modelName;
    private String requestStatus;
    private int page = 1;
    private int pageSize = 20;
    private int days = 30;
    private int limit = 10;

    public int getOffset() {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        return (safePage - 1) * safePageSize;
    }
}
