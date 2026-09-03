package com.helicalinsight.adhoc.recycle;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIRecycleBinService;

@Component
public class RecycleBinPurgeEligibility {
	
	private static final Logger logger = LoggerFactory.getLogger(RecycleBinPurgeEligibility.class);
	
	@Autowired
	private HIRecycleBinService recycleService;

	public PurgeEligibility evaluate(List<RecycleBinDTO> items, boolean force) {
		
		long start = System.currentTimeMillis();
		
		logger.debug("Evaluating Purge elibility of Recyclebin items of Size : {}" , items.size());
		
		Set<Long> eligible = new LinkedHashSet<>();
		Set<Long> blocked = new LinkedHashSet<>();
		if (items == null || items.isEmpty()) {
			return new PurgeEligibility(eligible, blocked);
		}
		if (force) {
			items.forEach(i -> eligible.add(i.getRecycleBinId()));
			return new PurgeEligibility(eligible, blocked);
		}

		Map<RecycleBinType, Set<Long>> idsByType = new EnumMap<>(RecycleBinType.class);
		for (RecycleBinDTO item : items) {
			if (item.getType() == null || item.getRecycleBinId() == null) {
				continue;
			}
			idsByType.computeIfAbsent(item.getType(), _ -> new LinkedHashSet<>()).add(item.getRecycleBinId());
		}

		Set<Long> blockedIds = new HashSet<>();
		addBlocked(blockedIds, idsByType.get(RecycleBinType.HI_RESOURCE_DB),recycleService::findResourceBinsBlockedByLiveDependents);
		addBlocked(blockedIds, idsByType.get(RecycleBinType.DS_GLOBAL_CONNECTIONS),recycleService::findGlobalBinsBlockedByLiveDependents);
		addBlocked(blockedIds, idsByType.get(RecycleBinType.HI_EFWD_CONNECTION),recycleService::findEfwdBinsBlockedByLiveDependents);
		addBlocked(blockedIds, idsByType.get(RecycleBinType.H_USERS),recycleService::findUserBinsBlockedByLiveDependents);
		addBlocked(blockedIds, idsByType.get(RecycleBinType.ORGANIZATION),recycleService::findOrgBinsBlockedByLiveDependents);

		Set<Long> allIds = items.stream().map(RecycleBinDTO::getRecycleBinId).collect(Collectors.toCollection(LinkedHashSet::new));
		for (Long id : allIds) {
			if (blockedIds.contains(id)) {
				blocked.add(id);
			} else {
				eligible.add(id);
			}
		}
		long end = System.currentTimeMillis();
		Double timeElapsed = (end-start)/1000.0;
		logger.debug("Purge elibility evaluation completed in {} seconds. Blocked items : {} , Eligible Items : {}" , timeElapsed, items.size(), blocked, eligible);
		return new PurgeEligibility(eligible, blocked);
	}

	private static void addBlocked(Set<Long> blockedIds, Set<Long> ids,
			java.util.function.Function<Set<Long>, Set<Long>> finder) {
		if (ids == null || ids.isEmpty()) {
			return;
		}
		blockedIds.addAll(finder.apply(ids));
	}
}
