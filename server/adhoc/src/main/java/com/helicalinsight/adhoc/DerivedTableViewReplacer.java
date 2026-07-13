package com.helicalinsight.adhoc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.QueryBuilderException;
import com.helicalinsight.adhoc.genericsql.SecurityExpressionEvaluator;
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.security.SqlUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.DialectHelper;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Replaces metadata view references in the FROM clause of a raw SQL query with view SQL
 * obtained from {@link DerivedTableFetchHandler}.
 */
public final class DerivedTableViewReplacer {

    private static final DerivedTableFetchHandler VIEW_FETCH_HANDLER = new DerivedTableFetchHandler();

    private static final String IDENTIFIER_QUOTE_REGEX_KEY = "sql.query.identifier.quote.regex";
    private static final String DEFAULT_IDENTIFIER_QUOTE_REGEX = "[\"`\\[\\]]";

    private static final Pattern IDENTIFIER_QUOTE_PATTERN = Pattern.compile(resolveIdentifierRegexPart(
            IDENTIFIER_QUOTE_REGEX_KEY, DEFAULT_IDENTIFIER_QUOTE_REGEX));

    private DerivedTableViewReplacer() {
    }

    private static String resolveIdentifierRegexPart(String propertyKey, String defaultValue) {
        Map<String, String> properties = new PropertiesFileReader().read("Admin", "defaults.properties");
        if (properties == null) {
            return defaultValue;
        }
        String configuredValue = properties.get(propertyKey);
        return StringUtils.isNotBlank(configuredValue) ? configuredValue : defaultValue;
    }

    /**
     * Replaces view names in the FROM clause when they are defined as type {@code view} in metadata service data.
     */
    public static String replaceViewsInQuery(String sql, Metadata metadata, JsonObject metadataServiceData,
                                             String location, String metadataFileName, String classifier,
                                             JsonObject formData) {
        List<MetadataView> metadataViews = prepareViewsFromMetadata(metadataServiceData);
        if (metadataViews.isEmpty() || StringUtils.isBlank(sql)) {
            return sql;
        }

        ViewReplacementSettings settings = new ViewReplacementSettings(metadata, formData);
        Map<String, MetadataView> viewLookup = buildViewLookup(metadataViews);
        Map<String, String> resolvedViewQueries = resolveReferencedViewQueries(
                sql, viewLookup, location, metadataFileName, classifier, formData, settings);

        if (resolvedViewQueries.isEmpty()) {
            return sql;
        }

        return rewriteFromClause(sql, resolvedViewQueries, settings);
    }

