package com.helicalinsight.instant.ai.payload;

/**
 * SQL → InstantBI {@code report_model} request. Downstream Python path is
 * {@code /sql-to-report-model}.
 */
public class SqlToReportModelPayload implements IInstantBIPayload {

    private final String sql;
    private final String location;
    private final String metadataFileName;

    public SqlToReportModelPayload(String sql, String location, String metadataFileName) {
        this.sql = sql;
        this.location = location;
        this.metadataFileName = metadataFileName;
    }

    public String getSql() {
        return sql;
    }

    public String getLocation() {
        return location;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }
}
