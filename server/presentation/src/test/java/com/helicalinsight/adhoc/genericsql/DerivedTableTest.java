package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DerivedTableTest {

	@Test
	public void ut_a1_testEquals_SameProperties() {
		DerivedTable table1 = new DerivedTable("name1", "alias1", "query1", "type1");
		DerivedTable table2 = new DerivedTable("name1", "alias1", "query1", "type1");

		assertTrue(table1.equals(table2));
	}

	@Test
	public void ut_a2_testEquals_DifferentProperties() {
		DerivedTable table1 = new DerivedTable("name1", "alias1", "query1", "type1");
		DerivedTable table2 = new DerivedTable("name2", "alias2", "query2", "type2");

		assertFalse(table1.equals(table2));
	}

	@Test
	public void ut_a3_testEquals_WithNull() {
		DerivedTable table = new DerivedTable("name", "alias", "query", "type");
		assertFalse(table.equals(null));
	}

	@Test
	public void ut_a4_testEquals_WithDifferentType() {
		DerivedTable table = new DerivedTable("name", "alias", "query", "type");
		assertFalse(table.equals("not a DerivedTable object"));
	}

	@Test
	public void ut_a5_testHashCode_SameProperties() {
		DerivedTable table1 = new DerivedTable("name1", "alias1", "query1", "type1");
		DerivedTable table2 = new DerivedTable("name1", "alias1", "query1", "type1");

		assertEquals(table1.hashCode(), table2.hashCode());
	}

	@Test
	public void ut_a6_testHashCode_DifferentProperties() {
		DerivedTable table1 = new DerivedTable("name1", "alias1", "query1", "type1");
		DerivedTable table2 = new DerivedTable("name2", "alias2", "query2", "type2");

		assertNotEquals(table1.hashCode(), table2.hashCode());
	}

	@Test
	public void ut_a7_test_ToString() {
		DerivedTable table = new DerivedTable("name", "alias", "query", "type");

		String expectedToString = "DerivedTable{name='name', aliasName='alias', query='query', type='type'}";
		assertEquals(expectedToString, table.toString());
	}

	@Test
	public void ut_a8_test_SetterGetter() {
		DerivedTable table = new DerivedTable("name", "alias", "query", "type");

		assertEquals("name", table.getName());

		assertEquals("alias", table.getAliasName());

		assertEquals("query", table.getQuery());

		assertEquals("type", table.getType());
	}
}
