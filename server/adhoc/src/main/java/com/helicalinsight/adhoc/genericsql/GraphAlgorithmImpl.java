package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an implementation of the GraphAlgorithm interface for handling graph-related operations.
 */
final class GraphAlgorithmImpl implements GraphAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryContext.class);

    @NotNull
    private final List<Node> selectedNodes;

    @NotNull
    private final UndirectedGraph<Node> graph;
    private final BreadthFirstSearch<Node> breadthFirstSearch;
    @NotNull
    private List<Node> connectedComponents;
    /**
     * Constructs a new GraphAlgorithmImpl object with the provided list of selected nodes and node map.
     *
     * @param selectedNodes 		List of selected nodes.
     * @param nodes                 Map containing nodes.
     * @throws QueryBuilderException if the list of selected tables is empty or invalid SQL cannot be generated.
     */
    public GraphAlgorithmImpl(@NotNull List<String> selectedNodes, Map<String, Node> nodes) {
        if (selectedNodes.isEmpty()) {
            throw new QueryBuilderException("The list of selected tables is empty.");
        }
        //Check whether valid sql can be generated
        //Pick only unique nodes
        this.selectedNodes = new ArrayList<>();
        for (String table : selectedNodes) {
            Node node = nodes.get(table);//'table' --> catalog.schema.tableName
            if (node != null) {
                List<Node> adjacencyList = node.getAdjacencyList();
                if (adjacencyList == null || adjacencyList.isEmpty()) {
                    throw new QueryBuilderException("The table " + table + " is not joined with any other table in " +
                            "the metadata. Can't build SQL. Please define joins before you use the table.");
                }
                if (!this.selectedNodes.contains(node)) {
                    this.selectedNodes.add(node);
                }
            }
        }

        //If check is not required.
        Collections.sort(this.selectedNodes);

        this.connectedComponents = new ArrayList<>();

        this.graph = new UndirectedGraph<>();
        Collection<Node> nodeCollection = nodes.values();
        for (Node node : nodeCollection) {
            this.graph.addNode(node);
            List<Node> adjacencyList = node.getAdjacencyList();
            for (Node neighbour : adjacencyList) {
                if (!this.graph.containsNode(neighbour)) {
                    this.graph.addNode(neighbour);
                }
                if (!this.graph.edgeExists(node, neighbour)) {
                    this.graph.addEdge(node, neighbour);
                }
            }
        }

        this.graph.sortEdges();
        this.breadthFirstSearch = new BreadthFirstSearchShortestPathImpl<>(this.graph);
    }
    /**
     * Removes indirect edges from the list of connected components.
     *
     * @param connectedComponents      List of connected components.
     * @return Updated list of connected components with indirect edges removed.
     */
    @SuppressWarnings("unused")
    public List<Node> optimizedConnectedComponents() {
        return removeIndirectEdges(this.connectedComponents());
    }

    @Nullable
    private List<Node> removeIndirectEdges(List<Node> connectedComponents) {
        if (connectedComponents.isEmpty()) {
            //This should never happen
            throw new IllegalStateException("The selected tables can't be joined as there are no tables in the " +
                    "connected components. Illegal state.");
        }

        int size = connectedComponents.size();
        if (size <= 3) {
            return connectedComponents;
        }

        //First two nodes are always directly connected. From 2nd index combined path starts

        //The first index(the second element in the list) is either part of the selection or vital to form a path. So
        //we can skip it.

        connectedComponents = new CopyOnWriteArrayList<>(connectedComponents);

        boolean removed = false;
        for (int index = 2, previousIndex = index; index < connectedComponents.size() - 1; previousIndex = index,
                index++) {
            if (removed) {
                index = previousIndex;
            }

            Node node = connectedComponents.get(index);
            Node previous = connectedComponents.get(index - 1);
            Node next = connectedComponents.get(index + 1);

            if (!this.selectedNodes.contains(node) && this.graph.edgeExists(previous, next)) {
                connectedComponents.remove(node);
                removed = true;
            } else {
                removed = false;
            }
        }

        return connectedComponents;
    }
    /**
     * Retrieves the list of connected components in the graph.
     *
     * @return List of connected components.
     */
    @Override
    @Nullable
    public List<Node> connectedComponents() {
        if (logger.isDebugEnabled()) {
            logger.debug("The selected nodes by the user are " + this.selectedNodes);
        }

        //Select the first node. Usually first one as it has more probability to
        //get connected with remaining nodes
        final Node source = this.selectedNodes.iterator().next();
        this.selectedNodes.remove(source);
        this.connectedComponents.add(source);

        for (Node destination : this.selectedNodes) {
            if (destination.visited) {
                continue;
            }

            findEdges(source, destination);

            destination.visited = true;

            //First mark the nodes that are found in the selected path as visited
            for (Node node : this.selectedNodes) {
                if (!node.visited) {
                    if (this.connectedComponents.contains(node)) {
                        markAsVisited(node);
                    }
                }
            }

            //If there are any remaining nodes that are directly connected to the members
            //in the existing shortest path mark them also as visited and append to the
            //shortest path
            for (Node node : this.selectedNodes) {
                if (!node.visited) {
                    for (int index = 1; index < this.connectedComponents.size(); index++) {
                        Node component = this.connectedComponents.get(index);
                        boolean edgeExists = this.graph.edgeExists(node, component);
                        if (edgeExists) {
                            //The node is directly connected to some member
                            //Append to the current path
                            markAsVisited(node);
                            //Node is marked as visited. So, exit.
                            break;
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(this.connectedComponents);
    }
    /**
     * Finds edges between source and destination nodes and updates the connected components list accordingly.
     *
     * @param source      	Source node.
     * @param destination 	Destination node.
     * @throws QueryBuilderException if the shortest path between source and destination nodes cannot be found.
     */
    private void findEdges(Node source, Node destination) {
        if (this.graph.edgeExists(source, destination)) {
            addEdge(source, destination);
        } else {
            List<Node> bfsResult = this.breadthFirstSearch.bfs(source, destination);
            if (bfsResult == null) {
                //Can't prepare sql query
                throw new QueryBuilderException(String.format("Can't prepare sql query. Can't join tables %s and " +
                        "%s" + ".", source.getName(), destination.getName()));
            }

            int size = bfsResult.size();
            //Add all the shortest path members to the connectedComponents except the source
            for (int index = 1; index < size; index++) {
                Node node = bfsResult.get(index);
                if (!this.connectedComponents.contains(node)) {
                    this.connectedComponents.add(node);
                }
            }
        }
    }

    private void markAsVisited(Node node) {
        node.visited = true;
        if (!this.connectedComponents.contains(node)) {
            this.connectedComponents.add(node);
        }
    }

    private void addEdge(Node source, Node destination) {
        if (!this.connectedComponents.contains(source)) {
            this.connectedComponents.add(source);
        }

        if (!this.connectedComponents.contains(destination)) {
            this.connectedComponents.add(destination);
        }
    }
    /**
     * Checks if the combined path is valid.
     *
     * @param combinedPath 			Combined path of nodes.
     * @return {@code true} if the combined path is valid, {@code false} otherwise.
     * @throws IllegalStateException if the selected tables cannot be joined.
     */
    @Override
    public boolean isValid(@Nullable List<Node> combinedPath) {
        if (combinedPath == null) {
            throw new IllegalStateException("The selected tables can't be joined. Illegal state.");
        }

        int size = combinedPath.size();

        boolean valid = false;
        for (int index = (size - 1); index > 0; index--) {
            if (index == 1) {
                continue;
            }
            if ((index != (size - 1)) && !valid) {
                return false;
            }
            Node member = combinedPath.get(index);
            for (int counter = index - 1; counter >= 0; counter--) {
                Node previous = combinedPath.get(counter);
                if (this.graph.edgeExists(member, previous)) {
                    valid = true;
                    break;
                } else if (counter == 0) {
                    valid = false;
                }
            }
        }
        return valid;
    }
}