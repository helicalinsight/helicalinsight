package com.helicalinsight.admin.graph;

@FunctionalInterface
public interface GraphEdgeProvider<T> {
    void contribute(DependencyGraph<T> graph);
}