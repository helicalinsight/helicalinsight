package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.ExternalRelationships;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.processor.DBProcessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiConnectionMergeAdhocTableTest {

	@Test
	public void ut_a1_test_merge() {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		adhocTable.merge();
	}

	@Test
	public void ut_a2_test_merge() {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		when(metadata.getCached()).thenReturn(true);

		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
			mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(false);
			adhocTable.merge();

		}
	}

	@Test
	public void ut_a3_test_merge() {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Database database = mock(Database.class);
		ConnectionDetails mdConnectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		when(metadata.getCached()).thenReturn(true);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("SampleDatabase");
		when(metadata.getConnectionDetails()).thenReturn(mdConnectionDetails);
		when(mdConnectionDetails.getConnectionId()).thenReturn("id");
		when(mdConnectionDetails.getDriverClass()).thenReturn(driverClass);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(tables.getTableList()).thenReturn(tableList);
		try (MockedStatic<AppStatistics> mockedStatic = mockStatic(AppStatistics.class)) {
			try (MockedStatic<DBProcessor> mockedStatic2 = mockStatic(DBProcessor.class)) {
				mockedStatic2.when(() -> DBProcessor.checkAndReplaceSpecialChars(anyString())).thenReturn("");

				mockedStatic.when(() -> AppStatistics.isSPARK_STARTED()).thenReturn(true);
				mockedStatic.when(() -> AppStatistics.isMASTER_STARTED()).thenReturn(true);
				adhocTable.merge();
			}
		}
	}
	@Test
	public void ut_a4_test_mergeTablesAndJoin() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("mergeTablesAndJoin");
		method.setAccessible(true);
		Connections connections = mock(Connections.class);
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(null);
		method.invoke(adhocTable);
	}
	
	@Test
	public void ut_a5_test_mergeTablesAndJoin() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("mergeTablesAndJoin");
		method.setAccessible(true);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		Database multiDatabase = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Relationships relationships = mock(Relationships.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		
		List<ConnectionDatabase> connectionDatabaseList = new ArrayList<>();
		connectionDatabaseList.add(connectionDatabase);
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(connectionDatabaseList);
		when(connectionDatabase.getDatabase()).thenReturn(multiDatabase);
		when(multiDatabase.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(metadata.getDatabase()).thenReturn(multiDatabase);
		when(multiDatabase.getRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(null);
		method.invoke(adhocTable);
	}
	
	@Test
	public void ut_a6_test_mergeTablesAndJoin() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("mergeTablesAndJoin");
		method.setAccessible(true);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		Database multiDatabase = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		List<Relationship> listOfRelations = new ArrayList<>();
		listOfRelations.add(relationship);
		List<ConnectionDatabase> connectionDatabaseList = new ArrayList<>();
		connectionDatabaseList.add(connectionDatabase);
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(connectionDatabaseList);
		when(connectionDatabase.getDatabase()).thenReturn(multiDatabase);
		when(multiDatabase.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		when(multiDatabase.getRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(listOfRelations);
		when(database.getRelationships()).thenReturn(null);
		
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Relationships.class)).thenReturn(relationships);
			method.invoke(adhocTable);
		}
	}
	
	@Test
	public void ut_a7_test_mergeTablesAndJoin() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("mergeTablesAndJoin");
		method.setAccessible(true);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		Database multiDatabase = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		List<Relationship> listOfRelations = new ArrayList<>();
		listOfRelations.add(relationship);
		List<ConnectionDatabase> connectionDatabaseList = new ArrayList<>();
		connectionDatabaseList.add(connectionDatabase);
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(connectionDatabaseList);
		when(connectionDatabase.getDatabase()).thenReturn(multiDatabase);
		when(multiDatabase.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(tableList);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		when(multiDatabase.getRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(listOfRelations);
		when(database.getRelationships()).thenReturn(relationships);
		method.invoke(adhocTable);
	}
	
	@Test
	public void ut_a8_test_updateMultipleDatabase() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("updateMultipleDatabase");
		method.setAccessible(true);
		Connections connections = mock(Connections.class);
		ConnectionDatabase connectionDatabase = mock(ConnectionDatabase.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		Tables tables = mock(Tables.class);
		
		List<ConnectionDatabase> connectionDatabaseList = new ArrayList<>();
		connectionDatabaseList.add(connectionDatabase);
		
		when(metadata.getConnections()).thenReturn(connections);
		when(connections.getConnectionDatabase()).thenReturn(connectionDatabaseList);
		when(connectionDatabase.getDatabase()).thenReturn(database);
		when(connectionDatabase.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getConnectionId()).thenReturn("id");
		when(database.getName()).thenReturn("dbName");
		when(database.getTables()).thenReturn(tables);
		try (MockedStatic<DBProcessor> mockedStatic2 = mockStatic(DBProcessor.class)) {
			mockedStatic2.when(() -> DBProcessor.checkAndReplaceSpecialChars(anyString())).thenReturn("");
			method.invoke(adhocTable);
		}
	}
	@Test
	public void ut_a9_test_getDbIdDbNameMap() {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Map<String, String> dbIdDbNameMap = adhocTable.getDbIdDbNameMap();
		Map<String, String> map = new HashMap<>();
		assertEquals(map, dbIdDbNameMap);
	}
	
	@Test
	public void ut_b1_test_getFullyQualifedColumnMap() {
		Metadata metadata = mock(Metadata.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Map<String, String> fullyQualifedColumnMap = adhocTable.getFullyQualifedColumnMap();
		Map<String, String> map = new HashMap<>();
		assertEquals(map, fullyQualifedColumnMap);
	}
	
	@Test
	public void ut_b2_test_updateTableName() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Relationships relationships = mock(Relationships.class);
		
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		
		when(column.getId()).thenReturn("cId");
		when(column.getName()).thenReturn("cName");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(tables.getTableList()).thenReturn(tableList);
		when(database.getName()).thenReturn("dbName");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnList);
		
		when(database.getRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(null);
		when(table.getOriginalName()).thenReturn("orginalName");
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("updateTableName", Database.class, String.class);
		method.setAccessible(true);
		method.invoke(adhocTable, database, "dummy");
		
	}
	
	@Test
	public void ut_b3_test_updateTableName() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		Join join = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock( RightTable.class);
		
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		List<Relationship> listOfRelations = new ArrayList<>();
		listOfRelations.add(relationship);
		List<Join> joins = new ArrayList<>();
		joins.add(join);
		
		when(column.getId()).thenReturn("cId");
		when(column.getName()).thenReturn("cName");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(tables.getTableList()).thenReturn(tableList);
		when(database.getName()).thenReturn("dbName");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(columnList);
		
		when(database.getRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(listOfRelations);	
		when(relationship.getTable()).thenReturn("table");
		when(relationship.getReferenceTable()).thenReturn("refTable");
		when(relationship.getJoin()).thenReturn(joins);
		
		when(join.getLeftTable()).thenReturn(leftTable);
		when(leftTable.getTable()).thenReturn("lTable");
		when(join.getRightTable()).thenReturn(rightTable);
		when(rightTable.getTable()).thenReturn("rTable");
		
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("updateTableName", Database.class, String.class);
		method.setAccessible(true);
		method.invoke(adhocTable, database, "dummy");
		
	}
	
	@Test
	public void ut_b4_test_crossJoinRelationship() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		ExternalRelationships relationships = mock(ExternalRelationships.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
	
		when(metadata.getExternalRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(null);
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("crossJoinRelationship");
		method.setAccessible(true);
		method.invoke(adhocTable);
	}
	
	@Test
	public void ut_b5_test_crossJoinRelationship() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ExternalRelationships relationships = mock(ExternalRelationships.class);
		MultiConnectionMergeAdhocTable adhocTable = new MultiConnectionMergeAdhocTable(metadata);
		Relationship relationship = mock(Relationship.class);
		Join join = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock( RightTable.class);
		Relationships allRelationships = mock(Relationships.class);
		
		List<Relationship> listOfRelations = new ArrayList<>();
		listOfRelations.add(relationship);
		List<Join> joins = new ArrayList<>();
		joins.add(join);
		when(metadata.getExternalRelationships()).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(listOfRelations);
		when(relationship.getJoin()).thenReturn(joins);
		when(join.getLeftTable()).thenReturn(leftTable);
		when(leftTable.getDbId()).thenReturn("lDbId");
		when(leftTable.getTable()).thenReturn("lTable");
		when(join.getRightTable()).thenReturn(rightTable);
		when(rightTable.getDbId()).thenReturn("rDbId");
		when(rightTable.getTable()).thenReturn("rTable");
		when(relationship.getTable()).thenReturn("table");
		when(relationship.getReferenceTable()).thenReturn("refTable");
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getRelationships()).thenReturn(null);
		when(allRelationships.getListOfRelations()).thenReturn(null);
	
		Method method = MultiConnectionMergeAdhocTable.class.getDeclaredMethod("crossJoinRelationship");
		method.setAccessible(true);
		
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Relationships.class)).thenReturn(allRelationships);
			method.invoke(adhocTable);
		}
	}
}
