package com.helicalinsight.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.GroovyUtils;

public class GroovyManagedJdbcHandlerTest {

	private static final String GROOVY_CONNECTION_ID = "15";
	private static final String RESOLVED_GLOBAL_ID = "42";
	private static final String DENIED_MESSAGE = "You may not have sufficient privilege to access the dataSource";

	@Test
	public void readDS_checksExecuteOnResolvedGlobalIdBeforeReadingConnection() throws Exception {
		GroovyManagedJdbcHandler handler = handlerWithGroovyContent();
		AtomicBoolean authenticated = new AtomicBoolean(false);
		AtomicReference<String> readRequest = new AtomicReference<>();

		try (MockedStatic<GroovyUtils> groovy = mockStatic(GroovyUtils.class);
				MockedStatic<DataSourceSecurityUtility> security = mockStatic(DataSourceSecurityUtility.class);
				MockedConstruction<DataSourceReader> readers = mockConstruction(DataSourceReader.class,
						(mock, context) -> when(mock.doService(anyString(), anyString(), anyString(), anyString()))
								.thenAnswer(invocation -> {
									assertTrue(authenticated.get());
									assertEquals("core", invocation.getArgument(0));
									assertEquals("dataSource", invocation.getArgument(1));
									assertEquals("read", invocation.getArgument(2));
									readRequest.set(invocation.getArgument(3));
									return globalReadResponse();
								}))) {

			groovy.when(() -> GroovyUtils.executeGroovy(anyString(), eq("evalCondition"), eq(JsonObject.class)))
					.thenReturn(resolvedCondition(RESOLVED_GLOBAL_ID));
			security.when(() -> DataSourceSecurityUtility.isGlobalAccessible(RESOLVED_GLOBAL_ID,
					DataSourceSecurityUtility.EXECUTE)).thenAnswer(invocation -> {
						authenticated.set(true);
						return null;
					});

			ObjectNode connection = handler.readDS(GROOVY_CONNECTION_ID, GlobalJdbcType.MANAGED_GROOVY_DATASOURCE);

			JsonObject request = JsonParser.parseString(readRequest.get()).getAsJsonObject();
			assertEquals(RESOLVED_GLOBAL_ID, request.get("globalId").getAsString());
			assertEquals(RESOLVED_GLOBAL_ID, request.get("id").getAsString());
			assertEquals("global", request.get("classifier").getAsString());
			assertEquals("dynamicDataSource", request.get("type").getAsString());

			assertEquals("dbuser", connection.get("username").asText());
			assertEquals("jdbc:h2:mem:travel", connection.get("url").asText());
			assertEquals("org.h2.Driver", connection.get("driverClassName").asText());
			assertEquals("travel", connection.get("catalog").asText());
			assertEquals("travel_db", connection.get("database").asText());
			assertEquals("public", connection.get("schema").asText());

			security.verify(() -> DataSourceSecurityUtility.isGlobalAccessible(RESOLVED_GLOBAL_ID,
					DataSourceSecurityUtility.EXECUTE));
			security.verify(() -> DataSourceSecurityUtility.isGlobalAccessible(eq(GROOVY_CONNECTION_ID), anyString()),
					never());
			assertEquals(1, readers.constructed().size());
		}
	}

	@Test
	public void readDS_deniesResolvedGlobalConnectionAndDoesNotReadIt() {
		GroovyManagedJdbcHandler handler = handlerWithGroovyContent();

		try (MockedStatic<GroovyUtils> groovy = mockStatic(GroovyUtils.class);
				MockedStatic<DataSourceSecurityUtility> security = mockStatic(DataSourceSecurityUtility.class);
				MockedConstruction<DataSourceReader> readers = mockConstruction(DataSourceReader.class)) {

			groovy.when(() -> GroovyUtils.executeGroovy(anyString(), eq("evalCondition"), eq(JsonObject.class)))
					.thenReturn(resolvedCondition(RESOLVED_GLOBAL_ID));
			security.when(() -> DataSourceSecurityUtility.isGlobalAccessible(RESOLVED_GLOBAL_ID,
					DataSourceSecurityUtility.EXECUTE)).thenThrow(new AccessDeniedException(DENIED_MESSAGE));

			AccessDeniedException denied = assertThrows(AccessDeniedException.class,
					() -> handler.readDS(GROOVY_CONNECTION_ID, GlobalJdbcType.MANAGED_GROOVY_DATASOURCE));

			assertEquals(DENIED_MESSAGE, denied.getMessage());
			security.verify(() -> DataSourceSecurityUtility.isGlobalAccessible(RESOLVED_GLOBAL_ID,
					DataSourceSecurityUtility.EXECUTE));
			assertEquals(0, readers.constructed().size());
		}
	}

	@Test
	public void readDS_blankResolvedGlobalIdRejectsBeforeAccessCheck() {
		GroovyManagedJdbcHandler handler = handlerWithGroovyContent();

		try (MockedStatic<GroovyUtils> groovy = mockStatic(GroovyUtils.class);
				MockedStatic<DataSourceSecurityUtility> security = mockStatic(DataSourceSecurityUtility.class);
				MockedConstruction<DataSourceReader> readers = mockConstruction(DataSourceReader.class)) {

			groovy.when(() -> GroovyUtils.executeGroovy(anyString(), eq("evalCondition"), eq(JsonObject.class)))
					.thenReturn(resolvedCondition(""));

			EfwServiceException rejected = assertThrows(EfwServiceException.class,
					() -> handler.readDS(GROOVY_CONNECTION_ID, GlobalJdbcType.MANAGED_GROOVY_DATASOURCE));

			assertEquals("You do not have privileges to access the global connections.", rejected.getMessage());
			security.verify(() -> DataSourceSecurityUtility.isGlobalAccessible(anyString(), anyString()), never());
			assertEquals(0, readers.constructed().size());
		}
	}

	private static GroovyManagedJdbcHandler handlerWithGroovyContent() {
		GroovyManagedJdbcHandler handler = spy(new GroovyManagedJdbcHandler());
		ObjectNode content = JsonNodeFactory.instance.objectNode();
		content.putObject("data").put("condition", "public Object evalCondition() { return null; }");
		doReturn(content).when(handler).readDsContent(anyString(), anyString());
		return handler;
	}

	private static JsonObject resolvedCondition(String globalId) {
		JsonObject resolved = new JsonObject();
		resolved.addProperty("globalId", globalId);
		resolved.addProperty("catalog", "travel");
		resolved.addProperty("database", "travel_db");
		resolved.addProperty("schema", "public");
		return resolved;
	}

	private static String globalReadResponse() throws Exception {
		ObjectNode response = JsonNodeFactory.instance.objectNode();
		response.put("userName", "dbuser");
		response.put("jdbcUrl", "jdbc:h2:mem:travel");
		response.put("driverName", "org.h2.Driver");
		response.put("id", RESOLVED_GLOBAL_ID);
		ObjectNode body = JsonNodeFactory.instance.objectNode();
		body.put("status", 1);
		body.set("response", response);
		return new ObjectMapper().writeValueAsString(body);
	}
}
