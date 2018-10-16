package berack96.sim.util.graph;

import berack96.sim.util.graph.visit.Dijkstra;
import berack96.sim.util.graph.visit.Tarjan;
import berack96.sim.util.graph.visit.VisitInfo;
import berack96.sim.util.graph.visit.VisitStrategy;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
public class MapGraph<V, W extends Number> implements Graph<V, W> {

    /**
     * Map that contains the edges from a vertex to another<br>
     * The first vertex is the vertex where start the edge, the second one is where the edge goes<br>
     * If an edge exist, then it's weight is returned
     */
    private final Map<V, Map<V, W>> edges = new HashMap<>();

    /**
     * Map that contains the vertex as key and all the marker as the value associated with that vertex.
     */
    private final Map<V, Set<String>> marked = new HashMap<>();

    /**
     * Need this variable for not calculating each time the SCC or the cyclic part if the graph doesn't change
     */
    private Tarjan<V, W> tarjan = null;

    /**
     * Need this variable for not calculating each time the distance from a vertex to all his destinations if the graph doesn't change
     */
    private Map<V, Dijkstra<V, W>> dijkstra = null;

    @Override
    public boolean isCyclic() {
        return stronglyConnectedComponents().size() != numberOfVertices();
    }

    @Override
    public boolean isDAG() {
        return !isCyclic();
    }

    @Override
    public Vertex<V> getVertex(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        return new Vertex<>(this, vertex);
    }

    @Override
    public void addVertex(V vertex) throws NullPointerException {
        checkNull(vertex);
        graphChanged();
        edges.put(vertex, new HashMap<>());
    }

    @Override
    public boolean addVertexIfAbsent(V vertex) throws NullPointerException {
        if (contains(vertex))
            return false;
        addVertex(vertex);
        return true;
    }

    @Override
    public void addAllVertices(Collection<V> vertices) throws NullPointerException {
        checkNull(vertices);
        vertices.forEach(this::addVertexIfAbsent);
    }

    @Override
    public void removeVertex(V vertex) throws NullPointerException {
        if (contains(vertex)) {
            graphChanged();
            edges.remove(vertex);
            edges.forEach((v, map) -> map.remove(vertex));
        }
    }

    @Override
    public void removeAllVertex() {
        graphChanged();
        edges.clear();
    }

    @Override
    public boolean contains(V vertex) throws NullPointerException {
        checkNull(vertex);
        return edges.containsKey(vertex);
    }

    @Override
    public void mark(V vertex, String mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);

