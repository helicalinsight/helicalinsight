package com.helicalinsight.adhoc.genericsql;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.security.SqlUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JaxbUtils;

/**
 * Derives {@code usedColumns} for custom expressions that do not already supply them.
 * Parses the custom {@code column} value (constant, metadata column, formula, or subquery)
 * and attaches fully qualified metadata column names so downstream validation and FROM-clause
 * building can discover referenced tables.
 */
public final class CustomUsedColumnsDeriver {

    private static final Logger logger = LoggerFactory.getLogger(CustomUsedColumnsDeriver.class);

    /** Enables complexQuery token extraction in {@link SqlUtils}. */
    private static final String ALLOW_COMPLEX = "allow_complex_json_queries";

    /**
     * Quoted identifiers: {@code "col"}, {@code `col`}, {@code [col]}.
     * Needed because {@link SqlUtils} misses columns inside {@code sum(a/b)} and {@code (col)}.
     */
    private static final Pattern QUOTED_IDENTIFIER =
            Pattern.compile("\"([^\"]+)\"|`([^`]+)`|\\[([^\\]]+)]");

    /** Unquoted SQL identifiers, matched on identifier boundaries (not substrings). */
    private static final Pattern UNQUOTED_IDENTIFIER =
            Pattern.compile("(?i)(?<![A-Za-z0-9_])([A-Za-z_][A-Za-z0-9_]*)(?![A-Za-z0-9_])");

    private CustomUsedColumnsDeriver() {
    }

    /**
     * Mutates {@code formData} in place: for each custom item missing {@code usedColumns},
     * derives and writes resolved metadata FQDNs.
     *
     * @param formData     adhoc form data (columns / filters / having / functions)
     * @param metadataJson metadata JSON string (same payload passed to prepareQuery)
     */
    public static void enrich(@NotNull JsonObject formData, @Nullable String metadataJson) {
        if (StringUtils.isBlank(metadataJson)) {
            return;
        }
        Metadata metadata;
        try {
            metadata = JaxbUtils.jsonStringToObject(Metadata.class, metadataJson);
        } catch (Exception ex) {
            logger.warn("Could not parse metadata for usedColumns derivation", ex);
            return;
        }
        if (metadata == null) {
            return;
        }
        enrich(formData, metadata);
    }

    /**
     * Mutates {@code formData} in place using the provided metadata.
     */
    public static void enrich(@NotNull JsonObject formData, @NotNull Metadata metadata) {
        if (metadata.getDatabase() == null) {
            return;
        }
        try {
            IMetadataStore store = new MetadataStoreBuilder().setMetadata(metadata).createMetadataStore();
            List<String> fqColumns = store.getFullyQualifiedColumnsList();
            if (fqColumns == null || fqColumns.isEmpty()) {
                return;
            }
            Map<String, Set<String>> aliasToOriginals = store.getAliasToOriginalsSetMapping();

            enrichArray(GsonUtility.optJsonArray(formData, "columns"), fqColumns, aliasToOriginals);
            enrichArray(GsonUtility.optJsonArray(formData, "filters"), fqColumns, aliasToOriginals);
            enrichArray(GsonUtility.optJsonArray(formData, "having"), fqColumns, aliasToOriginals);

            JsonObject functions = GsonUtility.optJsonObject(formData, "functions");
            if (functions != null) {
                enrichArray(GsonUtility.optJsonArray(functions, "aggregate"), fqColumns, aliasToOriginals);
            }
        } catch (RuntimeException ex) {
            logger.warn("Could not derive usedColumns from metadata", ex);
        }
    }

