package com.helicalinsight.datasource;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
public interface GlobalJdbcType {
    public static final String TYPE = "global.jdbc";
    public static final String NON_POOLED = "nonPooled";
    public static final String STATIC_DATASOURCE = "staticDataSource";
    public static final String DYNAMIC_DATASOURCE = "dynamicDataSource";
    public static final String NOSQL_DATASOURCE = "noSqlDataSource";
    public static final String NOSQL_DB_DATASOURCE = "db.noSql";
    public static final String MANAGED_GROOVY_DATASOURCE = "sql.jdbc.groovy.managed";
    public static final String GROOVY_DATASOURCE = "sql.jdbc.groovy";
    public static final String PLAIN_JDBC = "sql.jdbc";
}
