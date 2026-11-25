package com.helicalinsight.adhoc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.ViewLabelsRetrievalComponent;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.JdbcStatementLimit;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.utility.JsonUtils;

public class HIJasperQueryExecuterTest {

	@Test
	public void executeComponent_tempQueryWithFullLimit_authenticatesAndKeepsRawQuery() {
		JsonObject mapJson = new JsonObject();
		mapJson.addProperty("map_id", 1);
		mapJson.addProperty("type", "global.jdbc");
		mapJson.addProperty("temp_uuid", "abc-temp");

		JsonObject form = new JsonObject();
		form.addProperty("limit", "full");
		form.add("mapJson", mapJson);

		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "global.jdbc");
		connectionPart.addProperty("id", "1");

		JsonObject column = new JsonObject();
		column.addProperty("name", "travel_id");
		column.addProperty("type", "java.lang.Integer");
		JsonObject metadataRow = new JsonObject();
		metadataRow.add("travel_id", column);
		JsonArray metadata = new JsonArray();
		metadata.add(metadataRow);
		JsonObject dataSet = new JsonObject();
		dataSet.add("metadata", metadata);

		DriverConnection driverConnection = new DriverConnection();
		driverConnection.setDriverClass("org.apache.derby.jdbc.AutoloadedDriver");

