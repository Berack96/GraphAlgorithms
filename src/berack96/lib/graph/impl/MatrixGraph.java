package berack96.lib.graph.impl;

import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphDirected;

import java.util.*;

/**
 * An implementation of the graph using a matrix for representing the edges
 *
 * @param <V> the vertex
 * @author Berack96
 */
public class MatrixGraph<V> extends GraphDirected<V> {

	private final Map<V, Integer> map = new HashMap<>();
	private int[][] matrix = new int[0][0];

	@Override
	public Iterator<V> iterator() {
		return map.keySet().iterator();
	}

	@Override
	protected Graph<V> getNewInstance() {
		return new MatrixGraph<>();
	}

	@Override
	public void add(V vertex) {
		check(vertex);
		if (map.containsKey(vertex))
			removeAllEdge(vertex);
		else {
			map.put(vertex, map.size());
			matrix = modifyMatrix(map.size());
		}
	}

	@Override
	public boolean contains(V vertex) {
		check(vertex);
		return map.containsKey(vertex);
	}

	@Override
	public void remove(V vertex) {
		checkVert(vertex);
		int x = map.remove(vertex);
		int newSize = map.size();

		int[][] newMatrix = new int[newSize][newSize];
		for (int i = 0; i < newSize; i++)
			for (int j = 0; j < newSize; j++) {
				int indexI = i + (i < x ? 0 : 1);
				int indexJ = j + (j < x ? 0 : 1);

				newMatrix[i][j] = matrix[indexI][indexJ];
			}

		matrix = newMatrix;
		map.replaceAll((vert, index) -> index > x ? index - 1 : index);
	}

	@Override
	public int addEdge(V vertex1, V vertex2, int weight) {
		checkVert(vertex1, vertex2);
		int i = map.get(vertex1);
		int j = map.get(vertex2);

		int old = matrix[i][j];
		matrix[i][j] = weight;
		return old;
	}

	@Override
	public int getWeight(V vertex1, V vertex2) {
		checkVert(vertex1, vertex2);
		return matrix[map.get(vertex1)][map.get(vertex2)];
	}

	@Override
	public Collection<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		int x = map.get(vertex);
		Collection<V> children = new HashSet<>();
		Map<Integer, V> invert = getInverted();

		for (int i = 0; i < matrix.length; i++)
			if (matrix[x][i] != NO_EDGE)
				children.add(invert.get(i));
		return children;
	}

	@Override
	public Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		int x = map.get(vertex);
		Collection<V> ancestors = new HashSet<>();
		Map<Integer, V> invert = getInverted();

		for (int i = 0; i < matrix.length; i++)
			if (matrix[i][x] != NO_EDGE)
				ancestors.add(invert.get(i));
		return ancestors;
	}

	/**
	 * From here on there are some optimization for the methods of the generic DirectedGraph
	 **/

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public int numberOfEdges() {
		int sum = 0;
		for (int[] adj : matrix)
			for (int edge : adj)
				if (edge != NO_EDGE)
					sum++;
		return sum;
	}

	@Override
	public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		int degree = 0, x = map.get(vertex);
		for (int[] ints : matrix) degree += ints[x] == NO_EDGE ? 0 : 1;
		return degree;
	}

	@Override
	public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		int degree = 0, x = map.get(vertex);
		for (int ints : matrix[x]) degree += ints == NO_EDGE ? 0 : 1;
		return degree;
	}

	@Override
	public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		checkVert(vertex);
		int x = map.get(vertex);
		Arrays.fill(matrix[x], NO_EDGE);
		for (int[] ints : matrix) ints[x] = NO_EDGE;
	}

	@Override
	public void removeAllEdge() {
		for (int[] ints : matrix)
			Arrays.fill(ints, NO_EDGE);
	}

	@Override
	public void removeAll() {
		map.clear();
		matrix = new int[0][0];
	}

	@Override
	public void addAll(Collection<V> vertices) throws NullPointerException {
		check(vertices);
		for (V vert : vertices)
			if (vert != null)
				map.compute(vert, (v, i) -> {
					if (i == null)
						return map.size();
					removeAllEdge(vert);
					return i;
				});
		matrix = modifyMatrix(map.size());
	}

	private int[][] modifyMatrix(int newSize) {
		int oldSize = matrix.length;
		if (newSize <= oldSize)
			return matrix;

		int[][] newMatrix = new int[newSize][newSize];
		for (int[] ints : newMatrix)
			Arrays.fill(ints, NO_EDGE);
		for (int i = 0; i < oldSize; i++)
			System.arraycopy(matrix[i], 0, newMatrix[i], 0, oldSize);

		return newMatrix;
	}

	private Map<Integer, V> getInverted() {
		Map<Integer, V> invert = new HashMap<>(map.size() + 1, 1);
		map.forEach((v, i) -> invert.put(i, v));
		return invert;
	}
}
