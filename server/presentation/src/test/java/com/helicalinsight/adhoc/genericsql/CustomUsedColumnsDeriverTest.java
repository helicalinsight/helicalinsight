package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;

public class CustomUsedColumnsDeriverTest {

	@Test
	public void derivesUsedColumnsFromQuotedArithmeticAndParentheses() {
		JsonObject formData = new JsonObject();
		JsonArray columns = new JsonArray();
		JsonObject customColumn = new JsonObject();
		customColumn.addProperty("column", "sum(\"employee_id\"/\"travel_id\") - (\"destination_id\")");
		customColumn.addProperty("alias", "didbytid");
		customColumn.addProperty("custom", true);
		customColumn.addProperty("floatingType", "discrete");
		columns.add(customColumn);
		formData.add("columns", columns);

		CustomUsedColumnsDeriver.enrich(formData, travelMetadata());

		JsonArray usedColumns = customColumn.getAsJsonArray("usedColumns");
		assertNotNull(usedColumns);
		assertEquals(3, usedColumns.size());
		assertTrue(usedColumns.toString().contains("employee_id"));
		assertTrue(usedColumns.toString().contains("travel_id"));
		assertTrue(usedColumns.toString().contains("destination_id"));
	}

	@Test
	public void derivesUsedColumnsFromNestedCustomFilterNotInSelect() {
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());

		JsonArray filters = new JsonArray();
		JsonObject filter = new JsonObject();
		JsonObject nested = new JsonObject();
		nested.addProperty("column", "\"booking_platform\"");
		nested.addProperty("alias", "bk_pf");
		filter.add("column", nested);
		filter.addProperty("alias", "bk_pf");
		filter.addProperty("custom", true);
		filters.add(filter);
		formData.add("filters", filters);

		CustomUsedColumnsDeriver.enrich(formData, travelMetadataWithBooking());

		JsonArray filterUsed = filter.getAsJsonArray("usedColumns");
		assertNotNull(filterUsed);
		assertTrue(filterUsed.toString().contains("booking_platform"));
	}

	private static Metadata travelMetadata() {
		Metadata metadata = new Metadata();
		Database database = new Database();
		database.setName("hi");
		Tables tables = new Tables();
		List<Table> tableList = new ArrayList<>();
		tableList.add(table("employee", "employee_id"));
		tableList.add(table("travel", "travel_id", "destination_id"));
		tables.setTableList(tableList);
		database.setTables(tables);
		metadata.setDatabase(database);
		return metadata;
	}

	private static Metadata travelMetadataWithBooking() {
		Metadata metadata = travelMetadata();
		Table travel = metadata.getDatabase().getTables().getTableList().get(1);
		Column booking = new Column();
		booking.setName("booking_platform");
		booking.setOriginalName("booking_platform");
		booking.setAliasName("booking_platform");
		booking.setType("varchar");
		travel.getColumns().getColumn().add(booking);
		return metadata;
	}

	private static Table table(String tableName, String... columnNames) {
		Table table = new Table();
		table.setName(tableName);
		table.setAliasName(tableName);
		Columns columns = new Columns();
		List<Column> columnList = new ArrayList<>();
		for (String columnName : columnNames) {
			Column column = new Column();
			column.setName(columnName);
			column.setOriginalName(columnName);
			column.setAliasName(columnName);
			column.setType("integer");
			columnList.add(column);
		}
		columns.setColumn(columnList);
		table.setColumns(columns);
		return table;
	}
}
