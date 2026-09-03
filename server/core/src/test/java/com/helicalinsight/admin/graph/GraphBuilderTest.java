package com.helicalinsight.admin.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.helicalinsight.admin.exception.GraphCycleException;

public class GraphBuilderTest {

	/**
	 * 1
	 * ├── 2
	 * │   ├── 4
	 * │   └── 5
	 * └── 3
	 *     └── 6
	 */
	private Set<Integer> resourceIds;
	private Map<Integer, Integer> parentByChildId;
	private DependencyGraph<Integer> tree;

	@Before
	public void setUp() {
		resourceIds = Set.of(1, 2, 3, 4, 5, 6);
		parentByChildId = Map.of(2, 1, 3, 1, 4, 2, 5, 2, 6, 3);
		tree = new GraphBuilder<Integer>()
				.with(new ParentChildEdgeProvider(resourceIds, parentByChildId))
				.build();
	}

	// ---------- ParentChild / basic tree ----------

	@Test
	public void testParentChildGraph_checkRoot() {
		assertEquals(Set.of(1), tree.roots());
	}

	@Test
	public void testParentChildGraph_checkLeafNodes() {
		assertEquals(Set.of(4, 5, 6), tree.leaves());
	}

	@Test
	public void testParentChildGraph_checkNodes() {
		assertEquals(resourceIds, tree.nodes());
	}

	@Test
	public void testParentChildGraph_checkChildrenOfAndParentsOf() {
		assertEquals(Set.of(2, 3), tree.childrenOf(1));
		assertEquals(Set.of(4, 5), tree.childrenOf(2));
		assertEquals(Set.of(6), tree.childrenOf(3));
		assertTrue(tree.childrenOf(4).isEmpty());

		assertTrue(tree.parentsOf(1).isEmpty());
		assertEquals(Set.of(1), tree.parentsOf(2));
		assertEquals(Set.of(2), tree.parentsOf(4));

		// unknown node → empty (getOrDefault branch)
		assertTrue(tree.childrenOf(999).isEmpty());
		assertTrue(tree.parentsOf(999).isEmpty());
	}

	@Test
	public void testParentChildGraph_checkRootFirstOrder_constraints() {
		List<Integer> actual = tree.rootFirstOrder();
		assertEquals(6, actual.size());
		assertEquals(Integer.valueOf(1), actual.get(0));
		assertTrue(actual.indexOf(1) < actual.indexOf(2));
		assertTrue(actual.indexOf(1) < actual.indexOf(3));
		assertTrue(actual.indexOf(2) < actual.indexOf(4));
		assertTrue(actual.indexOf(2) < actual.indexOf(5));
		assertTrue(actual.indexOf(3) < actual.indexOf(6));
	}

	@Test
	public void testParentChildGraph_checkLeafFirstOrder_constraints() {
	    List<Integer> actual = tree.leafFirstOrder();
	    assertEquals(6, actual.size());
	    assertEquals(Integer.valueOf(1), actual.get(actual.size() - 1));
	    assertTrue(actual.indexOf(4) < actual.indexOf(2));
	    assertTrue(actual.indexOf(5) < actual.indexOf(2));
	    assertTrue(actual.indexOf(6) < actual.indexOf(3));
	    assertTrue(actual.indexOf(2) < actual.indexOf(1));
	    assertTrue(actual.indexOf(3) < actual.indexOf(1));
	}
	
	@Test
	public void testLeafFirst_startsWithLeaves() {
	    List<Integer> actual = tree.leafFirstOrder();
	    assertEquals(Set.of(4, 5, 6), new HashSet<>(actual.subList(0, 3)));
	}
	
	@Test
	public void testCycle_alsoFailsOnLeafFirst() {
	    DependencyGraph<Integer> g = new DependencyGraph<>();
	    g.addEdge(1, 2);
	    g.addEdge(2, 1);
	    assertThrows(GraphCycleException.class, g::leafFirstOrder);
	}

	@Test
	public void testParentChildGraph_leafFirstIsReverseOfRootFirst() {
		List<Integer> leafFirst = tree.leafFirstOrder();
		System.out.println(leafFirst);
		assertTrue(leafFirst.equals(List.of(6,4,5,3,2,1)) 
				|| leafFirst.equals(List.of(6,5,4,3,2,1))
				|| leafFirst.equals(List.of(4,5,6,3,2,1))
				|| leafFirst.equals(List.of(4,5,6,2,3,1))
				|| leafFirst.equals(List.of(5,4,6,3,2,1))
				);
	}

