package com.helicalinsight.core.datasource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;

import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.concurrent.StreamedResultset;
import com.helicalinsight.datasource.ResultSetDataSourceFactory;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.jasperreports.engine.JRDataSource;

public class ResultSetDataSourceFactoryTest {

	private StreamedResultset streamedResultset;

	private ResultSet currentRs;

	private ResultSetDataSourceFactory factory;

	@Before
	public void setup() {

		streamedResultset = mock(StreamedResultset.class);

		currentRs = mock(ResultSet.class);

		factory = new ResultSetDataSourceFactory(streamedResultset);
	}

	@Test
	public void testCreate_WhenCurrentResultSetIsNull() throws Exception {

		when(streamedResultset.getCurrentResultSet()).thenReturn(null);

		when(streamedResultset.init()).thenReturn(currentRs);

		JRDataSource ds = factory.create();

		assertNotNull(ds);

		verify(streamedResultset).init();

		verify(currentRs).beforeFirst();
	}

	@Test
	public void testCreate_WhenCurrentResultSetExists_FirstUse() throws Exception {

		when(streamedResultset.getCurrentResultSet()).thenReturn(currentRs);

		JRDataSource ds = factory.create();

		assertNotNull(ds);

		verify(streamedResultset, never()).init();

		verify(currentRs).beforeFirst();
	}

	@Test
	public void testCreate_WhenAlreadyUsed() throws Exception {

		when(streamedResultset.getCurrentResultSet()).thenReturn(currentRs);

		when(streamedResultset.init()).thenReturn(currentRs);

		factory.create();

		JRDataSource ds = factory.create();

		assertNotNull(ds);

		verify(streamedResultset, times(1)).init();

		verify(currentRs, times(1)).beforeFirst();
	}

	@Test
	public void testCreate_WhenBeforeFirstThrowsException() throws Exception {

		when(streamedResultset.getCurrentResultSet()).thenReturn(currentRs);

		doThrow(new RuntimeException("DB Error")).when(currentRs).beforeFirst();

		try {

			factory.create();

			fail();

		} catch (EfwServiceException e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testCreate_WhenInitThrowsException() throws Exception {

		when(streamedResultset.getCurrentResultSet()).thenReturn(null);

		when(streamedResultset.init()).thenThrow(new RuntimeException("Init failed"));

		try {

			factory.create();

			fail();

		} catch (EfwServiceException e) {

			assertNotNull(e);
		}
	}

	@Test
	public void testConstructor() {

		ResultSet rs = mock(ResultSet.class);

		ResultSetDataSourceFactory obj = new ResultSetDataSourceFactory(rs);

		assertNotNull(obj);
	}
}