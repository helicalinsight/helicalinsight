package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKey;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKeys;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKey;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKeys;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConstraintsCleanerTest {

	@Test
	public void ut_a1_test_removeUnselectedColumns() {
		ConstraintsCleaner cleaner = new ConstraintsCleaner();
		Database database = mock(Database.class);

		when(database.getTables()).thenReturn(null);
		cleaner.removeUnselectedColumns(database);
	}

	@Test
	public void ut_a2_test_removeUnselectedColumns() {
		ConstraintsCleaner cleaner = new ConstraintsCleaner();
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		when(tables.getTableList()).thenReturn(null);
		when(database.getTables()).thenReturn(tables);
		cleaner.removeUnselectedColumns(database);
	}

	
	@Test
	public void ut_a3_test_removeUnselectedColumns() {
		ConstraintsCleaner cleaner = new ConstraintsCleaner();
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		List<Table> tableList = Arrays.asList(table);
		when(table.getForeignKeys()).thenReturn(foreignKeys);
		when(column.getName()).thenReturn("cName");
		when(columns.getColumn()).thenReturn(null);
		when(table.getColumns()).thenReturn(columns);
		when(tables.getTableList()).thenReturn(tableList);
		when(database.getTables()).thenReturn(tables);
		cleaner.removeUnselectedColumns(database);
	}
	
	@Test
	public void ut_a4_test_removeUnselectedColumns() {
		ConstraintsCleaner cleaner = new ConstraintsCleaner();
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		ForeignKeys foreignKeys = mock(ForeignKeys.class);
		ForeignKey foreignKey = mock(ForeignKey.class);
		PrimaryKeys primaryKeys = mock(PrimaryKeys.class);
		PrimaryKey primaryKey = mock(PrimaryKey.class);
		
		List<PrimaryKey> primaryKeyList = new ArrayList<>();
		primaryKeyList.add(null);
		primaryKeyList.add(primaryKey);
		
		List<Table> tableList = Arrays.asList(table);
		List<Column> columnList = Arrays.asList(column);
		List<ForeignKey> foreignKeyList = new ArrayList<>();
		foreignKeyList.add(null);
		foreignKeyList.add(foreignKey);
		
		when(primaryKey.getName()).thenReturn("pName");
		when(primaryKeys.getPrimaryKey()).thenReturn(primaryKeyList);
		when(table.getPrimaryKeys()).thenReturn(primaryKeys);
		when(foreignKey.getName()).thenReturn("fName");
		when(foreignKeys.getForeignKeyList()).thenReturn(foreignKeyList);
		when(table.getForeignKeys()).thenReturn(foreignKeys);
		when(column.getName()).thenReturn("cName");
		when(columns.getColumn()).thenReturn(columnList);
		when(table.getColumns()).thenReturn(columns);
		when(tables.getTableList()).thenReturn(tableList);
		when(database.getTables()).thenReturn(tables);
		cleaner.removeUnselectedColumns(database);
	}
	
}
