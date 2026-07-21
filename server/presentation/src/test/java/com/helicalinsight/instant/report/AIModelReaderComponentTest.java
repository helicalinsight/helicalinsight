package com.helicalinsight.instant.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.adhoc.report.ReportOpenHelper;

public class AIModelReaderComponentTest {

    @Test(expected = IllegalArgumentException.class)
    public void executeComponentThrowsWhenModelMissing() {
        AIModelReaderComponent component = new AIModelReaderComponent();
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file");

        try (MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)) {
            mockedStatic.when(() -> ReportOpenHelper.getModelStateDb(anyString(), anyString())).thenReturn(null);
            component.executeComponent(formJson.toString());
        }
    }

    @Test
    public void executeComponentReturnsModelContent() {
        AIModelReaderComponent component = new AIModelReaderComponent();
        AdhocReport adhocReport = mock(AdhocReport.class);
        JsonObject formJson = new JsonObject();
        formJson.addProperty("dir", "dir");
        formJson.addProperty("file", "file.model");
        JsonObject content = new JsonObject();
        content.addProperty("state", "ready");

        try (MockedStatic<ReportOpenHelper> mockedStatic = mockStatic(ReportOpenHelper.class)) {
            mockedStatic.when(() -> ReportOpenHelper.getModelStateDb("dir", "file.model")).thenReturn(adhocReport);
            mockedStatic.when(() -> ReportOpenHelper.getModelContentAsJson(adhocReport)).thenReturn(content);

            assertEquals(content.toString(), component.executeComponent(formJson.toString()));
        }
    }

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(new AIModelReaderComponent().isThreadSafeToCache());
    }
}
