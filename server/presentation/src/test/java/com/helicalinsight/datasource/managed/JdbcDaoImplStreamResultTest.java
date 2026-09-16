package com.helicalinsight.datasource.managed;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.admin.dto.ResultSetConfigDTO;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.parallelprocessor.TaskExecutorService;

public class JdbcDaoImplStreamResultTest {

	private JdbcDaoImpl jdbcDao;
	private TaskExecutorService taskExecutorService;
	private ResultSetConfigDTO savedConfig;
	private int savedTimeout;

	@Before
	public void setUp() throws Exception {
		ApplicationProperties props = ApplicationProperties.getInstance();
		savedConfig = props.getResultSetConfig();
		savedTimeout = props.getJdbcQueryCancellationTime();
		props.setResultSetConfig(new ResultSetConfigDTO(10, 100, Collections.emptyMap(), true));
		props.setJdbcQueryCancellationTime(30);

		jdbcDao = new JdbcDaoImpl();
		taskExecutorService = mock(TaskExecutorService.class);
		java.lang.reflect.Field field = JdbcDaoImpl.class.getDeclaredField("taskExecutorService");
		field.setAccessible(true);
		field.set(jdbcDao, taskExecutorService);
	}

	@After
	public void tearDown() {
		ApplicationProperties props = ApplicationProperties.getInstance();
		props.setResultSetConfig(savedConfig);
		props.setJdbcQueryCancellationTime(savedTimeout);
	}

	@Test
	public void cancelStatement_WithNullStatement_DoesNotThrow() throws Exception {
		Method cancel = JdbcDaoImpl.class.getDeclaredMethod("cancelStatement", Statement.class, Exception.class);
		cancel.setAccessible(true);
		cancel.invoke(jdbcDao, null, new SQLException("setup failed"));
	}

	@Test
	public void streamResult_WhenGetMetaDataFails_PropagatesQueryException_NotStatementIsClosedNpe()
			throws Exception {
		Connection connection = mock(Connection.class);
		when(connection.getMetaData()).thenThrow(new SQLException("driver metadata unavailable"));

		@SuppressWarnings("unchecked")
		CallBack<ResultSet> callBack = mock(CallBack.class);

		try {
			jdbcDao.streamResult(connection, "select 1", callBack);
			fail("Expected QueryException");
		} catch (QueryException ex) {
			assertTrue(ex.getCause() instanceof SQLException);
			assertFalse(containsMessage(ex, "isClosed"));
			assertFalse(ex.getCause() instanceof NullPointerException);
			assertTrue(ex.getCause().getMessage().contains("driver metadata unavailable"));
		}
	}

	@Test
	public void streamResult_WhenTypedCreateStatementFails_FallsBackToPlainCreateStatement() throws Exception {
		Connection connection = mock(Connection.class);
		DatabaseMetaData dbMeta = mock(DatabaseMetaData.class);
		Statement statement = mock(Statement.class);
		ResultSet resultSet = mock(ResultSet.class);
		ResultSetMetaData rsMeta = mock(ResultSetMetaData.class);

		when(connection.getMetaData()).thenReturn(dbMeta);
		when(dbMeta.getDatabaseProductName()).thenReturn("H2");
		when(connection.getClientInfo()).thenReturn(new Properties());
		doThrow(new SQLException("typed statement not supported")).when(connection)
				.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		when(connection.createStatement()).thenReturn(statement);
		when(statement.executeQuery(anyString())).thenReturn(resultSet);
		when(resultSet.getMetaData()).thenReturn(rsMeta);
		when(rsMeta.getColumnCount()).thenReturn(1);
		when(rsMeta.getColumnLabel(1)).thenReturn("brand");
		when(rsMeta.getColumnName(1)).thenReturn("brand");
		when(rsMeta.getColumnType(1)).thenReturn(Types.VARCHAR);
		when(rsMeta.isNullable(1)).thenReturn(ResultSetMetaData.columnNullable);
		when(rsMeta.getPrecision(1)).thenReturn(255);
		when(rsMeta.getScale(1)).thenReturn(0);
		when(resultSet.next()).thenReturn(false);

		when(taskExecutorService.submit(any(Runnable.class), anyString())).thenAnswer(invocation -> {
			Runnable runnable = invocation.getArgument(0);
			runnable.run();
			return CompletableFuture.completedFuture(null);
		});

		@SuppressWarnings("unchecked")
		CallBack<ResultSet> callBack = mock(CallBack.class);

		jdbcDao.streamResult(connection, "select brand from products group by brand", callBack);

		verify(connection).createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		verify(connection).createStatement();
		verify(statement).setFetchSize(anyInt());
		verify(callBack).process(any(ResultSet.class));
	}

	private static boolean containsMessage(Throwable t, String fragment) {
		while (t != null) {
			if (t.getMessage() != null && t.getMessage().contains(fragment)) {
				return true;
			}
			t = t.getCause();
		}
		return false;
	}
}
