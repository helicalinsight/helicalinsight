package com.helicalinsight.datasource.managed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResultSetCacheTypeConversionTest {

	@Before
	public void setUp() {
		Map<String, String> map = new HashMap<>();
		map.put(FakeJsonNode.class.getName(), "java.lang.String");
		ResultSetCacheTypeConversion.configure(map);
	}

	@After
	public void tearDown() {
		ResultSetCacheTypeConversion.configure(Collections.emptyMap());
	}

	@Test
	public void convertForWrite_null_returnsNull() {
		assertNull(ResultSetCacheTypeConversion.convertForWrite(null));
	}

	@Test
	public void convertForWrite_mappedType_returnsString() {
		FakeJsonNode node = new FakeJsonNode("{\"a\":1}");
		Object converted = ResultSetCacheTypeConversion.convertForWrite(node);
		assertEquals("{\"a\":1}", converted);
		assertTrue(converted instanceof String);
	}

	@Test
	public void convertForWrite_unmappedType_returnsSameInstance() {
		Integer value = 42;
		assertSame(value, ResultSetCacheTypeConversion.convertForWrite(value));
	}

	@Test
	public void convertForWrite_mappedSuperclass_returnsString() {
		Map<String, String> map = new HashMap<>();
		map.put(FakeJsonNode.class.getName(), "java.lang.String");
		ResultSetCacheTypeConversion.configure(map);
		FakeJsonNode child = new FakeJsonNodeChild("{\"c\":1}");
		Object converted = ResultSetCacheTypeConversion.convertForWrite(child);
		assertEquals("{\"c\":1}", converted);
		assertTrue(converted instanceof String);
	}

	@Test
	public void convertForWrite_emptyMap_returnsSameInstance() {
		ResultSetCacheTypeConversion.configure(Collections.emptyMap());
		FakeJsonNode node = new FakeJsonNode("x");
		assertSame(node, ResultSetCacheTypeConversion.convertForWrite(node));
	}

	@Test
	public void convertForWrite_nullConfigure_clearsMap() {
		ResultSetCacheTypeConversion.configure(null);
		FakeJsonNode node = new FakeJsonNode("x");
		assertSame(node, ResultSetCacheTypeConversion.convertForWrite(node));
	}

	@Test
	public void wrapForWrite_getObjectByIndex_convertsMappedType() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject(1)).thenReturn(new FakeJsonNode("{\"k\":\"v\"}"));
		when(rs.getObject(2)).thenReturn(100);

		ResultSet wrapped = ResultSetCacheTypeConversion.wrapForWrite(rs);
		assertEquals("{\"k\":\"v\"}", wrapped.getObject(1));
		assertEquals(100, wrapped.getObject(2));
	}

	@Test
	public void wrapForWrite_getObjectByLabel_convertsMappedType() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject("meta")).thenReturn(new FakeJsonNode("[1,2]"));

		ResultSet wrapped = ResultSetCacheTypeConversion.wrapForWrite(rs);
		assertEquals("[1,2]", wrapped.getObject("meta"));
	}

	@Test
	public void wrapForWrite_next_delegates() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);

		ResultSet wrapped = ResultSetCacheTypeConversion.wrapForWrite(rs);
		assertTrue(wrapped.next());
		assertFalse(wrapped.next());
		verify(rs, times(2)).next();
	}

	@Test
	public void batchLimited_stopsAfterBatchSize() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, true, true, true);

		ResultSet limited = ResultSetCacheTypeConversion.batchLimited(rs, 2);
		assertTrue(limited.next());
		assertTrue(limited.next());
		assertFalse(limited.next());
		verify(rs, never()).close();
	}

	@Test
	public void batchLimited_close_doesNotCloseDelegate() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		ResultSet limited = ResultSetCacheTypeConversion.batchLimited(rs, 5);
		limited.close();
		verify(rs, never()).close();
	}

	@Test
	public void populate_withWrap_storesStringAndSerializes() throws Exception {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		when(rs.getMetaData()).thenReturn(meta);
		when(meta.getColumnCount()).thenReturn(2);
		when(meta.getColumnType(1)).thenReturn(Types.VARCHAR);
		when(meta.getColumnType(2)).thenReturn(Types.OTHER);
		when(meta.getColumnLabel(1)).thenReturn("id");
		when(meta.getColumnLabel(2)).thenReturn("meta");
		when(meta.getColumnName(1)).thenReturn("id");
		when(meta.getColumnName(2)).thenReturn("meta");
		when(meta.isNullable(1)).thenReturn(ResultSetMetaData.columnNullable);
		when(meta.isNullable(2)).thenReturn(ResultSetMetaData.columnNullable);
		when(meta.getPrecision(1)).thenReturn(0);
		when(meta.getPrecision(2)).thenReturn(0);
		when(meta.getScale(1)).thenReturn(0);
		when(meta.getScale(2)).thenReturn(0);
		when(rs.next()).thenReturn(true, false);
		when(rs.getObject(1)).thenReturn("row1");
		when(rs.getObject(2)).thenReturn(new FakeJsonNode("{\"ok\":true}"));

		CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
		crs.populate(ResultSetCacheTypeConversion.wrapForWrite(rs));
		assertTrue(crs.next());
		assertEquals("row1", crs.getObject(1));
		assertEquals("{\"ok\":true}", crs.getObject(2));
		assertTrue(crs.getObject(2) instanceof String);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(crs);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
			CachedRowSet restored = (CachedRowSet) ois.readObject();
			restored.beforeFirst();
			assertTrue(restored.next());
			assertEquals("{\"ok\":true}", restored.getObject(2));
		}
	}

	/** Stand-in for a non-serializable driver JSON type. */
	public static class FakeJsonNode {
		private final String json;

		public FakeJsonNode(String json) {
			this.json = json;
		}

		@Override
		public String toString() {
			return json;
		}
	}

	public static final class FakeJsonNodeChild extends FakeJsonNode {
		public FakeJsonNodeChild(String json) {
			super(json);
		}
	}
}
