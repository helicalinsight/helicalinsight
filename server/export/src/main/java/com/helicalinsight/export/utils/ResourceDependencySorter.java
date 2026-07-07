package com.helicalinsight.export.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.helicalinsight.export.handler.ImportManagerContext;
/**
 * ResourceDependencySorter Utility class for sorting resources based on their dependencies using topological sorting.
 */
public class ResourceDependencySorter {

	private final ImportManagerContext context;

	public ResourceDependencySorter(ImportManagerContext context) {
		this.context = context;
	}
	/**
     * Sorts resources based on their dependencies using sorting.
     * @return A List of resource paths in sorted order.
     */
	public List<String> sort() {

		List<String> result = new ArrayList<>();
		Map<String, List<String>> dependencies = context.getManifest().getDependencies();

		// Create a map to store the in-degree of each vertex (resource path)
		Map<String, Integer> inDegreeMap = new HashMap<>();
		for (String path : context.getManifest().getResourcePaths()) {
			inDegreeMap.put(path, 0);
		}

		// Calculate the in-degree of each vertex
		for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
			String path = entry.getKey();
			List<String> dependents = entry.getValue();

			for (String dependent : dependents) {
				inDegreeMap.put(dependent, inDegreeMap.getOrDefault(dependent, 0) + 1);
			}
		}

		// Create a queue to store vertices with in-degree 0
		Queue<String> queue = new LinkedList<>();
		for (String path : inDegreeMap.keySet()) {
			if (inDegreeMap.get(path) == 0) {
				queue.offer(path);
			}
		}

		// Process the vertices
		while (!queue.isEmpty()) {
			String path = queue.poll();
			result.add(path);

			if (dependencies.containsKey(path)) {
				List<String> dependents = dependencies.get(path);
				for (String dependent : dependents) {
					inDegreeMap.put(dependent, inDegreeMap.get(dependent) - 1);
					if (inDegreeMap.get(dependent) == 0) {
						queue.offer(dependent);
					}
				}
			}
		}
		Collections.reverse(result);
		return result;
	}

}
