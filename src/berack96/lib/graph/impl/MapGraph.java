package berack96.lib.graph.impl;

import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphDirected;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Graph that uses HashMap for vertices and edges<br>
 * More specifically it utilizes a Map containing all the vertices mapped to all their edges<br>
 * Technically this version of the graph combine the fast adding/removing of the edges of the Matrix implementation,
 * with the low memory and fast adding/removing of vertices of the Linked List implementation.<br>
 * This happen if the HashMap is not reallocated. So in the end each operation of adding or removing has O(n)
 *
 * @param <V> the vertices
 * @author Berack96
 */
public class MapGraph<V> extends GraphDirected<V> {

	/**
	 * Map that contains the edges from a vertex to another<br>
	 * The first vertex is the vertex where start the edge, the second one is where the edge goes<br>
	 * If an edge exist, then it's weight is returned
	 */
	private final Map<V, Map<V, Integer>> edges = new HashMap<>();

	@Override
	public Iterator<V> iterator() {
		return edges.keySet().iterator();
	}


	@Override
	protected Graph<V> getNewInstance() {
		return new MapGraph<>();
	}

	@Override
	public void add(V vertex) {
		check(vertex);
		edges.computeIfAbsent(vertex, v -> new HashMap<>());
		edges.forEach((v, adj) -> adj.remove(vertex));
		edges.get(vertex).clear();
	}

	@Override
	public boolean contains(V vertex) {
		check(vertex);
		return edges.containsKey(vertex);
	}

	@Override
	public void remove(V vertex) {
		checkVert(vertex);
		edges.remove(vertex);
		edges.forEach((v, map) -> map.remove(vertex));
	}

	@Override
	public int addEdge(V vertex1, V vertex2, int weight) {
		checkVert(vertex1, vertex2);
		Map<V, Integer> edge = edges.get(vertex1);
		Integer old = edge.get(vertex2);
		old = old == null ? NO_EDGE : old;

		if (weight == NO_EDGE)
			edge.remove(vertex2);
		else
			edge.put(vertex2, weight);
		return old;
	}

	@Override
	public int getWeight(V vertex1, V vertex2) {
		checkVert(vertex1, vertex2);
		Integer weight = edges.get(vertex1).get(vertex2);
		return weight == null ? NO_EDGE : weight;
	}

	@Override
	public Collection<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		return new HashSet<>(edges.get(vertex).keySet());
	}

	@Override
	public Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		Collection<V> ancestors = new HashSet<>();
		edges.forEach((v, adj) -> {
			if (adj.containsKey(vertex))
				ancestors.add(v);
		});
		return ancestors;
	}

	@Override
	public void removeAll() {
		edges.clear();
	}

	@Override
    public int size() {
        return edges.size();
    }

    @Override
    public int numberOfEdges() {
        AtomicInteger sum = new AtomicInteger(0);
        edges.forEach((v, map) -> sum.getAndAdd(map.size()));

        return sum.get();
    }
}
