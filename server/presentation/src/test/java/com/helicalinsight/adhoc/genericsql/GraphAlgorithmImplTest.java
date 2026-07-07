package com.helicalinsight.adhoc.genericsql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphAlgorithmImplTest {

	@Test(expected = QueryBuilderException.class)
	public void ut_a1_test_GraphAlgorithmImpl() {
		List<String> selectedNodes = new ArrayList<>();
		Map<String, Node> nodes = new HashMap<>();
		new GraphAlgorithmImpl(selectedNodes, nodes);
	}

	@Test(expected = QueryBuilderException.class)
	public void ut_a2_test_GraphAlgorithmImpl() {
		List<String> selectedNodes = new ArrayList<>();
		selectedNodes.add("table");
		Map<String, Node> nodes = new HashMap<>();
		Node node = mock(Node.class);
		nodes.put("table", node);
		when(node.getAdjacencyList()).thenReturn(null);
		new GraphAlgorithmImpl(selectedNodes, nodes);
	}

	@Test(expected = QueryBuilderException.class)
	public void ut_a3_test_GraphAlgorithmImpl() {
		List<String> selectedNodes = new ArrayList<>();
		selectedNodes.add("table");
		selectedNodes.add("t1");
		selectedNodes.add("t2");
		Map<String, Node> nodes = new HashMap<>();
		Node node = mock(Node.class);
		Node n1 = mock(Node.class);
		Node n2 = mock(Node.class);
		nodes.put("table", node);
		nodes.put("t1", n1);
		nodes.put("t2", n2);
		List<Node> list = new ArrayList<>();
		list.add(node);
		List<Node> l1 = new ArrayList<>();
		l1.add(n1);
		List<Node> l2 = new ArrayList<>();
		l2.add(n2);
		when(node.getAdjacencyList()).thenReturn(list);
		when(n1.getAdjacencyList()).thenReturn(l1);
		when(n2.getAdjacencyList()).thenReturn(l2);
		GraphAlgorithmImpl algorithmImpl = new GraphAlgorithmImpl(selectedNodes, nodes);
		algorithmImpl.optimizedConnectedComponents();
	}

	@Test
	public void ut_a4_test_GraphAlgorithmImpl()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<String> selectedNodes = new ArrayList<>();
		selectedNodes.add("table");
		selectedNodes.add("t1");
		selectedNodes.add("t2");
		Map<String, Node> nodes = new HashMap<>();
		Node node = mock(Node.class);
		Node n1 = mock(Node.class);
		Node n2 = mock(Node.class);
		nodes.put("table", node);
		nodes.put("t1", n1);
		nodes.put("t2", n2);
		List<Node> list = new ArrayList<>();
		list.add(node);
		List<Node> l1 = new ArrayList<>();
		l1.add(n1);
		List<Node> l2 = new ArrayList<>();
		l2.add(n2);
		when(node.getAdjacencyList()).thenReturn(list);
		when(n1.getAdjacencyList()).thenReturn(l1);
		when(n2.getAdjacencyList()).thenReturn(l2);

		GraphAlgorithmImpl algorithmImpl = new GraphAlgorithmImpl(selectedNodes, nodes);
		BreadthFirstSearch<Node> breadthFirstSearch = mock(BreadthFirstSearch.class);
		Field field = GraphAlgorithmImpl.class.getDeclaredField("breadthFirstSearch");
		field.setAccessible(true);
		field.set(algorithmImpl, breadthFirstSearch);

		when(breadthFirstSearch.bfs(any(), any())).thenReturn(list);
		algorithmImpl.optimizedConnectedComponents();
	}
}
