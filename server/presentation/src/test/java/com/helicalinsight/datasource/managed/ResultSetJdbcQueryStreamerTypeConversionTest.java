package com.helicalinsight.datasource.managed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Verifies stream-style chunked populate uses the same wrap + batchLimited path.
 */
public class ResultSetJdbcQueryStreamerTypeConversionTest {

	@Before
	public void setUp() {
		Map<String, String> map = new HashMap<>();
		map.put(ResultSetCacheTypeConversionTest.FakeJsonNode.class.getName(), "java.lang.String");
		ResultSetCacheTypeConversion.configure(map);
	}

	@After
	public void tearDown() {
		ResultSetCacheTypeConversion.configure(Collections.emptyMap());
	}

	@Test
	public void chunkedPopulate_convertsMappedCellsAndUsesPopulate() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		when(rs.getMetaData()).thenReturn(meta);
		when(meta.getColumnCount()).thenReturn(1);
		when(meta.getColumnType(1)).thenReturn(Types.OTHER);
		when(meta.getColumnLabel(1)).thenReturn("meta");
		when(meta.getColumnName(1)).thenReturn("meta");
		when(meta.isNullable(1)).thenReturn(ResultSetMetaData.columnNullable);
		when(meta.getPrecision(1)).thenReturn(0);
		when(meta.getScale(1)).thenReturn(0);
		when(rs.next()).thenReturn(true, true, true, false);
		when(rs.getObject(1)).thenReturn(
				new ResultSetCacheTypeConversionTest.FakeJsonNode("{\"n\":1}"),
				new ResultSetCacheTypeConversionTest.FakeJsonNode("{\"n\":2}"),
				new ResultSetCacheTypeConversionTest.FakeJsonNode("{\"n\":3}"));

		ResultSet view = ResultSetCacheTypeConversion.wrapForWrite(rs);
		List<CachedRowSet> batches = new ArrayList<>();
		int batchSize = 2;
		boolean first = true;
		while (true) {
			CachedRowSet batch = RowSetProvider.newFactory().createCachedRowSet();
			batch.populate(ResultSetCacheTypeConversion.batchLimited(view, batchSize));
			int size = batch.size();
			if (size == 0) {
				if (first) {
					batches.add(batch);
				}
				break;
			}
			first = false;
			batches.add(batch);
			if (size < batchSize) {
				break;
			}
		}

		assertEquals(2, batches.size());
		assertEquals(2, batches.get(0).size());
		assertEquals(1, batches.get(1).size());

		batches.get(0).beforeFirst();
		assertTrue(batches.get(0).next());
		assertEquals("{\"n\":1}", batches.get(0).getObject(1));
		assertTrue(batches.get(0).getObject(1) instanceof String);
		assertTrue(batches.get(0).next());
		assertEquals("{\"n\":2}", batches.get(0).getObject(1));

		batches.get(1).beforeFirst();
		assertTrue(batches.get(1).next());
		assertEquals("{\"n\":3}", batches.get(1).getObject(1));
		assertFalse(batches.get(1).next());
	}

	@Test
	public void chunkedPopulate_emptyResult_yieldsEmptyBatch() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		when(rs.getMetaData()).thenReturn(meta);
		when(meta.getColumnCount()).thenReturn(1);
		when(meta.getColumnType(1)).thenReturn(Types.VARCHAR);
		when(meta.getColumnLabel(1)).thenReturn("c");
		when(meta.getColumnName(1)).thenReturn("c");
		when(meta.isNullable(1)).thenReturn(ResultSetMetaData.columnNullable);
		when(meta.getPrecision(1)).thenReturn(0);
		when(meta.getScale(1)).thenReturn(0);
		when(rs.next()).thenReturn(false);

		ResultSet view = ResultSetCacheTypeConversion.wrapForWrite(rs);
		CachedRowSet batch = RowSetProvider.newFactory().createCachedRowSet();
		batch.populate(ResultSetCacheTypeConversion.batchLimited(view, 10));
		assertEquals(0, batch.size());
	}
}
