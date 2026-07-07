package com.helicalinsight.cache;

import org.junit.Test;

import com.helicalinsight.cache.manager.MetadataCacheException;

public class MetadataCacheExceptionTest {

	@Test
	public void test() {
		String message = "no any response";
		Throwable cause=null;
		MetadataCacheException  cacheManager = new MetadataCacheException (message, cause);
		MetadataCacheException  cache = new MetadataCacheException (cause);
		MetadataCacheException  cache1 = new MetadataCacheException ("message");
	}
}
