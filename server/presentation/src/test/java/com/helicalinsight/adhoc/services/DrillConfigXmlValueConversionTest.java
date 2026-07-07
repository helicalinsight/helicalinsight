package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.efw.utility.JsonUtils;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * Unit tests for Drill dfs.json → drillConfig.xml attribute conversion using mock REST response.
 */
public class DrillConfigXmlValueConversionTest {

    private static final String MOCK_JSON_RESOURCE = "/drill/dfs-mock-response.json";

    @Before
    public void setUp() throws Exception {
        setReplaceRulesCache(rulesMatchingSettingXml());
    }

    @After
    public void tearDown() throws Exception {
        setReplaceRulesCache(null);
    }

    @Test
    public void ut_a1_mockResponseLoads() throws Exception {
        JSONObject root = loadMockDfsResponse();
        assertEquals("dfs", root.getString("name"));
        assertTrue(root.getJSONObject("config").getJSONObject("formats").has("csv"));
    }

    @Test
    public void ut_a2_nullFieldsFromMockBecomeEmpty() throws Exception {
        JSONObject iceberg = loadMockFormats().getJSONObject("iceberg");
        assertEquals("", JsonUtils.applyDrillConfigXmlValueForAttribute(iceberg.get("properties")));
        assertEquals("", JsonUtils.applyDrillConfigXmlValueForAttribute(iceberg.get("snapshotId")));

        JSONObject ltsv = loadMockFormats().getJSONObject("ltsv");
        assertEquals("", JsonUtils.applyDrillConfigXmlValueForAttribute(ltsv.get("escapeCharacter")));
    }

    @Test
    public void ut_a3_csvFormatEscapesQuoteNewlineAndTab() throws Exception {
        JSONObject csv = loadMockFormats().getJSONObject("csv");
        assertEquals("&quot;", JsonUtils.applyDrillConfigXmlValueForAttribute(csv.get("quote")));
        assertEquals("&quot;", JsonUtils.applyDrillConfigXmlValueForAttribute(csv.get("escape")));
        assertEquals("&#10;", JsonUtils.applyDrillConfigXmlValueForAttribute(csv.get("lineDelimiter")));
        assertEquals("true", JsonUtils.applyDrillConfigXmlValueForAttribute(csv.get("extractHeader")));
    }

    @Test
    public void ut_a4_tsvFieldDelimiterEscapesTab() throws Exception {
        JSONObject tsv = loadMockFormats().getJSONObject("tsv");
        assertEquals("&#09;", JsonUtils.applyDrillConfigXmlValueForAttribute(tsv.get("fieldDelimiter")));
    }

    @Test
    public void ut_a5_httpdLogFormatEscapesEmbeddedNewline() throws Exception {
        JSONObject httpd = loadMockFormats().getJSONObject("httpd");
        assertEquals("common&#10;combined", JsonUtils.applyDrillConfigXmlValueForAttribute(httpd.get("logFormat")));
    }

    @Test
    public void ut_a6_buildInnerConfigMatchesHandlerLine289() throws Exception {
        JSONObject csv = loadMockFormats().getJSONObject("csv");
        JSONObject inner = buildInnerConfigLikeHandler(csv);

        assertEquals("text", inner.getString("@type"));
        assertEquals("&quot;", inner.getString("@quote"));
        assertEquals("&quot;", inner.getString("@escape"));
        assertEquals("&#10;", inner.getString("@lineDelimiter"));
        assertEquals(",", inner.getString("@fieldDelimiter"));
        assertEquals("#", inner.getString("@comment"));
        assertEquals("true", inner.getString("@extractHeader"));
        assertEquals(".csv", inner.getString("@extensions"));
    }

    @Test
    public void ut_a7_hidwWorkspaceNullDefaultInputFormat() throws Exception {
        JSONObject hidw = loadMockDfsResponse().getJSONObject("config").getJSONObject("workspaces").getJSONObject("hidw");
        assertTrue(JSONUtils.isNull(hidw.get("defaultInputFormat")));
        assertEquals("", JsonUtils.applyDrillConfigXmlValueForAttribute(hidw.get("defaultInputFormat")));
    }

    private static JSONObject buildInnerConfigLikeHandler(JSONObject eachJsonElement) {
        JSONObject inner = new JSONObject();
        for (Object eachKey : eachJsonElement.keySet()) {
            String keyName = eachKey.toString();
            String keyInner = "@" + keyName;
            inner.put(keyInner, JsonUtils.applyDrillConfigXmlValueForAttribute(eachJsonElement.get(keyName)));
            if (eachJsonElement.has("extensions")) {
                inner.put("@extensions", "." + eachJsonElement.getJSONArray("extensions").get(0));
            }
        }
        return inner;
    }

    private static JSONObject loadMockFormats() throws Exception {
        return loadMockDfsResponse().getJSONObject("config").getJSONObject("formats");
    }

    private static JSONObject loadMockDfsResponse() throws Exception {
        InputStream stream = DrillConfigXmlValueConversionTest.class.getResourceAsStream(MOCK_JSON_RESOURCE);
        assertNotNull("Mock resource missing: " + MOCK_JSON_RESOURCE, stream);
        String json;
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
        }
        return JSONObject.fromObject(json);
    }

    /** Same rules as setting.xml drillConfigXmlReplaceRules CDATA. */
    private static List<String[]> rulesMatchingSettingXml() {
        List<String[]> rules = new ArrayList<>();
        rules.add(new String[]{"&", "&amp;"});
        rules.add(new String[]{"<", "&lt;"});
        rules.add(new String[]{">", "&gt;"});
        rules.add(new String[]{"\n", "&#10;"});
        rules.add(new String[]{"\r", ""});
        rules.add(new String[]{"\t", "&#09;"});
        rules.add(new String[]{"\"", "&quot;"});
        return rules;
    }

    private static void setReplaceRulesCache(List<String[]> rules) throws Exception {
        Field field = JsonUtils.class.getDeclaredField("drillConfigXmlReplaceRules");
        field.setAccessible(true);
        field.set(null, rules);
    }
}
