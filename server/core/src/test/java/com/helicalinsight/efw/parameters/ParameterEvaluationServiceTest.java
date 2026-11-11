package com.helicalinsight.efw.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class ParameterEvaluationServiceTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-28T10:15:00Z"), ZONE);
    private static final String DATE = "yyyy-MM-dd";
    private static final String DATE_TIME = "dd-MM-yyyy HH:mm:ss";

    private final ParameterEvaluationService service = new ParameterEvaluationService(catalog());

    @Test
    public void resolvesShippedFunctionsFromAFixedClock() {
        assertEquals("2026-10-28", eval("today", DATE, false, true, null));
        assertEquals("28-10-2026 23:59:59", eval("today", DATE_TIME, true, false, null));
        assertEquals("2026-10-27", eval("yesterday", DATE, false, true, null));
        assertEquals("2026-10-29", eval("tomorrow", DATE, false, true, null));
        assertEquals("2026-10-29", eval("tomorrow+5", DATE, false, true, null));
        assertEquals("2026-10-31", eval("today+3", DATE, false, true, null));
        assertEquals("2026-10-01", eval("month", DATE, false, true, null));
        assertEquals("31-10-2026 23:59:59", eval("month", DATE_TIME, true, false, null));
        assertEquals("2026-09-01", eval("month-1", DATE, false, true, null));
        assertEquals("2026-10-01", eval("quarter", DATE, false, true, null));
        assertEquals("2025-01-01", eval("year-1", DATE, false, true, null));
        assertEquals("to_date", eval("to_date", DATE, false, true, null));
        assertEquals("28-10-2026 15:45:00", eval("to_date", DATE_TIME, true, false, null));
        assertEquals("2020-01-01", eval("2020-01-01", DATE, false, true, null));
        assertEquals("2020-01-15", eval("anchor_today", DATE, false, true, "2020-01-15"));
    }

    @Test
    public void weekComesFromTheCatalogRatherThanEvaluatorCode() {
        String withWeek = """
                {"functions":[{"name":"week","unit":"WEEKS","boundary":"WEEK","offset":0,"direction":"NONE","acceptsModifier":true}]}
                """;
        ParameterEvaluationService weekService = new ParameterEvaluationService(RelativeFunctionCatalog.fromJson(withWeek));
        assertEquals("2026-10-26", weekService.evaluate("week", EvaluationRequest.single(ZONE, CLOCK, DATE)));
        assertEquals("week", service.evaluate("week", EvaluationRequest.single(ZONE, CLOCK, DATE)));
    }

    @Test
    public void rejectsAnUnknownUnitWhenTheCatalogLoads() {
        String bad = """
                {"functions":[{"name":"fiscal","unit":"FISCAL","boundary":"YEAR"}]}
                """;
        assertThrows(EfwServiceException.class, () -> RelativeFunctionCatalog.fromJson(bad));
    }

    @Test
    public void anchorTokenWithoutADateFails() {
        assertThrows(EfwServiceException.class, () -> eval("anchor_month", DATE, false, true, null));
    }

    @Test
    public void cannedReportDateParameterBecomesTheJasperPattern() {
        JsonObject form = new JsonObject();
        form.addProperty(HcrParameterEvaluator.TIME_ZONE, "Asia/Kolkata");
        JsonObject anchors = new JsonObject();
        anchors.addProperty("__anchor__OrderDate", "2020-01-15");
        form.add(HcrParameterEvaluator.ANCHORS, anchors);
        JsonObject connection = new JsonObject();
        connection.addProperty("OrderDate", "today");
        form.add("connectionDetails", connection);
        JsonArray parameters = new JsonArray();
        JsonObject parameter = new JsonObject();
        parameter.addProperty("name", "OrderDate");
        parameter.addProperty("className", "java.util.Date");
        parameter.addProperty("defaultExpression", "today");
        parameters.add(parameter);
        JsonObject range = new JsonObject();
        range.addProperty("name", "Span");
        range.addProperty("className", "java.lang.String");
        range.addProperty("valueFormat", "YYYY-MM-DD");
        JsonArray tokens = new JsonArray();
        tokens.add("month");
        tokens.add("to_date");
        range.add("defaultExpression", tokens);
        parameters.add(range);
        JsonObject designer = new JsonObject();
        designer.add("parameters", parameters);
        form.add("designerProperties", designer);

        HcrParameterEvaluator.resolve(form, service, CLOCK);

        assertEquals("28-10-2026 00:00:00", parameter.get("defaultExpression").getAsString());
        assertEquals("28-10-2026 00:00:00", connection.get("OrderDate").getAsString());
        assertEquals("2026-10-01", range.getAsJsonArray("defaultExpression").get(0).getAsString());
        assertEquals("2026-10-28", range.getAsJsonArray("defaultExpression").get(1).getAsString());
        assertEquals(false, form.has(HcrParameterEvaluator.ANCHORS));
    }

    private String eval(String raw, String pattern, boolean rangeEnd, boolean singleValue, String anchor) {
        EvaluationRequest request = singleValue
                ? EvaluationRequest.single(ZONE, CLOCK, pattern)
                : rangeEnd
                        ? EvaluationRequest.rangeEnd(ZONE, CLOCK, pattern)
                        : EvaluationRequest.rangeStart(ZONE, CLOCK, pattern);
        return service.evaluate(raw, request.withAnchor(anchor));
    }

    private static RelativeFunctionCatalog catalog() {
        return RelativeFunctionCatalog.fromJson("""
                {"functions":[
                  {"name":"today","unit":"DAYS","boundary":"DAY","offset":0,"direction":"NONE","acceptsModifier":true},
                  {"name":"tomorrow","unit":"DAYS","boundary":"DAY","offset":1,"direction":"NEXT","acceptsModifier":false},
                  {"name":"yesterday","unit":"DAYS","boundary":"DAY","offset":1,"direction":"LAST","acceptsModifier":false},
                  {"name":"month","unit":"MONTHS","boundary":"MONTH","offset":0,"direction":"NONE","acceptsModifier":true},
                  {"name":"year","unit":"YEARS","boundary":"YEAR","offset":0,"direction":"NONE","acceptsModifier":true},
                  {"name":"quarter","unit":"MONTHS","multiplier":3,"boundary":"QUARTER","offset":0,"direction":"NONE","acceptsModifier":true},
                  {"name":"to_date","boundary":"NOW","direction":"LAST","acceptsModifier":false,"inheritsUnit":true}
                ]}
                """);
    }
}