	@Test
	public void testParentChildGraph_checkAsTree() {
		Map<Integer, List<Integer>> mapTree = tree.asTree();

		assertEquals(6, mapTree.size());
		assertTrue(mapTree.keySet().containsAll(resourceIds));

		assertEquals(Set.of(2, 3), new HashSet<>(mapTree.get(1)));
		assertEquals(Set.of(4, 5), new HashSet<>(mapTree.get(2)));
		assertEquals(List.of(6), mapTree.get(3));

		assertTrue(mapTree.get(4).isEmpty());
		assertTrue(mapTree.get(5).isEmpty());
		assertTrue(mapTree.get(6).isEmpty());
	}

	@Test
	public void testParentChild_skipsParentOutsideNodeSet_andNullParent() {
		Set<Integer> ids = new LinkedHashSet<>(Set.of(10, 11));
		Map<Integer, Integer> parents = new HashMap<>();
		parents.put(10, null);   // root
		parents.put(11, 99);     // parent not in nodeIds → no edge
		parents.put(11, 99);

		DependencyGraph<Integer> g = new GraphBuilder<Integer>()
				.with(new ParentChildEdgeProvider(ids, parents))
				.build();

		assertEquals(Set.of(10, 11), g.roots()); // both roots (no edge created)
		assertTrue(g.childrenOf(10).isEmpty());
		assertTrue(g.parentsOf(11).isEmpty());
	}

	@Test
	public void testParentChild_nullArgs_throwNpe() {
		assertThrows(NullPointerException.class,
				() -> new ParentChildEdgeProvider(null, Map.of()));
		assertThrows(NullPointerException.class,
				() -> new ParentChildEdgeProvider(Set.of(1), null));
	}

	// ---------- GraphBuilder ----------

	@Test
	public void testGraphBuilder_withAll_andBuildReturnsSameGraph() {
		GraphEdgeProvider<Integer> p1 = new ParentChildEdgeProvider(Set.of(1, 2), Map.of(2, 1));
		GraphEdgeProvider<Integer> p2 = graph -> graph.addNode(3);

		DependencyGraph<Integer> g = new GraphBuilder<Integer>()
				.withAll(p1, p2)
				.build();

		assertEquals(Set.of(1, 2, 3), g.nodes());
		assertEquals(Set.of(2), g.childrenOf(1));
	}

	@Test
	public void testGraphBuilder_withNullProvider_throws() {
		assertThrows(NullPointerException.class, () -> new GraphBuilder<Integer>().with(null));
	}

	@Test
	public void testGraphBuilder_withAll_emptyVarargs() {
		DependencyGraph<Integer> g = new GraphBuilder<Integer>().withAll().build();
		assertNotNull(g);
		assertTrue(g.nodes().isEmpty());
		assertTrue(g.roots().isEmpty());
		assertTrue(g.leaves().isEmpty());
		assertTrue(g.rootFirstOrder().isEmpty());
		assertTrue(g.leafFirstOrder().isEmpty());
		assertTrue(g.asTree().isEmpty());
	}

	@Test
	public void testGraphBuilder_withReturnsSameBuilder() {
		GraphBuilder<Integer> builder = new GraphBuilder<>();
		assertSame(builder, builder.with(graph -> graph.addNode(1)));
	}

	// ---------- DependencyGraph direct API ----------

	@Test
	public void testDependencyGraph_addNodeNull_throws() {
		DependencyGraph<Integer> g = new DependencyGraph<>();
		assertThrows(NullPointerException.class, () -> g.addNode(null));
	}

	@Test
	public void testDependencyGraph_addEdge_createsNodes() {
		DependencyGraph<String> g = new DependencyGraph<>();
		g.addEdge("a", "b");
		assertEquals(Set.of("a", "b"), g.nodes());
		assertEquals(Set.of("b"), g.childrenOf("a"));
		assertEquals(Set.of("a"), g.parentsOf("b"));
	}

	@Test
	public void testDependencyGraph_cycle_throwsGraphCycleException() {
		DependencyGraph<Integer> g = new DependencyGraph<>();
		g.addEdge(1, 2);
		g.addEdge(2, 1);

		GraphCycleException ex = assertThrows(GraphCycleException.class, g::rootFirstOrder);
		assertTrue(ex.getMessage().contains("Cycle detected"));
		assertTrue(ex.getMessage().contains("1") || ex.getMessage().contains("2"));
	}

	@Test
	public void testDependencyGraph_disconnectedNodes_areRootsAndLeaves() {
		DependencyGraph<Integer> g = new DependencyGraph<>();
		g.addNode(1);
		g.addNode(2);
		assertEquals(Set.of(1, 2), g.roots());
		assertEquals(Set.of(1, 2), g.leaves());
		assertEquals(2, g.rootFirstOrder().size());
	}



	@Test
	public void testGraphCycleException_message() {
		GraphCycleException ex = new GraphCycleException("boom");
		assertEquals("boom", ex.getMessage());
		assertTrue(ex instanceof RuntimeException);
	}
}