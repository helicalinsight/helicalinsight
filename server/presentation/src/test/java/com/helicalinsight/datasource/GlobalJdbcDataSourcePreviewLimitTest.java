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
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;

public class GlobalJdbcDataSourcePreviewLimitTest {

	private static final String ORIGINAL = "select * from travel_details";
	private static final String WRAPPED = "select * from (select * from travel_details) foo limit 10";

	private EfwdService efwdService;
	private JdbcServiceImpl jdbcService;
	private GlobalJdbcDataSource dataSource;

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
		dataSource = new GlobalJdbcDataSource();
	}

	@Test
	public void wrappedSqlIsIgnoredWithoutMaxRows() {
		JsonObject request = new JsonObject();
		request.addProperty("processedLimitQuery", WRAPPED);

		execute(request);

		verify(jdbcService).execute(any(), eq(1), eq(ORIGINAL));
		verify(jdbcService, never()).setLimit(any());
	}

	@Test
	public void statementLimitRunsTheOriginalSqlAndSetsMaxRows() {
		JsonObject request = new JsonObject();
		request.addProperty("maxRows", 10);
		request.addProperty("processedLimitQuery", ORIGINAL);

		execute(request);

		verify(jdbcService).setLimit(10);
		verify(jdbcService).execute(any(), eq(1), eq(ORIGINAL));
	}

	private void execute(JsonObject request) {
		try (MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
			context.when(() -> ApplicationContextAccessor.getBean(IEfwdService.class)).thenReturn(efwdService);
			jsonUtils.when(JsonUtils::getGlobalConnectionsPath).thenReturn("connections.xml");
			dataSource.getJSONData(request, new JsonObject(), new JsonObject(), ApplicationProperties.getInstance());
		}
	}

	private void set(String name, Object value) throws Exception {
		java.lang.reflect.Field field = EfwdService.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(efwdService, value);
	}
}
