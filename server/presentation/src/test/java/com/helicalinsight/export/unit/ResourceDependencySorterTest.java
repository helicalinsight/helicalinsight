package com.helicalinsight.export.unit;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.utils.ResourceDependencySorter;

public class ResourceDependencySorterTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testSort() {
		ImportManagerContext context = new ImportManagerContext();
		Manifest manifest = new Manifest();
		manifest.setResourcePaths(Arrays.asList("a", "b", "c"));
		Map<String, List<String>> deps = new HashMap<>();
		deps.put("a", Arrays.asList("b"));
		deps.put("b", Arrays.asList("c"));
		manifest.setDependencies(deps);
		context.setManifest(manifest);

		ResourceDependencySorter sorter = new ResourceDependencySorter(context);
		List<String> sorted = sorter.sort();
		Assert.assertEquals(3, sorted.size());
		Assert.assertTrue(sorted.contains("a"));
		Assert.assertTrue(sorted.contains("b"));
		Assert.assertTrue(sorted.contains("c"));
	}
}
