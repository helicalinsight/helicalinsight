package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.efw.utility.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Utility class for performing clustering analysis on a database schema.
 * Determines connected components within the schema based on relationships between tables.
 * Created by author on 08-03-2015.
 * @author Rajasekhar
 */
public class ClusteringAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ClusteringAlgorithm.class);
    @Nullable
    private final Metadata metadata;
    /**
     * Constructs a ClusteringAlgorithm instance with the given metadata.
     * @param metadata 				 metadata containing schema information.
     * @throws QueryBuilderException if the metadata is null.
     */
    public ClusteringAlgorithm(@Nullable Metadata metadata) {
        if (metadata == null) {
            throw new QueryBuilderException("The schema information could not be retrieved.");
        }
        this.metadata = metadata;
    }
    /**
     * Finds the connected components within the database schema based on table relationships.
     * @return A set of connected components, where each component is a set of tables.
     */
    @NotNull
    public HashSet<LinkedHashSet<String>> connectedComponents() {
        Database database;
        if (this.metadata != null) {
            database = this.metadata.getDatabase();
        } else {
            throw new QueryBuilderException("The metadata of the selected database is null");
        }

        boolean foundAllNodesConnected = false;

        List<String> allVertices = AdhocUtils.allVertices(database);

        List<Pair<String>> edgesList = new ArrayList<>();

        populateEdges(database, edgesList);

        Map<String, ArrayList<String>> cluster = cluster(allVertices, edgesList);

        if (logger.isDebugEnabled()) {
            logger.debug("All vertices of the database are " + allVertices + ". The populated " +
                    "edges are " + edgesList + ". And the cluster of the database is " + cluster);
        }

        HashSet<LinkedHashSet<String>> setOfConnectedComponents = new HashSet<>();

        for (Map.Entry<String, ArrayList<String>> entry : cluster.entrySet()) {
            String node = entry.getKey();
            ArrayList<String> adjacencyList = entry.getValue();
            int currentIndex = 0;

            int size = adjacencyList.size();
            if (size != 0) {
                while (true) {
                    int lastStartingPoint = currentIndex;
                    HashSet<String> transitiveAdjacencySet = getTransitiveAdjacencySet(cluster, adjacencyList,
                            currentIndex);
                    addTransitivelyAdjacentNodes(node, adjacencyList, transitiveAdjacencySet);
                    int sizeOfRelations = adjacencyList.size();

                    if (sizeOfRelations > size) {
                        lastStartingPoint = currentIndex;
                        currentIndex = size;
                        size = sizeOfRelations;
                    }

                    LinkedHashSet<String> graph = new LinkedHashSet<>();

                    graph.add(node);
                    graph.addAll(adjacencyList);

                    if (graph.containsAll(allVertices)) {
                        foundAllNodesConnected = true;
                        setOfConnectedComponents.add(graph);
                        break;
                    }

                    if ((currentIndex == lastStartingPoint)) {
                        setOfConnectedComponents.add(graph);
                        break;
                    }
                }
            } else {
                LinkedHashSet<String> singleNodeGraph = new LinkedHashSet<>();
                singleNodeGraph.add(node);
                setOfConnectedComponents.add(singleNodeGraph);
            }
            if (foundAllNodesConnected) {
                break;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("The connected components of the database are " + setOfConnectedComponents);
        }
        return setOfConnectedComponents;
    }
    /**
     * Retrieves the list of edges from the relationships defined in the database metadata.
     * @param database 			 database provides relationships.
     * @param edges 			 list to populate with edges.
     * @throws QueryBuilderException if the database or edges list is null.
     */
    private void populateEdges(@Nullable Database database, @Nullable List<Pair<String>> edges) {
        if (database == null || edges == null) {
            throw new QueryBuilderException("The database/edges list is null");
        }
        Relationships relationships = database.getRelationships();
        if (relationships != null) {
            List<Relationship> listOfRelations = relationships.getListOfRelations();
            if (listOfRelations != null) {
                for (Relationship relationship : listOfRelations) {
                    edges.add(new Pair<>(relationship.getTable(), relationship.getReferenceTable()));
                }
            }
        }
    }
    /**
     * Creates a group of map where each vertex is associated with its adjacency list.
     * @param vertices 			 list of vertices.
     * @param edges 			 list of edges.
     * @return A map representing the cluster.
     * @throws QueryBuilderException if the vertices or edges list is null.
     */
    @NotNull
    Map<String, ArrayList<String>> cluster(@Nullable List<String> vertices, @Nullable List<Pair<String>> edges) {
        if (vertices == null || edges == null) {
            throw new QueryBuilderException("The database vertices/edges list is null");
        }
        Map<String, ArrayList<String>> cluster = new HashMap<>();

        for (String vertex : vertices) {
            cluster.put(vertex, adjacencyList(edges, vertex));
        }
        return cluster;
    }
    /**
     * Retrieves the transitive adjacency set for a given cluster and adjacency list.
     * @param cluster 				 cluster map.
     * @param adjacencyList 		 adjacency list.
     * @param counter 				 starting index for iteration.
     * @return transitive adjacency set.
     */
    @NotNull
    private static HashSet<String> getTransitiveAdjacencySet(@NotNull Map<String, ArrayList<String>> cluster,
                                                             @NotNull ArrayList<String> adjacencyList, int counter) {
        HashSet<String> transitiveAdjacencySet = new HashSet<>();
        for (int index = counter; index < adjacencyList.size(); index++) {
            String adjacentNode = adjacencyList.get(index);
            ArrayList<String> adjacentNodesList = cluster.get(adjacentNode);
            if (adjacentNodesList != null) {
                transitiveAdjacencySet.addAll(adjacentNodesList);
            }
        }
        return transitiveAdjacencySet;
    }
    /**
     * Adds transitively adjacent nodes to the adjacency list.
     * @param node 				 		 starting node.
     * @param adjacencyList 			 adjacency list.
     * @param transitivelyAdjacent 		 set of transitively adjacent nodes.
     */
    private static void addTransitivelyAdjacentNodes(String node, @NotNull ArrayList<String> adjacencyList,
                                                     @NotNull HashSet<String> transitivelyAdjacent) {
        for (String next : transitivelyAdjacent) {
            if ((!next.equals(node)) && (!adjacencyList.contains(next))) {
                adjacencyList.add(next);
            }
        }
    }
    /**
     * Retrieves the adjacency list for a given vertex from the list of edges.
     * @param edges 			 list of edges.
     * @param node 				 vertex.
     * @return The adjacency list for the vertex.
     */
    @NotNull
    private ArrayList<String> adjacencyList(@NotNull List<Pair<String>> edges, String node) {
        ArrayList<String> adjacencyList = new ArrayList<>();
        for (Pair<String> thePair : edges) {
            if (thePair.contains(node)) {
                String adjacentNode = thePair.find(node);
                if ((adjacentNode != null) && (!adjacencyList.contains(adjacentNode))) {
                    adjacencyList.add(adjacentNode);
                }
            }
        }
        return adjacencyList;
    }
}
