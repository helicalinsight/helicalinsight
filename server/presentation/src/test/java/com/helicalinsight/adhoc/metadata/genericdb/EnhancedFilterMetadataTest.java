package com.helicalinsight.adhoc.metadata.genericdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;

public class EnhancedFilterMetadataTest {

	@Test
	public void ut_a1_test_removeColumn() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Metadata metadata = mock(Metadata.class);
		EnhancedFilterMetadata enhancedFilterMetadata = new EnhancedFilterMetadata(metadata);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		List<Column> list = new ArrayList<>();
		list.add(column);
		
		when(column.getId()).thenReturn("id");
		when(table.getColumns()).thenReturn(columns);
		when(columns.getColumn()).thenReturn(list);
		List<String> columnExpressions = new ArrayList<>();
		columnExpressions.add("id");
		Method method = EnhancedFilterMetadata.class.getDeclaredMethod("removeColumn", Table.class, List.class);
		method.setAccessible(true);
		method.invoke(enhancedFilterMetadata, table, columnExpressions);

	}
}
