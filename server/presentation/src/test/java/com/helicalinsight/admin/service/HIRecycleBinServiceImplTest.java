package com.helicalinsight.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.dao.HIResourceMappingDao;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHIResourceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.impl.HIRecycleBinServiceImpl;
import com.helicalinsight.core.request.RecycleBinItem;
import com.helicalinsight.efw.exceptions.EfwServiceException;

@RunWith(MockitoJUnitRunner.class)
public class HIRecycleBinServiceImplTest {

	@InjectMocks
	private HIRecycleBinServiceImpl service;

	@Mock
	private HIRecycleBinDao hiRecycleBinDao;

	@Mock
	private HIResourceMappingDao resourceDao;

	@Mock
	private ResourceTypeServiceDB resourceTypeService;

	@Test
	public void saveDelegatesToDao() {
		HIRecycleBin bin = new HIRecycleBin();
		when(hiRecycleBinDao.save(bin)).thenReturn(true);

		assertTrue(service.save(bin));
		verify(hiRecycleBinDao).save(bin);
	}

	@Test
	public void deleteByIdLoadsPlainBinAndDeletes() {
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(1L);
		when(hiRecycleBinDao.findHIRecycleBinByIdPlain(1L)).thenReturn(bin);
		when(hiRecycleBinDao.delete(bin)).thenReturn(true);

		assertTrue(service.delete(1L));

		verify(hiRecycleBinDao).findHIRecycleBinByIdPlain(1L);
		verify(hiRecycleBinDao).delete(bin);
	}

	@Test
	public void deleteBinDeletesEfwddChildrenBeforeDaoDelete() {
		HIRecycleBin bin = resourceBin(10, ".efwdd");
		when(hiRecycleBinDao.delete(bin)).thenReturn(true);

		assertTrue(service.delete(bin));

		verify(resourceDao).deleteChildrenByParentId(10);
		verify(hiRecycleBinDao).delete(bin);
	}

	@Test
	public void deleteBinDeletesHcrChildrenBeforeDaoDelete() {
		HIRecycleBin bin = resourceBin(11, "hcr");
		when(hiRecycleBinDao.delete(bin)).thenReturn(true);

		assertTrue(service.delete(bin));

		verify(resourceDao).deleteChildrenByParentId(11);
		verify(hiRecycleBinDao).delete(bin);
	}

	@Test
	public void deleteBinSkipsChildDeletionForOtherResourceTypes() {
		HIRecycleBin bin = resourceBin(12, ".report");
		when(hiRecycleBinDao.delete(bin)).thenReturn(true);

		assertTrue(service.delete(bin));

		verify(resourceDao, never()).deleteChildrenByParentId(anyInt());
		verify(hiRecycleBinDao).delete(bin);
	}

	@Test
	public void listDelegatesToDao() {
		List<RecycleBinItem> items = List.of(new RecycleBinItem());
		when(hiRecycleBinDao.list()).thenReturn(items);

		assertSame(items, service.list());
	}

	@Test
	public void findHIRecycleBinsByResourceIdsDelegatesToDao() {
		Map<Integer, RecycleBinDTO> bins = Map.of(1, new RecycleBinDTO());
		when(hiRecycleBinDao.findHIRecycleBinsByResourceIds(List.of(1))).thenReturn(bins);

		assertEquals(bins, service.findHIRecycleBinsByResourceIds(List.of(1)));
	}

	@Test
	public void findHIRecycleBinByGlobalIdReturnsBinWhenPresent() {
		HIRecycleBin bin = new HIRecycleBin();
		when(hiRecycleBinDao.findHIRecycleBinByGlobalId(5)).thenReturn(Optional.of(bin));

		assertSame(bin, service.findHIRecycleBinByGlobalId(5));
	}

	@Test(expected = EfwServiceException.class)
	public void findHIRecycleBinByGlobalIdThrowsWhenMissing() {
		when(hiRecycleBinDao.findHIRecycleBinByGlobalId(6)).thenReturn(Optional.empty());
		service.findHIRecycleBinByGlobalId(6);
	}

