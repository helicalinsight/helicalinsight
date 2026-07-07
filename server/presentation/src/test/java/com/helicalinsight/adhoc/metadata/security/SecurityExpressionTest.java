package com.helicalinsight.adhoc.metadata.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityExpressionTest {

	 private SecurityExpression expression;

	    @Before
	    public void setUp() {
	        expression = new SecurityExpression();
	    }

	    @Test
	    public void ut_a1_test_GettersAndSetters() {
	        expression.setId("1");
	        assertEquals("1", expression.getId());

	        expression.setType("Type");
	        assertEquals("Type", expression.getType());

	        expression.setExpressionName("ExpressionName");
	        assertEquals("ExpressionName", expression.getExpressionName());

	        expression.setExpressionType("ExpressionType");
	        assertEquals("ExpressionType", expression.getExpressionType());

	        expression.setOn("On");
	        assertEquals("On", expression.getOn());

	        expression.setAccessType("AccessType");
	        assertEquals("AccessType", expression.getAccessType());

	        expression.setCondition("Condition");
	        assertEquals("Condition", expression.getCondition());

	        expression.setFilter("Filter");
	        assertEquals("Filter", expression.getFilter());
	    }

	    @Test
	    public void ut_a2_test_ToString() {
	        expression.setId("1");
	        expression.setType("Type");
	        expression.setExpressionName("ExpressionName");
	        expression.setExpressionType("ExpressionType");
	        expression.setOn("On");
	        expression.setAccessType("AccessType");
	        expression.setCondition("Condition");
	        expression.setFilter("Filter");

	        String expectedToString = "SecurityExpression{" +
	                "id='1', " +
	                "type='Type', " +
	                "expressionName='ExpressionName', " +
	                "expressionType='ExpressionType', " +
	                "on='On', " +
	                "accessType='AccessType', " +
	                "condition='Condition', " +
	                "filter='Filter'" +
	                "}";
	        assertEquals(expectedToString, expression.toString());
	    }

	    @Test
	    public void ut_a3_test_EqualsAndHashCode() {
	        SecurityExpression expression1 = new SecurityExpression();
	        expression1.setId("1");
	        expression1.setType("Type");
	        expression1.setExpressionName("ExpressionName");
	        expression1.setExpressionType("ExpressionType");
	        expression1.setOn("On");
	        expression1.setAccessType("AccessType");
	        expression1.setCondition("Condition");
	        expression1.setFilter("Filter");

	        SecurityExpression expression2 = new SecurityExpression();
	        expression2.setId("1");
	        expression2.setType("Type");
	        expression2.setExpressionName("ExpressionName");
	        expression2.setExpressionType("ExpressionType");
	        expression2.setOn("On");
	        expression2.setAccessType("AccessType");
	        expression2.setCondition("Condition");
	        expression2.setFilter("Filter");

	        assertTrue(expression1.equals(expression2));
	        assertTrue(expression2.equals(expression1));
	        assertEquals(expression1.hashCode(), expression2.hashCode());
	    }
	    
	    @Test
	    public void ut_a4_test_EqualsAndHashCode() {
	        SecurityExpression expression1 = new SecurityExpression();
	        expression1.setId("1");
	        expression1.setType("Type");
	        expression1.setExpressionName(null);
	        expression1.setExpressionType("ExpressionType");
	        expression1.setOn("On");
	        expression1.setAccessType("AccessType");
	        expression1.setCondition("Condition");
	        expression1.setFilter("Filter");

	        SecurityExpression expression2 = new SecurityExpression();
	        expression2.setId("1");
	        expression2.setType("Type");
	        expression2.setExpressionName("ExpressionName");
	        expression2.setExpressionType("ExpressionType");
	        expression2.setOn("On");
	        expression2.setAccessType("AccessType");
	        expression2.setCondition("Condition");
	        expression2.setFilter("Filter");

	        assertFalse(expression1.equals(expression2));
	        
	    }

	    @Test
	    public void  ut_a5_test_EqualsAndHashCode() {
	        SecurityExpression expression1 = new SecurityExpression();
	        expression1.setId("1");
	        expression1.setType("Type");
	        expression1.setExpressionName("ExpressionName");
	        expression1.setExpressionType(null);
	        expression1.setOn("On");
	        expression1.setAccessType("AccessType");
	        expression1.setCondition("Condition");
	        expression1.setFilter("Filter");

	        SecurityExpression expression2 = new SecurityExpression();
	        expression2.setId("1");
	        expression2.setType("Type");
	        expression2.setExpressionName("ExpressionName");
	        expression2.setExpressionType("ExpressionType");
	        expression2.setOn("On");
	        expression2.setAccessType("AccessType");
	        expression2.setCondition("Condition");
	        expression2.setFilter("Filter");

	        assertFalse(expression1.equals(expression2));
	        
	    }
	    
	    @Test
	    public void  ut_a6_test_EqualsAndHashCode() {
	        SecurityExpression expression1 = new SecurityExpression();
	        expression1.setId(null);
	        expression1.setType(null);
	        expression1.setExpressionName("ExpressionName");
	        expression1.setExpressionType("ExpressionType");
	        expression1.setOn(null);
	        expression1.setAccessType(null);
	        expression1.setCondition(null);
	        expression1.setFilter(null);

	        SecurityExpression expression2 = new SecurityExpression();
	        expression2.setId("1");
	        expression2.setType("Type");
	        expression2.setExpressionName("ExpressionName");
	        expression2.setExpressionType("ExpressionType");
	        expression2.setOn("On");
	        expression2.setAccessType("AccessType");
	        expression2.setCondition("Condition");
	        expression2.setFilter("Filter");

	        assertFalse(expression1.equals(expression2));
	        
	    }

	    @Test
	    public void  ut_a7_test_EqualsAndHashCode() {
	        SecurityExpression expression1 = new SecurityExpression();
	        expression1.setId(null);
	        expression1.setType(null);
	        expression1.setExpressionName("ExpressionName");
	        expression1.setExpressionType("ExpressionType");
	        expression1.setOn(null);
	        expression1.setAccessType(null);
	        expression1.setCondition(null);
	        expression1.setFilter(null);

	        SecurityExpression expression2 = new SecurityExpression();
	        expression2.setId(null);
	        expression2.setType(null);
	        expression2.setExpressionName("ExpressionName");
	        expression2.setExpressionType("ExpressionType");
	        expression2.setOn(null);
	        expression2.setAccessType(null);
	        expression2.setCondition(null);
	        expression2.setFilter(null);

	        assertTrue(expression1.equals(expression2));
	        
	    }
}
