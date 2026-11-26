package com.helicalinsight.datasource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.JdbcServiceImpl;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.JsonUtils;

public class EfwdServicePreviewLimitTest {

	private static final String ORIGINAL = "select * from travel_details";
	private static final String WRAPPED = "select * from (select * from travel_details) foo limit 10";

	private EfwdService efwdService;
	private JdbcServiceImpl jdbcService;

	@Before
	public void setUp() throws Exception {
		efwdService = new EfwdService();
		EfwdQueryProcessor processor = mock(EfwdQueryProcessor.class);
		EfwdServiceUtility utility = mock(EfwdServiceUtility.class);
		jdbcService = mock(JdbcServiceImpl.class);
		when(processor.getQuery2(any(), any())).thenReturn(ORIGINAL);
		when(utility.getEfwd2(any())).thenReturn(new Efwd(1, "sql"));
		when(jdbcService.execute(any(), eq(1), any())).thenReturn(new JsonObject());
		set("efwdQueryProcessor", processor);
		set("efwdServiceUtility", utility);
		set("jdbcService", jdbcService);
	}

	@Test
	public void wrappedSqlIsIgnoredWhenLimitIsNull() {
		efwdService.setProcessedLimitQuery(WRAPPED);

		execute();

		verify(jdbcService).execute(any(), eq(1), eq(ORIGINAL));
		verify(jdbcService, never()).setLimit(any());
	}

	@Test
	public void statementLimitKeepsTheOriginalSql() {
		efwdService.setLimit(10);
		efwdService.setProcessedLimitQuery(ORIGINAL);

		execute();

		verify(jdbcService).setLimit(10);
		verify(jdbcService).execute(any(), eq(1), eq(ORIGINAL));
	}

	@Test
	public void unlimitedQueryRunsTheOriginalSql() {
		execute();

		verify(jdbcService).execute(any(), eq(1), eq(ORIGINAL));
		verify(jdbcService, never()).setLimit(any());
	}

	private void execute() {
		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
			jsonUtils.when(JsonUtils::getGlobalConnectionsPath).thenReturn("connections.xml");
			efwdService.execute("{}", "{\"type\":\"global.jdbc\"}", "{\"id\":1}",
					ApplicationProperties.getInstance());
		}
	}

	private void set(String name, Object value) throws Exception {
		java.lang.reflect.Field field = EfwdService.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(efwdService, value);
	}
}
