//package com.helicalinsight.efw.utility;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.CALLS_REAL_METHODS;
//import static org.mockito.Mockito.mockStatic;
//
//import org.junit.Test;
//import org.mockito.MockedStatic;
//
//import com.google.gson.JsonObject;
//
//public class JsonUtilsImportExportOptionsTest {
//
//	@Test
//	public void returnsEmptyObjectWhenImportExportOptionsMissing() {
//		JsonObject settings = new JsonObject();
//
//		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class, CALLS_REAL_METHODS)) {
//			json.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
//
//			JsonObject options = JsonUtils.getImportExportOptions();
//
//			assertNotNull(options);
//			assertTrue(options.entrySet().isEmpty());
//		}
//	}
//
//	@Test
//	public void coercesXmlBooleanStringsAndPreservesOtherTypes() {
//		JsonObject configured = new JsonObject();
//		configured.addProperty("enforceDatasource", "true");
//		configured.addProperty("enforceRecycleBin", "false");
//		configured.addProperty("label", "Recycle Bin");
//		configured.addProperty("maxItems", 10);
//
//		JsonObject settings = new JsonObject();
//		settings.add("importExportOptions", configured);
//
//		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class, CALLS_REAL_METHODS)) {
//			json.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
//
//			JsonObject options = JsonUtils.getImportExportOptions();
//
//			assertTrue(options.get("enforceDatasource").getAsJsonPrimitive().isBoolean());
//			assertTrue(options.get("enforceDatasource").getAsBoolean());
//			assertTrue(options.get("enforceRecycleBin").getAsJsonPrimitive().isBoolean());
//			assertFalse(options.get("enforceRecycleBin").getAsBoolean());
//			assertEquals("Recycle Bin", options.get("label").getAsString());
//			assertEquals(10, options.get("maxItems").getAsInt());
//		}
//	}
//
//	@Test
//	public void picksUpNewKeysDynamically() {
//		JsonObject configured = new JsonObject();
//		configured.addProperty("enforceShare", "true");
//		configured.addProperty("enforceSchedules", "false");
//
//		JsonObject settings = new JsonObject();
//		settings.add("importExportOptions", configured);
//
//		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class, CALLS_REAL_METHODS)) {
//			json.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
//
//			JsonObject options = JsonUtils.getImportExportOptions();
//
//			assertTrue(options.has("enforceShare"));
//			assertTrue(options.has("enforceSchedules"));
//			assertTrue(options.get("enforceShare").getAsJsonPrimitive().isBoolean());
//			assertTrue(options.get("enforceShare").getAsBoolean());
//			assertTrue(options.get("enforceSchedules").getAsJsonPrimitive().isBoolean());
//			assertFalse(options.get("enforceSchedules").getAsBoolean());
//		}
//	}
//
//	@Test
//	public void keepsNativeBooleanPrimitivesAsBooleans() {
//		JsonObject configured = new JsonObject();
//		configured.addProperty("enforceDatasource", true);
//		configured.addProperty("enforceRecycleBin", false);
//
//		JsonObject settings = new JsonObject();
//		settings.add("importExportOptions", configured);
//
//		try (MockedStatic<JsonUtils> json = mockStatic(JsonUtils.class, CALLS_REAL_METHODS)) {
//			json.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
//
//			JsonObject options = JsonUtils.getImportExportOptions();
//
//			assertTrue(options.get("enforceDatasource").getAsJsonPrimitive().isBoolean());
//			assertTrue(options.get("enforceDatasource").getAsBoolean());
//			assertTrue(options.get("enforceRecycleBin").getAsJsonPrimitive().isBoolean());
//			assertFalse(options.get("enforceRecycleBin").getAsBoolean());
//		}
//	}
//}
