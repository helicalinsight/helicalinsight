package com.helicalinsight.adhoc.genericsql;

/*****************************************************************************
 * @author: Rajasekhar
 *
 * Inspired from UndirectedGraph.java of Keith Schwarz (htiek@cs.stanford.edu).
 * Original code credit goes to the author.
 *
 * <p/>
 * A class representing an undirected graph where each edge has an associated
 * real-valued length.  Internally, the class is represented by an adjacency
 * list where each edges appears twice - once in the forward direction and
 * once in the reverse.  In fact, this implementation was formed by taking
 * a standard adjacency list and then duplicating the logic to ensure each
 * edge appears twice.
 */

import java.util.*;
/**
 * A class representing an undirected graph where each edge has an associated real-valued length.
 * 
 * @param <T> The type of the nodes in the graph, which must implement the Comparable interface.
 */
@SuppressWarnings("unused")
final class UndirectedGraph<T extends Comparable<T>> implements Iterable<T> {
    /* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
    private final Map<T, List<T>> graph = new HashMap<>();

    /**
     * Adds a new node to the graph.  If the node already exists, this
     * function is a no-op.
     *
     * @param node The node to add.
     * @return Whether or not the node was added.
     */
    public boolean addNode(T node) {
        /* If the node already exists, don't do anything. */
        if (this.graph.containsKey(node)) {
            return false;
        }

        /* Otherwise, add the node with an empty set of outgoing edges. */
        this.graph.put(node, new ArrayList<T>());
        return true;
    }

    /**
     * Given a node, returns whether that node exists in the graph.
     *
     * @param node The node in question.
     * @return Whether that node exists in the graph.
     */
    public boolean nodeExists(T node) {
        return this.graph.containsKey(node);
    }

    /**
     * Given two nodes, adds an arc of that length between those nodes.  If
     * either endpoint does not exist in the graph, throws a
     * NoSuchElementException.
     *
     * @param one The first node.
     * @param two The second node.
     * @throws NoSuchElementException If either the start or destination nodes
     *                                do not exist.
     */
    public void addEdge(T one, T two) {
        /* Confirm both endpoints exist. */
        requireNonNullAndGraphContains(one, two);

        /* Add the edge in both directions. */
        List<T> firstNodeList = this.graph.get(one);
        if (!firstNodeList.contains(two)) {
            firstNodeList.add(two);
        }
        List<T> secondNodeList = this.graph.get(two);
        if (!secondNodeList.contains(one)) {
            secondNodeList.add(one);
        }
    }

    private void requireNonNullAndGraphContains(T one, T two) {
        if (!this.graph.containsKey(one) || !this.graph.containsKey(two)) {
            throw new NoSuchElementException("Both nodes must be in the graph.");
        }
    }

    /**
     * Sorts the internal data structure of the graph. Type parameter must implement Comparable
     * interface
     *
     * @throws java.lang.IllegalStateException If either the start or destination nodes
     *                                         do not exist.
     */
    public void sortEdges() {
        //Check if graph is empty
        if (this.graph.isEmpty()) {
            throw new IllegalStateException("Empty graph. Add nodes and edges.");
        }

        for (Map.Entry<T, List<T>> entry : this.graph.entrySet()) {
            List<T> values = entry.getValue();
            if (!values.isEmpty()) {
                Collections.sort(values);
            }
        }
    }

    /**
     * Removes the edge between the indicated endpoints from the graph.  If the
     * edge does not exist, this operation is a no-op.  If either endpoint does
     * not exist, this throws a NoSuchElementException.
     *
     * @param one The start node.
     * @param two The destination node.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public void removeEdge(T one, T two) {
        /* Confirm both endpoints exist. */
        requireNonNullAndGraphContains(one, two);

        /* Remove the edges from both adjacency lists. */
        this.graph.get(one).remove(two);
        this.graph.get(two).remove(one);
    }

    /**
     * Given two endpoints, returns whether an edge exists between them.  If
     * either endpoint does not exist in the graph, throws a
     * NoSuchElementException.
     *
     * @param one The first endpoint.
     * @param two The second endpoint.
     * @return Whether an edge exists between the endpoints.
     * @throws NoSuchElementException If the endpoints are not nodes in the
     *                                graph.
     */
    public boolean edgeExists(T one, T two) {
        /* Confirm both endpoints exist. */
        requireNonNullAndGraphContains(one, two);

        /* Graph is symmetric, so we can just check either endpoint. */
        return this.graph.get(one).contains(two);
    }

    /**
     * Given a node in the graph, returns an immutable view of the edges
     * leaving that node.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public List<T> edgesFrom(T node) {
        /* Check that the node exists. */
        List<T> arcs = this.graph.get(node);
        if (arcs == null) {
            throw new NoSuchElementException("Source node does not exist.");
        }

        return Collections.unmodifiableList(arcs);
    }

    /**
     * Returns whether a given node is contained in the graph.
     *
     * @param node The node to test for inclusion.
     * @return Whether that node is contained in the graph.
     */
    public boolean containsNode(T node) {
        return this.graph.containsKey(node);
    }

    /**
     * Returns an iterator that can traverse the nodes in the graph.
     *
     * @return An iterator that traverses the nodes in the graph.
     */
    public Iterator<T> iterator() {
        return this.graph.keySet().iterator();
    }

    /**
     * Returns the number of nodes in the graph.
     *
     * @return The number of nodes in the graph.
     */
    public int size() {
        return this.graph.size();
    }

    /**
     * Returns whether the graph is empty.
     *
     * @return Whether the graph is empty.
     */
    public boolean isEmpty() {
        return this.graph.isEmpty();
    }

    /**
     * Returns a human-readable representation of the graph.
     *
     * @return A human-readable representation of the graph.
     */
    public String toString() {
        return this.graph.toString();
    }
}