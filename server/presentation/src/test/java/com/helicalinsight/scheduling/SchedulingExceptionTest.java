package com.helicalinsight.scheduling;

import org.junit.Test;

public class SchedulingExceptionTest {

	@Test
	public void testSchedulingException() {
		SchedulingException exception = new SchedulingException("message");
		exception = new SchedulingException("message", new RuntimeException());
		exception = new SchedulingException(new Throwable());
	}
}
