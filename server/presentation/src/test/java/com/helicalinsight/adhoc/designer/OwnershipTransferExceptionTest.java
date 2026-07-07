package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helicalinsight.adhoc.exception.OwnershipTransferException;

public class OwnershipTransferExceptionTest {
	
	@Test
	public void ut_a1_test() {
		OwnershipTransferException exception = new OwnershipTransferException("message");
		Throwable cause = new Throwable();
		exception = new OwnershipTransferException("message",cause);
		exception = new OwnershipTransferException(cause);
		assertNotNull(exception);
	}

}
