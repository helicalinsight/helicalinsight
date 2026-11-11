package com.helicalinsight.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;

public class SseStreamSessionTest {

	@Test
	public void nestedFillFailureReportsAccessDeniedRootCause() {
		AccessDeniedException denied = new AccessDeniedException(
				"You may not have sufficient privilege to access the dataSource");
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText(
				"com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory.resolve($P{Query1})");
		JRExpressionEvalException jasper = new JRExpressionEvalException(expression, denied);
		EfwServiceException wrapped = new EfwServiceException(jasper.getMessage(), jasper);
		ExecutionException execution = new ExecutionException(wrapped);

		String message = SseStreamSession.errorText(execution);

		assertEquals(
				"AccessDeniedException: You may not have sufficient privilege to access the dataSource",
				message);
		assertFalse(message.contains("LazySubDatasetDataSourceFactory"));
	}

	@Test
	public void throwableWithoutCauseKeepsItsOwnMessage() {
		RuntimeException abort = new RuntimeException("aborted");

		assertEquals("aborted", SseStreamSession.errorText(abort));
	}
}
