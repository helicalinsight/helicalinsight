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
public class JoinClauseTest {

	@Test
	public void ut_a1_testEquals_SameObject() {
		StringBuilder current = new StringBuilder("current");
		StringBuilder previous = new StringBuilder("previous");
		StringBuilder joinType = new StringBuilder("joinType");
		StringBuilder on = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current, previous, joinType, on);
		assertTrue(clause1.equals(clause1));
	}

	@Test
	public void ut_a2_testEquals_NullObject() {
		StringBuilder current = new StringBuilder("current");
		StringBuilder previous = new StringBuilder("previous");
		StringBuilder joinType = new StringBuilder("joinType");
		StringBuilder on = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current, previous, joinType, on);
		assertFalse(clause1.equals(null));
	}

	@Test
	public void ut_a3_testEquals_DifferentClass() {
		StringBuilder current = new StringBuilder("current");
		StringBuilder previous = new StringBuilder("previous");
		StringBuilder joinType = new StringBuilder("joinType");
		StringBuilder on = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current, previous, joinType, on);
		assertFalse(clause1.equals(new Object()));
	}
	@Test
	public void ut_a4_testEquals_SameValues() {
		StringBuilder current1 = new StringBuilder("current");
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);

		StringBuilder current2 = new StringBuilder("current");
		StringBuilder previous2 = new StringBuilder("previous");
		StringBuilder joinType2 = new StringBuilder("joinType");
		StringBuilder on2 = new StringBuilder("on");
		JoinClause clause2 = new JoinClause(current2, previous2, joinType2, on2);

		assertFalse(clause1.equals(clause2));
	}
	@Test
	public void ut_a5_testEquals() {
		StringBuilder current1 = null;
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);

		StringBuilder current2 = null;
		StringBuilder previous2 = new StringBuilder("previous");
		StringBuilder joinType2 = new StringBuilder("joinType");
		StringBuilder on2 = new StringBuilder("on");
		JoinClause clause2 = new JoinClause(current2, previous2, joinType2, on2);

		assertFalse(clause1.equals(clause2));
	}

	@Test
	public void ut_a6_testEquals() {
		StringBuilder current1 = null;
		StringBuilder previous1 = null;
		StringBuilder joinType1 = new StringBuilder("joinType1");
		StringBuilder on1 = new StringBuilder("on1");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);

		StringBuilder current2 = null;
		StringBuilder previous2 = null;
		StringBuilder joinType2 = new StringBuilder("joinType2");
		StringBuilder on2 = new StringBuilder("on2");
		JoinClause clause2 = new JoinClause(current2, previous2, joinType2, on2);

		assertFalse(clause1.equals(clause2));
	}
	
	@Test
	public void ut_a7_testEquals() {
		StringBuilder current1 = null;
		StringBuilder previous1 = null;
		StringBuilder joinType1 =null;
		StringBuilder on1 = new StringBuilder("on1");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);

		StringBuilder current2 = null;
		StringBuilder previous2 = null;
		StringBuilder joinType2 = null;
		StringBuilder on2 = new StringBuilder("on2");
		JoinClause clause2 = new JoinClause(current2, previous2, joinType2, on2);

		assertFalse(clause1.equals(clause2));
	}
	
	@Test
    public void ut_a8_testHashCode_SameValues() {
        StringBuilder current = new StringBuilder("current");
        StringBuilder previous = new StringBuilder("previous");
        StringBuilder joinType = new StringBuilder("joinType");
        StringBuilder on = new StringBuilder("on");
        JoinClause clause1 = new JoinClause(current, previous, joinType, on);
        JoinClause clause2 = new JoinClause(current, previous, joinType, on);

        assertEquals(clause1.hashCode(), clause2.hashCode());
    }

    @Test
    public void ut_a9_testHashCode_DifferentValues() {
        StringBuilder current1 = new StringBuilder("current1");
        StringBuilder previous1 = new StringBuilder("previous1");
        StringBuilder joinType1 = new StringBuilder("joinType1");
        StringBuilder on1 = new StringBuilder("on1");
        JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);

        StringBuilder current2 = new StringBuilder("current2");
        StringBuilder previous2 = new StringBuilder("previous2");
        StringBuilder joinType2 = new StringBuilder("joinType2");
        StringBuilder on2 = new StringBuilder("on2");
        JoinClause clause2 = new JoinClause(current2, previous2, joinType2, on2);

        assertNotEquals(clause1.hashCode(), clause2.hashCode());
    }

    @Test
    public void ut_b1_testHashCode_NullValues() {
        JoinClause clause1 = new JoinClause(null, null, null, null);
        JoinClause clause2 = new JoinClause(null, null, null, null);

        assertEquals(clause1.hashCode(), clause2.hashCode());
    }
    @Test
    public void ut_b2_test_toString() {
    	StringBuilder current1 = new StringBuilder("current");
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);
		clause1.setFirstClause(true);
		String string = clause1.toString();
		assertNotNull(string);
    }
    
    @Test
    public void ut_b3_test_toString() {
    	StringBuilder current1 = new StringBuilder("current");
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);
		String string = clause1.toString();
		assertNotNull(string);

    }
    
    @Test
    public void ut_b4_test_toString() {
    	StringBuilder current1 = new StringBuilder("current");
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);
		clause1.addAndConditions(current1);
		clause1.addAndConditions(previous1);
		
		String string = clause1.toString();
		assertNotNull(string);

    }
    
    @Test
    public void ut_b5_test_toString() {
    	StringBuilder current1 = new StringBuilder("current");
		StringBuilder previous1 = new StringBuilder("previous");
		StringBuilder joinType1 = new StringBuilder("joinType");
		StringBuilder on1 = new StringBuilder("on");
		JoinClause clause1 = new JoinClause(current1, previous1, joinType1, on1);
		clause1.addAndConditions(current1);
		clause1.addAndConditions(previous1);
		clause1.setFirstClause(true);
		String string = clause1.toString();
		assertNotNull(string);
    }

}
