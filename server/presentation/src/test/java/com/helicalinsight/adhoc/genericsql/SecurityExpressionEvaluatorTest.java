package com.helicalinsight.adhoc.genericsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.utility.SecurityExpressionEvaluator;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecurityExpressionEvaluatorTest {

	@Test	
	public void ut_a1_test_evaluateExpression() {
		SecurityExpressionEvaluator evaluator = new SecurityExpressionEvaluator();
		try(MockedStatic<SecurityExpressionEvaluator> mockedStatic = mockStatic(SecurityExpressionEvaluator.class)){
			mockedStatic.when(()-> SecurityExpressionEvaluator.evaluateExpression(anyString())).thenReturn(true);
			boolean evaluateExpression = com.helicalinsight.adhoc.genericsql.SecurityExpressionEvaluator.evaluateExpression("expression");
			assertTrue(evaluateExpression);
			
		}
	}
	
	@Test	
	public void ut_a2_test_validateCondition() {
		try(MockedStatic<SecurityExpressionEvaluator> mockedStatic = mockStatic(SecurityExpressionEvaluator.class)){
			mockedStatic.when(()-> SecurityExpressionEvaluator.validateCondition(anyString())).thenReturn(new JsonObject());
			String validateCondition = com.helicalinsight.adhoc.genericsql.SecurityExpressionEvaluator.validateCondition("expression").toString();
			assertTrue(JsonParser.parseString(validateCondition).getAsJsonObject().entrySet().isEmpty());
			
		}
	}
	
	@Test	
	public void ut_a3_test_check() {
		try(MockedStatic<SecurityExpressionEvaluator> mockedStatic = mockStatic(SecurityExpressionEvaluator.class)){
			mockedStatic.when(()-> SecurityExpressionEvaluator.check(anyString(),anyString())).thenReturn(true);
			boolean check = com.helicalinsight.adhoc.genericsql.SecurityExpressionEvaluator.check("multiValue", "argument");
			assertTrue(check);	
		}
	}
	
	@Test	
	public void ut_a4_test_replaceExpression() {
		try(MockedStatic<SecurityExpressionEvaluator> mockedStatic = mockStatic(SecurityExpressionEvaluator.class)){
			mockedStatic.when(()-> SecurityExpressionEvaluator.replaceExpression(anyString())).thenReturn("response");
			String expression = com.helicalinsight.adhoc.genericsql.SecurityExpressionEvaluator.replaceExpression("expression");
			assertEquals("response", expression);
		}
	}
}
