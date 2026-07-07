package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColumnsRetrievalThreadTest {

	@Test
	public void ut_a1_test_run() throws SQLException {
		JsonObject formData = new JsonObject();
		String connectionType = "";
		String catalog = "";
		String schema = "";
		List<String> assignedTables = new ArrayList<>();
		assignedTables.add("table1");
		JsonArray allTables = new JsonArray();

		ColumnsRetrievalThread columnsRetrievalThread = new ColumnsRetrievalThread(formData, connectionType, catalog,
				schema, assignedTables, allTables);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(metaData);

		JsonObject columnsJson = new JsonObject();
		JsonObject columns = new JsonObject();
		columns.addProperty("key", "value");
		columnsJson.add("columns", columns);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ColumnDetails> mockedStatic2 = mockStatic(ColumnDetails.class)) {
				mockedStatic2.when(() -> ColumnDetails.getColumnInfoForTable(any(), any(), any(), any()))
						.thenReturn(columnsJson.toString());

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any()))
						.thenReturn(driverConnection);

				columnsRetrievalThread.run();
			}

		}

	}
	
	@Test
	public void ut_a2_test_run() throws SQLException {
		JsonObject formData = new JsonObject();
		String connectionType = "";
		String catalog = "";
		String schema = "";
		List<String> assignedTables = new ArrayList<>();
		assignedTables.add("table1");
		JsonArray allTables = new JsonArray();

		ColumnsRetrievalThread columnsRetrievalThread = new ColumnsRetrievalThread(formData, connectionType, catalog,
				schema, assignedTables, allTables);

		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);

		when(driverConnection.getConnection()).thenReturn(connection);
		when(connection.getMetaData()).thenReturn(metaData);

		JsonObject columnsJson = new JsonObject();
		
		
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ColumnDetails> mockedStatic2 = mockStatic(ColumnDetails.class)) {
				mockedStatic2.when(() -> ColumnDetails.getColumnInfoForTable(any(), any(), any(), any()))
						.thenReturn(columnsJson.toString());

				mockedStatic.when(() -> ConnectionProviderFactory.getConnection(any(), any()))
						.thenReturn(driverConnection);

				columnsRetrievalThread.run();
			}

		}

	}
	
	
}
