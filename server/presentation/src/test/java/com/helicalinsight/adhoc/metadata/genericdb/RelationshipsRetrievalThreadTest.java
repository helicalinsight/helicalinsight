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

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKeys;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKeys;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RelationshipsRetrievalThreadTest {

	@Test(expected = MetadataRetrievalException.class)
	public void ut_a1_test_run() {
		JsonObject formData = new JsonObject();
		String connectionType = "";
		String catalog = "";
		String schema = "";
		List<Table> assignedTables = new ArrayList<>();
		PrimaryKeyTemplate primaryKeyTemplate = mock(PrimaryKeyTemplate.class);
		ForeignKeyTemplate foreignKeyTemplate = mock(ForeignKeyTemplate.class);
		RelationshipsRetrievalThread retrievalThread = new RelationshipsRetrievalThread(formData, connectionType,
				catalog, schema, assignedTables, primaryKeyTemplate, foreignKeyTemplate);

		retrievalThread.run();
	}

	@Test
	public void ut_a2_test_run() throws SQLException {
		JsonObject formData = new JsonObject();
		String connectionType = "";
		String catalog = "";
		String schema = "";
		Table table = mock(Table.class);
		List<Table> assignedTables = new ArrayList<>();
		assignedTables.add(table);
		PrimaryKeyTemplate primaryKeyTemplate = mock(PrimaryKeyTemplate.class);
		ForeignKeyTemplate foreignKeyTemplate = mock(ForeignKeyTemplate.class);
		RelationshipsRetrievalThread retrievalThread = new RelationshipsRetrievalThread(formData, connectionType,
				catalog, schema, assignedTables, primaryKeyTemplate, foreignKeyTemplate);
		DriverConnection driverConnection = mock(DriverConnection.class);
		Connection connection = mock(Connection.class);
		DatabaseMetaData metaData = mock(DatabaseMetaData.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		PrimaryKeys primaryKeys = mock(PrimaryKeys.class);

		when(connection.getMetaData()).thenReturn(metaData);
		when(driverConnection.getConnection()).thenReturn(connection);
		try (MockedStatic<ConnectionProviderFactory> mockedStatic = mockStatic(ConnectionProviderFactory.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<ForeignKeyDetails> mockedStatic3 = mockStatic(ForeignKeyDetails.class)) {
					try (MockedStatic<PrimaryKeyDetails> mockedStatic4 = mockStatic(PrimaryKeyDetails.class)) {
						mockedStatic4.when(() -> PrimaryKeyDetails.getPrimaryKeys(any(), any(), any(), any()))
								.thenThrow(new SQLException());

						mockedStatic3.when(() -> ForeignKeyDetails.getForeignKeys(any(), any(), any(), any()))
								.thenThrow(new SQLException());

						mockedStatic2.when(() -> ApplicationContextAccessor.getBean(ForeignKeys.class))
								.thenReturn(foreignKeys);
						mockedStatic2.when(() -> ApplicationContextAccessor.getBean(PrimaryKeys.class))
								.thenReturn(primaryKeys);

						mockedStatic.when(() -> ConnectionProviderFactory.getConnection(formData, connectionType))
								.thenReturn(driverConnection);

						retrievalThread.run();
					}
				}
			}
		}

	}

}
