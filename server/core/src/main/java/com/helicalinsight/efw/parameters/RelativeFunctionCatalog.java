package com.helicalinsight.efw.parameters;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;

/**
 * @since 7.0.1 RC1
 * Loads System/Admin/parameterFunctions.json,  matches relative-date tokens and builds the Catalog
 * 
 * This class has been added to move the relative function resolving mechanism to backend. Currently it is resolving at client side,
 * which caused issues in server side rendering eg: Canned Report
 * 
 */
public final class RelativeFunctionCatalog {

    private static final Logger logger = LoggerFactory.getLogger(RelativeFunctionCatalog.class);
    private static final Set<String> UNITS = Set.of("DAYS", "WEEKS", "MONTHS", "YEARS", "HOURS", "MINUTES", "SECONDS");
    private static final Set<String> BOUNDARIES = Set.of("DAY", "WEEK", "MONTH", "QUARTER", "YEAR", "HOUR", "MINUTE", "SECOND", "NOW");

    private final Map<String, RelativeFunctionDefinition> functions;
    private final Pattern tokenPattern;

    private RelativeFunctionCatalog(Map<String, RelativeFunctionDefinition> functions, Pattern tokenPattern) {
        this.functions = functions;
        this.tokenPattern = tokenPattern;
    }

    public static RelativeFunctionCatalog empty() {
        return new RelativeFunctionCatalog(Map.of(), null);
    }

    public static RelativeFunctionCatalog load() {
        try {
            File file = catalogFile();
            if (!file.isFile()) {
                logger.error("Parameter function catalog was not found at {}", file);
                return empty();
            }
            return fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()));
        } catch (IOException | RuntimeException ex) {
            logger.error("Could not load the parameter function catalog", ex);
            return empty();
        }
    }

    /**
     * The catalog file as JSON for downstream callers. A missing or unreadable
     * file is an empty {@code functions} array.
     */
    public static JsonObject rawJson() {
        JsonObject empty = new JsonObject();
        empty.add("functions", new JsonArray());
        try {
            File file = catalogFile();
            if (!file.isFile()) {
                logger.error("Parameter function catalog was not found at {}", file);
                return empty;
            }
            JsonObject parsed = GsonUtility.parseString(FileUtils.readFileToString(file, Charset.defaultCharset()), JsonObject.class);
            if (parsed == null || !parsed.has("functions") || !parsed.get("functions").isJsonArray()) {
                return empty;
            }
            return parsed;
        } catch (IOException | RuntimeException ex) {
            logger.error("Could not load the parameter function catalog", ex);
            return empty;
        }
    }

    private static File catalogFile() {
        return new File(ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator + "parameterFunctions.json");
    }

    public record Match(RelativeFunctionDefinition function, boolean anchored, String modifier) {
    }

    public Match match(String raw) {
        if (StringUtils.isBlank(raw) || tokenPattern == null) {
            return null;
        }
        Matcher matcher = tokenPattern.matcher(raw.trim().replace(" ", "+"));
        if (!matcher.matches()) {
            return null;
        }
        RelativeFunctionDefinition function = functions.get(matcher.group(2));
        return function == null ? null : new Match(function, matcher.group(1) != null, matcher.group(3));
    }

    public static RelativeFunctionCatalog fromJson(String json) {
        JsonArray array = GsonUtility.optJsonArray(GsonUtility.parseString(json, JsonObject.class), "functions");
        if (array == null) {
            throw new EfwServiceException("Parameter function catalog is missing the functions array");
        }
        Map<String, RelativeFunctionDefinition> byName = new LinkedHashMap<>();
        for (JsonElement element : array) {
            RelativeFunctionDefinition definition = readDefinition(element.getAsJsonObject());
            if (byName.put(definition.getName(), definition) != null) {
                throw new EfwServiceException("Duplicate parameter function: " + definition.getName());
            }
        }
        return new RelativeFunctionCatalog(byName, compile(byName.keySet()));
    }

    public boolean isEmpty() {
        return functions.isEmpty();
    }

    private static RelativeFunctionDefinition readDefinition(JsonObject item) {
        String name = GsonUtility.optString(item, "name");
        String boundary = GsonUtility.optString(item, "boundary");
        if (StringUtils.isBlank(name) || StringUtils.isBlank(boundary)) {
            throw new EfwServiceException("Parameter function catalog entry is missing name or boundary");
        }
        if (!BOUNDARIES.contains(boundary)) {
            throw new EfwServiceException("Unknown boundary " + boundary + " on parameter function " + name);
        }
        String unit = StringUtils.trimToNull(GsonUtility.optString(item, "unit"));
        if (!"NOW".equals(boundary) && (unit == null || !UNITS.contains(unit))) {
            throw new EfwServiceException("Unknown unit " + unit + " on parameter function " + name);
        }
        int multiplier = GsonUtility.optIntValue(item, "multiplier", 1);
        if (multiplier < 1) {
            throw new EfwServiceException("Parameter function " + name + " has an invalid multiplier");
        }
        return new RelativeFunctionDefinition(name, unit, boundary, GsonUtility.optInt(item, "offset"),
                StringUtils.defaultIfBlank(GsonUtility.optString(item, "direction"), "NONE"),
                GsonUtility.optBooleanValue(item, "acceptsModifier", true),
                GsonUtility.optBoolean(item, "inheritsUnit"), multiplier);
    }

    private static Pattern compile(Set<String> names) {
        if (names.isEmpty()) {
            return null;
        }
        String alternation = names.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
        return Pattern.compile("^(anchor_)?(" + alternation + ")([-+]\\d+)?$");
    }
}
