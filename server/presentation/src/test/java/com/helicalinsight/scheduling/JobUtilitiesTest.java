package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.quartz.JobExecutionContext;

public class JobUtilitiesTest {

	@Test
	public void testGetJobcontext() {
		JobUtilities jobUtilities = new JobUtilities();
		JobUtilities.setJobcontext(null);
		JobExecutionContext jobcontext = JobUtilities.getJobcontext();
		assertNull(jobcontext);
	}
}
