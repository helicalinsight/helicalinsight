package com.helicalinsight.datasource;

public final class PreviewLimit {
    public enum Kind { UNLIMITED, STATEMENT, SQL_WRAP }

    private final Kind kind;
    private final int rows;

    private PreviewLimit(Kind kind, int rows) {
        this.kind = kind;
        this.rows = rows;
    }

    public static PreviewLimit resolve(String driverClass, String requestedLimit, int defaultRows) {
        if (requestedLimit != null && "full".equalsIgnoreCase(requestedLimit)) {
            return new PreviewLimit(Kind.UNLIMITED, 0);
        }
        int rows = requestedLimit == null ? defaultRows : Integer.parseInt(requestedLimit);
        if (JdbcStatementLimit.useStatementLimit(driverClass)) {
            return new PreviewLimit(Kind.STATEMENT, rows);
        }
        return new PreviewLimit(Kind.SQL_WRAP, rows);
    }

    public Kind kind() {
        return kind;
    }

    public int rows() {
        return rows;
    }
}