package berack96.lib.graph.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;

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
 * @param <W> the weight of the edges
 * @author Berack96
 */
public class MapGraph<V, W extends Number> extends AGraph<V, W> {

    /**
     * Map that contains the edges from a vertex to another<br>
     * The first vertex is the vertex where start the edge, the second one is where the edge goes<br>
     * If an edge exist, then it's weight is returned
     */
    private final Map<V, Map<V, W>> edges = new HashMap<>();
    
    @Override
    public Iterator<V> iterator() {
        return edges.keySet().iterator();
    }


	@Override
	protected Graph<V, W> getNewInstance() {
		return new MapGraph<>();
	}

	@Override
	protected void addVertex(V vertex) {
        edges.put(vertex, new HashMap<>());
	}

	@Override
	protected boolean containsVertex(V vertex) {
		return edges.containsKey(vertex);
	}

	@Override
	protected void removeVertex(V vertex) {
		edges.remove(vertex);
        edges.forEach((v, map) -> map.remove(vertex));
	}

	@Override
	protected void removeAllVertices() {
        edges.clear();
	}

	@Override
	protected boolean containsEdgeImpl(V vertex1, V vertex2) {
		return contains(vertex1) && contains(vertex2) && edges.get(vertex1).containsKey(vertex2);
	}

	@Override
	protected W addEdgeImpl(V vertex1, V vertex2, W weight) {
		return edges.get(vertex1).put(vertex2, weight);
	}

	@Override
	protected W getWeightImpl(V vertex1, V vertex2) {
        return edges.get(vertex1).get(vertex2);
	}

    @Override
    protected Collection<Edge<V, W>> getEdgesOutImpl(V vertex) {
        Collection<Edge<V, W>> collection = new HashSet<>();
        edges.get(vertex).forEach((dest, weight) -> collection.add(new Edge<>(vertex, dest, weight)));
        return collection;
    }
    
	@Override
	protected Collection<Edge<V, W>> getEdgesInImpl(V vertex) {
		Collection<Edge<V, W>> collection = new HashSet<>();
        edges.forEach((source, edge) -> {
            if (edge.get(vertex) != null)
                collection.add(new Edge<>(source, vertex, edge.get(vertex)));
        });
        return collection;
	}

	@Override
	protected void removeEdgeImpl(V vertex1, V vertex2) {
		edges.get(vertex1).remove(vertex2);
	}

	@Override
	protected void removeAllOutEdgeImpl(V vertex) {
		edges.put(vertex, new HashMap<>());
	}

	@Override
	protected void removeAllInEdgeImpl(V vertex) {
        edges.forEach((v, map) -> map.remove(vertex));
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
