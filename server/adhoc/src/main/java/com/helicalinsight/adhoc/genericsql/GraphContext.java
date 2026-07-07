package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.utility.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a context for constructing a graph of tables with their relationships and weights.
 * This class is responsible for generating nodes with weights and their adjacency lists.
 * 
 * Created by author on 10/19/2015.
 * @author Rajasekhar
 */
class GraphContext {

    private final Metadata metadata;
    private final ClusteringAlgorithm clusteringAlgorithm;

    public GraphContext(Metadata metadata) {
        this.metadata = metadata;
        this.clusteringAlgorithm = new ClusteringAlgorithm(metadata);
    }
    /**
     * Constructs a graph representation of tables with their relationships and weights.
     * @return A map of table names to `Node` objects representing the nodes in the graph,
     *         along with their weights and adjacency lists.
     */
    public Map<String, Node> nodesWithWeights() {
        Database database = this.metadata.getDatabase();
        List<String> vertices = AdhocUtils.allVertices(database);
        Relationships relationships = database.getRelationships();

        Map<String, Node> nodesWithWeights = new TreeMap<>();
        List<Pair<String>> edges = new ArrayList<>();
        //Prepare edges only once. Also set the weights of individual nodes.
        //Node weight is calculated using the principle that if the node or the table is a referenced table
        //or in other words, has a foreign key as a column then the node weight increases by 1.

        //So, total node weight of a table is the total number of incoming edges when a graph is constructed.
        //A relationship between two tables represents an edge in the graph.
        boolean firstTime = true;
        if (relationships != null) {
            List<Relationship> listOfRelations = relationships.getListOfRelations();
            if (listOfRelations != null) {
                for (String vertex : vertices) {
                    Node node = new Node(vertex);
                    setNodeWeight(edges, firstTime, listOfRelations, vertex, node);
                    nodesWithWeights.put(vertex, node);
                    firstTime = false;
                }
            }
        }

        Map<String, ArrayList<String>> cluster = this.clusteringAlgorithm.cluster(vertices, edges);
        //Set adjacency list for each node.
        for (String vertex : vertices) {
            Node node = nodesWithWeights.get(vertex);
            if (node == null) {//This condition is true when no relationships are present in metadata
                node = new Node(vertex);
                nodesWithWeights.put(vertex, node);
            }
            ArrayList<Node> adjacencyList = adjacencyList(nodesWithWeights, cluster, vertex);
            node.setAdjacencyList(adjacencyList);
        }
        return nodesWithWeights;
    }
    /**
     * Sets the weight of each node based on its relationships with other tables.
     * @param edges 		 		list of edges between tables.
     * @param firstTime 	 		Indicates whether this is the first time setting node weights.
     * @param listOfRelations 	    list of relationships between tables.
     * @param vertex 			    name of the current table being processed.
     * @param node The node whose weight needs to be set.
     */
    private void setNodeWeight(List<Pair<String>> edges, boolean firstTime, List<Relationship> listOfRelations,
                               String vertex, Node node) {
        for (Relationship relationship : listOfRelations) {
            if (firstTime) {
                prepareEdges(edges, relationship);
            }

            List<Join> joins = relationship.getJoin();
            if (joins != null) {
                for (Join join : joins) {
                	 if (vertex.equalsIgnoreCase(join.getRightTable().getTable())) {
                        node.setWeight(node.getWeight() + 1);
                    }
                }
            }
        }
    }
    /**
     * Retrieves the adjacency list for a given vertex in the graph.
     * @param nodes 		A map containing all nodes in the graph.
     * @param cluster 		A map representing the clustering of vertices.
     * @param vertex 		vertex for which the adjacency list is to be retrieved.
     * @return An ArrayList containing the neighboring nodes of the specified vertex.
     */
    @NotNull
    private ArrayList<Node> adjacencyList(Map<String, Node> nodes, Map<String, ArrayList<String>> cluster,
                                          String vertex) {
        ArrayList<String> verticesList = cluster.get(vertex);
        ArrayList<Node> adjacencyList = new ArrayList<>();
        if (verticesList != null) {
            for (String neighbour : verticesList) {
            	 adjacencyList.add(nodes.get(neighbour));
            }
        }
        return adjacencyList;
    }
    /**
     * Prepares the edges of the graph based on the relationships defined in the metadata.
     * @param edges 			 list to store the edges.
     * @param relationship 		 relationship for which the edge is to be prepared.
     */
    private void prepareEdges(List<Pair<String>> edges, Relationship relationship) {
        String table = relationship.getTable();
        String referenceTable = relationship.getReferenceTable();
        Pair<String> pair = new Pair<>(table, referenceTable);
        if (!edges.contains(pair)) {
            edges.add(pair);
        }
    }
}
