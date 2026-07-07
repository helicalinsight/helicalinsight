package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;

public class HIRecycleBinResourceRestoreHandlerTest {

	@InjectMocks
	private HIRecycleBinResourceRestoreHandler handler;

	@Mock
	private HIResourceServiceDB serviceDb;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenRootResourceMissing() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(300L);
		bin.setResourceId(1);

		when(serviceDb.getHIResourcesByIds(List.of(1), false)).thenReturn(Collections.emptyList());

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		assertEquals(Boolean.FALSE, deleteMap.get(300L));
		verify(serviceDb, never()).restoreResourcesByIds(anyList());
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenRecycleBinDeleteFails() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(313L);
		bin.setResourceId(130);

		HIResource resource = deletedFile(130, null);
		when(serviceDb.getHIResourcesByIds(List.of(130), false)).thenReturn(List.of(resource));
		when(serviceDb.getChildrenResourceByParentIds(List.of(130))).thenReturn(Collections.emptyList());
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(313L)).thenReturn(false);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(List.of(130));
		assertEquals(Boolean.TRUE, deleteMap.get(313L));
	}

	@Test
	public void handleRecycleBinDtoRestoresDeletedFileWithNoParent() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(301L);
		bin.setResourceId(10);

		HIResource resource = deletedFile(10, null);
		when(serviceDb.getHIResourcesByIds(List.of(10), false)).thenReturn(List.of(resource));
		when(serviceDb.getChildrenResourceByParentIds(List.of(10))).thenReturn(Collections.emptyList());
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(301L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(bin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(List.of(10));
		assertEquals(Boolean.TRUE, deleteMap.get(301L));
		verify(recycleBinService).delete(301L);
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenRootFolderIsNotDeleted() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(314L);
		bin.setResourceId(140);

		HIResource activeFolder = activeFolder(140, null);
		when(serviceDb.getHIResourcesByIds(List.of(140), false)).thenReturn(List.of(activeFolder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(140))).thenReturn(Collections.emptyList());
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		assertEquals(Boolean.FALSE, deleteMap.get(314L));
		verify(serviceDb, never()).restoreResourcesByIds(anyList());
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenRootResourceIsNotDeleted() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(306L);
		bin.setResourceId(15);

		HIResource activeFile = activeFile(15, null);
		when(serviceDb.getHIResourcesByIds(List.of(15), false)).thenReturn(List.of(activeFile));
		when(serviceDb.getChildrenResourceByParentIds(List.of(15))).thenReturn(null);
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		assertEquals(Boolean.FALSE, deleteMap.get(306L));
		verify(serviceDb, never()).restoreResourcesByIds(anyList());
		verify(recycleBinService, never()).delete(306L);
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenParentIsDeleted() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(302L);
		bin.setResourceId(12);

		HIResource parent = deletedFile(11, null);
		HIResource child = deletedFile(12, 11);

		when(serviceDb.getHIResourcesByIds(List.of(12), false)).thenReturn(List.of(child));
		when(serviceDb.getChildrenResourceByParentIds(List.of(12))).thenReturn(Collections.emptyList());
		when(serviceDb.getHIResourcesByIds(List.of(11), false)).thenReturn(List.of(parent));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		assertEquals(Boolean.FALSE, deleteMap.get(302L));
	}

	@Test
	public void handleRecycleBinDtoReturnsFalseWhenParentResourceIsMissing() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(307L);
		bin.setResourceId(50);

		HIResource child = deletedFile(50, 51);
		when(serviceDb.getHIResourcesByIds(List.of(50), false)).thenReturn(List.of(child));
		when(serviceDb.getChildrenResourceByParentIds(List.of(50))).thenReturn(Collections.emptyList());
		when(serviceDb.getHIResourcesByIds(List.of(51), false)).thenReturn(Collections.emptyList());
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertFalse(handler.handle(bin, deleteMap));

		assertEquals(Boolean.FALSE, deleteMap.get(307L));
	}

	@Test
	public void handleRecycleBinDtoPreloadsMultiLevelAncestors() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(308L);
		bin.setResourceId(60);

		HIResource child = deletedFile(60, 61);
		HIResource parent = activeFolder(61, 62);
		HIResource grandparent = activeFolder(62, null);

		when(serviceDb.getHIResourcesByIds(List.of(60), false)).thenReturn(List.of(child));
		when(serviceDb.getChildrenResourceByParentIds(List.of(60))).thenReturn(Collections.emptyList());
		when(serviceDb.getHIResourcesByIds(List.of(61), false)).thenReturn(List.of(parent));
		when(serviceDb.getHIResourcesByIds(List.of(62), false)).thenReturn(List.of(grandparent));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(308L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(bin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(List.of(60));
		verify(recycleBinService).delete(308L);
	}

	@Test
	public void handleRecycleBinDtoRestoresDeletedEmptyFolder() {
		RecycleBinDTO rootBin = new RecycleBinDTO();
		rootBin.setRecycleBinId(312L);
		rootBin.setResourceId(120);

		HIResource emptyFolder = deletedFolder(120, null);

		when(serviceDb.getHIResourcesByIds(List.of(120), false)).thenReturn(List.of(emptyFolder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(120))).thenReturn(Collections.emptyList());
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(312L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(rootBin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(List.of(120));
		verify(recycleBinService).delete(312L);
	}

	@Test
	public void handleRecycleBinDtoRestoresFolderAndChildResources() {
		RecycleBinDTO rootBin = new RecycleBinDTO();
		rootBin.setRecycleBinId(303L);
		rootBin.setResourceId(20);

		RecycleBinDTO childBin = new RecycleBinDTO();
		childBin.setRecycleBinId(304L);
		childBin.setResourceId(21);

		HIResource folder = deletedFolder(20, null);
		HIResource child = deletedFile(21, 20);

		when(serviceDb.getHIResourcesByIds(List.of(20), false)).thenReturn(List.of(folder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(20))).thenReturn(List.of(21));
		when(serviceDb.getHIResourcesByIds(List.of(21), false)).thenReturn(List.of(child));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList()))
				.thenReturn(Map.of(21, childBin));
		when(recycleBinService.delete(303L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(rootBin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(argThat(ids -> ids.contains(20) && ids.contains(21)));
		verify(recycleBinService).deleteRecycleBinsByIds(List.of(childBin));
		assertEquals(Boolean.TRUE, deleteMap.get(304L));
		assertEquals(Boolean.TRUE, deleteMap.get(303L));
		verify(recycleBinService).delete(303L);
	}

	@Test
	public void handleRecycleBinDtoRestoresFolderWithTwoDeletedChildrenUsingRestorableCache() {
		RecycleBinDTO rootBin = new RecycleBinDTO();
		rootBin.setRecycleBinId(309L);
		rootBin.setResourceId(80);

		HIResource folder = deletedFolder(80, null);
		HIResource firstChild = deletedFile(81, 80);
		HIResource secondChild = deletedFile(82, 80);

		when(serviceDb.getHIResourcesByIds(List.of(80), false)).thenReturn(List.of(folder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(80))).thenReturn(List.of(81, 82));
		when(serviceDb.getHIResourcesByIds(List.of(81, 82), false)).thenReturn(List.of(firstChild, secondChild));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(309L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(rootBin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(argThat(ids -> ids.containsAll(List.of(80, 81, 82))));
		verify(recycleBinService).delete(309L);
	}

	@Test
	public void handleRecycleBinDtoSkipsChildBinDeletionWhenChildHasNoRecycleBinEntry() {
		RecycleBinDTO rootBin = new RecycleBinDTO();
		rootBin.setRecycleBinId(310L);
		rootBin.setResourceId(90);

		HIResource folder = deletedFolder(90, null);
		HIResource child = deletedFile(91, 90);

		when(serviceDb.getHIResourcesByIds(List.of(90), false)).thenReturn(List.of(folder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(90))).thenReturn(List.of(91));
		when(serviceDb.getHIResourcesByIds(List.of(91), false)).thenReturn(List.of(child));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(310L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(rootBin, deleteMap));

		verify(recycleBinService, never()).deleteRecycleBinsByIds(anyList());
		verify(serviceDb).restoreResourcesByIds(argThat(ids -> ids.contains(90) && ids.contains(91)));
	}

	@Test
	public void handleRecycleBinDtoDoesNotAddChildBinWhenChildCannotBeRestored() {
		RecycleBinDTO rootBin = new RecycleBinDTO();
		rootBin.setRecycleBinId(311L);
		rootBin.setResourceId(100);

		HIResource folder = deletedFolder(100, null);
		HIResource activeChild = activeFile(101, 100);

		when(serviceDb.getHIResourcesByIds(List.of(100), false)).thenReturn(List.of(folder));
		when(serviceDb.getChildrenResourceByParentIds(List.of(100))).thenReturn(List.of(101));
		when(serviceDb.getHIResourcesByIds(List.of(101), false)).thenReturn(List.of(activeChild));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(311L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(rootBin, deleteMap));

		verify(recycleBinService, never()).deleteRecycleBinsByIds(anyList());
		verify(serviceDb).restoreResourcesByIds(List.of(100));
	}

	@Test
	public void handleRecycleBinDtoRestoresChildWhenParentIsNotDeleted() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(305L);
		bin.setResourceId(32);

		HIResource parent = activeFolder(31, null);
		HIResource child = deletedFile(32, 31);

		when(serviceDb.getHIResourcesByIds(List.of(32), false)).thenReturn(List.of(child));
		when(serviceDb.getChildrenResourceByParentIds(List.of(32))).thenReturn(Collections.emptyList());
		when(serviceDb.getHIResourcesByIds(List.of(31), false)).thenReturn(List.of(parent));
		when(recycleBinService.findHIRecycleBinsByResourceIds(anyList())).thenReturn(Collections.emptyMap());
		when(recycleBinService.delete(305L)).thenReturn(true);

		Map<Long, Boolean> deleteMap = new HashMap<>();

		assertTrue(handler.handle(bin, deleteMap));

		verify(serviceDb).restoreResourcesByIds(List.of(32));
		assertEquals(Boolean.TRUE, deleteMap.get(305L));
	}

	@Test
	public void restoreReturnsFalseWhenResourceIdMissingFromContext() throws Exception {
		Object context = newRestoreContext();
		Set<Integer> toDelete = new HashSet<>();

		assertFalse(invokeRestore(999, context, toDelete));
	}

	@Test
	public void isRestorableReturnsTrueWhenResourceMissingFromContext() throws Exception {
		Object context = newRestoreContext();
		Set<Integer> toDelete = new HashSet<>();
		assertTrue(invokeIsRestorable(999, toDelete, context));
	}

	private boolean invokeRestore(Integer resourceId, Object context, Set<Integer> toDelete) throws Exception {
		Method restore = HIRecycleBinResourceRestoreHandler.class.getDeclaredMethod("restore", Integer.class,
				restoreContextClass(), Set.class);
		restore.setAccessible(true);
		return (boolean) restore.invoke(handler, resourceId, context, toDelete);
	}

	private boolean invokeIsRestorable(Integer resourceId, Set<Integer> toDelete, Object context) throws Exception {
		Method isRestorable = HIRecycleBinResourceRestoreHandler.class.getDeclaredMethod("isRestorable", Integer.class,
				Set.class, restoreContextClass());
		isRestorable.setAccessible(true);
		return (boolean) isRestorable.invoke(handler, resourceId, toDelete, context);
	}

	private Object newRestoreContext() throws Exception {
		Constructor<?> constructor = restoreContextClass().getDeclaredConstructor();
		constructor.setAccessible(true);
		return constructor.newInstance();
	}

	private Class<?> restoreContextClass() throws ClassNotFoundException {
		return Class.forName("com.helicalinsight.adhoc.recycle.handler.HIRecycleBinResourceRestoreHandler$RestoreContext");
	}

	private static HIResource deletedFile(int resourceId, Integer parentId) {
		HIResource resource = new HIResource(resourceId, parentId, true);
		resource.setFolder(false);
		return resource;
	}

	private static HIResource deletedFolder(int resourceId, Integer parentId) {
		HIResource resource = new HIResource(resourceId, parentId, true);
		resource.setFolder(true);
		return resource;
	}

	private static HIResource activeFolder(int resourceId, Integer parentId) {
		HIResource resource = new HIResource(resourceId, parentId, false);
		resource.setFolder(true);
		return resource;
	}

	private static HIResource activeFile(int resourceId, Integer parentId) {
		HIResource resource = new HIResource(resourceId, parentId, false);
		resource.setFolder(false);
		return resource;
	}
}
