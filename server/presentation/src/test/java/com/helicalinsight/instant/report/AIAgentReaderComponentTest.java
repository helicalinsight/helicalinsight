package com.helicalinsight.instant.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;

public class AIAgentReaderComponentTest {

    @Test(expected = IllegalArgumentException.class)
    public void executeComponentThrowsWhenAgentMissing() {
        AIAgentReaderComponent component = new AIAgentReaderComponent();
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file");

        try (MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)) {
            mockedStatic.when(() -> ReportOpenHelper.getAgentStateDb(anyString(), anyString())).thenReturn(null);
            component.executeComponent(formJson.toString());
        }
    }

    @Test
    public void executeComponentReturnsAgentContent() {
        AIAgentReaderComponent component = new AIAgentReaderComponent();
        AdhocReport adhocReport = mock(AdhocReport.class);
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file.agent");
        JsonObject content = new JsonObject();
        content.addProperty("state", "ready");

        try (MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)) {
            mockedStatic.when(() -> ReportOpenHelper.getAgentStateDb("dir", "file.agent")).thenReturn(adhocReport);
            mockedStatic.when(() -> ReportOpenHelper.getAgentContentAsJson(adhocReport)).thenReturn(content);

            assertEquals(content.toString(), component.executeComponent(formJson.toString()));
        }
    }

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(new AIAgentReaderComponent().isThreadSafeToCache());
    }
}
