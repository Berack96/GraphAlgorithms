package berack96.lib.graph.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;

import java.util.*;

/**
 * An implementation of the graph using an adjacent list for representing the edges
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public class ListGraph<V, W extends Number> extends AGraph<V, W> {

	final private Map<V, List<Adj>> adj = new HashMap<>();

	@Override
	public Iterator<V> iterator() {
		return adj.keySet().iterator();
	}

	@Override
	protected Graph<V, W> getNewInstance() {
		return new ListGraph<>();
	}

	@Override
	protected void addVertex(V vertex) {
		adj.put(vertex, new LinkedList<>());
	}

	@Override
	protected boolean containsVertex(V vertex) {
		return adj.containsKey(vertex);
	}

	@Override
	protected void removeVertex(V vertex) {
		adj.remove(vertex);
		adj.forEach((v, l) -> {
			Set<Adj> set = new HashSet<>();
			l.forEach(adj -> {
				if(adj.vertex.equals(vertex))
					set.add(adj);
			});
			l.removeAll(set);
		});
	}

	@Override
	protected void removeAllVertices() {
		adj.clear();
	}

	@Override
	protected boolean containsEdgeImpl(V vertex1, V vertex2) {
		if(!adj.containsKey(vertex1))
			return false;
		
		for(Adj a : adj.get(vertex1))
			if(a.vertex.equals(vertex2))
				return true;
		return false;
	}

	@Override
	protected W addEdgeImpl(V vertex1, V vertex2, W weight) {
		W ret = null;
		List<Adj> l = adj.get(vertex1);
		for(Adj a : l)
			if(a.vertex.equals(vertex2)) {
				ret = a.weight;
				a.weight = weight;
			}
		if(ret == null)
			l.add(new Adj(vertex2, weight));
		return ret;
	}

	@Override
	protected W getWeightImpl(V vertex1, V vertex2) {
		W ret = null;
		for(Adj a : adj.get(vertex1))
			if(a.vertex.equals(vertex2))
				ret = a.weight;
		return ret;
	}

	@Override
	protected Collection<Edge<V, W>> getEdgesOutImpl(V vertex) {
		Set<Edge<V,W>> set = new HashSet<>();
		adj.get(vertex).forEach(a -> set.add(new Edge<>(vertex, a.vertex, a.weight)));
		return set;
	}

	@Override
	protected Collection<Edge<V, W>> getEdgesInImpl(V vertex) {
		Set<Edge<V,W>> set = new HashSet<>();
		adj.forEach((v, l) -> l.forEach(a -> {
			if(a.vertex.equals(vertex))
				set.add(new Edge<>(v, a.vertex, a.weight));
		}));
		return set;
	}

	@Override
	protected void removeEdgeImpl(V vertex1, V vertex2) {
		Adj ret = null;
		List<Adj> l = adj.get(vertex1);
		for(Adj a : l)
			if(a.vertex.equals(vertex2))
				ret = a;
		l.remove(ret);
	}

	@Override
	protected void removeAllOutEdgeImpl(V vertex) {
		adj.compute(vertex,(v, l) -> new LinkedList<>());
	}

	@Override
	protected void removeAllInEdgeImpl(V vertex) {
		adj.forEach((v, l) -> {
			Set<Adj> set = new HashSet<>();
			l.forEach(adj -> {
				if(adj.vertex.equals(vertex))
					set.add(adj);
			});
			l.removeAll(set);
		});
	}
	
	private class Adj {
		private final V vertex;
		private W weight;
		
		private Adj(V vertex, W weight) {
			this.vertex = vertex;
			this.weight = weight;
		}
	}
}