        Set<String> set = marked.computeIfAbsent(vertex, (m) -> new HashSet<>());
        set.add(mark);
    }

    @Override
    public void unMark(V vertex, String mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);
        marked.get(vertex).remove(mark);
    }

    @Override
    public void unMark(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        marked.get(vertex).clear();
    }

    @Override
    public Set<String> getMarks(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        return marked.computeIfAbsent(vertex, (m) -> new HashSet<>());
    }

    @Override
    public void unMarkAll(String mark) {
        checkNull(mark);
        marked.forEach((v, m) -> m.remove(mark));
    }

    @Override
    public void unMarkAll() {
        marked.clear();
    }

    @Override
    public W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);
        checkNull(weight);

        graphChanged();
        return edges.get(vertex1).put(vertex2, weight);
    }

    @Override
    public W addEdge(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
        return addEdge(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    @Override
    public W addEdgeAndVertices(V vertex1, V vertex2, W weight) throws NullPointerException {
        addVertexIfAbsent(vertex1);
        addVertexIfAbsent(vertex2);
        return addEdge(vertex1, vertex2, weight);
    }

    @Override
    public W addEdgeAndVertices(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
        return addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    @Override
    public void addAllEdges(Collection<Edge<V, W>> edges) throws NullPointerException {
        edges.forEach((edge) -> addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight()));
    }

    @Override
    public W getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);

        return edges.get(vertex1).get(vertex2);
    }

    @Override
    public void removeEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);

        graphChanged();
        edges.get(vertex1).remove(vertex2);
    }

    @Override
    public void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        graphChanged();
        edges.forEach((v, map) -> map.remove(vertex));
    }

    @Override
    public void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        graphChanged();
        edges.put(vertex, new HashMap<>());
    }

    @Override
    public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        removeVertex(vertex);
        addVertex(vertex);
    }

    @Override
    public void removeAllEdge() {
        graphChanged();
        edges.forEach((v, map) -> map.clear());
    }

    @Override
    public boolean containsEdge(V vertex1, V vertex2) throws NullPointerException {
        return (contains(vertex1) && contains(vertex2)) && edges.get(vertex1).get(vertex2) != null;
    }

    @Override
    public Set<V> vertices() {
        return new HashSet<>(edges.keySet());
    }

    @Override
    public Set<Edge<V, W>> edges() {
        Set<Edge<V, W>> allEdges = new HashSet<>();
        edges.forEach((source, map) -> map.forEach((destination, weight) -> allEdges.add(new Edge<>(source, destination, weight))));
        return allEdges;
    }

    @Override
    public Set<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        Set<Edge<V, W>> set = new HashSet<>();
        edges.forEach((source, map) -> map.forEach((destination, weight) -> {
            if (destination.equals(vertex) || source.equals(vertex))
                set.add(new Edge<>(source, destination, weight));
        }));
        return set;
    }

    @Override
    public Collection<Edge<V, W>> getEdgesIn(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        Collection<Edge<V, W>> collection = new HashSet<>();
        edges.forEach((source, edge) -> {
            if (edge.get(vertex) != null)
                collection.add(new Edge<>(source, vertex, edge.get(vertex)));
        });

        return collection;
    }

    @Override
    public Collection<Edge<V, W>> getEdgesOut(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        Collection<Edge<V, W>> collection = new HashSet<>();
        edges.get(vertex).forEach((dest, weight) -> collection.add(new Edge<>(vertex, dest, weight)));

        return collection;
    }

    @Override
    public Set<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        return new HashSet<>(edges.get(vertex).keySet());
    }

    @Override
    public Set<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        Set<V> set = new HashSet<>();
        edges.forEach((v, map) -> {
            if (map.containsKey(vertex)) set.add(v);
        });
        return set;
    }

    @Override
    public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        AtomicInteger sum = new AtomicInteger();
        edges.forEach((v, map) -> {
            if (map.containsKey(vertex))
                sum.getAndIncrement();
        });

        return sum.get();
    }

    @Override
    public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        return edges.get(vertex).size();
    }

    @Override
    public int degree(V vertex) throws NullPointerException, IllegalArgumentException {
        return degreeIn(vertex) + degreeOut(vertex);
    }

    @Override
    public int numberOfVertices() {
        return edges.size();
    }

    @Override
    public int numberOfEdges() {
        AtomicInteger sum = new AtomicInteger(0);
        edges.forEach((v, map) -> sum.getAndAdd(map.size()));

        return sum.get();
    }

    @Override
    public VisitInfo<V> visit(V source, VisitStrategy<V, W> strategy, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        return strategy.visit(this, source, visit);
    }

    @Override
    public Graph<V, W> transpose() {
        Graph<V, W> graph = new MapGraph<>();
        for (V vertex : edges.keySet())
            graph.addVertex(vertex);

        edges.forEach((source, map) -> map.forEach((destination, weight) -> graph.addEdge(destination, source, weight)));

        return graph;
    }

    @Override
    public List<V> topologicalSort() throws UnsupportedOperationException {
        if (!isDAG())
            throw new UnsupportedOperationException(NOT_DAG);
        return getTarjan().getTopologicalSort();
    }

    @Override
    public Collection<Collection<V>> stronglyConnectedComponents() {
        return getTarjan().getSCC();
    }

    @Override
    public Graph<V, W> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException {
        Graph<V, W> sub = new MapGraph<>();
        Set<V> vertices = new HashSet<>();

        int finalDepth = depth > 0 ? depth : 0;
        VisitStrategy<V, W> strategy = (graph, sourceVertex, visit) -> {
            int currentDepth = 0;
            final LinkedList<Map.Entry<V, Integer>> toVisitChildren = new LinkedList<>();
            toVisitChildren.add(new AbstractMap.SimpleEntry<>(sourceVertex, 0));
            vertices.add(source);

            while (!toVisitChildren.isEmpty() && currentDepth + 1 <= finalDepth) {
                final Map.Entry<V, Integer> current = toVisitChildren.removeFirst();
                currentDepth = current.getValue() + 1;
                final int finalCurrentDepth = currentDepth;

                for (V child : graph.getChildren(current.getKey()))
                    if (!vertices.contains(child)) {
                        toVisitChildren.addLast(new AbstractMap.SimpleEntry<>(child, finalCurrentDepth));
                        vertices.add(child);
                    }
            }
            return null;
        };

        strategy.visit(this, source, null);

        sub.addAllVertices(vertices);
        for (V vertex : vertices)
            getEdgesOut(vertex).forEach((edge) -> {
                try {
                    sub.addEdge(edge);
                } catch (Exception ignored) {
                }
            });

        return sub;
    }

    @Override
    public Graph<V, W> subGraph(final String marker) {
        final Graph<V, W> graph = new MapGraph<>();
        final Set<V> allVertices = new HashSet<>();

        marked.forEach((vertex, mark) -> {
            if (mark.contains(marker) || (marker == null && !mark.isEmpty()))
                allVertices.add(vertex);
        });

        if (marker == null) {
            Collection<V> toAdd = graph.vertices();
            toAdd.removeAll(allVertices);
            allVertices.clear();
            allVertices.addAll(toAdd);
        }

        graph.addAllVertices(allVertices);
        for (V vertex : graph.vertices())
            getEdgesOut(vertex).forEach((edge) -> {
                try {
                    graph.addEdge(edge);
                } catch (Exception ignored) {
                }
            });

        return graph;
    }

    @Override
    public List<Edge<V, W>> distance(V source, V destination) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        checkNullAndExist(source);
        checkNullAndExist(destination);

        Dijkstra<V, W> dijkstra = getDijkstra(source);
        List<Edge<V, W>> path = dijkstra.getLastDistance().get(destination);
        if (path == null)
            throw new UnsupportedOperationException(NOT_CONNECTED);
        return new ArrayList<>(path);
    }

    @Override
    public Map<V, List<Edge<V, W>>> distance(V source) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(source);
        return new HashMap<>(getDijkstra(source).getLastDistance());
    }

    @Override
    public Iterator<V> iterator() {
        return edges.keySet().iterator();
    }


    /**
     * Simple function that set all the memory vars at null if the graph changed
     */
    private void graphChanged() {
        tarjan = null;
        dijkstra = null;
    }

    private Dijkstra<V, W> getDijkstra(V source) {
        if (dijkstra == null)
            dijkstra = new HashMap<>();
        if (dijkstra.get(source) == null) {
            Dijkstra<V, W> newDijkstra = new Dijkstra<>();
            newDijkstra.visit(this, source, null);
            dijkstra.put(source, newDijkstra);
        }

        return dijkstra.get(source);
    }

    private Tarjan<V, W> getTarjan() {
        if (tarjan == null) {
            tarjan = new Tarjan<>();
            tarjan.visit(this, null, null);
        }

        return tarjan;
    }

    private void checkNull(Object object) {
        if (object == null)
            throw new NullPointerException(PARAM_NULL);
    }

    private void checkNullAndExist(V vertex) {
        checkNull(vertex);
        if (!edges.containsKey(vertex))
            throw new IllegalArgumentException(VERTEX_NOT_CONTAINED);
    }
}
