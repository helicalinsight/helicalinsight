package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;

public class RecycleBinHandlerTest {

	private final RecycleBinHandler handler = new RecycleBinHandler() {};

	@Test
	public void defaultHandleRecycleBinReturnsFalse() {
		assertFalse(handler.handle(new HIRecycleBin()));
	}

	@Test
	public void defaultHandleRecycleBinDtoReturnsFalse() {
		assertFalse(handler.handle(new RecycleBinDTO()));
	}

	@Test
	public void defaultHandleRecycleBinWithCompletedListReturnsFalse() {
		assertFalse(handler.handle(new HIRecycleBin(), new ArrayList<>()));
	}

	@Test
	public void defaultHandleRecycleBinWithMapReturnsTrue() {
		assertTrue(handler.handle(new HIRecycleBin(), new HashMap<>()));
	}

	@Test
	public void defaultHandleRecycleBinDtoWithMapReturnsTrue() {
		assertTrue(handler.handle(new RecycleBinDTO(), new HashMap<>()));
	}

	@Test
	public void defaultHandleRecycleBinWithMapStoresNoEntries() {
		Map<Long, Boolean> completed = new HashMap<>();
		handler.handle(new HIRecycleBin(), completed);
		assertTrue(completed.isEmpty());
	}
}