	@Test(expected = EfwServiceException.class)
	public void findHIRecycleBinByEFWDIdThrowsWhenMissing() {
		when(hiRecycleBinDao.findHIRecycleBinByEFWDId(7)).thenReturn(Optional.empty());
		service.findHIRecycleBinByEFWDId(7);
	}

	@Test(expected = EfwServiceException.class)
	public void findHIRecycleBinByUserIdThrowsWhenMissing() {
		when(hiRecycleBinDao.findHIRecycleBinBYUserId(8)).thenReturn(Optional.empty());
		service.findHIRecycleBinByUserId(8);
	}

	@Test
	public void deleteRecycleBinsByIdsSkipsWhenListIsNullOrEmpty() {
		service.deleteRecycleBinsByIds(null);
		service.deleteRecycleBinsByIds(Collections.emptyList());

		verify(resourceTypeService, never()).getResourceType(any());
		verify(hiRecycleBinDao, never()).deleteRecycleBinsByIds(anyList());
	}

	@Test
	public void deleteRecycleBinsByIdsDeletesChildrenForEfwddAndBulkDeletesBins() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(100L);
		bin.setResourceId(20);
		bin.setResourceTypeId(2L);

		ResourceType resourceType = new ResourceType();
		resourceType.setExtension(".efwdd");
		when(resourceTypeService.getResourceType(2L)).thenReturn(resourceType);

		service.deleteRecycleBinsByIds(List.of(bin));

		verify(resourceDao).deleteChildrenByParentId(20);
		verify(hiRecycleBinDao).deleteRecycleBinsByIds(List.of(100L));
	}

	@Test
	public void deleteRecycleBinsByIdsSkipsBinsWithMissingMetadata() {
		RecycleBinDTO missingResourceId = new RecycleBinDTO();
		missingResourceId.setRecycleBinId(101L);
		missingResourceId.setResourceTypeId(2L);

		RecycleBinDTO missingType = new RecycleBinDTO();
		missingType.setRecycleBinId(102L);
		missingType.setResourceId(21);
		missingType.setResourceTypeId(3L);
		when(resourceTypeService.getResourceType(3L)).thenReturn(null);

		service.deleteRecycleBinsByIds(List.of(missingResourceId, missingType));

		verify(resourceDao, never()).deleteChildrenByParentId(anyInt());
		verify(hiRecycleBinDao).deleteRecycleBinsByIds(List.of(101L, 102L));
	}

	@Test
	public void getAllDelegatesToDao() {
		List<RecycleBinDTO> bins = List.of(new RecycleBinDTO());
		when(hiRecycleBinDao.getAllRecycleBinDTOs()).thenReturn(bins);

		assertSame(bins, service.getAll());
	}

	@Test
	public void isRecycleBinPresentDelegatesToDao() {
		when(hiRecycleBinDao.isRecycleBinPresent(9L)).thenReturn(true);

		assertTrue(service.isRecycleBinPresent(9L));
	}

	@Test
	public void deleteHIRecycleByEfwdIdDelegatesToDao() {
		service.deleteHIRecycleByEfwdId(15);
		verify(hiRecycleBinDao).deleteHIRecycleByEfwdId(15);
	}

	@Test
	public void findAllResourceOfRecycleBinItemDelegatesToDao() {
		Map<String, List<Object>> resources = Map.of("resources", List.of());
		when(hiRecycleBinDao.findAllResourceOfRecycleBinItem(16L)).thenReturn(resources);

		assertEquals(resources, service.findAllResourceOfRecycleBinItem(16L));
	}

	private static HIRecycleBin resourceBin(int resourceId, String extension) {
		ResourceType resourceType = new ResourceType();
		resourceType.setExtension(extension);

		HIResource resource = org.mockito.Mockito.mock(HIResource.class);
		when(resource.getResourceId()).thenReturn(resourceId);
		when(resource.getResourceType()).thenReturn(resourceType);

		HIRecycleBinHIResourceDB resourceDb = new HIRecycleBinHIResourceDB();
		resourceDb.setHiResource(resource);

		HIRecycleBin bin = new HIRecycleBin();
		bin.setHiRecycleBinHIResourceDB(resourceDb);
		return bin;
	}
}