		AtomicBoolean authenticated = new AtomicBoolean(false);
		AtomicReference<JsonObject> authSnapshot = new AtomicReference<>();
		List<String> constructorPayloads = new ArrayList<>();

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<DataSourceSecurityUtility> security = mockStatic(DataSourceSecurityUtility.class);
				MockedStatic<ConnectionProviderFactory> connections = mockStatic(ConnectionProviderFactory.class);
				MockedStatic<DataSourceUtils> dataSourceUtils = mockStatic(DataSourceUtils.class,
						Mockito.CALLS_REAL_METHODS);
				MockedStatic<MetadataUtils> metadataUtils = mockStatic(MetadataUtils.class);
				MockedStatic<ViewLabelsRetrievalComponent> labels = mockStatic(ViewLabelsRetrievalComponent.class);
				MockedConstruction<EnhancedQueryExecutor> executors = mockConstruction(EnhancedQueryExecutor.class,
						(mock, context) -> {
							constructorPayloads.add((String) context.arguments().get(0));
							when(mock.getConnectionPartFromTemp()).thenReturn(connectionPart);
							when(mock.getQueryFromTemp()).thenReturn("select * from travel_details");
							when(mock.getResultSetFromTemp()).thenAnswer(invocation -> {
								assertTrue(authenticated.get());
								return dataSet;
							});
						})) {

			jsonUtils.when(JsonUtils::getEfwdExtension).thenReturn("efwd");
			security.when(() -> DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(any(JsonObject.class)))
					.thenAnswer(invocation -> {
						JsonObject json = invocation.getArgument(0);
						authSnapshot.set(JsonParser.parseString(json.toString()).getAsJsonObject());
						authenticated.set(true);
						return null;
					});
			connections.when(() -> ConnectionProviderFactory.getConnectionFromTemp(any(JsonObject.class), anyString()))
					.thenReturn(driverConnection);
			dataSourceUtils.when(() -> DataSourceUtils.addExtraDataForWorkflowProcess(any(JsonObject.class), anyString()))
					.thenAnswer(invocation -> null);
			metadataUtils.when(() -> MetadataUtils.dialectOfDatabase(anyString()))
					.thenReturn("org.hibernate.dialect.DerbyTenSevenDialect");

			String result = new HIJasperQueryExecuter().executeComponent(form.toString());

			JsonObject authJson = authSnapshot.get();
			assertEquals(DataSourceSecurityUtility.EXECUTE, authJson.get("access").getAsString());
			assertTrue(authJson.get("isTemp").getAsBoolean());
			assertEquals("abc-temp.efwd", authJson.getAsJsonObject("efwd").get("file").getAsString());

			assertEquals(2, constructorPayloads.size());
			JsonObject executedRequest = JsonParser.parseString(constructorPayloads.get(1)).getAsJsonObject();
			assertEquals("select * from travel_details", executedRequest.get("processedLimitQuery").getAsString());

			security.verify(() -> DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(any(JsonObject.class)),
					times(1));
			connections.verify(
					() -> ConnectionProviderFactory.getConnectionFromTemp(any(JsonObject.class), eq("global.jdbc")),
					times(1));
			connections.verify(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()),
					never());
			labels.verify(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class), anyString(), anyString()),
					never());

			JsonObject fields = JsonParser.parseString(result).getAsJsonObject();
			JsonObject field = fields.getAsJsonArray("field").get(0).getAsJsonObject();
			assertEquals("travel_id", field.get("name").getAsString());
			assertEquals("java.lang.Integer", field.get("clazz").getAsString());
			assertEquals(2, executors.constructed().size());
		}
	}

	@Test
	public void executeComponent_tempQueryOnStatementDriver_setsMaxRowsAndKeepsRawQuery() {
		JsonObject executed = executeTemp(form(null), true, null);
		assertEquals(10, executed.get("maxRows").getAsInt());
		assertEquals("select * from travel_details", executed.get("processedLimitQuery").getAsString());
	}

	@Test
	public void executeComponent_tempQueryOnWrapDriver_runsWrappedSqlWithoutMaxRows() {
		JsonObject executed = executeTemp(form(null), false, "select * from (select * from travel_details) foo limit 10");
		assertFalse(executed.has("maxRows"));
		assertEquals("select * from (select * from travel_details) foo limit 10",
				executed.get("processedLimitQuery").getAsString());
	}

	@Test
	public void executeComponent_requestedLimit_reachesStatementAndWrap() {
		JsonObject statement = executeTemp(form("25"), true, null);
		assertEquals(25, statement.get("maxRows").getAsInt());

		JsonObject wrap = executeTemp(form("25"), false, "wrapped-25");
		assertFalse(wrap.has("maxRows"));
		assertEquals("wrapped-25", wrap.get("processedLimitQuery").getAsString());
	}

	@Test
	public void executeComponent_sqlAdhoc_doesNotApplyAPreviewLimit() {
		JsonObject mapJson = new JsonObject();
		mapJson.addProperty("map_id", 1);
		mapJson.addProperty("type", "sql.adhoc");
		mapJson.addProperty("temp_uuid", "abc-temp");
		JsonObject form = new JsonObject();
		form.add("mapJson", mapJson);

		JsonObject executed = executeTemp(form, true, null);
		assertFalse(executed.has("maxRows"));
		assertFalse(executed.has("processedLimitQuery"));
	}

	@Test
	public void executeComponent_savedQuery_wrapsSqlWithTheDefaultRowCount() {
		JsonObject executed = executeSaved(form(null));
		assertFalse(executed.has("maxRows"));
		assertEquals("wrapped", executed.get("processedLimitQuery").getAsString());
	}

	private JsonObject form(String limit) {
		JsonObject mapJson = new JsonObject();
		mapJson.addProperty("map_id", 1);
		mapJson.addProperty("type", "global.jdbc");
		mapJson.addProperty("temp_uuid", "abc-temp");
		JsonObject form = new JsonObject();
		if (limit != null) {
			form.addProperty("limit", limit);
		}
		form.add("mapJson", mapJson);
		return form;
	}

	private JsonObject executeTemp(JsonObject form, boolean statementLimit, String wrappedSql) {
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "global.jdbc");
		connectionPart.addProperty("id", "1");
		JsonObject dataSet = dataSet();
		DriverConnection driverConnection = new DriverConnection();
		driverConnection.setDriverClass("org.apache.derby.jdbc.AutoloadedDriver");
		List<String> constructorPayloads = new ArrayList<>();

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<DataSourceSecurityUtility> security = mockStatic(DataSourceSecurityUtility.class);
				MockedStatic<ConnectionProviderFactory> connections = mockStatic(ConnectionProviderFactory.class);
				MockedStatic<DataSourceUtils> dataSourceUtils = mockStatic(DataSourceUtils.class,
						Mockito.CALLS_REAL_METHODS);
				MockedStatic<MetadataUtils> metadataUtils = mockStatic(MetadataUtils.class);
				MockedStatic<ViewLabelsRetrievalComponent> labels = mockStatic(ViewLabelsRetrievalComponent.class);
				MockedStatic<JdbcStatementLimit> limits = mockStatic(JdbcStatementLimit.class);
				MockedConstruction<EnhancedQueryExecutor> executors = mockConstruction(EnhancedQueryExecutor.class,
						(mock, context) -> {
							constructorPayloads.add((String) context.arguments().get(0));
							when(mock.getConnectionPartFromTemp()).thenReturn(connectionPart);
							when(mock.getConnectionPart()).thenReturn(connectionPart);
							when(mock.getQueryFromTemp()).thenReturn("select * from travel_details");
							when(mock.getQuery()).thenReturn("select * from travel_details");
							when(mock.getResultSetFromTemp()).thenReturn(dataSet);
							when(mock.getResultSet()).thenReturn(dataSet);
						})) {
			jsonUtils.when(JsonUtils::getEfwdExtension).thenReturn("efwd");
			security.when(() -> DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(any(JsonObject.class)))
					.thenAnswer(invocation -> null);
			connections.when(() -> ConnectionProviderFactory.getConnectionFromTemp(any(JsonObject.class), anyString()))
					.thenReturn(driverConnection);
			dataSourceUtils.when(() -> DataSourceUtils.addExtraDataForWorkflowProcess(any(JsonObject.class), anyString()))
					.thenAnswer(invocation -> null);
			metadataUtils.when(() -> MetadataUtils.dialectOfDatabase(anyString()))
					.thenReturn("org.hibernate.dialect.DerbyTenSevenDialect");
			limits.when(() -> JdbcStatementLimit.useStatementLimit(any())).thenReturn(statementLimit);
			labels.when(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class), anyString(), anyString()))
					.thenAnswer(invocation -> wrappedSql == null ? "wrapped-" + invocation.getArgument(2) : wrappedSql);

			new HIJasperQueryExecuter().executeComponent(form.toString());
			if (!statementLimit && form.getAsJsonObject("mapJson").get("type").getAsString().equals("global.jdbc")) {
				labels.verify(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class), anyString(), anyString()),
						times(1));
			} else {
				labels.verify(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class), anyString(), anyString()),
						never());
			}
		}
		int executedIndex = constructorPayloads.size() - 1;
		return JsonParser.parseString(constructorPayloads.get(executedIndex)).getAsJsonObject();
	}

	private JsonObject executeSaved(JsonObject form) {
		form.getAsJsonObject("mapJson").remove("temp_uuid");
		form.getAsJsonObject("mapJson").addProperty("dir", "reports");
		form.getAsJsonObject("mapJson").addProperty("file", "report.efwd");
		JsonObject connectionPart = new JsonObject();
		connectionPart.addProperty("type", "global.jdbc");
		connectionPart.addProperty("id", "1");
		DriverConnection driverConnection = new DriverConnection();
		driverConnection.setDriverClass("com.mysql.cj.jdbc.Driver");
		List<String> constructorPayloads = new ArrayList<>();

		try (MockedStatic<ConnectionProviderFactory> connections = mockStatic(ConnectionProviderFactory.class);
				MockedStatic<DataSourceUtils> dataSourceUtils = mockStatic(DataSourceUtils.class,
						Mockito.CALLS_REAL_METHODS);
				MockedStatic<MetadataUtils> metadataUtils = mockStatic(MetadataUtils.class);
				MockedStatic<ViewLabelsRetrievalComponent> labels = mockStatic(ViewLabelsRetrievalComponent.class);
				MockedConstruction<EnhancedQueryExecutor> executors = mockConstruction(EnhancedQueryExecutor.class,
						(mock, context) -> {
							constructorPayloads.add((String) context.arguments().get(0));
							when(mock.getConnectionPart()).thenReturn(connectionPart);
							when(mock.getQuery()).thenReturn("select * from travel_details");
							when(mock.getResultSet()).thenReturn(dataSet());
						})) {
			connections.when(() -> ConnectionProviderFactory.getConnection(any(JsonObject.class), anyString()))
					.thenReturn(driverConnection);
			dataSourceUtils.when(() -> DataSourceUtils.addExtraDataForWorkflowProcess(any(JsonObject.class), anyString()))
					.thenAnswer(invocation -> null);
			metadataUtils.when(() -> MetadataUtils.dialectOfDatabase(anyString()))
					.thenReturn("org.hibernate.dialect.MySQLDialect");
			labels.when(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class), anyString(), eq("10")))
					.thenReturn("wrapped");

			new HIJasperQueryExecuter().executeComponent(form.toString());
			labels.verify(() -> ViewLabelsRetrievalComponent.query(any(JsonObject.class),
					eq("org.hibernate.dialect.MySQLDialect"), eq("10")), times(1));
		}
		return JsonParser.parseString(constructorPayloads.get(constructorPayloads.size() - 1)).getAsJsonObject();
	}

	private JsonObject dataSet() {
		JsonObject column = new JsonObject();
		column.addProperty("name", "travel_id");
		column.addProperty("type", "java.lang.Integer");
		JsonObject metadataRow = new JsonObject();
		metadataRow.add("travel_id", column);
		JsonArray metadata = new JsonArray();
		metadata.add(metadataRow);
		JsonObject dataSet = new JsonObject();
		dataSet.add("metadata", metadata);
		return dataSet;
	}
}
