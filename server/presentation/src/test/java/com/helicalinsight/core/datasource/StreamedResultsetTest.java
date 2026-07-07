package com.helicalinsight.core.datasource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.concurrent.StreamedResultset;
import com.helicalinsight.datasource.ChunkIterator;

public class StreamedResultsetTest {

    private StreamedResultset streamedResultset;

    private ChunkIterator<ResultSet> iterator;

    private ResultSet rs1;
    private ResultSet rs2;

    private ResultSetMetaData metaData;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {

        rs1 = mock(ResultSet.class);
        rs2 = mock(ResultSet.class);

        metaData = mock(ResultSetMetaData.class);

        iterator = mock(ChunkIterator.class);

        streamedResultset = spy(new StreamedResultset("dummy"));

        setField(streamedResultset, "iterator", iterator);
        setField(streamedResultset, "currentRs", rs1);
        setField(streamedResultset, "metaData", metaData);
    }

    @Test
    public void testNext_SingleResultSet() throws Exception {

        when(rs1.next())
                .thenReturn(true)
                .thenReturn(false);

        when(iterator.hasNext()).thenReturn(false);

        boolean first = streamedResultset.next();
        boolean second = streamedResultset.next();

        assertTrue(first);
        assertFalse(second);

        verify(rs1, times(2)).next();
        verify(rs1).close();
    }

    @Test
    public void testNext_MultipleChunks() throws Exception {

        when(rs1.next()).thenReturn(false);

        when(iterator.hasNext())
                .thenReturn(true)
                .thenReturn(false);

        when(iterator.next()).thenReturn(rs2);

        when(rs2.next())
                .thenReturn(true)
                .thenReturn(false);

        boolean first = streamedResultset.next();
        boolean second = streamedResultset.next();

        assertTrue(first);
        assertFalse(second);

        verify(rs1).close();
        verify(rs2).close();
    }

    @Test
    public void testNext_WhenNoChunksAvailable() throws Exception {

        setField(streamedResultset, "currentRs", null);

        when(iterator.hasNext()).thenReturn(false);

        boolean result = streamedResultset.next();

        assertFalse(result);
    }

    @Test
    public void testGetMetaData_FromCurrentResultSet() throws Exception {

        when(rs1.getMetaData()).thenReturn(metaData);

        ResultSetMetaData result = streamedResultset.getMetaData();

        assertEquals(metaData, result);

        verify(rs1).getMetaData();
    }

    @Test
    public void testGetMetaData_FromCachedMetadata() throws Exception {

        setField(streamedResultset, "currentRs", null);

        ResultSetMetaData result = streamedResultset.getMetaData();

        assertEquals(metaData, result);
    }

    @Test
    public void testNext_WhenExceptionOccurs() throws Exception {

        when(rs1.next()).thenThrow(new SQLException("DB error"));

        boolean result = streamedResultset.next();

        assertFalse(result);
    }

    @Test
    public void testCurrentResultSetBecomesNullAfterClose() throws Exception {

        when(rs1.next()).thenReturn(false);
        when(iterator.hasNext()).thenReturn(false);

        streamedResultset.next();

        verify(rs1).close();

        Object current =
                getField(streamedResultset, "currentRs");

        assertNull(current);
    }

    /**
     * Reflection utility to set private fields.
     */
    private void setField(Object target,
                          String fieldName,
                          Object value) throws Exception {

        Field field =
                target.getClass().getDeclaredField(fieldName);

        field.setAccessible(true);

        field.set(target, value);
    }

    /**
     * Reflection utility to read private fields.
     */
    private Object getField(Object target,
                            String fieldName) throws Exception {

        Field field =
                target.getClass().getDeclaredField(fieldName);

        field.setAccessible(true);

        return field.get(target);
    }
}