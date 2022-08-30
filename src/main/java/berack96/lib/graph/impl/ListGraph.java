package berack96.lib.graph.impl;

import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphDirected;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of the graph using an adjacent list for representing the edges
 *
 * @param <V> the vertex
 * @author Berack96
 */
public class ListGraph<V> extends GraphDirected<V> {

	// in case of thread safety use -> Collections.synchronizedSortedMap(TreeMap)
	final private Map<V, List<Adj>> adj = getDefaultMap();

	@Override
	public Iterator<V> iterator() {
		return adj.keySet().iterator();
	}

	@Override
	protected Graph<V> getNewInstance() {
		return new ListGraph<>();
	}

	@Override
	public void add(V vertex) {
		check(vertex);
		if (adj.containsKey(vertex))
			removeAllEdge(vertex);
		else
			adj.put(vertex, new LinkedList<>());
	}

	@Override
	public boolean contains(V vertex) {
		check(vertex);
		return adj.containsKey(vertex);
	}

	@Override
	public void remove(V vertex) {
		checkVert(vertex);
		adj.remove(vertex);
		adj.forEach((v, list) -> list.remove(getAdj(list, vertex)));
	}

	@Override
	public int addEdge(V vertex1, V vertex2, int weight) {
		checkVert(vertex1, vertex2);

		List<Adj> list = adj.get(vertex1);
		Adj a = getAdj(list, vertex2);
		int old = a == null ? NO_EDGE : a.weight;

		if (weight == NO_EDGE)
			list.remove(a);
		else if (old == NO_EDGE)
			list.add(new Adj(vertex2, weight));
		else
			a.weight = weight;
		return old;
	}

	@Override
	public int getWeight(V vertex1, V vertex2) {
		checkVert(vertex1, vertex2);
		Adj a = getAdj(adj.get(vertex1), vertex2);
		return a == null ? NO_EDGE : a.weight;
	}

	@Override
	public Set<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		Set<V> children = getDefaultSet();
		for (Adj adj : adj.get(vertex))
			children.add(adj.vertex);
		return children;
	}

	@Override
	public Set<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		Set<V> ancestors = getDefaultSet();
		adj.forEach((v, list) -> {
			if (getAdj(list, vertex) != null)
				ancestors.add(v);
		});

		return ancestors;
	}


	/**
	 * From here on there are some optimization for the methods of the generic DirectedGraph
	 **/

	@Override
	public int size() {
		return adj.size();
	}

	@Override
	public int numberOfEdges() {
		AtomicInteger size = new AtomicInteger(0);
		adj.values().forEach(list -> size.addAndGet(list.size()));
		return size.get();
	}

	@Override
	public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		AtomicInteger degree = new AtomicInteger(0);
		adj.values().forEach(list -> degree.addAndGet(getAdj(list, vertex) != null ? 1 : 0));
		return degree.get();
	}

	@Override
	public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		return adj.get(vertex).size();
	}

	@Override
	public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		adj.get(vertex).clear();
		adj.forEach((v, list) -> list.remove(getAdj(list, vertex)));
	}

	@Override
	public void removeAllEdge() {
		adj.forEach((v, list) -> list.clear());
	}

	@Override
	public void removeAll() {
		adj.clear();
	}

	private Adj getAdj(List<Adj> list, V vertex) {
		for (Adj adj : list)
			if (Objects.equals(adj.vertex, vertex))
				return adj;
		return null;
	}

	private class Adj {
		private final V vertex;
		private int weight;

		private Adj(V vertex, int weight) {
			this.vertex = vertex;
			this.weight = weight;
		}
	}
}
