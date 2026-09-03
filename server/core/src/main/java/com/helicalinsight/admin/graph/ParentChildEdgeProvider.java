package com.helicalinsight.admin.graph;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** childId → parentId (null/missing = root). FileBrowser + RecycleBin. */
public final class ParentChildEdgeProvider implements GraphEdgeProvider<Integer> {

    private final Set<Integer> nodeIds;
    private final Map<Integer, Integer> parentByChildId;

    public ParentChildEdgeProvider(Set<Integer> nodeIds, Map<Integer, Integer> parentByChildId) {
        this.nodeIds = Objects.requireNonNull(nodeIds, "nodeIds");
        this.parentByChildId = Objects.requireNonNull(parentByChildId, "parentByChildId");
    }

    @Override
    public void contribute(DependencyGraph<Integer> graph) {
        for (Integer id : nodeIds) {
            graph.addNode(id);
        }
        for (Integer childId : nodeIds) {
            Integer parentId = parentByChildId.get(childId);
            if (parentId != null && nodeIds.contains(parentId)) {
                graph.addEdge(parentId, childId);
            }
        }
    }
}