package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

	@Test
	public void derivesUsedColumnsFromSqlTextRawInColumns() {
		JsonObject formData = new JsonObject();
		JsonArray columns = new JsonArray();
		columns.add(sqlTextRawItem("sampletraveldata.public.travel_details.travel_cost", "1072", "display"));
		formData.add("columns", columns);

		CustomUsedColumnsDeriver.enrich(formData, travelCostMetadata());

		JsonArray usedColumns = columns.get(0).getAsJsonObject().getAsJsonArray("usedColumns");
		assertNotNull(usedColumns);
		assertTrue(usedColumns.toString().contains("travel_cost"));
		assertTrue(usedColumns.toString().contains("meet_cancellation_status"));
	}

	@Test
	public void derivesUsedColumnsFromSqlTextRawInFilters() {
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		JsonArray filters = new JsonArray();
		filters.add(sqlTextRawItem("sampletraveldata.public.travel_details.travel_cost", "1072", "display"));
		formData.add("filters", filters);

		CustomUsedColumnsDeriver.enrich(formData, travelCostMetadata());

		JsonArray usedColumns = filters.get(0).getAsJsonObject().getAsJsonArray("usedColumns");
		assertNotNull(usedColumns);
		assertTrue(usedColumns.toString().contains("travel_cost"));
		assertTrue(usedColumns.toString().contains("meet_cancellation_status"));
	}

	@Test
	public void derivesUsedColumnsFromSqlTextRawInHaving() {
		JsonObject formData = new JsonObject();
		formData.add("columns", new JsonArray());
		JsonArray having = new JsonArray();
		having.add(sqlTextRawItem("sampletraveldata.public.travel_details.travel_cost", "1072", "display"));
		formData.add("having", having);

		CustomUsedColumnsDeriver.enrich(formData, travelCostMetadata());

		JsonArray usedColumns = having.get(0).getAsJsonObject().getAsJsonArray("usedColumns");
		assertNotNull(usedColumns);
		assertTrue(usedColumns.toString().contains("travel_cost"));
		assertTrue(usedColumns.toString().contains("meet_cancellation_status"));
	}

	@Test
	public void mergesSqlTextRawUsedColumnsWithExisting() {
		JsonObject item = sqlTextRawItem("sampletraveldata.public.travel_details.travel_cost", "1072", "display");
		JsonArray existing = new JsonArray();
		existing.add("hi.travel_details.travel_cost");
		item.add("usedColumns", existing);

		JsonObject formData = new JsonObject();
		JsonArray columns = new JsonArray();
		columns.add(item);
		formData.add("columns", columns);

		CustomUsedColumnsDeriver.enrich(formData, travelCostMetadata());

		JsonArray usedColumns = item.getAsJsonArray("usedColumns");
		assertNotNull(usedColumns);
		assertTrue(usedColumns.toString().contains("travel_cost"));
		assertTrue(usedColumns.toString().contains("meet_cancellation_status"));
	}

	@Test
	public void ignoresNonRawDatabaseFunctionWithoutCustom() {
		JsonObject item = new JsonObject();
		JsonObject column = new JsonObject();
		column.addProperty("name", "hi.travel_details.travel_cost");
		column.addProperty("id", "1072");
		item.add("column", column);
		item.addProperty("alias", "display");
		JsonObject databaseFunction = new JsonObject();
		databaseFunction.addProperty("functionName", "sql.text.concat");
		databaseFunction.addProperty("dataType", "text");
		JsonObject parameters = new JsonObject();
		parameters.addProperty("column",
				"SUM(travel_details.travel_cost) FILTER(WHERE meeting_details.meet_cancellation_status = 'Yes')");
		databaseFunction.add("parameters", parameters);
		item.add("databaseFunction", databaseFunction);

		JsonObject formData = new JsonObject();
		JsonArray columns = new JsonArray();
		columns.add(item);
		formData.add("columns", columns);

		CustomUsedColumnsDeriver.enrich(formData, travelCostMetadata());

		assertNull(item.get("usedColumns"));
	}

	private static JsonObject sqlTextRawItem(String columnName, String columnId, String alias) {
		JsonObject item = new JsonObject();
		JsonObject column = new JsonObject();
		column.addProperty("name", columnName);
		column.addProperty("id", columnId);
		item.add("column", column);
		item.addProperty("alias", alias);
		JsonObject databaseFunction = new JsonObject();
		databaseFunction.addProperty("functionName", "sql.text.raw");
		databaseFunction.addProperty("dataType", "text");
		JsonObject parameters = new JsonObject();
		parameters.addProperty("column",
				"SUM(travel_details.travel_cost) FILTER(WHERE meeting_details.meet_cancellation_status = 'Yes')");
		databaseFunction.add("parameters", parameters);
		item.add("databaseFunction", databaseFunction);
		item.addProperty("floatingType", "discrete");
		return item;
	}

	private static Metadata travelCostMetadata() {
		Metadata metadata = new Metadata();
		Database database = new Database();
		database.setName("hi");
		Tables tables = new Tables();
		List<Table> tableList = new ArrayList<>();
		tableList.add(table("travel_details", "travel_cost"));
		tableList.add(table("meeting_details", "meet_cancellation_status"));
		tables.setTableList(tableList);
		database.setTables(tables);
		metadata.setDatabase(database);
		return metadata;
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
