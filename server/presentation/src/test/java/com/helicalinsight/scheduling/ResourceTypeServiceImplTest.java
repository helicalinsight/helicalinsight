package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.dao.ResourceTypeDao;
import com.helicalinsight.scheduling.service.impl.ResourceTypeServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ResourceTypeServiceImplTest {

	@Mock
	ResourceTypeDao resourceTypeDao;
	@Mock
	ResourceType resourceType;
	@InjectMocks
	ResourceTypeServiceImpl resourceTypeServiceImpl;

	@Test
	public void testAddResourceType() {
		Long id = 11l;
		when(resourceTypeDao.addResourceType(resourceType)).thenReturn(id);
		Long addResourceType = resourceTypeServiceImpl.addResourceType(resourceType);
		assertEquals(addResourceType, id);
	}

	@Test
	public void testEditResourceType() {
		resourceTypeServiceImpl.editResourceType(resourceType);
	}

	@Test
	public void testdeleteResourceType() {
		resourceTypeServiceImpl.deleteResourceType(11l);
	}

	@Test
	public void testGetResourceType() {
		when(resourceTypeDao.getResourceType(anyLong())).thenReturn(resourceType);
		ResourceType resourceType2 = resourceTypeServiceImpl.getResourceType(11l);
		assertNotNull(resourceType2);
	}

	@Test
	public void testGetResourceTypeByTypeAndExtension() {
		when(resourceTypeDao.getResourceTypeByTypeAndExtension(anyString(),anyString())).thenReturn(resourceType);
		ResourceType resourceType2 = resourceTypeServiceImpl.getResourceTypeByTypeAndExtension("type", "extension");
		assertNotNull(resourceType2);
	}

	@Test
	public void testfindUniqueResourceType() {
		when(resourceTypeDao.findUniqueResourceType(resourceType)).thenReturn(resourceType);
		ResourceType resourceType2 = resourceTypeServiceImpl.findUniqueResourceType(resourceType);
		assertNotNull(resourceType2);
	}

	@Test
	public void testDeleteAllResourceType() {
		resourceTypeServiceImpl.deleteAllResourceType();
	}

	@Test
	public void testGetAllResourceTypes() {
		resourceTypeServiceImpl.getAllResourceTypes();
	}
}
