package com.helicalinsight.adhoc.genericsql;

import java.util.List;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;

/**
 * @author Somen
 *         Created by helical021 on 5/11/2020.
 */
public class SqlContextProxy {

    private SqlQueryContext context;

    public SqlContextProxy(SqlQueryContext context) {
        this.context = context;

    }


    public String quotes(String column) {
        getDerivedTableColumn();
        return this.context.quotes(column);
    }

    public List<String> getDerivedTableColumn() {
        return this.context.getDerivedTableColumns();
    }

    public String databaseFunction(JsonObject json) {
        return this.context.databaseFunction(json);
    }

    public JsonObject getFormData() {
        return this.context.getFormData();
    }

    @NotNull
    public Map<String, String> getColumnsMap() {
        return this.context.getColumnsMap();
    }

    @NotNull
    public List<String> getDerivedTableColumns() {
        return this.context.getDerivedTableColumns();
    }

    public boolean isApplyAggregation() {
        return this.context.isApplyAggregation();
    }

    public String getTableName(String fullName) {
        return this.context.getTableName(fullName);
    }

    public boolean isDistinctResults() {
        return this.context.isDistinctResults();
    }

    @NotNull
    public List<String> getDerivedTableNames() {
        return this.context.getDerivedTableNames();
    }

    @NotNull
    public List<String> getRequestedTables() {
        return this.context.getRequestedTables();
    }

    @NotNull
    public List<String> getGraphNodes() {
        return this.context.getGraphNodes();
    }

    public List<String> getTables() {
        return this.context.getTables();
    }

    public SqlFunction getStandardSqlFunction() {
        return this.context.getStandardSqlFunction();
    }

    public Dialect getHibernateDialect() {
        return this.context.getHibernateDialect();
    }

    public String getOpenQuote() {
        return this.context.getOpenQuote();
    }

    public void setOpenQuote(String openQuote) {
        this.context.setOpenQuote(openQuote);
    }

    public String getCloseQuote() {
        return this.context.getCloseQuote();
    }


    public String getDatabaseName() {
        return this.context.getDatabaseName();
    }

    @NotNull
    public Map<String, String> getNameAndFullNameMap() {
        return this.context.getNameAndFullNameMap();
    }

    public List<Relationship> getListOfRelationships() {
        return this.context.getListOfRelationships();
    }


    public boolean isApplyWhere() {
        return this.context.isApplyWhere();
    }

    public boolean isApplyGroupBy() {
        return this.context.isApplyGroupBy();
    }

    public boolean isApplyOrderBy() {
        return this.context.isApplyOrderBy();
    }

    public boolean isApplyHaving() {
        return this.context.isApplyHaving();
    }

    public PreConstructedFilters getPreConstructedFilters() {
        return this.context.getPreConstructedFilters();
    }

    public boolean isLimitRequested() {
        return this.context.isLimitRequested();
    }

    public String getDriverClassName() {
        return this.context.getDriverClassName();
    }

    public String getQueryOffset() {
        return this.context.getQueryOffset();
    }

    public String getQueryLimit() {
        return this.context.getQueryLimit();
    }

    public String getReferenceFile() {
        return this.context.getReferenceFile();
    }

    public Metadata getMetadata() {
        return this.context.getMetadata();
    }

    @NotNull
    public List<DerivedTable> getDerivedTables() {
        return this.context.getDerivedTables();
    }

}
