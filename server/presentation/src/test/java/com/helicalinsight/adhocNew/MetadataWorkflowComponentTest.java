package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.DatabaseMetadataProvider;
import com.helicalinsight.adhoc.MetadataWorkflowComponent;
import com.helicalinsight.adhoc.metadata.genericdb.ColumnsRetrievalThread;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataMultiThreadingUtilities;
import com.helicalinsight.adhoc.metadata.genericdb.TableDetails;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataWorkflowComponentTest {

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a1_test_executeComponent() {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		formJson.add("parameters", null);
		component.executeComponent(formJson.toString());
	}

	@Test(expected = IncompleteFormDataException.class)
	public void ut_a2_test_executeComponent() {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		formJson.add("parameters", new JsonObject());
		component.executeComponent(formJson.toString());
	}

	@Test
	public void ut_a3_test_executeComponent() {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();

		try (MockedConstruction<DatabaseMetadataProvider> construction = mockConstruction(
				DatabaseMetadataProvider.class, (mock, context) -> {
					when(mock.executeComponent(anyString())).thenReturn("response");
				})) {

			String executeComponent = component.executeComponent(formJson.toString());
			assertEquals("response", executeComponent);
		}

	}

	@Test
	public void ut_a4_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "tree");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("catalog", "catalog");
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "sName");
		schemas.add(schemaJson);
		json.add("schemas", schemas);
		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchTables", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tables = new ArrayList<>();
		tables.add("travel_details");

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					mockedStatic3.when(
							() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(), anyString()))
							.thenReturn(tables);

					mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);
					mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
							.thenReturn(driverConnection);

					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals("classifier",
							JsonParser.parseString(executeComponent).getAsJsonObject().get("classifier").getAsString());

				}
			}
		}
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a5_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "tree");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("catalog", "catalog");

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchTables", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tables = new ArrayList<>();
		tables.add("travel_details");

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					mockedStatic3.when(
							() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(), anyString()))
							.thenReturn(tables);

					mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);
					mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
							.thenReturn(driverConnection);

					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals("classifier",
							JsonParser.parseString(executeComponent).getAsJsonObject().get("classifier").getAsString());

				}
			}
		}
	}

	@Test
	public void ut_a6_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "tree");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchTables", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tables = new ArrayList<>();
		tables.add("travel_details");

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					mockedStatic3.when(
							() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(), anyString()))
							.thenReturn(tables);

					mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);
					mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
							.thenReturn(driverConnection);

					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals("classifier",
							JsonParser.parseString(executeComponent).getAsJsonObject().get("classifier").getAsString());

				}
			}
		}
	}

	@Test
	public void ut_a7_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "true");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "tree");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchTables", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tables = new ArrayList<>();
		tables.add("travel_details");

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					mockedStatic3.when(
							() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(), anyString()))
							.thenReturn(tables);

					mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);
					mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
							.thenReturn(driverConnection);

					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals("classifier",
							JsonParser.parseString(executeComponent).getAsJsonObject().get("classifier").getAsString());

				}
			}
		}
	}

	@Test
	public void ut_a8_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchTables", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tables = new ArrayList<>();
		tables.add("travel_details");

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					mockedStatic3.when(
							() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(), anyString()))
							.thenReturn(tables);

					mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);
					mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
							.thenReturn(jsonObject);

					mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
							.thenReturn(driverConnection);

					String executeComponent = component.executeComponent(formJson.toString());
					assertEquals("classifier",
							JsonParser.parseString(executeComponent).getAsJsonObject().get("classifier").getAsString());

				}
			}
		}
	}

	@Test
	public void ut_a9_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "name");
		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		schemaJson.add("tables", tables);
		;
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tables.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class),
								anyString(), anyString(), anyString())).thenReturn(columns);

						mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(),
								anyString())).thenReturn(tablesList);

						mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);
						mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
								.thenReturn(driverConnection);

						String executeComponent = component.executeComponent(formJson.toString());
						assertEquals("classifier", JsonParser.parseString(executeComponent).getAsJsonObject()
								.get("classifier").getAsString());

					}

				}
			}
		}
	}

	@Test
	public void ut_b1_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();
		schemaJson.addProperty("name", "name");
		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		schemaJson.add("tables", tables);
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tables.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class), any(),
								anyString(), anyString())).thenReturn(columns);

						mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(),
								anyString())).thenReturn(tablesList);

						mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);
						mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
								.thenReturn(driverConnection);

						String executeComponent = component.executeComponent(formJson.toString());
						assertEquals("classifier", JsonParser.parseString(executeComponent).getAsJsonObject()
								.get("classifier").getAsString());

					}

				}
			}
		}
	}

	@Test
	public void ut_b2_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();

		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		schemaJson.add("tables", tables);
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tables.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class), any(),
								anyString(), anyString())).thenReturn(columns);

						mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class), anyString(),
								anyString())).thenReturn(tablesList);

						mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);
						mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
								.thenReturn(jsonObject);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
								.thenReturn(driverConnection);

						String executeComponent = component.executeComponent(formJson.toString());
						assertEquals("classifier", JsonParser.parseString(executeComponent).getAsJsonObject()
								.get("classifier").getAsString());

					}

				}
			}
		}
	}

	@Test(expected = EfwServiceException.class)
	public void ut_b3_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();

		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		tables.add("travel_details");
		
		
		schemaJson.add("tables", tables);
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tablesList.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic5 = mockStatic(
								MetadataMultiThreadingUtilities.class)) {
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled())
									.thenReturn(true).thenReturn(false);
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.getThreshold())
							.thenReturn(1);
					

							mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class),
									any(), anyString(), anyString())).thenReturn(columns);

							mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class),
									anyString(), anyString())).thenReturn(tablesList);

							mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);
							mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);

							mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							component.executeComponent(formJson.toString());
							

						}
					}
				}
			}
		}
	}

	@Test
	public void ut_b4_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();

		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		tables.add("travel_details");
		
		
		schemaJson.add("tables", tables);
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tablesList.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic5 = mockStatic(
								MetadataMultiThreadingUtilities.class)) {
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled())
									.thenReturn(true).thenReturn(false);
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.getThreshold())
							.thenReturn(2);
					

							mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class),
									any(), anyString(), anyString())).thenReturn(columns);

							mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class),
									anyString(), anyString())).thenReturn(tablesList);

							mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);
							mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);

							mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							String executeComponent = component.executeComponent(formJson.toString());
							assertEquals("classifier", JsonParser.parseString(executeComponent).getAsJsonObject()
									.get("classifier").getAsString());

						}
					}
				}
			}
		}
	}

	@Test
	public void ut_b5_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();

		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		tables.add("travel_details");
		
		
		schemaJson.add("tables", tables);
		schemaJson.addProperty("name", "sName");
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		parameters.addProperty("fetchColumns", "true");
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tablesList.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic5 = mockStatic(
								MetadataMultiThreadingUtilities.class)) {
							try(MockedConstruction<HIManagedThread> construction = mockConstruction(HIManagedThread.class,(mock,context)->{
								doAnswer((invocation) -> { 
									throw new Exception();
								}).when(mock).setName(any());
							})){
								
							
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled())
									.thenReturn(true).thenReturn(false);
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.getThreshold())
							.thenReturn(2);
					

							mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class),
									any(), anyString(), anyString())).thenReturn(columns);

							mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class),
									anyString(), anyString())).thenReturn(tablesList);

							mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);
							mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);

							mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							String executeComponent = component.executeComponent(formJson.toString());
							assertEquals("classifier", JsonParser.parseString(executeComponent).getAsJsonObject()
									.get("classifier").getAsString());

							}
						}
					}
				}
			}
		}
	}

	
	@Test(expected = EfwServiceException.class)
	public void ut_b6_test_executeComponent() throws SQLException {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		JsonObject formJson = new JsonObject();
		JsonObject parameters = new JsonObject();
		formJson.addProperty("type", "type");
		formJson.addProperty("classifier", "classifier");
		parameters.addProperty("fetchCatalogs", "false");
		parameters.addProperty("fetchSchemas", "true");
		parameters.addProperty("view", "false");
		JsonArray fetchData = new JsonArray();
		JsonObject json = new JsonObject();
		JsonArray schemas = new JsonArray();
		JsonObject schemaJson = new JsonObject();

		JsonArray tables = new JsonArray();
		tables.add("eachTable");
		tables.add("travel_details");
		
		
		schemaJson.add("tables", tables);
		schemaJson.addProperty("name", "sName");
		schemas.add(schemaJson);
		json.add("schemas", schemas);

		fetchData.add(json);
		parameters.add("fetchData", fetchData);
		formJson.add("parameters", parameters);

		JsonObject jsonObject = new JsonObject();
		JsonArray catalogs = new JsonArray();
		jsonObject.add("catalogs", catalogs);
		jsonObject.add("schemas", catalogs);

		List<String> tablesList = new ArrayList<>();
		tablesList.add("travel_details");

		Object columns = new JsonObject();

		Connection connection = mock(Connection.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		DriverConnection driverConnection = mock(DriverConnection.class);
		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(databaseMetaData);

		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<DatabaseDetails> mockedStatic2 = mockStatic(DatabaseDetails.class)) {
				try (MockedStatic<TableDetails> mockedStatic3 = mockStatic(TableDetails.class)) {
					try (MockedStatic<ColumnsRetrievalThread> mockedStatic4 = mockStatic(
							ColumnsRetrievalThread.class)) {
						try (MockedStatic<MetadataMultiThreadingUtilities> mockedStatic5 = mockStatic(
								MetadataMultiThreadingUtilities.class)) {
							try(MockedConstruction<HIManagedThread> construction = mockConstruction(HIManagedThread.class,(mock,context)->{
								doAnswer((invocation) -> { 
									throw new Exception();
								}).when(mock).setName(any());
							})){
								
							
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.isMultiThreadingEnabled())
									.thenReturn(true).thenReturn(false);
							mockedStatic5.when(() -> MetadataMultiThreadingUtilities.getThreshold())
							.thenReturn(2);
					

							mockedStatic4.when(() -> ColumnsRetrievalThread.columnList(any(DatabaseMetaData.class),
									any(), anyString(), anyString())).thenReturn(columns);

							mockedStatic3.when(() -> TableDetails.getListOfTables(any(DatabaseMetaData.class),
									anyString(), anyString())).thenReturn(tablesList);

							mockedStatic2.when(() -> DatabaseDetails.newRetrieveCatalogs(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);
							mockedStatic2.when(() -> DatabaseDetails.newRetrieveSchemas(any(DatabaseMetaData.class)))
									.thenReturn(jsonObject);

							mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), anyString()))
									.thenReturn(driverConnection);

							 component.executeComponent(formJson.toString());
							

							}
						}
					}
				}
			}
		}
	}

	@Test
	public void ut_b7_test_isThreadSafeToCache() {
		MetadataWorkflowComponent component = new MetadataWorkflowComponent();
		boolean threadSafeToCache = component.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
