package com.helicalinsight.datasource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.JdbcDaoImpl;
import com.helicalinsight.datasource.managed.PlainJdbcConnectionProvider;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class JDBCDriverPreviewLimitTest {

	private static final String ORIGINAL = "select * from travel_details";
	private static final String WRAPPED = "select * from (select * from travel_details) foo limit 10";

	private JdbcDaoImpl jdbcDao;
	private JDBCDriver driver;
	private JsonObject connectionDetails;

	@Before
	public void setUp() throws Exception {
		jdbcDao = mock(JdbcDaoImpl.class);
		when(jdbcDao.query(any(), any())).thenReturn(new JsonObject());
		driver = new JDBCDriver();
		connectionDetails = new JsonObject();
		connectionDetails.addProperty("Url", "jdbc:h2:mem:preview");
		connectionDetails.addProperty("User", "sa");
		connectionDetails.addProperty("Pass", "");
		connectionDetails.addProperty("Driver", "org.h2.Driver");
	}

	@Test
	public void wrappedSqlIsIgnoredWithoutMaxRows() {
		JsonObject request = new JsonObject();
		request.addProperty("processedLimitQuery", WRAPPED);

		execute(request);

		verify(jdbcDao).query(any(), eq(ORIGINAL));
		verify(jdbcDao, never()).setLimit(any());
	}

	@Test
	public void statementLimitRunsTheOriginalSqlAndSetsMaxRows() {
		JsonObject request = new JsonObject();
		request.addProperty("maxRows", 25);
		request.addProperty("processedLimitQuery", ORIGINAL);

		execute(request);

		verify(jdbcDao).setLimit(25);
		verify(jdbcDao).query(any(), eq(ORIGINAL));
	}

	private void execute(JsonObject request) {
		EfwdQueryProcessor processor = mock(EfwdQueryProcessor.class);
		when(processor.getQuery2(any(), any())).thenReturn(ORIGINAL);
		PlainJdbcConnectionProvider connections = mock(PlainJdbcConnectionProvider.class);
		when(connections.newConnection(any())).thenReturn(mock(Connection.class));

		try (MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
			context.when(() -> ApplicationContextAccessor.getBean(EfwdQueryProcessor.class)).thenReturn(processor);
			context.when(() -> ApplicationContextAccessor.getBean(PlainJdbcConnectionProvider.class))
					.thenReturn(connections);
			context.when(() -> ApplicationContextAccessor.getBean(IJdbcDao.class)).thenReturn(jdbcDao);
			driver.getJSONData(request, connectionDetails, new JsonObject(), ApplicationProperties.getInstance());
		}
	}
}
