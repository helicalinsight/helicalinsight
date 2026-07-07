package com.helicalinsight.efw.vf;

import org.junit.Test;

import com.helicalinsight.efw.utility.FileBrowserContext;

public class FileBrowserContextTest {

	@Test
	public void testtriggerFileBrowserCache() {
		FileBrowserContext browserContext = new FileBrowserContext();
		browserContext.triggerFileBrowserCache();
	}
	@Test
	public void testTriggerFileBrowserCache() {
		FileBrowserContext browserContext = new FileBrowserContext();
		String path = "path";
		int parentId = 11;
		String parentLogicalPath = "logical";
		boolean isFromWatcher = true;
		browserContext.triggerFileBrowserCache(path,parentId,parentLogicalPath,isFromWatcher);
	}
	@Test
	public void testshutdownExecutor() {
		FileBrowserContext browserContext = new FileBrowserContext();
		browserContext.shutdownExecutor();
	}
	
}
