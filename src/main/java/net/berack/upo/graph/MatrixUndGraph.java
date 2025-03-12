package net.berack.upo.graph;

import java.util.*;

import net.berack.upo.Graph;
import net.berack.upo.GraphUndirected;

public class MatrixUndGraph<V> extends GraphUndirected<V> {

    Map<V, Integer> map = getDefaultMap();
    private int[][] matrix = new int[0][0];

    @Override
    protected Graph<V> getNewInstance() {
        return new MatrixUndGraph<>();
    }

    @Override
    public boolean contains(V vertex) throws NullPointerException {
        check(vertex);
        return map.containsKey(vertex);
    }

    @Override
    public void add(V vertex) throws NullPointerException {
        check(vertex);
        if (map.containsKey(vertex))
            removeAllEdge(vertex);
        else {
            map.put(vertex, map.size());
            matrix = modifyMatrix(map.size());
        }
    }

    @Override
    public void remove(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        int x = map.remove(vertex);
        int newSize = map.size();

        int[][] newMatrix = new int[newSize][];
        for (int i = 0; i < newSize; i++)
            newMatrix[i] = i < x ? matrix[i] : new int[i];

        for (int i = x; i < newSize; i++)
            for (int j = 0; j < newMatrix[i].length; j++)
                newMatrix[i][j] = matrix[i + 1][j + (j < x ? 0 : 1)];

        matrix = newMatrix;
        map.replaceAll((_, index) -> index > x ? index - 1 : index);
    }

    @Override
    public int getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex1, vertex2);
        int x = map.get(vertex1);
        int y = map.get(vertex2);
        return x == y ? 0 : matrix[Math.max(x, y)][Math.min(x, y)];
    }

    @Override
    public int addEdge(V vertex1, V vertex2, int weight) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex1, vertex2);
        int x = map.get(vertex1);
        int y = map.get(vertex2);
        int max = Math.max(x, y);
        int min = Math.min(x, y);

        int old = matrix[max][min];
        matrix[max][min] = weight;
        return old;
    }

    @Override
    public Set<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        V[] inverted = getInverted();
        Set<V> set = getDefaultSet();
        int x = map.get(vertex);
        for (int i = 0; i < matrix.length; i++)
            if (i < x && matrix[x][i] != 0)
                set.add(inverted[i]);
            else if (i > x && matrix[i][x] != 0)
                set.add(inverted[i]);
        return set;
    }

    @Override
    public Set<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
        return getChildren(vertex);
    }

    @Override
    public Set<Edge<V>> edges() {
        V[] inverted = getInverted();
        Set<Edge<V>> edges = getDefaultSet();

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                if (matrix[i][j] != NO_EDGE)
                    edges.add(new Edge<>(inverted[i], inverted[j], matrix[i][j]));
        return edges;
    }

    @Override
    public int degree(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        int x = map.get(vertex);
        int degree = 0;
        for (int i = 0; i < x; i++)
            if (matrix[x][i] != NO_EDGE)
                degree++;
        for (int i = x; i < matrix.length; i++)
            if (matrix[i][x] != NO_EDGE)
                degree++;
        return degree;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int numberOfEdges() {
        int num = 0;
        for (int[] ints : matrix)
            for (int edge : ints)
                if (edge != NO_EDGE)
                    num++;
        return num;
    }

    @Override
    public Iterator<V> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        int x = map.get(vertex);

        Arrays.fill(matrix[x], NO_EDGE);
        for (int i = x + 1; i < matrix.length; i++)
            matrix[i][x] = NO_EDGE;
    }

    @Override
    public void removeAllEdge() {
        for (int[] adj : matrix)
            Arrays.fill(adj, NO_EDGE);
    }

    private int[][] modifyMatrix(int newSize) {
        int oldSize = matrix.length;
        if (newSize <= oldSize)
            return matrix;
        int[][] newMatrix = new int[newSize][];
        System.arraycopy(matrix, 0, newMatrix, 0, oldSize);

        for (int i = oldSize; i < newSize; i++) {
            newMatrix[i] = new int[i];
            Arrays.fill(newMatrix[i], NO_EDGE);
        }

        return newMatrix;
    }

    @SuppressWarnings("unchecked")
    private V[] getInverted() {
        V[] invert = (V[]) new Object[map.size()];
        map.forEach((v, i) -> invert[i] = v);
        return invert;
    }
}
