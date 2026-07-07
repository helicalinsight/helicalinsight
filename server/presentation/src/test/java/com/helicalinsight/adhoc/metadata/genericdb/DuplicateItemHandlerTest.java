package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AliasNameExistsException;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DuplicateItemHandlerTest {

	@Test
	public void ut_a1_test_duplicateColumns() {
		DuplicateItemHandler duplicateItemHandler = new DuplicateItemHandler();
		Metadata metadata = mock(Metadata.class);
		JsonArray duplicateColumnsArray =null;
		DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
	}
	@Test
	public void ut_a2_test_duplicateColumns() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		when(tables.getTableList()).thenReturn(null);
		JsonArray duplicateColumnsArray =new JsonArray();
		JsonObject duplicateColumn = new JsonObject();
		duplicateColumn.addProperty("alias", "alias");
		duplicateColumn.addProperty("tableId", "tableId");
		duplicateColumn.addProperty("originalId", "originalId");
		duplicateColumnsArray.add(duplicateColumn);
		
		DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
	}
	
	@Test(expected = DuplicateColumnException.class)
	public void ut_a3_test_duplicateColumns() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		when(column.getName()).thenReturn("c_name");
		when(column.getId()).thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateColumnsArray =new JsonArray();
		JsonObject duplicateColumn = new JsonObject();
		duplicateColumn.addProperty("alias", "c_name");
		duplicateColumn.addProperty("tableId", "12");
		duplicateColumn.addProperty("originalId", "originalId");
		duplicateColumnsArray.add(duplicateColumn);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
		}
		
	}
	
	@Test
	public void ut_a4_test_duplicateColumns() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(column.getName()).thenReturn("c_name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateColumnsArray =new JsonArray();
		JsonObject duplicateColumn = new JsonObject();
		duplicateColumn.addProperty("alias", "alias");
		duplicateColumn.addProperty("tableId", "12");
		duplicateColumn.addProperty("originalId", "originalId");
		duplicateColumn.addProperty("type", "type");
		duplicateColumn.addProperty("originalName", "originalName");
		
		duplicateColumn.addProperty("defaultFunction", "defaultFunction");
		
		
		
		duplicateColumnsArray.add(duplicateColumn);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
		}
		
	}
	
	@Test
	public void ut_a5_test_duplicateColumns() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateColumnsArray =new JsonArray();
		JsonObject duplicateColumn = new JsonObject();
		duplicateColumn.addProperty("alias", "alias");
		duplicateColumn.addProperty("tableId", "12");
		duplicateColumn.addProperty("originalId", "originalId");
		duplicateColumn.addProperty("type", "type");
		duplicateColumn.addProperty("originalName", "originalName");
		
		duplicateColumn.addProperty("defaultFunction", "defaultFunction");
		
		
		
		duplicateColumnsArray.add(duplicateColumn);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
		}
		
	}
	@Test
	public void ut_a6_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		JsonArray duplicateTablesArray =null;
		DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
	}
	@Test
	public void ut_a7_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(table.getName()).thenReturn("t_Name");
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(null);
		JsonArray duplicateTablesArray = new JsonArray();
		JsonObject duplicateTable = new JsonObject();
		duplicateTable.addProperty("originalName", "t_Name");
		duplicateTablesArray.add(duplicateTable);
		duplicateTable.addProperty("originalId", "12");
		JsonObject columnsObj = new JsonObject();
		
		duplicateTable.add("columns", columnsObj);
		
		DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
		
	}
	
	@Test(expected = TableNameExistsException.class)
	public void ut_a8_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(table.getAliasName()).thenReturn("o_name");
		when(table.getName()).thenReturn("t_Name");
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateTablesArray = new JsonArray();
		JsonObject duplicateTable = new JsonObject();
		duplicateTable.addProperty("originalName", "t_Name");
		duplicateTable.addProperty("originalId", "12");
		JsonObject columnsObj = new JsonObject();
		
		duplicateTable.add("columns", columnsObj);
		
		duplicateTablesArray.add(duplicateTable);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
		}
		
		
		
	}
	@Test(expected = AliasNameExistsException.class)
	public void ut_a9_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(table.getAliasName()).thenReturn("o_name");
		when(table.getName()).thenReturn("t_Name");
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateTablesArray = new JsonArray();
		JsonObject duplicateTable = new JsonObject();
		duplicateTable.addProperty("originalName", "o_name");
		duplicateTable.addProperty("originalId", "12");
		JsonObject columnsObj = new JsonObject();
		
		duplicateTable.add("columns", columnsObj);
		
		duplicateTablesArray.add(duplicateTable);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
		}
		
		
		
	}
	
	@Test
	public void ut_b1_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(table.getAliasName()).thenReturn("a_name");
		when(table.getName()).thenReturn("t_Name");
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateTablesArray = new JsonArray();
		JsonObject duplicateTable = new JsonObject();
		duplicateTable.addProperty("originalName", "o_name");
		duplicateTable.addProperty("originalId", "12");
		duplicateTable.addProperty("alias", "alias");
		JsonObject columnsObj = new JsonObject();
		JsonObject columnJson = new JsonObject();
		columnJson.addProperty("alias","alias");
		columnsObj.add("c_name", columnJson);
		duplicateTable.add("columns", columnsObj);
		
		duplicateTablesArray.add(duplicateTable);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(columns);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			
			DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
		}
		
		
		
	}

	@Test
	public void ut_b2_test_duplicateTable() {
		Metadata metadata = mock(Metadata.class);
		Tables tables =mock(Tables.class);
		Database database = mock(Database.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(table.getAliasName()).thenReturn("a_name");
		when(table.getName()).thenReturn("t_Name");
		when(column.getName()).thenReturn("c_name");
		when(column.getOriginalName()).thenReturn("Org_Name");
		when(column.getId()).thenReturn("id").thenReturn("originalId");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getId()).thenReturn("12");
		when(tables.getTableList()).thenReturn(tableList);
		JsonArray duplicateTablesArray = new JsonArray();
		JsonObject duplicateTable = new JsonObject();
		duplicateTable.addProperty("originalName", "o_name");
		duplicateTable.addProperty("originalId", "12");
		duplicateTable.addProperty("alias", "alias");
		JsonObject columnsObj = new JsonObject();
		JsonObject columnJson = new JsonObject();
		columnJson.addProperty("alias","alias");
		columnJson.addProperty("tableId", "12");
		columnJson.addProperty("originalId","originalId");
		columnJson.addProperty("originalName","originalName");
		columnJson.addProperty("defaultFunction","defaultFunction");
		
		columnsObj.add("a_name", columnJson);
		duplicateTable.add("columns", columnsObj);
		
		duplicateTablesArray.add(duplicateTable);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Table.class)).thenReturn(table);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Columns.class)).thenReturn(columns);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(Column.class)).thenReturn(column);
			
			DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
		}
		
		
		
	}


}
