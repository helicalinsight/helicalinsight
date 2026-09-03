package com.helicalinsight.admin.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import com.helicalinsight.admin.exception.GraphCycleException;

public final class DependencyGraph<T> {

	private final Set<T> nodes = new LinkedHashSet<>();
	private final Map<T, Set<T>> children = new HashMap<>();
	private final Map<T, Set<T>> parents = new HashMap<>();

	public void addNode(T node) {
		Objects.requireNonNull(node, "node");
		nodes.add(node);
		children.computeIfAbsent(node, _ -> new LinkedHashSet<>());
		parents.computeIfAbsent(node, _ -> new LinkedHashSet<>());
	}

	/** parent → child (folder→file, provider→dependent) */
	public void addEdge(T parent, T child) {
		addNode(parent);
		addNode(child);
		children.get(parent).add(child);
		parents.get(child).add(parent);
	}

	public Set<T> nodes() {
		return Collections.unmodifiableSet(nodes);
	}

	public Set<T> childrenOf(T node) {
		return Collections.unmodifiableSet(children.getOrDefault(node, Set.of()));
	}

	public Set<T> parentsOf(T node) {
		return Collections.unmodifiableSet(parents.getOrDefault(node, Set.of()));
	}

	/** Roots first — same as ResourceDependencySorter before reverse */
	public List<T> rootFirstOrder() {
		return kahn(true);
	}

	/** Leaves first — matches Import sorter after Collections.reverse */
	public List<T> leafFirstOrder() {
		return kahn(false);
	}

	public Map<T, List<T>> asTree() {
		Map<T, List<T>> tree = new LinkedHashMap<>();
		for (T node : nodes) {
			tree.put(node, new ArrayList<>(children.getOrDefault(node, Set.of())));
		}
		return tree;
	}

	public Set<T> roots() {
		Set<T> roots = new LinkedHashSet<>();
		for (T node : nodes) {
			if (parents.getOrDefault(node, Set.of()).isEmpty()) {
				roots.add(node);
			}
		}
		return roots;
	}

	public Set<T> leaves() {
		Set<T> leaves = new LinkedHashSet<>();
		for (T node : nodes) {
			if (children.getOrDefault(node, Set.of()).isEmpty()) {
				leaves.add(node);
			}
		}
		return leaves;
	}

	private List<T> kahn(boolean parentToChild) {
		Map<T, Integer> inDegree = new HashMap<>();
		for (T node : nodes) {
			inDegree.put(node, 0);
		}
		if (parentToChild) {
			for (Set<T> kids : children.values()) {
				for (T child : kids) {
					inDegree.merge(child, 1, Integer::sum);
				}
			}
		} else {
			for (Set<T> pars : parents.values()) {
				for (T parent : pars) {
					inDegree.merge(parent, 1, Integer::sum);
				}
			}
		}

		Queue<T> queue = new ArrayDeque<>();
		for (T node : nodes) {
			if (inDegree.get(node) == 0) {
				queue.offer(node);
			}
		}

		List<T> ordered = new ArrayList<>(nodes.size());
		while (!queue.isEmpty()) {
			T current = queue.poll();
			ordered.add(current);
			Set<T> nextNodes = parentToChild ? children.getOrDefault(current, Set.of())
					: parents.getOrDefault(current, Set.of());
			for (T next : nextNodes) {
				int deg = inDegree.merge(next, -1, Integer::sum);
				if (deg == 0) {
					queue.offer(next);
				}
			}
		}

		if (ordered.size() != nodes.size()) {
			Set<T> leftover = new HashSet<>(nodes);
			leftover.removeAll(ordered);
			throw new GraphCycleException("Cycle detected. Unordered nodes: " + leftover);
		}
		return ordered;
	}
}