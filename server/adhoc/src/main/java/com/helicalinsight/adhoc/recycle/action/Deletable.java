package com.helicalinsight.adhoc.recycle.action;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.core.request.RecycleBinDatasource;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.core.request.RecycleBinUser;

@Component
public class Deletable {

    private static final String COMPLETED = "completed";

    @Autowired
    private HIRecycleBinService recycleBinService;

    @Autowired
    private HIResourceServiceDB resourceServiceDb;

    public boolean check(Long startId, Map<String, List<Long>> recycleBinIdMap) {
        ArrayDeque<Long> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        queue.add(startId);

        while (!queue.isEmpty()) {
            Long id = queue.poll();
            if (!visited.add(id)) {
                continue;
            }

            if (!recycleBinService.isRecycleBinPresent(id)) {
                continue;
            }

            Map<String, List<Object>> associatedFiles =
                    recycleBinService.findAllResourceOfRecycleBinItem(id);

            if (associatedFiles.containsKey("users")) {
                for (Object user : associatedFiles.get("users")) {
                    RecycleBinUser item = (RecycleBinUser) user;
                    if (!item.isDeleted()) {
                        return false;
                    }
                    HIRecycleBin bin = recycleBinService.findHIRecycleBinByUserId(item.getId());
                    markCompleted(recycleBinIdMap, bin.getId());
                }
            }

            if (associatedFiles.containsKey("resources")) {
                if (associatedFiles.containsKey("unfiltered")) {
                    for (Object resource : associatedFiles.get("unfiltered")) {
                        HIResource r = (HIResource) resource;
                        if (!r.isDeleted()) {
                            return false;
                        }
                    }
                }

                for (Object resource : associatedFiles.get("resources")) {
                    RecycleBinResourceItem item = (RecycleBinResourceItem) resource;
                    if (!item.isDeleted()) {
                        return false;
                    }

                    Long linkedBinId = resourceServiceDb
                            .getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(item.getResourceId());
                    if (linkedBinId != null) {
                        queue.add(linkedBinId);
                        markCompleted(recycleBinIdMap, linkedBinId);
                    }
                }
            }

            if (associatedFiles.containsKey("dataSources")) {
                for (Object dataSource : associatedFiles.get("dataSources")) {
                    RecycleBinDatasource item = (RecycleBinDatasource) dataSource;
                    if (!item.isDeleted()) {
                        return false;
                    }

                    Long binId;
                    if (StringUtils.isNotBlank(item.getDirectory())) {
                        HIRecycleBin bin = recycleBinService.findHIRecycleBinByEFWDId(item.getConnectionId());
                        binId = bin.getId();
                    } else {
                        HIRecycleBin bin = recycleBinService.findHIRecycleBinByGlobalId(item.getConnectionId());
                        binId = bin.getId();
                    }
                    markCompleted(recycleBinIdMap, binId);
                }
            }
        }
        return true;
    }

    private void markCompleted(Map<String, List<Long>> recycleBinIdMap, Long binId) {
        if (binId == null) {
            return;
        }
        List<Long> completed = recycleBinIdMap.get(COMPLETED);
        if (completed != null && !completed.contains(binId)) {
            completed.add(binId);
        }
    }
    
}
