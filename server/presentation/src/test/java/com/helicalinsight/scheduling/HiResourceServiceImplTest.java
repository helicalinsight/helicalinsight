package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.dao.HiResourceDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.impl.HiResourceServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class HiResourceServiceImplTest {

	@Mock
    HiResourceDao hiResourceDao;
	@Mock
	HiResource hiResource;
	@InjectMocks
	HiResourceServiceImpl hiResourceServiceImpl;
	
	@Test
	public void testAddHiResource() {
		Long id = 11l;
		when(hiResourceDao.addHiResource(hiResource)).thenReturn(id);
		Long addHiResource = hiResourceServiceImpl.addHiResource(hiResource);
		assertEquals(addHiResource,id);
	}
	
	@Test
	public void testEditHiResource() {
		hiResourceServiceImpl.editHiResource(hiResource);	
	}
	
	@Test
	public void testDeleteHiResource() {
		hiResourceServiceImpl.deleteHiResource(11l);	
	}
	
	@Test
	public void testGetHiResource() {
		when(hiResourceDao.getHiResource(11l)).thenReturn(hiResource);
		HiResource hiResource2 = hiResourceServiceImpl.getHiResource(11l);	
		assertNotNull(hiResource2);
	}
	@Test
	public void testGetHiResourceByPath() {
		when(hiResourceDao.getHiResourceByPath("path",11l)).thenReturn(hiResource);
		HiResource hiResourceByPath = hiResourceServiceImpl.getHiResourceByPath("path",11l);	
		assertNotNull(hiResourceByPath);
	}
	
	@Test
	public void testFindUniqueHiResource() {
		HiResource hiResourceByPath = hiResourceServiceImpl.findUniqueHiResource(hiResource);	
		assertNull(hiResourceByPath);
	}
	@Test
	public void testDeleteAllHiResource() {
		hiResourceServiceImpl.deleteAllHiResource();	
	}
	
	@Test
	public void testGetResourceTypeById() {
		when(hiResourceDao.getResourceTypeById(anyLong())).thenReturn(null);
		ResourceType resourceTypeById = hiResourceServiceImpl.getResourceTypeById(11l);
		assertNull(resourceTypeById);
	}
	@Test
	public void testGetUserById() {
		hiResourceServiceImpl.getUserById(11l);	
	}
	
	@Test
	public void testGetAllSchedulesById() {
		 List<Schedules> list = new ArrayList<>();
		 when(hiResourceDao.getAllSchedulesById(anyLong())).thenReturn(list);
		List<Schedules> allSchedulesById = hiResourceServiceImpl.getAllSchedulesById(11l);	
		assertEquals(allSchedulesById, list);
	}
	@Test
	public void testDeleteAllMigratedEntries() {
		hiResourceServiceImpl.deleteAllMigratedEntries();	
	}
	
	@Test
	public void testGetHiResourceByPath1() {
		hiResourceServiceImpl.getHiResourceByPath("path");	
	}
}
