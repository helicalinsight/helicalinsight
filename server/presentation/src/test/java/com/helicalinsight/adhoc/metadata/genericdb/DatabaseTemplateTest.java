package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
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
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKey;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKeys;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKey;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKeys;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseTemplateTest {

	@Test
	public void ut_a1_test_getDatabase() throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		DatabaseTemplate databaseTemplate = new DatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);
		Table table = new Table();
		Columns columns = mock(Columns.class);
		PrimaryKeyTemplate primaryKeyTemplate = mock(PrimaryKeyTemplate.class);
		PrimaryKeys primaryKeys = mock(PrimaryKeys.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		ForeignKeyTemplate foreignKeyTemplate = mock(ForeignKeyTemplate.class);
		RelationshipsTemplate relationshipsTemplate = mock(RelationshipsTemplate.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		Column col = mock(Column.class);
		PrimaryKey primaryKey = mock(PrimaryKey.class);
		ForeignKey foreignKey = mock(ForeignKey.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("selectedSchema", "DBSchema");

		when(connection.getMetaData()).thenReturn(databaseMetaData);
		List<String> listOfTables = new ArrayList<>();
		listOfTables.add("table1");
		listOfTables.add("table2");

		JsonObject columnInfo = new JsonObject();

		Field field = DatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		Field field2 = DatabaseTemplate.class.getDeclaredField("primaryKeyTemplate");
		field2.setAccessible(true);
		field2.set(databaseTemplate, primaryKeyTemplate);

		Field field3 = DatabaseTemplate.class.getDeclaredField("foreignKeyTemplate");
		field3.setAccessible(true);
		field3.set(databaseTemplate, foreignKeyTemplate);

		Field field4 = DatabaseTemplate.class.getDeclaredField("relationshipsTemplate");
		field4.setAccessible(true);
		field4.set(databaseTemplate, relationshipsTemplate);
		List<Relationship> list = new ArrayList<>();
		list.add(relationship);
		List<Column> listOfColumns = new ArrayList<>();
		listOfColumns.add(null);
		listOfColumns.add(col);

		List<PrimaryKey> keyList = new ArrayList<PrimaryKey>();
		keyList.add(null);
		keyList.add(primaryKey);

		List<ForeignKey> foreignKeyList = new ArrayList<ForeignKey>();
		foreignKeyList.add(null);
		foreignKeyList.add(foreignKey);

		when(foreignKeys.getForeignKeyList()).thenReturn(foreignKeyList);
		when(primaryKeys.getPrimaryKey()).thenReturn(keyList);
		when(columns.getColumn()).thenReturn(listOfColumns);
		when(relationshipsTemplate.getRelationships(tables)).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(list);
		when(foreignKeyTemplate.getForeignKeyList(any())).thenReturn(null);
		when(primaryKeyTemplate.getPrimaryKeyList(any())).thenReturn(null);
		when(columnsTemplate.getColumnsList(any(), any())).thenReturn(null);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<TableDetails> mockedStatic1 = mockStatic(TableDetails.class)) {
				try (MockedStatic<ColumnDetails> mockedStatic2 = mockStatic(ColumnDetails.class)) {
					try (MockedStatic<PrimaryKeyDetails> mockedStatic3 = mockStatic(PrimaryKeyDetails.class)) {
						try (MockedStatic<ForeignKeyDetails> mockedStatic4 = mockStatic(ForeignKeyDetails.class)) {
							mockedStatic4.when(() -> ForeignKeyDetails.getForeignKeys(any(), any(), any(), any()))
									.thenReturn(columnInfo.toString());
							mockedStatic3.when(() -> PrimaryKeyDetails.getPrimaryKeys(any(), any(), any(), any()))
									.thenReturn(columnInfo.toString());

							mockedStatic2.when(() -> ColumnDetails.getColumnInfoForTable(any(), any(), any(), any()))
									.thenReturn(columnInfo.toString());

							mockedStatic1.when(() -> TableDetails.getListOfTables(any(), any(), any()))
									.thenReturn(listOfTables);

							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class))
									.thenReturn(database);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class))
									.thenReturn(tables);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class))
									.thenReturn(columns);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(PrimaryKeys.class))
									.thenReturn(primaryKeys);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ForeignKeys.class))
									.thenReturn(foreignKeys);

							databaseTemplate.getDatabase(connection, formData);
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_a2_test_getDatabase() throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		DatabaseTemplate databaseTemplate = new DatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);
		Table table = new Table();
		Columns columns = mock(Columns.class);
		PrimaryKeyTemplate primaryKeyTemplate = mock(PrimaryKeyTemplate.class);
		PrimaryKeys primaryKeys = mock(PrimaryKeys.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		ForeignKeyTemplate foreignKeyTemplate = mock(ForeignKeyTemplate.class);
		RelationshipsTemplate relationshipsTemplate = mock(RelationshipsTemplate.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		Column col = mock(Column.class);
		PrimaryKey primaryKey = mock(PrimaryKey.class);
		ForeignKey foreignKey = mock(ForeignKey.class);

		JsonObject formData = new JsonObject();
		formData.addProperty("selectedSchema", "DBSchema");

		when(connection.getMetaData()).thenReturn(databaseMetaData);
		when(connection.getCatalog()).thenReturn("catalog");
		List<String> listOfTables = new ArrayList<>();
		listOfTables.add("table1");
		listOfTables.add("table2");

		JsonObject columnInfo = new JsonObject();

		Field field = DatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		Field field2 = DatabaseTemplate.class.getDeclaredField("primaryKeyTemplate");
		field2.setAccessible(true);
		field2.set(databaseTemplate, primaryKeyTemplate);

		Field field3 = DatabaseTemplate.class.getDeclaredField("foreignKeyTemplate");
		field3.setAccessible(true);
		field3.set(databaseTemplate, foreignKeyTemplate);

		Field field4 = DatabaseTemplate.class.getDeclaredField("relationshipsTemplate");
		field4.setAccessible(true);
		field4.set(databaseTemplate, relationshipsTemplate);
		List<Relationship> list = new ArrayList<>();
		list.add(relationship);
		List<Column> listOfColumns = new ArrayList<>();
		listOfColumns.add(null);
		listOfColumns.add(col);

		List<PrimaryKey> keyList = new ArrayList<PrimaryKey>();
		keyList.add(null);
		keyList.add(primaryKey);

		List<ForeignKey> foreignKeyList = new ArrayList<ForeignKey>();
		foreignKeyList.add(null);
		foreignKeyList.add(foreignKey);

		when(foreignKeys.getForeignKeyList()).thenReturn(foreignKeyList);
		when(primaryKeys.getPrimaryKey()).thenReturn(keyList);
		when(columns.getColumn()).thenReturn(listOfColumns);
		when(relationshipsTemplate.getRelationships(tables)).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(list);
		when(foreignKeyTemplate.getForeignKeyList(any())).thenReturn(null);
		when(primaryKeyTemplate.getPrimaryKeyList(any())).thenReturn(null);
		when(columnsTemplate.getColumnsList(any(), any())).thenReturn(null);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<TableDetails> mockedStatic1 = mockStatic(TableDetails.class)) {
				try (MockedStatic<ColumnDetails> mockedStatic2 = mockStatic(ColumnDetails.class)) {
					try (MockedStatic<PrimaryKeyDetails> mockedStatic3 = mockStatic(PrimaryKeyDetails.class)) {
						try (MockedStatic<ForeignKeyDetails> mockedStatic4 = mockStatic(ForeignKeyDetails.class)) {
							mockedStatic4.when(() -> ForeignKeyDetails.getForeignKeys(any(), any(), any(), any()))
									.thenThrow(new SQLException());
							mockedStatic3.when(() -> PrimaryKeyDetails.getPrimaryKeys(any(), any(), any(), any()))
									.thenThrow(new SQLException());

							mockedStatic2.when(() -> ColumnDetails.getColumnInfoForTable(any(), any(), any(), any()))
									.thenReturn(columnInfo.toString());

							mockedStatic1.when(() -> TableDetails.getListOfTables(any(), any(), any()))
									.thenReturn(listOfTables);

							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class))
									.thenReturn(database);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class))
									.thenReturn(tables);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class))
									.thenReturn(columns);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(PrimaryKeys.class))
									.thenReturn(primaryKeys);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ForeignKeys.class))
									.thenReturn(foreignKeys);

							databaseTemplate.getDatabase(connection, formData);
						}
					}
				}
			}
		}

	}

	@Test(expected = MetadataRetrievalException.class)
	public void ut_a3_test_getDatabase() throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		DatabaseTemplate databaseTemplate = new DatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		ColumnsTemplate columnsTemplate = mock(ColumnsTemplate.class);
		Table table = new Table();
		Columns columns = mock(Columns.class);
		PrimaryKeyTemplate primaryKeyTemplate = mock(PrimaryKeyTemplate.class);
		PrimaryKeys primaryKeys = mock(PrimaryKeys.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		ForeignKeyTemplate foreignKeyTemplate = mock(ForeignKeyTemplate.class);
		RelationshipsTemplate relationshipsTemplate = mock(RelationshipsTemplate.class);
		Relationships relationships = mock(Relationships.class);
		Relationship relationship = mock(Relationship.class);
		Column col = mock(Column.class);
		PrimaryKey primaryKey = mock(PrimaryKey.class);
		ForeignKey foreignKey = mock(ForeignKey.class);

		JsonObject formData = new JsonObject();

		when(connection.getMetaData()).thenThrow(new SQLException());
		when(connection.getCatalog()).thenReturn(null);
		List<String> listOfTables = new ArrayList<>();
		listOfTables.add("table1");
		listOfTables.add("table2");

		JsonObject columnInfo = new JsonObject();

		Field field = DatabaseTemplate.class.getDeclaredField("columnsTemplate");
		field.setAccessible(true);
		field.set(databaseTemplate, columnsTemplate);

		Field field2 = DatabaseTemplate.class.getDeclaredField("primaryKeyTemplate");
		field2.setAccessible(true);
		field2.set(databaseTemplate, primaryKeyTemplate);

		Field field3 = DatabaseTemplate.class.getDeclaredField("foreignKeyTemplate");
		field3.setAccessible(true);
		field3.set(databaseTemplate, foreignKeyTemplate);

		Field field4 = DatabaseTemplate.class.getDeclaredField("relationshipsTemplate");
		field4.setAccessible(true);
		field4.set(databaseTemplate, relationshipsTemplate);
		List<Relationship> list = new ArrayList<>();
		list.add(relationship);
		List<Column> listOfColumns = new ArrayList<>();
		listOfColumns.add(null);
		listOfColumns.add(col);

		List<PrimaryKey> keyList = new ArrayList<PrimaryKey>();
		keyList.add(null);
		keyList.add(primaryKey);

		List<ForeignKey> foreignKeyList = new ArrayList<ForeignKey>();
		foreignKeyList.add(null);
		foreignKeyList.add(foreignKey);

		when(foreignKeys.getForeignKeyList()).thenReturn(foreignKeyList);
		when(primaryKeys.getPrimaryKey()).thenReturn(keyList);
		when(columns.getColumn()).thenReturn(listOfColumns);
		when(relationshipsTemplate.getRelationships(tables)).thenReturn(relationships);
		when(relationships.getListOfRelations()).thenReturn(list);
		when(foreignKeyTemplate.getForeignKeyList(any())).thenReturn(null);
		when(primaryKeyTemplate.getPrimaryKeyList(any())).thenReturn(null);
		when(columnsTemplate.getColumnsList(any(), any())).thenReturn(null);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<TableDetails> mockedStatic1 = mockStatic(TableDetails.class)) {
				try (MockedStatic<ColumnDetails> mockedStatic2 = mockStatic(ColumnDetails.class)) {
					try (MockedStatic<PrimaryKeyDetails> mockedStatic3 = mockStatic(PrimaryKeyDetails.class)) {
						try (MockedStatic<ForeignKeyDetails> mockedStatic4 = mockStatic(ForeignKeyDetails.class)) {
							mockedStatic4.when(() -> ForeignKeyDetails.getForeignKeys(any(), any(), any(), any()))
									.thenThrow(new SQLException());
							mockedStatic3.when(() -> PrimaryKeyDetails.getPrimaryKeys(any(), any(), any(), any()))
									.thenThrow(new SQLException());

							mockedStatic2.when(() -> ColumnDetails.getColumnInfoForTable(any(), any(), any(), any()))
									.thenReturn(columnInfo.toString());

							mockedStatic1.when(() -> TableDetails.getListOfTables(any(), any(), any()))
									.thenReturn(listOfTables);

							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class))
									.thenReturn(database);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Tables.class))
									.thenReturn(tables);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(Columns.class))
									.thenReturn(columns);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(PrimaryKeys.class))
									.thenReturn(primaryKeys);
							mockedStatic.when(() -> ApplicationContextAccessor.getBean(ForeignKeys.class))
									.thenReturn(foreignKeys);

							databaseTemplate.getDatabase(connection, formData);
						}
					}
				}
			}
		}

	}

	@Test(expected = MetadataRetrievalException.class)
	public void ut_a4_test_getDatabase() throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		DatabaseTemplate databaseTemplate = new DatabaseTemplate();
		Connection connection = mock(Connection.class);
		Database database = mock(Database.class);
		
		JsonObject formData = new JsonObject();

		when(connection.getCatalog()).thenThrow(new SQLException());
		List<String> listOfTables = new ArrayList<>();
		listOfTables.add("table1");
		listOfTables.add("table2");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);

			databaseTemplate.getDatabase(connection, formData);

		}

	}

}
