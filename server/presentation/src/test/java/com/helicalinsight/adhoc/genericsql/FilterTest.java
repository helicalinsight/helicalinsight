package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FilterTest {

	@Test
	public void ut_a1_test_equals() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		assertTrue(filter.equals(filter));

	}

	@Test
	public void ut_a2_test_NullObject() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		assertFalse(filter.equals(null));
	}

	@Test
	public void ut_a3_test_DifferentClass() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		assertFalse(filter.equals(new Object()));
	}

	@Test
	public void ut_a4_test_SameValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		assertTrue(filter1.equals(filter2));
	}

	@Test
	public void ut_a5_test_DifferentValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column2", "condition2", false, "dataType2", "custom2",
				Arrays.asList("value3", "value4"), false, context, false);
		assertFalse(filter1.equals(filter2));
	}

	@Test
	public void ut_a6_test_NullFields() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter(null, null, true, null, null, null, true, context, true);
		Filter filter2 = new Filter(null, null, true, null, null, null, true, context, true);
		assertTrue(filter1.equals(filter2));
	}

	@Test
	public void ut_a7_test_NullFieldsInOneObject() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column1", null, true, "dataType1", null, Arrays.asList("value1", "value2"), true,
				context, true);
		assertFalse(filter1.equals(filter2));
	}

	@Test
	public void ut_a8_testHashCode_SameValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		assertEquals(filter1.hashCode(), filter2.hashCode());
	}

	@Test
	public void ut_a9_testHashCode_DifferentValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column2", "condition2", false, "dataType2", "custom2",
				Arrays.asList("value3", "value4"), false, context, false);
		assertNotEquals(filter1.hashCode(), filter2.hashCode());
	}

	@Test
	public void ut_b1_testHashCode_NullValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter(null, null, true, null, null, null, true, context, true);
		Filter filter2 = new Filter(null, null, true, null, null, null, true, context, true);
		assertEquals(filter1.hashCode(), filter2.hashCode());
	}

	@Test
	public void ut_b2_testHashCode_NullValuesInOneObject() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		Filter filter1 = new Filter("column1", "condition1", true, "dataType1", "custom1",
				Arrays.asList("value1", "value2"), true, context, true);
		Filter filter2 = new Filter("column1", null, true, "dataType1", null, Arrays.asList("value1", "value2"), true,
				context, true);
		assertNotEquals(filter1.hashCode(), filter2.hashCode());
	}

	@Test
	public void ut_b3_test_ConstructorAndGetters() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		List<String> values = Arrays.asList("value1", "value2");
		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1", values, true, context, true);

		assertEquals("column1", filter.getColumn());
		assertEquals("condition1", filter.getCondition());
		assertEquals("dataType1", filter.getDataType());
		assertEquals("custom1", filter.getCustom());
		assertEquals(values, filter.getValues());
	}

	@Test
	public void ut_b4_test_SettersAndGetters() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		List<String> values = Arrays.asList("value1", "value2");
		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1", values, true, context, true);
		filter.setColumn("testColumn");
		assertEquals("testColumn", filter.getColumn());

		filter.setCondition("testCondition");
		assertEquals("testCondition", filter.getCondition());

		filter.setDataType("testDataType");
		assertEquals("testDataType", filter.getDataType());

		filter.setCustom("testCustom");
		assertEquals("testCustom", filter.getCustom());
		
		filter.setIgnore(true);
		assertEquals(true, filter.isIgnore());
		
		List<String> values1 = Arrays.asList("value1", "value2");
		filter.setValues(values1);
		assertEquals(values1, filter.getValues());
	}

	@Test
	public void ut_b5_test_GetQuotedValues() {
		SqlQueryContext context = mock(SqlQueryContext.class);

		List<String> values = Arrays.asList("value1", "value2");
		Filter filter = new Filter("column1", "condition1", true, "dataType1", "custom1", values, true, context, true);
		
	    List<String> value = Arrays.asList("value1", "value2");
	    filter.setValues(value);
	    assertEquals("value1, value2", filter.getQuotedValues());
	}
	@Test
	public void ut_b6_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("value1", "value2");
		Filter filter = new Filter("column1", "in", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_b7_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("Null", "value2");
		Filter filter = new Filter("column1", "in", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_b8_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = null;
		Filter filter = new Filter("column1", "in", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_b9_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = null;
		Filter filter = new Filter("column1", "=", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_c1_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("Null", "value2");
		Filter filter = new Filter("column1", "=", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_c2_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("Null");
		Filter filter = new Filter("column1", "=", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_c3_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("value1");
		Filter filter = new Filter("column1", "=", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test(expected = QueryBuilderException.class)
	public void ut_c4_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("value1");
		Filter filter = new Filter("column1", "inRange", false, "dataType1", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	
	@Test
	public void ut_c5_test_toString() {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("value1","values2","value3");
		Filter filter = new Filter("column1", "inRange", false, "java.math.BigDecimal", "custom1", values, true, context, true);
		
		when(context.quotes(anyString())).thenReturn("filter");
		
		filter.toString();
	}
	@Test
	public void ut_c6_test_replace_all_() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SqlQueryContext context = mock(SqlQueryContext.class);
		List<String> values = Arrays.asList("value1","values2","value3");
		Filter filter = new Filter("column1", "<>", false, "java.math.BigDecimal", "custom1", values, true, context, true);
		
		Method method = Filter.class.getDeclaredMethod("replace_all_", Boolean.class);
		method.setAccessible(true);
		method.invoke(filter, true);
	}
}
