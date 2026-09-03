package com.helicalinsight.admin.graph;

import java.util.Objects;

public final class GraphBuilder<T> {

    private final DependencyGraph<T> graph = new DependencyGraph<>();

    public GraphBuilder<T> with(GraphEdgeProvider<T> provider) {
        Objects.requireNonNull(provider, "provider").contribute(graph);
        return this;
    }

    @SafeVarargs
    public final GraphBuilder<T> withAll(GraphEdgeProvider<T>... providers) {
        for (GraphEdgeProvider<T> provider : providers) {
            with(provider);
        }
        return this;
    }

    public DependencyGraph<T> build() {
        return graph;
    }
}