    /**
     * Builds the view list from metadata service JSON tables where {@code type} is {@code view}.
     */
    static List<MetadataView> prepareViewsFromMetadata(JsonObject metadataServiceData) {
        if (metadataServiceData == null) {
            return Collections.emptyList();
        }

        JsonObject tables = GsonUtility.optJsonObject(metadataServiceData, "tables");
        if (tables == null) {
            return Collections.emptyList();
        }

        List<MetadataView> metadataViews = new ArrayList<>();
        for (Map.Entry<String, JsonElement> tableEntry : tables.entrySet()) {
            if (!tableEntry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject tableJson = tableEntry.getValue().getAsJsonObject();
            String type = GsonUtility.optString(tableJson, "type");
            if (!"view".equalsIgnoreCase(type)) {
                continue;
            }

            String viewId = GsonUtility.optString(tableJson, "id");
            if (StringUtils.isBlank(viewId)) {
                continue;
            }

            String name = GsonUtility.optString(tableJson, "name");
            if (StringUtils.isBlank(name)) {
                name = tableEntry.getKey();
            }
            String alias = GsonUtility.optString(tableJson, "alias");
            metadataViews.add(new MetadataView(name, alias, viewId));
        }
        return metadataViews;
    }

    private static Map<String, MetadataView> buildViewLookup(List<MetadataView> metadataViews) {
        Map<String, MetadataView> lookup = new LinkedHashMap<>();
        for (MetadataView metadataView : metadataViews) {
            registerViewName(lookup, metadataView.getName(), metadataView);
            if (StringUtils.isNotBlank(metadataView.getAlias())) {
                registerViewName(lookup, metadataView.getAlias(), metadataView);
            }
        }
        return lookup;
    }

    private static void registerViewName(Map<String, MetadataView> lookup, String name, MetadataView metadataView) {
        lookup.put(normalizeIdentifier(name), metadataView);
    }

    private static Map<String, String> resolveReferencedViewQueries(String sql, Map<String, MetadataView> viewLookup,
                                                                    String location, String metadataFileName,
                                                                    String classifier, JsonObject formData,
                                                                    ViewReplacementSettings settings) {
        Map<String, String> resolvedQueries = new HashMap<>();
        for (String referencedViewName : findReferencedViewNames(sql, viewLookup)) {
            MetadataView metadataView = viewLookup.get(normalizeIdentifier(referencedViewName));
            if (metadataView == null) {
                metadataView = viewLookup.get(normalizeIdentifier(simpleTableName(referencedViewName)));
            }
            if (metadataView == null || resolvedQueries.containsKey(normalizeIdentifier(metadataView.getName()))) {
                continue;
            }
            String viewSql = fetchViewSql(metadataView, location, metadataFileName, classifier, formData, settings);
            resolvedQueries.put(normalizeIdentifier(metadataView.getName()), viewSql);
            if (StringUtils.isNotBlank(metadataView.getAlias())) {
                resolvedQueries.put(normalizeIdentifier(metadataView.getAlias()), viewSql);
            }
        }
        return resolvedQueries;
    }

    private static List<String> findReferencedViewNames(String sql, Map<String, MetadataView> viewLookup) {
        SqlUtils sqlUtils = new SqlUtils();
        String normalizedSql = sqlUtils.modifedSql(sql);
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(normalizedSql);
        } catch (Exception exception) {
            return Collections.emptyList();
        }

        if (!(statement instanceof Select)) {
            return Collections.emptyList();
        }

        List<String> referencedViews = new ArrayList<>();
        collectReferencedViewNames(((Select) statement).getSelectBody(), viewLookup, referencedViews);
        return referencedViews;
    }

    private static void collectReferencedViewNames(SelectBody selectBody, Map<String, MetadataView> viewLookup,
                                                   List<String> referencedViews) {
        if (selectBody instanceof PlainSelect) {
            collectReferencedViewNames((PlainSelect) selectBody, viewLookup, referencedViews);
        } else if (selectBody instanceof SetOperationList) {
            for (SelectBody body : ((SetOperationList) selectBody).getSelects()) {
                collectReferencedViewNames(body, viewLookup, referencedViews);
            }
        } else if (selectBody instanceof SubSelect) {
            collectReferencedViewNames(((SubSelect) selectBody).getSelectBody(), viewLookup, referencedViews);
        }
    }

