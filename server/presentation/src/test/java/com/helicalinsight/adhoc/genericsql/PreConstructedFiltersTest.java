package com.helicalinsight.adhoc.genericsql;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.adhoc.metadata.ISecureMetadata;
import com.helicalinsight.adhoc.metadata.MetadataSecurityObjectFactory;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PreConstructedFiltersTest {

	@Test
	public void ut_a1_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);

		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("t_id");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("c_id");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(null);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);

			}
		}
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ut_a2_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);
		ISecureMetadata securityClass = mock(ISecureMetadata.class);
		
		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("t_id");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("c_id");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.getFilters(anyString())).thenReturn(null);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(securityClass);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);

			}
		}
		
	}
	
	@Test
	public void ut_a3_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);
		ISecureMetadata securityClass = mock(ISecureMetadata.class);
		
		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("t_id");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("c_id");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.getFilters(anyString())).thenReturn("");
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(securityClass);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);

			}
		}
		
	}
	
	@Test
	public void ut_a4_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);
		ISecureMetadata securityClass = mock(ISecureMetadata.class);
		
		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("t_id");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("123");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.getFilters(anyString())).thenReturn("Hello ${world}. This is ${a.test}.");
		when(securityExpression.getExpressionType()).thenReturn("column");
		when(securityExpression.getAccessType()).thenReturn("grant");
		when(securityExpression.getOn()).thenReturn("123");
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(securityClass);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);
				
			}
		}
		
	}
	
	@Test
	public void ut_a5_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);
		ISecureMetadata securityClass = mock(ISecureMetadata.class);
		
		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("456");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("123");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.getFilters(anyString())).thenReturn("Hello ${world}. This is ${a.test}.");
		when(securityExpression.getExpressionType()).thenReturn("view");
		when(securityExpression.getAccessType()).thenReturn("grant");
		when(securityExpression.getOn()).thenReturn("456");
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(securityClass);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);
				
			}
		}
		
	}

	
	@Test
	public void ut_a6_test_PreConstructedFilters() {
		Metadata metadata = mock(Metadata.class);
		String openQuotes = "";
		String closeQuotes = "";
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		MetadataSecurity access = mock(MetadataSecurity.class);
		SecurityExpression securityExpression = mock(SecurityExpression.class);
		ISecureMetadata securityClass = mock(ISecureMetadata.class);
		
		List<SecurityExpression> expressions = new ArrayList<>();
		expressions.add(securityExpression);

		List<Table> list = new ArrayList<>();
		list.add(table);

		List<Column> list2 = new ArrayList<>();
		list2.add(column);

		List<ConnectionDatabase> cdbList = new ArrayList<>();
		cdbList.add(connectionDatabase);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(list);
		when(table.getId()).thenReturn("456");
		when(table.getName()).thenReturn("t_name");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list2);
		when(column.getId()).thenReturn("123");
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(cdbList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(metadata.getMetadataSecurity()).thenReturn(access);
		when(access.getExpressions()).thenReturn(expressions);
		when(securityExpression.getCondition()).thenReturn("condition");
		when(securityExpression.getFilter()).thenReturn("filter");
		when(securityExpression.getType()).thenReturn("type");
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.evaluateCondition(anyString())).thenReturn(true);
		when(securityClass.getFilters(anyString())).thenReturn("Hello ${world}. This is ${a.test}.");
		when(securityExpression.getExpressionType()).thenReturn("table");
		when(securityExpression.getAccessType()).thenReturn("grant");
		when(securityExpression.getOn()).thenReturn("456");
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<MetadataSecurityObjectFactory> mockedStatic1 = mockStatic(
					MetadataSecurityObjectFactory.class)) {
				mockedStatic1.when(() -> MetadataSecurityObjectFactory.getSecurityClass(anyString())).thenReturn(securityClass);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class))
						.thenReturn(mdServiceDb);
				PreConstructedFilters constructedFilters = new PreConstructedFilters(metadata, openQuotes, closeQuotes);
				
			}
		}
		
	}

}
