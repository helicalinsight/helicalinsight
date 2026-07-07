package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AggregateTest {

	
	@Test
    public void ut_a1_testEquals_SameProperties() {
        Aggregate aggregate1 = new Aggregate();
        aggregate1.setColumn("column1");
        aggregate1.setAlias("alias1");
        aggregate1.setFunction("function1");
        aggregate1.setCustom("custom1");
        aggregate1.setApplyBeforeAggregate(true);

        Aggregate aggregate2 = new Aggregate();
        aggregate2.setColumn("column1");
        aggregate2.setAlias("alias1");
        aggregate2.setFunction("function1");
        aggregate2.setCustom("custom1");
        aggregate2.setApplyBeforeAggregate(true);

        assertTrue(aggregate1.equals(aggregate2));
    }

    @Test
    public void ut_a2_testEquals_DifferentProperties() {
        Aggregate aggregate1 = new Aggregate();
        aggregate1.setColumn("column1");
        aggregate1.setAlias(null);
        aggregate1.setFunction("function1");
        aggregate1.setCustom("custom1");
        aggregate1.setApplyBeforeAggregate(true);

        Aggregate aggregate2 = new Aggregate();
        aggregate2.setColumn("column2");
        aggregate2.setAlias(null);
        aggregate2.setFunction("function2");
        aggregate2.setCustom("custom2");
        aggregate2.setApplyBeforeAggregate(false);

        assertFalse(aggregate1.equals(aggregate2));
    }

    @Test
    public void ut_a3_testEquals_DifferentType() {
        Aggregate aggregate = new Aggregate();
        assertFalse(aggregate.equals("not an Aggregate object"));
    }

    @Test
    public void ut_a4_testEquals_WithNull() {
        Aggregate aggregate = new Aggregate();
        assertFalse(aggregate.equals(null));
    }
    
    @Test
    public void ut_a5_testHashCode_SameProperties() {
        Aggregate aggregate1 = new Aggregate();
        aggregate1.setColumn("column1");
        aggregate1.setAlias("alias1");
        aggregate1.setFunction("function1");
        aggregate1.setCustom("custom1");
        aggregate1.setApplyBeforeAggregate(true);

        Aggregate aggregate2 = new Aggregate();
        aggregate2.setColumn("column1");
        aggregate2.setAlias("alias1");
        aggregate2.setFunction("function1");
        aggregate2.setCustom("custom1");
        aggregate2.setApplyBeforeAggregate(true);

        assertEquals(aggregate1.hashCode(), aggregate2.hashCode());
    }

    @Test
    public void ut_a5_testHashCode_DifferentProperties() {
        Aggregate aggregate1 = new Aggregate();
        aggregate1.setColumn("column1");
        aggregate1.setAlias("alias1");
        aggregate1.setFunction("function1");
        aggregate1.setCustom("custom1");
        aggregate1.setApplyBeforeAggregate(true);

        Aggregate aggregate2 = new Aggregate();
        aggregate2.setColumn("column2");
        aggregate2.setAlias("alias2");
        aggregate2.setFunction("function2");
        aggregate2.setCustom("custom2");
        aggregate2.setApplyBeforeAggregate(false);

        assertNotEquals(aggregate1.hashCode(), aggregate2.hashCode());
        assertEquals("custom2",aggregate2.getCustom());
        assertEquals("alias2",aggregate2.getAlias());
        assertEquals("function2",aggregate2.getFunction());
        assertEquals("column2",aggregate2.getColumn());
        assertEquals(false,aggregate2.getApplyBeforeAggregate());
        
        String string = aggregate2.toString();
        assertNotNull(string);
        
    }
}
