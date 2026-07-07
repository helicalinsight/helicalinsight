package com.helicalinsight.adhoc.recycle.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;

/**
 * HIRecycleBinResourceRestoreHandler
 * 
 * This class handles the restoration of resources from the recycle bin.
 * It implements the {@link  RecycleBinHandler} interface.
 */
@Component("hi_resource_db_restoreHandler")
public class HIRecycleBinResourceRestoreHandler implements RecycleBinHandler {

	@Autowired
	private HIResourceServiceDB serviceDb;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	
	@Override
	public boolean handle(RecycleBinDTO bin, Map<Long, Boolean> deleteMap) {
		Integer rootResourceId = bin.getResourceId();
		RestoreContext context = buildRestoreContext(rootResourceId);
		if (!context.resourceById.containsKey(rootResourceId)) {
			deleteMap.put(bin.getRecycleBinId(), false);
			return false;
		}
		Set<Integer> toDelete = new HashSet<>();
		boolean restored =  restore(bin.getResourceId(),context,toDelete);
		applyRestoredChanges(context, deleteMap);
		deleteMap.put(bin.getRecycleBinId(),restored);
		if(restored ) {
			return recycleBinService.delete(bin.getRecycleBinId());
		}
		return false;
	} 
	
	
	private boolean restore(Integer resourceId, RestoreContext context, Set<Integer> toDelete)  {
		
		HIResource resource  = context.resourceById.get(resourceId);
		
		if ( resource == null ) {
			return false;
		}
		
		if (Boolean.TRUE.equals(resource.getFolder()) && Boolean.TRUE.equals(resource.getDeleted())) {
			toDelete.add(resourceId);
			List<HIResource> children = context.childrenByParentId.getOrDefault(resourceId, List.of());
			for (HIResource child : children) {
				if (restore(child.getResourceId(), context, toDelete)) {
					RecycleBinDTO childBin = context.binByResourceId.get(child.getResourceId());
					if (childBin != null) {
						context.binsToDelete.add(childBin);
					}
				}
			}
		}
		if (Boolean.TRUE.equals(resource.getDeleted()) && isRestorable(resourceId, toDelete, context)) {
			context.restoredResourceIds.add(resourceId);
			return true;
		}
		return false;
	}
	
	
	private RestoreContext buildRestoreContext(Integer rootResourceId) {
		RestoreContext context = new RestoreContext();
		List<HIResource> rootResources = serviceDb.getHIResourcesByIds(List.of(rootResourceId),false);
		if (rootResources.isEmpty()) {
			return context;
		}
		context.resourceById.put(rootResourceId, rootResources.get(0));
		List<Integer> descendantIds = serviceDb.getChildrenResourceByParentIds(List.of(rootResourceId));
		if (descendantIds != null && !descendantIds.isEmpty()) {
			for (HIResource descendant : serviceDb.getHIResourcesByIds(descendantIds, false)) {
				context.resourceById.put(descendant.getResourceId(), descendant);
			}
		}

		preloadAncestors(context);

		for (HIResource resource : context.resourceById.values()) {
			if (resource.getParentId() != null) {
				context.childrenByParentId.computeIfAbsent(resource.getParentId(), id -> new ArrayList<>()).add(resource);
			}
		}

		context.binByResourceId.putAll(recycleBinService.findHIRecycleBinsByResourceIds(new ArrayList<>(context.resourceById.keySet())));
		return context;
	}

	private void preloadAncestors(RestoreContext context) {
		Set<Integer> missingParentIds = new HashSet<>();
		for (HIResource resource : context.resourceById.values()) {
			Integer parentId = resource.getParentId();
			if (parentId != null && !context.resourceById.containsKey(parentId)) {
				missingParentIds.add(parentId);
			}
		}
		while (!missingParentIds.isEmpty()) {
			List<HIResource> parents = serviceDb.getHIResourcesByIds(new ArrayList<>(missingParentIds), false);
			missingParentIds.clear();
			for (HIResource parent : parents) {
				context.resourceById.put(parent.getResourceId(), parent);
				Integer grandParentId = parent.getParentId();
				if (grandParentId != null && !context.resourceById.containsKey(grandParentId)) {
					missingParentIds.add(grandParentId);
				}
			}
		}
	}
	
	
	private void applyRestoredChanges(RestoreContext context, Map<Long, Boolean> deleteMap) {
		if (!context.restoredResourceIds.isEmpty()) {
			serviceDb.restoreResourcesByIds(context.restoredResourceIds);
		}
		if (!context.binsToDelete.isEmpty()) {
			recycleBinService.deleteRecycleBinsByIds(context.binsToDelete);
			for (RecycleBinDTO bin : context.binsToDelete) {
				deleteMap.put(bin.getRecycleBinId(), true);
			}
		}
	}
	
	private boolean isRestorable(Integer resourceId, Set<Integer> toDelete, RestoreContext context) {

		if (context.restorableCache.containsKey(resourceId)) {
			return context.restorableCache.get(resourceId);
		}

		HIResource resource = context.resourceById.get(resourceId);
		if (resource == null || resource.getParentId() == null) {
			context.restorableCache.put(resourceId, true);
			return true;
		}

		HIResource parent = context.resourceById.get(resource.getParentId());
		if (parent == null) {
			context.restorableCache.put(resourceId, false);
			return false;
		}

		if (!parent.isDeleted() || toDelete.contains(parent.getResourceId())) {
			boolean result = isRestorable(parent.getResourceId(), toDelete, context);
			context.restorableCache.put(resourceId, result);
			return result;
		}
		context.restorableCache.put(resourceId, false);
		return false;
	}
	
	
	private static final class RestoreContext {
		private final Map<Integer, HIResource> resourceById = new HashMap<>();
		private final Map<Integer, List<HIResource>> childrenByParentId = new HashMap<>();
		private final Map<Integer, RecycleBinDTO> binByResourceId = new HashMap<>();
		private final Map<Integer, Boolean> restorableCache = new HashMap<>();
		private final List<Integer> restoredResourceIds = new ArrayList<>();
		private final List<RecycleBinDTO> binsToDelete = new ArrayList<>();
	}

}
