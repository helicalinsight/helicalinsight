package com.helicalinsight.adhoc.recycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.exception.GraphCycleException;
import com.helicalinsight.admin.graph.DependencyGraph;
import com.helicalinsight.admin.graph.GraphBuilder;
import com.helicalinsight.admin.graph.ParentChildEdgeProvider;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;

@Component
public class RecycleBinPurgePlanner {

	private static final Logger logger = LoggerFactory.getLogger(RecycleBinPurgePlanner.class);

	@Autowired
	private HIResourceServiceDB resourceServiceDb;

	@Autowired
	private HIRecycleBinService recycleBinService;

	private final TransactionTemplate requiresNewTx;

	@Autowired
	public RecycleBinPurgePlanner(PlatformTransactionManager transactionManager) {
		this.requiresNewTx = new TransactionTemplate(transactionManager);
		this.requiresNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}
	
	public Set<Long> purge(List<RecycleBinDTO> items, PurgeEligibility eligibility, Map<Long, Boolean> deleteStatusMap) {
		return purge(items, eligibility, deleteStatusMap, false);
	}

	public Set<Long> purge(List<RecycleBinDTO> items, PurgeEligibility eligibility, Map<Long, Boolean> deleteStatusMap, boolean force) {

		Set<Long> completed = new LinkedHashSet<>();
		Map<RecycleBinType, List<RecycleBinDTO>> byType = new EnumMap<>(RecycleBinType.class);

		for (RecycleBinDTO item : items) {
			Long binId = item.getRecycleBinId();
			if (!eligibility.isEligible(binId)) {
				continue;
			}
			byType.computeIfAbsent(item.getType(), _ -> new ArrayList<>()).add(item);
		}

		purgeAllResources(byType.getOrDefault(RecycleBinType.HI_RESOURCE_DB, List.of()), deleteStatusMap, completed,force);
		purgeType(byType.get(RecycleBinType.HI_EFWD_CONNECTION), deleteStatusMap, completed);
		purgeType(byType.get(RecycleBinType.DS_GLOBAL_CONNECTIONS), deleteStatusMap, completed);
		purgeType(byType.get(RecycleBinType.H_USERS), deleteStatusMap, completed);
		purgeType(byType.get(RecycleBinType.ORGANIZATION), deleteStatusMap, completed);

		return completed;
	}

	private void purgeAllResources(List<RecycleBinDTO> resourceBins, Map<Long, Boolean> deleteStatusMap, Set<Long> completed, boolean force) {
		
		if (resourceBins == null || resourceBins.isEmpty()) {
			return;
		}

		Map<Integer, List<RecycleBinDTO>> binsByRootId = new HashMap<>();
		Set<Integer> rootIds = new LinkedHashSet<>();

		for (RecycleBinDTO bin : resourceBins) {
			Long binId = bin.getRecycleBinId();
			if (Boolean.TRUE.equals(deleteStatusMap.get(binId))) {
				completed.add(binId);
				continue;
			}
			Integer rootId = bin.getResourceId();
			if (rootId == null) {
				continue;
			}
			rootIds.add(rootId);
			binsByRootId.computeIfAbsent(rootId, _ -> new ArrayList<>()).add(bin);
		}

		if (rootIds.isEmpty()) {
			return;
		}

		Set<Integer> orderedRoots = orderResourceIdsLeafFirst(rootIds);
		for (Integer rootId : orderedRoots) {
			List<RecycleBinDTO> binsForRoot = binsByRootId.getOrDefault(rootId, List.of());

			boolean anyStillPresent = false;
			for (RecycleBinDTO bin : binsForRoot) {
				if (recycleBinService.isRecycleBinPresent(bin.getRecycleBinId())) {
					anyStillPresent = true;
					break;
				}
			}
			if (!anyStillPresent) {
				for (RecycleBinDTO bin : binsForRoot) {
					Long binId = bin.getRecycleBinId();
					completed.add(binId);
					deleteStatusMap.put(binId, true);
				}
				continue;
			}

			try {
				requiresNewTx.execute(_ -> {
					boolean deleted = resourceServiceDb.hardDeleteResourcesByIds(List.of(rootId), force);
					if (!deleted) {
						throw new EfwServiceException("Failed to hard-delete recycle-bin resources.");
					}
					return null;
				});
				for (RecycleBinDTO bin : binsForRoot) {
					Long binId = bin.getRecycleBinId();
					completed.add(binId);
					deleteStatusMap.put(binId, true);
				}
			} catch (RuntimeException e) {
				logger.error("Failed to purge recycle-bin resource root {}", rootId, e);
			}
		}
	}

	Set<Integer> orderResourceIdsLeafFirst(Collection<Integer> resourceIds) {
		Objects.requireNonNull(resourceIds, "resourceIds");
		Set<Integer> nodes = new LinkedHashSet<>();
		for (Integer id : resourceIds) {
			if (id != null) {
				nodes.add(id);
			}
		}
		if (nodes.isEmpty()) {
			return nodes;
		}
		if (nodes.size() == 1) {
			return nodes;
		}

		Map<Integer, Integer> parentByChild = resourceServiceDb.findParentIdsByResourceIds(nodes);
		try {
			DependencyGraph<Integer> graph = new GraphBuilder<Integer>()
					.with(new ParentChildEdgeProvider(nodes, parentByChild))
					.build();
			return new LinkedHashSet<>(graph.leafFirstOrder());
		} catch (GraphCycleException e) {
			throw new EfwServiceException("Cannot purge recycle-bin resources: parent/child cycle detected among eligible items. "+ e.getMessage(),e);
		}
	}

	private void purgeType(List<RecycleBinDTO> bins, Map<Long, Boolean> deleteStatusMap, Set<Long> completed) {
		if (bins == null || bins.isEmpty()) {
			return;
		}
		for (RecycleBinDTO bin : bins) {
			purgeOne(bin, deleteStatusMap, completed);
		}
	}

	private void purgeOne(RecycleBinDTO bin, Map<Long, Boolean> deleteStatusMap, Set<Long> completed) {
		Long binId = bin.getRecycleBinId();

		if (Boolean.TRUE.equals(deleteStatusMap.get(binId))) {
			completed.add(binId);
			return;
		}
		if (!recycleBinService.isRecycleBinPresent(binId)) {
			completed.add(binId);
			deleteStatusMap.put(binId, true);
			return;
		}

		try {
			requiresNewTx.execute(_ -> {
				RecycleBinHandler handler = RecycleBinHandlerFactory.getHandler(bin.getType().name(), "delete");
				handler.handle(bin, deleteStatusMap);
				return null;
			});
			completed.add(binId);
			deleteStatusMap.put(binId, true);
		} catch (RuntimeException e) {
			logger.error("Failed to purge recycle-bin item {}", binId, e);
		}
	}
}
