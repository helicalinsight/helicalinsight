package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RollupHandlerTest {

	private static final String BASE_QUERY =
			"select booking_platform, sum(travel_cost) as sum_travel_cost "
					+ "from travel_details "
					+ "group by booking_platform "
					+ "limit 1000";

	private static final String POSTGRES_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
	private static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQLDialect";

	@Test
	public void ut_a1_testAnsiRollupUsesRollupFunction() {
		SqlQueryContext context = mockContext(POSTGRES_DIALECT, true);

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<AuthenticationUtils> authUtils = mockAuth()) {
			jsonUtils.when(JsonUtils::newGetAdhocSqlSettings).thenReturn(adhocSqlSettings());

			String result = new RollupHandler(BASE_QUERY, context).applyRollup();

			System.out.println("=== ANSI rollup (PostgreSQL via JSQLParser) ===");
			System.out.println("Input:");
			System.out.println(BASE_QUERY);
			System.out.println("Output:");
			System.out.println(result);
			System.out.println();

			assertTrue(result.toUpperCase().contains("ROLLUP"));
			assertTrue(result.toUpperCase().contains("GROUP BY"));
			assertFalse(result.toUpperCase().contains("WITH ROLLUP"));
			assertTrue(result.toUpperCase().contains("ORDER BY"));
		}
	}

	@Test
	public void ut_a2_testNonAnsiRollupUsesWithRollup() {
		SqlQueryContext context = mockContext(MYSQL_DIALECT, false);

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<AuthenticationUtils> authUtils = mockAuth()) {
			jsonUtils.when(JsonUtils::newGetAdhocSqlSettings).thenReturn(adhocSqlSettings());

			String result = new RollupHandler(BASE_QUERY, context).applyRollup();

			System.out.println("=== Non-ANSI rollup (MySQL via JSQLParser) ===");
			System.out.println("Input:");
			System.out.println(BASE_QUERY);
			System.out.println("Output:");
			System.out.println(result);
			System.out.println();

			assertTrue(result.toUpperCase().contains("WITH ROLLUP"));
			assertTrue(result.toUpperCase().contains("ORDER BY"));
		}
	}

	@Test
	public void ut_a3_testAnsiAndNonAnsiSideBySide() {
		SqlQueryContext ansiContext = mockContext(POSTGRES_DIALECT, true);
		SqlQueryContext nonAnsiContext = mockContext(MYSQL_DIALECT, false);

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class);
				MockedStatic<AuthenticationUtils> authUtils = mockAuth()) {
			jsonUtils.when(JsonUtils::newGetAdhocSqlSettings).thenReturn(adhocSqlSettings());

			String ansiResult = new RollupHandler(BASE_QUERY, ansiContext).applyRollup();
			String nonAnsiResult = new RollupHandler(BASE_QUERY, nonAnsiContext).applyRollup();

			System.out.println("=== Side-by-side ANSI vs Non-ANSI (JSQLParser) ===");
			System.out.println("--- ANSI ---");
			System.out.println(ansiResult);
			System.out.println("--- Non-ANSI ---");
			System.out.println(nonAnsiResult);
			System.out.println();

			assertTrue(ansiResult.toUpperCase().contains("ROLLUP(") || ansiResult.toUpperCase().contains("ROLLUP ("));
			assertTrue(nonAnsiResult.toUpperCase().contains("WITH ROLLUP"));
		}
	}

	private static MockedStatic<AuthenticationUtils> mockAuth() {
		MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class);
		Principal principal = mock(Principal.class);
		User user = mock(User.class);
		when(user.getUsername()).thenReturn("testuser");
		when(principal.getLoggedInUser()).thenReturn(user);
		authUtils.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
		authUtils.when(AuthenticationUtils::getOrganization).thenReturn(null);
		return authUtils;
	}

	private static SqlQueryContext mockContext(String dialect, boolean strictAnsiQuotes) {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Metadata metadata = mock(Metadata.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);

		JsonObject formData = sampleFormData();

		when(context.getDriverClassName()).thenReturn(dialect.contains("MySQL") ? "com.mysql.jdbc.Driver" : "org.postgresql.Driver");
		when(context.getQueryOffset()).thenReturn("0");
		when(context.getQueryLimit()).thenReturn("1000");
		when(context.getReferenceFile()).thenReturn(dialect.contains("MySQL") ? "mysql" : "postgresql");
		when(context.getFormData()).thenReturn(formData);
		when(context.getMetadata()).thenReturn(metadata);
		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDialect()).thenReturn(dialect);

		when(context.getNameAndFullNameMap()).thenReturn(Collections.emptyMap());
		when(context.getColumnsMap()).thenReturn(Collections.emptyMap());
		when(context.getRequestedTables()).thenReturn(Collections.emptyList());
		when(context.getGraphNodes()).thenReturn(Collections.emptyList());
		when(context.getTables()).thenReturn(Collections.emptyList());
		when(context.getTableAliasMap()).thenReturn(Collections.emptyMap());
		when(context.getDerivedTableNames()).thenReturn(Collections.emptyList());
		when(context.getDerivedTableColumns()).thenReturn(Collections.emptyList());
		when(context.getDatabaseName()).thenReturn("sampletraveldata.public");
		when(context.isDistinctResults()).thenReturn(false);
		when(context.isApplyWhere()).thenReturn(false);
		when(context.isLimitRequested()).thenReturn(true);
		when(context.isApplyAggregation()).thenReturn(true);
		when(context.isApplyGroupBy()).thenReturn(true);
		when(context.isApplyOrderBy()).thenReturn(false);
		when(context.isApplyHaving()).thenReturn(false);
		when(context.isApplyRollup()).thenReturn(true);
		when(context.getOpenQuote()).thenReturn(strictAnsiQuotes ? "\"" : "`");
		when(context.getCloseQuote()).thenReturn(strictAnsiQuotes ? "\"" : "`");
		when(context.hasParenthesis(anyString())).thenReturn(false);
		when(context.doApplyQuotes(anyString())).thenAnswer(invocation -> {
			String value = invocation.getArgument(0);
			String quote = strictAnsiQuotes ? "\"" : "`";
			return quote + value + quote;
		});
		when(context.quotes(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		return context;
	}

	private static JsonObject sampleFormData() {
		JsonObject formData = new JsonObject();
		JsonObject functions = new JsonObject();
		JsonArray groupBy = new JsonArray();
		JsonObject groupByColumn = new JsonObject();
		groupByColumn.addProperty("column", "booking_platform");
		groupByColumn.addProperty("custom", true);
		groupBy.add(groupByColumn);
		functions.add("groupBy", groupBy);
		formData.add("functions", functions);

		JsonArray analytics = new JsonArray();
		JsonObject instruction = new JsonObject();
		instruction.addProperty("subTotals", true);
		analytics.add(instruction);
		formData.add("analytics", analytics);
		return formData;
	}

	private static JsonObject adhocSqlSettings() {
		JsonObject settings = new JsonObject();
		JsonObject ansiGroupByDialects = new JsonObject();
		JsonArray dialects = new JsonArray();

		JsonObject postgres = new JsonObject();
		postgres.addProperty("rollup", "true");
		postgres.addProperty("", POSTGRES_DIALECT);
		dialects.add(postgres);

		ansiGroupByDialects.add("dialect", dialects);
		settings.add("ansiGroupByDialects", ansiGroupByDialects);
		return settings;
	}
}
