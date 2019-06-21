package berack96.lib.graph.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.Vertex;
import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.Dijkstra;
import berack96.lib.graph.visit.impl.Tarjan;
import berack96.lib.graph.visit.impl.VisitInfo;

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
     * Map that contains the marker as key and a set of all the vertices that has it as the value.<br>
     * This map is build like this for performance in creating the marker for multiple vertices.<br>
     * If you flip the parameters (object and set) then has more performance over the single vertex.
     */
    private final Map<Object, Set<V>> markers = new HashMap<>();

    /**
     * Need this variable for not calculating each time the SCC or the cyclic part if the graph doesn't change
     */
    private Tarjan<V, W> tarjan = null;

    /**
     * Need this variable for not calculating each time the distance from a vertex to all his destinations if the graph doesn't change
     */
    private Map<V, Dijkstra<V, W>> dijkstra = new HashMap<>();

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
        edges.put(vertex, new HashMap<>());
        graphChanged();
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
            edges.remove(vertex);
            edges.forEach((v, map) -> map.remove(vertex));
            markers.forEach((mark, set) -> set.remove(vertex));
            graphChanged();
        }
    }

    @Override
    public void removeAllVertex() {
        edges.clear();
        markers.clear();
        graphChanged();
    }

    @Override
    public boolean contains(V vertex) throws NullPointerException {
        checkNull(vertex);
        return edges.containsKey(vertex);
    }
    
    @Override
    public Collection<Object> marks() {
    	Collection<Object> ret = new HashSet<>();
    	markers.forEach((m, v) -> {
    		if(v.size() > 0)
    			ret.add(m);
    	}); 
    	
    	return ret;
    }

    @Override
    public void mark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);

        Set<V> set = markers.computeIfAbsent(mark, (v) -> new HashSet<>());
        set.add(vertex);
    }

    @Override
    public void unMark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);
        markers.get(mark).remove(vertex);
    }

    @Override
    public void unMark(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        markers.forEach( (mark, set) -> set.remove(vertex) );
    }

	@Override
	public Collection<V> getMarkedWith(Object mark) throws NullPointerException {
		checkNull(mark);
		return markers.computeIfAbsent(mark, (v) -> new HashSet<>());
	}

    @Override
    public Collection<Object> getMarks(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        
        Collection<Object> marks = new HashSet<>();
        markers.forEach( (mark, set) -> {
        	if (set.contains(vertex))
        		marks.add(mark);
        });
        
        return marks;
    }

    @Override
    public void unMarkAll(Object mark) {
        checkNull(mark);
        markers.remove(mark);
    }

    @Override
    public void unMarkAll() {
        markers.clear();
    }

    @Override
    public W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);
        checkNull(weight);

        W old = edges.get(vertex1).put(vertex2, weight);
        graphChanged();
        return old;
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

        edges.get(vertex1).remove(vertex2);
        graphChanged();
    }

    @Override
    public void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        edges.forEach((v, map) -> map.remove(vertex));
        graphChanged();
    }

    @Override
    public void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        edges.put(vertex, new HashMap<>());
        graphChanged();
    }

    @Override
    public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        removeVertex(vertex);
        addVertex(vertex);
    }

    @Override
    public void removeAllEdge() {
        edges.forEach((v, map) -> map.clear());
        graphChanged();
    }

    @Override
    public boolean containsEdge(V vertex1, V vertex2) throws NullPointerException {
        return (contains(vertex1) && contains(vertex2)) && edges.get(vertex1).get(vertex2) != null;
    }

    @Override
    public Collection<V> vertices() {
        return new HashSet<>(edges.keySet());
    }

    @Override
    public Collection<Edge<V, W>> edges() {
        Set<Edge<V, W>> allEdges = new HashSet<>();
        edges.forEach((source, map) -> map.forEach((destination, weight) -> allEdges.add(new Edge<>(source, destination, weight))));
        return allEdges;
    }

    @Override
    public Collection<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
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
    public Collection<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);

        return new HashSet<>(edges.get(vertex).keySet());
    }

    @Override
    public Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
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
    public Graph<V, W> subGraph(final Object...marker) {
        final Graph<V, W> sub = new MapGraph<>();
        final Set<V> allVertices = new HashSet<>();
        final Set<Object> allMarkers = new HashSet<>();
        final boolean isEmpty = (marker == null || marker.length == 0);
        
        if (!isEmpty)
        	for (Object mark: marker)
        		allMarkers.add(mark);

        markers.forEach( (mark, set) -> {
        	if (isEmpty || allMarkers.contains(mark))
        		allVertices.addAll(set);
        });

        if (isEmpty) {
            Collection<V> toAdd = vertices();
            toAdd.removeAll(allVertices);
            allVertices.clear();
            allVertices.addAll(toAdd);
        }

        sub.addAllVertices(allVertices);
        for (V vertex : sub.vertices())
            edges.get(vertex).forEach( (dest, weight) -> {
                try {
                    sub.addEdge(vertex, dest, weight);
                } catch (Exception ignored) {}
            });

        return sub;
    }

    @Override
    public List<Edge<V, W>> distance(V source, V destination) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        checkNullAndExist(source);
        checkNullAndExist(destination);

        Dijkstra<V, W> dijkstra = getDijkstra(source);	/* Cached */
        List<Edge<V, W>> path = dijkstra.getLastDistance().get(destination);
        if (path == null)
            throw new UnsupportedOperationException(NOT_CONNECTED);
        return new ArrayList<>(path);
    }

    @Override
    public Map<V, List<Edge<V, W>>> distance(V source) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(source);
        return new HashMap<>(getDijkstra(source).getLastDistance());	/* Cached */
    }

    @Override
    public Iterator<V> iterator() {
        return edges.keySet().iterator();
    }


    /**
     * Simple function that reset all the caching variables if the graph changed
     */
    private void graphChanged() {
        tarjan = null;
        dijkstra.clear();
    }

    /**
     * Simple function that return the result of the Dijkstra visit, with the starting point as source.<br>
     * It also cache it, so multiple call will return always the same value unless the graph has changed.
     * @param source the source of the visit
     * @return the complete visit
     */
    private Dijkstra<V, W> getDijkstra(V source) {
        if (dijkstra.get(source) == null) {
            Dijkstra<V, W> newDijkstra = new Dijkstra<>();
            newDijkstra.visit(this, source, null);
            dijkstra.put(source, newDijkstra);
        }

        return dijkstra.get(source);
    }

    /**
     * Simple function that return the result of the Tarjan visit.<br>
     * It also cache it, so multiple call will return always the same value unless the graph has changed.
     * @return the tarjan visit
     */
    private Tarjan<V, W> getTarjan() {
        if (tarjan == null) {
            tarjan = new Tarjan<>();
            tarjan.visit(this, null, null);
        }

        return tarjan;
    }

    /**
     * Test if the object passed is null.
     * If it is throw an exception.
     * @param object the object to test
     */
    private void checkNull(Object object) {
        if (object == null)
            throw new NullPointerException(PARAM_NULL);
    }

    /**
     * Check if the vertex passed is null and if exist in the graph.
     * If not then throws eventual exception
     * @param vertex the vertex to test
     */
    private void checkNullAndExist(V vertex) {
        checkNull(vertex);
        if (!edges.containsKey(vertex))
            throw new IllegalArgumentException(VERTEX_NOT_CONTAINED);
    }
}