    private static void collectReferencedViewNames(PlainSelect plainSelect, Map<String, MetadataView> viewLookup,
                                                   List<String> referencedViews) {
        collectReferencedViewName(plainSelect.getFromItem(), viewLookup, referencedViews);
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                collectReferencedViewName(join.getRightItem(), viewLookup, referencedViews);
            }
        }
    }

    private static void collectReferencedViewName(FromItem fromItem, Map<String, MetadataView> viewLookup,
                                                    List<String> referencedViews) {
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            String tableName = stripIdentifierQuotes(SqlUtils.modifiedString(table.getName()));
            if (viewLookup.containsKey(normalizeIdentifier(tableName))
                    || viewLookup.containsKey(normalizeIdentifier(simpleTableName(tableName)))) {
                referencedViews.add(tableName);
            }
            return;
        }
        if (fromItem instanceof SubSelect) {
            collectReferencedViewNames(((SubSelect) fromItem).getSelectBody(), viewLookup, referencedViews);
        }
    }

    private static String fetchViewSql(MetadataView metadataView, String location, String metadataFileName,
                                       String classifier, JsonObject formData, ViewReplacementSettings settings) {
        JsonObject fetchFormData = new JsonObject();
        fetchFormData.addProperty("location", location);
        fetchFormData.addProperty("metadataFileName", metadataFileName);
        fetchFormData.addProperty("classifier", classifier);
        fetchFormData.addProperty("viewId", metadataView.getViewId());
        fetchFormData.addProperty("hasStoredProcedure", GsonUtility.optBoolean(formData, "hasStoredProcedure"));

        String connectionId = GsonUtility.optString(formData, "connectionId");
        if (StringUtils.isNotBlank(connectionId)) {
            fetchFormData.addProperty("connectionId", connectionId);
        }

        String response = VIEW_FETCH_HANDLER.executeComponent(fetchFormData.toString());
        JsonObject viewResponse = JsonParser.parseString(response).getAsJsonObject();
        String query = GsonUtility.optString(viewResponse, "query");
        String queryType = GsonUtility.optString(viewResponse, "queryType");

        if (StringUtils.isBlank(query)) {
            throw new QueryBuilderException("No query returned for view " + metadataView.getName());
        }

        return resolveViewQuery(query, queryType, formData);
    }

    private static String resolveViewQuery(String query, String queryType, JsonObject formData) {
        if (StringUtils.isEmpty(queryType) || queryType.equalsIgnoreCase("conditionIf")) {
            query = SecurityExpressionEvaluator.replaceExpression(query);
        } else if (queryType.equalsIgnoreCase("groovy")) {
            query = SqlQueryUtilities.evalAllExpressionFromFormData(formData, query);
        }
        if (queryType != null && queryType.equalsIgnoreCase("hasStoredProcedure")) {
            query = SecurityExpressionEvaluator.replaceExpression(query);
        }
        return query;
    }

    private static String rewriteFromClause(String sql, Map<String, String> resolvedViewQueries,
                                            ViewReplacementSettings settings) {
        SqlUtils sqlUtils = new SqlUtils();
        String normalizedSql = sqlUtils.modifedSql(sql);
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(normalizedSql);
        } catch (Exception exception) {
            return sql;
        }

        if (!(statement instanceof Select)) {
            return sql;
        }

        replaceViewsInSelect((Select) statement, resolvedViewQueries, settings);
        return statement.toString();
    }

    private static void replaceViewsInSelect(Select select, Map<String, String> resolvedViewQueries,
                                             ViewReplacementSettings settings) {
        SelectBody body = select.getSelectBody();
        if (body instanceof PlainSelect) {
            replaceViewsInPlainSelect((PlainSelect) body, resolvedViewQueries, settings);
        } else if (body instanceof SetOperationList) {
            for (SelectBody selectBody : ((SetOperationList) body).getSelects()) {
                if (selectBody instanceof PlainSelect) {
                    replaceViewsInPlainSelect((PlainSelect) selectBody, resolvedViewQueries, settings);
                } else if (selectBody instanceof SubSelect) {
                    replaceViewsInSubSelect((SubSelect) selectBody, resolvedViewQueries, settings);
                }
            }
        } else if (body instanceof SubSelect) {
            replaceViewsInSubSelect((SubSelect) body, resolvedViewQueries, settings);
        }
    }

    private static void replaceViewsInSubSelect(SubSelect subSelect, Map<String, String> resolvedViewQueries,
                                                ViewReplacementSettings settings) {
        SelectBody selectBody = subSelect.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            replaceViewsInPlainSelect((PlainSelect) selectBody, resolvedViewQueries, settings);
        } else if (selectBody instanceof SetOperationList) {
            for (SelectBody body : ((SetOperationList) selectBody).getSelects()) {
                if (body instanceof PlainSelect) {
                    replaceViewsInPlainSelect((PlainSelect) body, resolvedViewQueries, settings);
                } else if (body instanceof SubSelect) {
                    replaceViewsInSubSelect((SubSelect) body, resolvedViewQueries, settings);
                }
            }
        }
    }

    private static void replaceViewsInPlainSelect(PlainSelect plainSelect, Map<String, String> resolvedViewQueries,
                                                  ViewReplacementSettings settings) {
        plainSelect.setFromItem(replaceFromItem(plainSelect.getFromItem(), resolvedViewQueries, settings));
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                join.setRightItem(replaceFromItem(join.getRightItem(), resolvedViewQueries, settings));
            }
        }
    }

    private static FromItem replaceFromItem(FromItem fromItem, Map<String, String> resolvedViewQueries,
                                            ViewReplacementSettings settings) {
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            String resolvedQuery = findResolvedQuery(table, resolvedViewQueries);
            if (resolvedQuery != null) {
                return buildSubSelectFromView(table, resolvedQuery, settings);
            }
            return fromItem;
        }
        if (fromItem instanceof SubSelect) {
            replaceViewsInSubSelect((SubSelect) fromItem, resolvedViewQueries, settings);
        }
        return fromItem;
    }

    private static String findResolvedQuery(Table table, Map<String, String> resolvedViewQueries) {
        String tableName = normalizeIdentifier(table.getName());
        String resolvedQuery = resolvedViewQueries.get(tableName);
        if (resolvedQuery != null) {
            return resolvedQuery;
        }
        return resolvedViewQueries.get(normalizeIdentifier(simpleTableName(tableName)));
    }

    private static SubSelect buildSubSelectFromView(Table table, String resolvedQuery,
                                                    ViewReplacementSettings settings) {
        try {
            Statement innerStatement = CCJSqlParserUtil.parse(resolvedQuery);
            if (!(innerStatement instanceof Select)) {
                throw new QueryBuilderException("The view query is not a SELECT statement.");
            }

            SubSelect subSelect = new SubSelect();
            subSelect.setSelectBody(((Select) innerStatement).getSelectBody());
            if (table.getAlias() != null && StringUtils.isNotBlank(table.getAlias().getName())) {
                subSelect.setAlias(table.getAlias());
            } else if (!settings.isUnWrapSelect()) {
                subSelect.setAlias(new net.sf.jsqlparser.expression.Alias(
                        settings.quotes(SqlUtils.modifiedString(table.getName())), false));
            }
            return subSelect;
        } catch (QueryBuilderException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new QueryBuilderException("Failed to parse view query.", exception);
        }
    }

    private static String normalizeIdentifier(String identifier) {
        return stripIdentifierQuotes(identifier).toLowerCase(Locale.ROOT);
    }

    /**
     * Strips optional SQL identifier quotes (double quotes, back-ticks, square brackets) using the regex
     * configured via {@code sql.query.identifier.quote.regex}, matching the quote handling used by
     * {@link com.helicalinsight.adhoc.security.SqlQueryMetadataValidator}.
     */
    private static String stripIdentifierQuotes(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return identifier;
        }
        String unquoted = SqlUtils.modifiedString(identifier);
        return IDENTIFIER_QUOTE_PATTERN.matcher(unquoted).replaceAll("");
    }

    private static String simpleTableName(String tableName) {
        if (tableName == null) {
            return "";
        }
        int index = tableName.lastIndexOf('.');
        if (index >= 0 && index < tableName.length() - 1) {
            return tableName.substring(index + 1);
        }
        return tableName;
    }

    private static final class ViewReplacementSettings {
        private final String openQuote;
        private final String closeQuote;
        private final boolean unWrapSelect;

        ViewReplacementSettings(Metadata metadata, JsonObject formData) {
            this.unWrapSelect = GsonUtility.optBoolean(formData, "unWrapSelect");
            ConnectionDetails connectionDetails = metadata.getConnectionDetails();
            Properties properties = new Properties();
            properties.put(Environment.DIALECT, connectionDetails.getDialect());
            Dialect dialect = DialectHelper.getDialect(properties);
            this.openQuote = dialect.openQuote() == 0 ? "" : String.valueOf(dialect.openQuote());
            this.closeQuote = dialect.closeQuote() == 0 ? "" : String.valueOf(dialect.closeQuote());
        }

        boolean isUnWrapSelect() {
            return unWrapSelect;
        }

        String quotes(String name) {
            if (name.contains("(") && name.contains(")")) {
                return name;
            }
            if (name.startsWith(openQuote) && name.endsWith(closeQuote)) {
                return name;
            }
            if (!name.contains(".")) {
                return openQuote + name + closeQuote;
            }

            StringBuilder safeName = new StringBuilder();
            String[] parts = name.split("\\.");
            for (String part : parts) {
                safeName.append(openQuote).append(part).append(closeQuote).append(".");
            }
            return safeName.substring(0, safeName.length() - 1);
        }
    }

    static final class MetadataView {
        private final String name;
        private final String alias;
        private final String viewId;

        MetadataView(String name, String alias, String viewId) {
            this.name = name;
            this.alias = alias;
            this.viewId = viewId;
        }

        String getName() {
            return name;
        }

        String getAlias() {
            return alias;
        }

        String getViewId() {
            return viewId;
        }
    }
}
