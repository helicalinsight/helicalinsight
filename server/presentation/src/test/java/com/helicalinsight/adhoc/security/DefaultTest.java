package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefaultTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		Default default1 = new Default();
		boolean threadSafeToCache = default1.isThreadSafeToCache();
		assertFalse(threadSafeToCache);
	}
	
	@Test
	public void ut_a2_test_checkSqlInjection() throws Exception {
		Default default1 = new Default();
		JsonObject checkSqlInjection = default1.checkSqlInjection(new JsonObject(),"key", new Object());
		assertTrue(checkSqlInjection.entrySet().isEmpty());
		
	}
}
