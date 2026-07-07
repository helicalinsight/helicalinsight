package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
/**
 * Implementation of the Breadth First Search algorithm to find the shortest path between two nodes in an undirected graph.
 * @param <T> The type of nodes in the graph.
 */
final class BreadthFirstSearchShortestPathImpl<T extends Comparable<T>> implements BreadthFirstSearch<T> {

    private final UndirectedGraph<T> graph;

    /**
     * The shortest path between two nodes in a graph.
     */
    private final List<T> shortestPath;

    public BreadthFirstSearchShortestPathImpl(UndirectedGraph<T> graph) {
        this.graph = graph;
        this.shortestPath = new ArrayList<>();
    }

    /**
     * Finds the shortest path between two nodes (source and destination) in a graph.
     *
     * @return The shortest path stored as a list of nodes.
     * or null if a path is not found.
     * <p/>
     * Requires: source != null, destination != null and must have a name (e.g.
     * cannot be an empty string).
     */
    @Nullable
    public final List<T> bfs(T source, T destination) {
        this.shortestPath.clear();
        // A list that stores the path.
        List<T> path = new ArrayList<>();

        // If the source is the same as destination, I'm done.
        if (source.equals(destination) && this.graph.containsNode(source)) {
            path.add(source);
            return path;
        }

        // A queue to store the visited nodes.
        Queue<T> queue = new ArrayDeque<>();
        queue.offer(source);

        // A queue to store the visited nodes.
        Queue<T> visited = new ArrayDeque<>();
        while (!queue.isEmpty()) {
            T head = queue.poll();
            visited.offer(head);

            List<T> neighboursList = this.graph.edgesFrom(head);

            for (T neighbour : neighboursList) {
                path.add(neighbour);
                path.add(head);

                if (neighbour.equals(destination)) {
                    return bfs(source, destination, path);
                } else {
                    if (!visited.contains(neighbour)) {
                        queue.offer(neighbour);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds the nodes involved in the shortest path.
     *
     * @param origin      The source node.
     * @param destination The destination node.
     * @param path        The path that has nodes and their neighbours.
     * @return The shortest path.
     */
    @NotNull
    private List<T> bfs(T origin, T destination, @NotNull List<T> path) {
        // Finds out where the destination node directly comes from.
        T next = path.get(path.indexOf(destination) + 1);

        // Adds the destination node to the shortestPath.
        this.shortestPath.add(0, destination);

        if (next.equals(origin)) {
            // The original source node is found.
            this.shortestPath.add(0, origin);
            return this.shortestPath;
        } else {
            // We find where the source node of the destination node
            // comes from.
            // We then set the source node to be the destination node.
            return bfs(origin, next, path);
        }
    }
}