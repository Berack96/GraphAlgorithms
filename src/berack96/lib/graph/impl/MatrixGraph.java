package berack96.lib.graph.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;

import java.util.*;

/**
 * An implementation of the graph using a matrix for representing the edges
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public class MatrixGraph<V, W extends Number> extends AGraph<V, W> {

	final Map<V, Integer> map = new HashMap<>();
	final List<List<W>> matrix = new ArrayList<>();

	@Override
	public Iterator<V> iterator() {
		return map.keySet().iterator();
	}

	@Override
	protected Graph<V, W> getNewInstance() {
		return new MatrixGraph<>();
	}

	@Override
	protected void addVertex(V vertex) {
		map.put(vertex, map.size());

		List<W> newVert = new ArrayList<>(map.size());
		for (int i=0; i<map.size(); i++)
			newVert.add(null);

		matrix.forEach(list -> list.add(null));
		matrix.add(newVert);
	}

	@Override
	protected boolean containsVertex(V vertex) {
		return map.containsKey(vertex);
	}

	@Override
	protected void removeVertex(V vertex) {
		int x = map.remove(vertex);
		map.replaceAll((vert, index) -> index>x? index-1:index);

		matrix.remove(x);
		matrix.forEach(list -> {
			int i;
			for(i=x; i<list.size()-1; i++)
				list.set(i, list.get(i+1));
			if(--i>0)
				list.remove(i);
		});
	}

	@Override
	protected void removeAllVertices() {
		map.clear();
		matrix.clear();
	}

	@Override
	protected boolean containsEdgeImpl(V vertex1, V vertex2) {
		try {
			return matrix.get(map.get(vertex1)).get(map.get(vertex2)) != null;
		} catch (Exception ignore) {
			return false;
		}
	}

	@Override
	protected W addEdgeImpl(V vertex1, V vertex2, W weight) {
		return matrix.get(map.get(vertex1)).set(map.get(vertex2), weight);
	}

	@Override
	protected W getWeightImpl(V vertex1, V vertex2) {
		return matrix.get(map.get(vertex1)).get(map.get(vertex2));
	}

	@Override
	protected Collection<Edge<V, W>> getEdgesOutImpl(V vertex) {
		Set<Edge<V,W>> set = new HashSet<>();
		Map<Integer, V> inverted = new HashMap<>();
		map.keySet().forEach(v -> inverted.put(map.get(v), v));

		List<W> list = matrix.get(map.get(vertex));
		for(int i=0; i<list.size(); i++) {
			W weight = list.get(i);
			if (weight != null)
				set.add(new Edge<>(vertex, inverted.get(i), weight));
		}
		return set;
	}

	@Override
	protected Collection<Edge<V, W>> getEdgesInImpl(V vertex) {
		Set<Edge<V,W>> set = new HashSet<>();
		Map<Integer, V> inverted = new HashMap<>();
		map.keySet().forEach(v -> inverted.put(map.get(v), v));

		int x = map.get(vertex);
		for(int i=0; i<matrix.size(); i++) {
			W weight = matrix.get(i).get(x);
			if (weight != null)
				set.add(new Edge<>(inverted.get(i), vertex, weight));
		}
		return set;
	}

	@Override
	protected void removeEdgeImpl(V vertex1, V vertex2) {
		matrix.get(map.get(vertex1)).set(map.get(vertex2), null);
	}

	@Override
	protected void removeAllOutEdgeImpl(V vertex) {
		matrix.get(map.get(vertex)).replaceAll(var -> null);
	}

	@Override
	protected void removeAllInEdgeImpl(V vertex) {
		int x = map.get(vertex);
		matrix.forEach(list -> list.set(x, null));
	}
}
