package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Node implements Comparable<Node> {
    private final String name;
    boolean visited;
    //This list is sorted with node weights in descnending order
    private List<Node> adjacencyList;
    private int weight = 0;

    public Node(String name) {
        this.name = name;
        this.adjacencyList = new ArrayList<>();
        this.visited = false;
    }

    public Node(String name, List<Node> adjacencyList, int weight) {
        this.name = name;
        this.adjacencyList = adjacencyList;
        this.weight = weight;
        this.visited = false;
    }

    public List<Node> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(List<Node> adjacencyList) {
        if (adjacencyList == null) {
            throw new IllegalArgumentException("Adjacency list is null");
        }
        this.adjacencyList = adjacencyList;
        if (!adjacencyList.isEmpty()) {
            Collections.sort(this.adjacencyList);
        }
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int compareTo(@NotNull Node that) {
        return that.weight - this.weight;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Node node = (Node) other;

        //noinspection SimplifiableIfStatement
        if (Double.compare(node.weight, weight) != 0) {
            return false;
        }
        return !(name != null ? !name.equals(node.name) : node.name != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}