    private static void enrichArray(@Nullable JsonArray array, @NotNull List<String> fqColumns,
                                    @Nullable Map<String, Set<String>> aliasToOriginals) {
        if (array == null || array.isEmpty()) {
            return;
        }
        for (JsonElement element : array) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            deriveForItem(element.getAsJsonObject(), fqColumns, aliasToOriginals);
        }
    }

    private static void deriveForItem(@NotNull JsonObject item, @NotNull List<String> fqColumns,
                                      @Nullable Map<String, Set<String>> aliasToOriginals) {
        if (!item.has("custom") || item.has("usedColumns")) {
            return;
        }
        JsonElement columnElement = item.get("column");
        String expression = null;
        if (columnElement != null && columnElement.isJsonObject()) {
            JsonElement inner = columnElement.getAsJsonObject().get("column");
            if (inner != null && inner.isJsonPrimitive()) {
                expression = inner.getAsString();
            }
        } else if (columnElement != null && columnElement.isJsonPrimitive()) {
            expression = columnElement.getAsString();
        }
        if (StringUtils.isBlank(expression)) {
            return;
        }

        Set<String> resolved = resolveToFqdns(expression, fqColumns, aliasToOriginals);
        if (resolved.isEmpty()) {
            return;
        }

        JsonArray usedColumns = new JsonArray();
        for (String fqdn : resolved) {
            if (StringUtils.isNotBlank(fqdn)) {
                usedColumns.add(fqdn);
            }
        }
        if (usedColumns.isEmpty()) {
            return;
        }
        item.add("usedColumns", usedColumns);
    }

    @NotNull
    private static Set<String> resolveToFqdns(@NotNull String expression, @NotNull List<String> fqColumns,
                                              @Nullable Map<String, Set<String>> aliasToOriginals) {
        Set<String> resolved = new LinkedHashSet<>();
        if (fqColumns.isEmpty()) {
            return resolved;
        }

        Map<String, Set<String>> sqlMap = parseExpression(expression);
        Set<String> parsedColumns = new LinkedHashSet<>(emptyIfNull(sqlMap == null ? null : sqlMap.get("column")));
        parsedColumns.addAll(extractIdentifiers(expression));
        Set<String> parsedTables = emptyIfNull(sqlMap == null ? null : sqlMap.get("tables"));
        Set<String> complexParts = emptyIfNull(sqlMap == null ? null : sqlMap.get("complexQuery"));

        String normalizedExpression = normalizeForMatch(expression);

        for (String fqdn : fqColumns) {
            if (StringUtils.isBlank(fqdn)) {
                continue;
            }
            if (expressionMentionsFqdn(normalizedExpression, fqdn)
                    || matchesParsedRefs(fqdn, parsedColumns, parsedTables, complexParts)) {
                resolved.add(fqdn);
            }
        }

        // Bare / unambiguous column names from the parser or identifier scan
        if (aliasToOriginals != null) {
            for (String parsedColumn : parsedColumns) {
                Set<String> originals = originalsForParsedColumn(parsedColumn, aliasToOriginals);
                if (originals == null || originals.isEmpty()) {
                    continue;
                }
                if (originals.size() == 1) {
                    String only = originals.iterator().next();
                    if (StringUtils.isNotBlank(only)) {
                        resolved.add(only);
                    }
                    continue;
                }
                for (String original : originals) {
                    if (StringUtils.isBlank(original)) {
                        continue;
                    }
                    String tablePart = tablePortion(original);
                    if (tablePart != null && (parsedTablesContain(parsedTables, tablePart)
                            || expressionMentionsTable(normalizedExpression, tablePart))) {
                        resolved.add(original);
                    }
                }
            }
        }

        return resolved;
    }

    /**
     * Pulls quoted and unquoted identifiers out of a custom expression so usedColumns can be
     * derived even when {@link SqlUtils} does not visit nested arithmetic or parenthesized columns.
     * Example: {@code sum("employee_id"/"travel_id") - ("destination_id")}.
     */
    @NotNull
    private static Set<String> extractIdentifiers(@NotNull String expression) {
        Set<String> identifiers = new LinkedHashSet<>();
        Matcher quoted = QUOTED_IDENTIFIER.matcher(expression);
        while (quoted.find()) {
            String identifier = firstNonEmpty(quoted.group(1), quoted.group(2), quoted.group(3));
            addIdentifierAndTail(identifiers, identifier);
        }
        String withoutQuoted = QUOTED_IDENTIFIER.matcher(expression).replaceAll(" ");
        Matcher unquoted = UNQUOTED_IDENTIFIER.matcher(withoutQuoted);
        while (unquoted.find()) {
            addIdentifierAndTail(identifiers, unquoted.group(1));
        }
        return identifiers;
    }

    private static void addIdentifierAndTail(@NotNull Set<String> identifiers, @Nullable String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return;
        }
        String trimmed = identifier.trim();
        identifiers.add(trimmed);
        String tail = columnPortion(trimmed);
        if (tail != null && !tail.equals(trimmed)) {
            identifiers.add(tail);
        }
    }

    @Nullable
    private static Set<String> originalsForParsedColumn(@NotNull String parsedColumn,
                                                        @NotNull Map<String, Set<String>> aliasToOriginals) {
        Set<String> originals = aliasToOriginals.get(parsedColumn);
        if (originals == null || originals.isEmpty()) {
            originals = aliasToOriginals.get(stripQuotes(parsedColumn));
        }
        if (originals == null || originals.isEmpty()) {
            String tail = columnPortion(stripQuotes(parsedColumn));
            if (StringUtils.isNotBlank(tail) && !tail.equals(parsedColumn)) {
                originals = aliasToOriginals.get(tail);
            }
        }
        return originals;
    }

    @Nullable
    private static String firstNonEmpty(@Nullable String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    private static Map<String, Set<String>> parseExpression(@NotNull String expression) {
        try {
            SqlUtils sqlUtils = new SqlUtils();
            return sqlUtils.getSqlColumns(expression, ALLOW_COMPLEX);
        } catch (Exception ex) {
            logger.debug("Could not parse custom expression for usedColumns: {}", expression, ex);
            return null;
        }
    }

    private static boolean matchesParsedRefs(@NotNull String fqdn, @NotNull Set<String> parsedColumns,
                                             @NotNull Set<String> parsedTables, @NotNull Set<String> complexParts) {
        String table = tablePortion(fqdn);
        String column = columnPortion(fqdn);
        if (column == null) {
            return false;
        }

        boolean columnHit = containsIgnoreCase(parsedColumns, column)
                || containsIgnoreCase(complexParts, column);
        if (!columnHit) {
            return false;
        }

        // Qualified refs only here; bare columns are resolved via aliasToOriginals below
        if (table == null) {
            return false;
        }
        return parsedTablesContain(parsedTables, table) || containsIgnoreCase(complexParts, simpleName(table));
    }

    private static boolean expressionMentionsFqdn(@NotNull String normalizedExpression, @NotNull String fqdn) {
        String normalizedFqdn = normalizeForMatch(fqdn);
        if (normalizedExpression.contains(normalizedFqdn)) {
            return true;
        }
        String tableColumn = tableColumnSuffix(fqdn);
        return tableColumn != null && normalizedExpression.contains(normalizeForMatch(tableColumn));
    }

    private static boolean expressionMentionsTable(@NotNull String normalizedExpression, @NotNull String table) {
        String simple = simpleName(table);
        return normalizedExpression.contains(normalizeForMatch(table))
                || (simple != null && normalizedExpression.contains(normalizeForMatch(simple) + "."));
    }

    private static boolean parsedTablesContain(@NotNull Set<String> parsedTables, @NotNull String table) {
        if (containsIgnoreCase(parsedTables, table) || containsIgnoreCase(parsedTables, simpleName(table))) {
            return true;
        }
        for (String parsed : parsedTables) {
            if (StringUtils.equalsIgnoreCase(simpleName(parsed), simpleName(table))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsIgnoreCase(@NotNull Set<String> values, @Nullable String candidate) {
        if (candidate == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && StringUtils.equalsIgnoreCase(stripQuotes(value), stripQuotes(candidate))) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static String tablePortion(@NotNull String fqdn) {
        int lastDot = fqdn.lastIndexOf('.');
        if (lastDot <= 0) {
            return null;
        }
        return fqdn.substring(0, lastDot);
    }

    @Nullable
    private static String columnPortion(@NotNull String fqdn) {
        int lastDot = fqdn.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fqdn.length() - 1) {
            return fqdn;
        }
        return fqdn.substring(lastDot + 1);
    }

    @Nullable
    private static String tableColumnSuffix(@NotNull String fqdn) {
        String[] parts = fqdn.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        return parts[parts.length - 2] + "." + parts[parts.length - 1];
    }

    @Nullable
    private static String simpleName(@Nullable String qualified) {
        if (qualified == null) {
            return null;
        }
        int lastDot = qualified.lastIndexOf('.');
        if (lastDot < 0 || lastDot == qualified.length() - 1) {
            return qualified;
        }
        return qualified.substring(lastDot + 1);
    }

    @NotNull
    private static String normalizeForMatch(@NotNull String value) {
        return value.trim().replace("\"", "").replace("`", "").replace("[", "").replace("]", "")
                .toLowerCase(Locale.ROOT);
    }

    @NotNull
    private static String stripQuotes(@Nullable String value) {
        if (value == null) {
            return "";
        }
        return StringUtils.strip(value.trim(), "\"");
    }

    @NotNull
    private static Set<String> emptyIfNull(@Nullable Set<String> set) {
        return set == null ? Set.of() : set;
    }
}
