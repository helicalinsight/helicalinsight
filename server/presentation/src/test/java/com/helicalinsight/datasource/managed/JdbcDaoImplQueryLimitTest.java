package com.helicalinsight.datasource.managed;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

public class JdbcDaoImplQueryLimitTest {

	private JdbcDaoImpl jdbcDao;
	private TaskExecutorService taskExecutorService;
	private int savedTimeout;

	@Before
	public void setUp() throws Exception {
		savedTimeout = ApplicationProperties.getInstance().getJdbcQueryCancellationTime();
		ApplicationProperties.getInstance().setJdbcQueryCancellationTime(30);

		jdbcDao = new JdbcDaoImpl();
		taskExecutorService = mock(TaskExecutorService.class);
		java.lang.reflect.Field field = JdbcDaoImpl.class.getDeclaredField("taskExecutorService");
		field.setAccessible(true);
		field.set(jdbcDao, taskExecutorService);
		when(taskExecutorService.submit(any(java.util.concurrent.Callable.class), any()))
				.thenAnswer(invocation -> CompletableFuture.completedFuture(new JsonObject()));
	}

	@After
	public void tearDown() {
		ApplicationProperties.getInstance().setJdbcQueryCancellationTime(savedTimeout);
	}

	@Test
	public void queryAppliesMaxRowsAndFetchSizeOnce() throws Exception {
		Connection connection = connection();
		Statement statement = mock(Statement.class);
		when(connection.createStatement()).thenReturn(statement);

		jdbcDao.setLimit(10);
		jdbcDao.query(connection, "select * from travel_details");

		verify(statement).setMaxRows(10);
		verify(statement).setFetchSize(10);
	}

	@Test
	public void queryWithoutLimitDoesNotCapTheStatement() throws Exception {
		Connection connection = connection();
		Statement statement = mock(Statement.class);
		when(connection.createStatement()).thenReturn(statement);

		jdbcDao.query(connection, "select * from travel_details");

		verify(statement, never()).setMaxRows(anyInt());
		verify(statement, never()).setFetchSize(anyInt());
	}

	@Test
	public void limitStaysForTheNextQuery() throws Exception {
		Connection connection = connection();
		Statement statement = mock(Statement.class);
		when(connection.createStatement()).thenReturn(statement);

		jdbcDao.setLimit(10);
		jdbcDao.query(connection, "select * from travel_details");
		jdbcDao.query(connection, "select * from travel_details");

		verify(statement, times(2)).setMaxRows(10);
		verify(statement, times(2)).setFetchSize(10);
	}

	@Test
	public void unsupportedMaxRowsFailsTheQuery() throws Exception {
		Connection connection = connection();
		Statement statement = mock(Statement.class);
		when(connection.createStatement()).thenReturn(statement);
		doThrow(new SQLFeatureNotSupportedException("max rows")).when(statement).setMaxRows(10);

		jdbcDao.setLimit(10);
		try {
			jdbcDao.query(connection, "select * from travel_details");
			fail("Expected QueryException");
		} catch (QueryException ex) {
			assertTrue(ex.getCause() instanceof SQLFeatureNotSupportedException);
		}
		verify(statement, never()).setFetchSize(anyInt());
	}

	private Connection connection() throws Exception {
		Connection connection = mock(Connection.class);
		when(connection.getClientInfo()).thenReturn(new Properties());
		return connection;
	}
}
