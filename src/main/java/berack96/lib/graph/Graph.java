package berack96.lib.graph;

import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.BFS;
import berack96.lib.graph.visit.impl.Dijkstra;
import berack96.lib.graph.visit.impl.VisitInfo;

import java.util.*;
import java.util.function.Consumer;

/**
 * An abstract class for the graphs.<br>
 * This class is used for the graphs in general.<br>
 * There are more specific {@link GraphDirected} and {@link GraphUndirected} edges graph interfaces.<br>
 *
 * @param <V> The Object that represent a vertex
 * @author Berack96
 */
public abstract class Graph<V> implements Iterable<V> {

    //------------------- STAIC -----------------

    public static final int NO_EDGE = 0;
    public final static String NOT_CONNECTED = "The source vertex doesn't have a path that reach the destination";
    public final static String PARAM_NULL = "The parameter must not be null";
    public final static String VERTEX_NOT_CONTAINED = "The vertex must be contained in the graph";

    /**
     * Create the default map. All operations are O(log(n))<br>
     * It returns a TreeMap with a ObjectComparator as comparator.<br>
     * This way all the graphs will use the same maps.<br>
     * It is not required to use this method, but it is highly recommended.<br>
     * 
     * @return A newly created TreeMap instance with ObjectsComparator as comparator
     */
    public final static <X, Y> Map<X, Y> getDefaultMap() {
        return new TreeMap<X, Y>(ObjectsComparator.instance);
    }

    /**
     * Create the default set. All operations are O(log(n))<br>
     * It returns a TreeSet with a ObjectComparator as comparator.<br>
     * This way all the graphs will use the same sets.<br>
     * It is not required to use this method, but it is highly recommended.<br>
     * 
     * @return A newly created TreeSet instance with ObjectsComparator as comparator
     */
    public final static <X> Set<X> getDefaultSet() {
        return new TreeSet<X>(ObjectsComparator.instance);
    }

    //------------------- INSTANCE -----------------

    /**
     * Map that contains the vertex as key and a set of all the marker associated with it.
     */
    private final Map<V, Set<Object>> markers = getDefaultMap();

    /**
     * Get a new instance of this graph.
     *
     * @return A new instance of the graph
     */
    protected abstract Graph<V> getNewInstance();

    /**
     * Check if the vertex passed is contained in the graph or not.<br>
     * The vertex V1 is contained in the graph G, if and only if:<br>
     * exist V2 in G such that V2 == V1
     *
     * @param vertex the vertex to check
     * @return true if the vertex is contained, false otherwise
     */
    public abstract boolean contains(V vertex) throws NullPointerException;

    /**
     * Get an instance of the vertex linked with this graph.<br>
     * For more info see {@link Vertex}
     *
     * @param vertex the vertex
     * @return a vertex
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public final Vertex<V> get(V vertex) throws IllegalArgumentException {
        checkVert(vertex);
        return new Vertex<>(this, vertex);
    }

    /**
     * Add the vertex to the graph. If it's already in the graph it will be replaced and all its edges will be reset.<br>
     * Of course the vertex added will have no marks nor edge to any other vertex nor form any other vertex.
     *
     * @param vertex the vertex to add
     * @throws NullPointerException if the vertex is null
     */
    public abstract void add(V vertex) throws NullPointerException;

    /**
     * Add the specified vertex to the graph only if the graph doesn't contains it.<br>
     * The graph contains a vertex only if the method {@link #contains(Object)} returns true.
     *
     * @param vertex the vertex to add
     * @return true if it adds a vertex, false if it was already in the graph
     * @throws NullPointerException if the vertex is null
     */
    public final boolean addIfAbsent(V vertex) throws NullPointerException {
        if (contains(vertex))
            return false;
        add(vertex);
        return true;
    }

    /**
     * Add all the vertices contained in the collection to the graph.<br>
     * If a vertex is contained in the collection and in the graph is ignored and it will not be replaced.<br>
     * Null vertices will be ignored and they will not be added to the graph.
     *
     * @param vertices a collection of the vertices to add
     * @throws NullPointerException if the set is null
     */
    public void addAll(Collection<V> vertices) throws NullPointerException {
        check(vertices);
        for (V vertex : vertices)
            addIfAbsent(vertex);
    }

    /**
     * Remove the selected vertex from the graph.<br>
     * After this method's call the vertex will be no longer present in the graph, and nether all his edges and marks.
     *
     * @param vertex the vertex to remove
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained
     */
    public abstract void remove(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all the vertex contained in the graph.<br>
     * After this method's call the graph will be empty; no vertices nor edges.
     */
    public void removeAll() {
        unMarkAll();
        for (V vertex : vertices())
            remove(vertex);
    }

    /**
     * Check if the edge between the two vertex passed is contained in the graph or not.<br>
     * If one of the two vertices is not contained in the graph, then even the edge isn't
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @return true if the edge is contained, false otherwise
     * @throws NullPointerException if one of the parameters is null
     */
    public boolean containsEdge(V vertex1, V vertex2) throws NullPointerException {
        try {
            return getWeight(vertex1, vertex2) != NO_EDGE;
        } catch (IllegalArgumentException ignore) {
            return false;
        }
    }

    /**
     * Get the weight of the selected edge.<br>
     * If the edge doesn't exist, then 0 is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @return the weight previously set, or 0 if the edge doesn't exist
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public abstract int getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException;

    /**
     * Add an edge between the two vertex.<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param edge the edge to add
     * @return 0 or the previous weight of the edge if there was already one
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public final int addEdge(Edge<V> edge) throws NullPointerException, IllegalArgumentException {
        return addEdge(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    /**
     * Add an edge between the two vertex.<br>
     * This method will overwrite any existing edge between the two vertices.<br>
     * By default using this method will set the edge to the value 1.
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public final void addEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
        addEdge(vertex1, vertex2, 1);
    }

    /**
     * Add an edge between the two vertex.<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it's value is returned.<br>
     * If the weight passed is equals to 0 or {@link Graph#NO_EDGE}, then
     * the edge will be removed.
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @param weight  the weight of the edge
     * @return 0 or the previous weight of the edge if there was already one
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public abstract int addEdge(V vertex1, V vertex2, int weight) throws NullPointerException, IllegalArgumentException;

    /**
     * This particular function add an edge to the graph.<br>
     * If one of the two, or both vertices of the edge aren't contained in the graph, then the vertices will be added.<br>
     * This method will overwrite any existing edge between the two vertices.<br>
     * If there was a previous edge then it is returned
     *
     * @param edge the edge to add
     * @return 0 or the previous weight of the edge if there was already one
     * @throws NullPointerException if one of the parameter is null
     */
    public final int addEdgeAndVertices(Edge<V> edge) throws NullPointerException, IllegalArgumentException {
        return addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    /**
     * This particular function add an edge to the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * This method will overwrite any existing edge between the two vertices.<br>
     * By default using this method will set the edge to the value 1.
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @throws NullPointerException if one of the parameter is null
     */
    public final int addEdgeAndVertices(V vertex1, V vertex2) throws NullPointerException {
        return addEdgeAndVertices(vertex1, vertex2, 1);
    }

    /**
     * This particular function add an edge to the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * This method will overwrite any existing edge between the two vertices.<br>
     * If there was a previous edge then it is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @param weight  the weight of the edge
     * @return 0 or the previous weight of the edge if there was already one
     * @throws NullPointerException if one of the parameter is null
     */
    public final int addEdgeAndVertices(V vertex1, V vertex2, int weight) throws NullPointerException {
        addIfAbsent(vertex1);
        addIfAbsent(vertex2);
        return addEdge(vertex1, vertex2, weight);
    }

    /**
     * Add all the edges of the collection to the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * Any null edges will be ignored.<br>
     * This method will overwrite any existing edge between the two vertices.
     *
     * @param edges the edges to add
     * @throws NullPointerException if the set is null
     */
    public void addAllEdges(Collection<Edge<V>> edges) throws NullPointerException {
        edges.forEach(edge -> addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight()));
    }

    /**
     * Remove the edge between the two vertex by setting it's value to 0.<br>
     * If the edge doesn't exist, then this call does nothing.<br>
     * This method is equivalent to calling {@link Graph#addEdge(Object, Object, int)} )}
     * with the weight set to {@link Graph#NO_EDGE}
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public void removeEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
        addEdge(vertex1, vertex2, NO_EDGE);
    }

    /**
     * Remove all edges form a particular vertex of the graph.<br>
     * After this method's call the selected vertex will have 0 edges.<br>
     * It will be no longer possible to reach this vertex from any other vertex, and vice versa.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        unMark(vertex);
        remove(vertex);
        add(vertex);
    }

    /**
     * Remove all the edges of the graph.<br>
     * After this method's call the graph will have only vertices, and no edge.
     */
    public void removeAllEdge() {
        Set<V> vertices = vertices();
        removeAll();
        addAll(vertices);
    }

    /**
     * Retrieve all the edges of a particular vertex.<br>
     *
     * @param vertex a vertex of the graph
     * @return a set of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public abstract Set<Edge<V>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices that are children of the vertex passed as parameter.<br>
     * The vertices V(0-N) that are 'children' of a vertex V1, are all the vertices that have an edge
     * where V1 is the source of that edge.
     *
     * @param vertex the source vertex
     * @return a set of vertices
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public abstract Set<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices that have the vertex passed as their child.<br>
     * Basically is the opposite of {@link #getChildren(Object)}
     *
     * @param vertex a vertex of the graph
     * @return a set of ancestors of the vertex
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public abstract Set<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the marks of this graph.<br>
     * Specifically it will return a Set of marks where every mark<br>
     * as associated at least one vertex of the graph.<br>
     * If the graph doesn't have vertex marked then it is returned an empty Set.
     *
     * @return a set of marks
     */
    public final Set<Object> marks() {
        Set<Object> ret = getDefaultSet();
        markers.forEach((v, set) -> ret.addAll(set));
        return ret;
    }

    /**
     * Add to the specified vertex the mark passed.<br>
     * A vertex can have multiple marker.<br>
     * The null marker cannot be used.
     *
     * @param vertex the vertex to mark
     * @param mark   the mark to add
     * @throws NullPointerException     if one of the param is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public final void mark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        check(mark);
        checkVert(vertex);
        Set<Object> marks = markers.computeIfAbsent(vertex, v -> getDefaultSet());
        marks.add(mark);
    }

    /**
     * Remove the selected mark from the vertex.<br>
     *
     * @param vertex the vertex where remove the mark
     * @param mark   the mark to remove
     * @throws NullPointerException     if a param is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public final void unMark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        check(mark);
        checkVert(vertex);
        markers.computeIfPresent(vertex, (v, set) -> {
            set.remove(mark);
            if (set.size() > 0)
                return set;
            return null;
        });
    }

    /**
     * Unmark the vertex selected.<br>
     * After this call the vertex will not have any marked object to himself.
     *
     * @param vertex the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public final void unMark(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        try {
            markers.remove(vertex).clear();
        } catch (Exception ignore) {
        }
    }

    /**
     * Get all the vertices that are marked with the specific mark passed.<br>
     * If there aren't vertices with that mark then it is returned an empty set.<br>
     *
     * @param mark the mark
     * @return a set of all the vertices that are marked with that specific mark
     * @throws NullPointerException if the mark is null
     */
    public final Set<V> getMarkedWith(Object mark) throws NullPointerException {
        check(mark);
        Set<V> vertices = getDefaultSet();
        markers.forEach((v, set) -> {
            if (set.contains(mark))
                vertices.add(v);
        });
        return vertices;
    }

    /**
     * Get all the marker of this vertex.<br>
     * If the vertex doesn't have any mark, then it will return an empty set.<br>
     * Note: modifying the returned Set affect the marker of the vertex.
     *
     * @param vertex the vertex
     * @return a set of all the mark to the vertex or an empty Set if none
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public final Set<Object> getMarks(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        return markers.getOrDefault(vertex, getDefaultSet());
    }

    /**
     * Remove the selected mark from all the vertices
     *
     * @param mark the mark to remove
     * @throws NullPointerException if the mark is null
     */
    public final void unMarkAll(Object mark) throws NullPointerException {
        check(mark);
        Set<V> toRemove = getDefaultSet();
        markers.forEach((v, set) -> {
            set.remove(mark);
            if (set.size() == 0)
                toRemove.add(v);
        });
        markers.keySet().removeAll(toRemove);
    }

    /**
     * Remove all the marker to all the vertex.<br>
     * After this call the {@link #getMarks(Object)} applied to any vertex will return an empty set
     */
    public final void unMarkAll() {
        markers.values().forEach(Set::clear);
        markers.clear();
    }

    /**
     * Get all the vertices in the graph.<br>
     * If the graph doesn't contains vertices, it'll return an empty Set.<br>
     *
     * @return a set that include all the vertices
     */
    public Set<V> vertices() {
        Set<V> vertices = getDefaultSet();
        forEach(vertices::add);
        return vertices;
    }

    /**
     * Get all the edges in the graph.<br>
     * If the graph doesn't contains edges, it'll return an empty Set.<br>
     *
     * @return a Set that include all the edges
     */
    public abstract Set<Edge<V>> edges();

    /**
     * Tells the degree of a vertex.<br>
     * The degree of a vertex is the quantity of edges that have.<br>
     * Basically, it'll count how many edge it have.
     *
     * @param vertex a vertex of the graph
     * @return the degree of the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public abstract int degree(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Tells how many vertices are in the graph.
     *
     * @return the number of vertices
     */
    public abstract int size();

    /**
     * Tells how many edges are in the graph.
     *
     * @return the number of edges
     */
    public abstract int numberOfEdges();

    /**
     * Visit the graph accordingly to the strategy that is passed.<br>
     * Some strategy can accept a source vertex null, because they visit all the graph anyway.
     *
     * @param source   the starting vertex for the visit
     * @param strategy the algorithm for visiting the graph
     * @param visit    the function to apply at each vertex visited
     * @return an info of the visit if provided by the strategy
     * @throws NullPointerException          if one of the parameter is null (except the consumer)
     * @throws UnsupportedOperationException in the case the visit cannot be applied to the graph
     */
    public final VisitInfo<V> visit(V source, VisitStrategy<V> strategy, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        return strategy.visit(this, source, visit);
    }

    /**
     * Get a sub-graph of the current one based on the maximum depth that is given.<br>
     * If the depth is 1 then only the source and it's children will be in the sub-graph.<br>
     * If the depth is 2 then only the source, it's children and it's children of it's children will be in the sub-graph.<br>
     * And so on.<br>
     * Of course the sub-graph will contain the edges that link the vertices, but only the one selected.
     *
     * @param source the source vertex
     * @param depth  the maximum depth (must be a positive number, if &lt;=0 a graph containing only the source is returned)
     * @return a sub-graph of the original
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained
     */
    public final Graph<V> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException {
        checkVert(source);
        Graph<V> sub = getNewInstance();
        Set<V> vertices = getDefaultSet();
        new BFS<V>().setMaxDepth(Math.max(depth, 0)).visit(this, source, vertices::add);

        sub.addAll(vertices);
        for (V src : vertices)
            for (V dest : getChildren(src))
                if (sub.contains(dest))
                    sub.addEdge(src, dest, this.getWeight(src, dest));
        return sub;
    }

    /**
     * Get a sub-graph of the current one with only the vertex marked with the selected markers. (OR set operation)<br>
     * Each vertex will have all his markers and his edges, but only the ones with the destination marked with the same marker.<br>
     * If the marker is not specified or is null then the returning graph will have all the vertices that are not marked by any marker.<br>
     * If in the list of markers there is a null marker it will be skipped.<br>
     * If the graph doesn't contain any vertex with any marker passed then an empty graph is returned.
     *
     * @param marker one or more markers
     * @return a sub-graph of the current graph
     */
    public final Graph<V> subGraph(Object... marker) {
        final Graph<V> sub = getNewInstance();
        final Set<V> allVertices = getDefaultSet();
        final Set<Object> allMarkers = getDefaultSet();
        
        if (marker != null && marker.length > 0)
            for(int i=0; i<marker.length; i++)
                if(marker[i] != null)
                    allMarkers.add(marker[i]);
        
        if(allMarkers.size() > 0)
            markers.forEach((v, set) -> {
                if (!Collections.disjoint(allMarkers, set))
                    allVertices.add(v);
            });
        else {
            Set<V> toAdd = vertices();
            toAdd.removeAll(markers.keySet());
            allVertices.addAll(toAdd);
        }

        sub.addAll(allVertices);
        for (V src : sub.vertices()) {
            for (Object mark : getMarks(src))
                sub.mark(src, mark);
            for (V dest : getChildren(src))
                if (sub.contains(dest))
                    sub.addEdge(src, dest, getWeight(src, dest));
        }
        return sub;
    }

    /**
     * Get the minimum path from the source vertex to the destination vertex.<br>
     * If the source vertex can't reach the destination, then an exception is thrown.
     *
     * @param source      the vertex where to start
     * @param destination the destination chosen
     * @return an ordered list of edges from source to destination that represent the minimum path between the two vertices
     * @throws NullPointerException          if one of the parameter is null (except the consumer)
     * @throws IllegalArgumentException      if the vertex is not in the graph
     * @throws UnsupportedOperationException if from the source it's not possible to reach the destination
     */
    public final List<Edge<V>> distance(V source, V destination) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        checkVert(source, destination);
        List<Edge<V>> path = distance(source).get(destination);
        if (path == null)
            throw new UnsupportedOperationException(NOT_CONNECTED);
        return path;
    }

    /**
     * Get the minimum path from the source vertex to all the possible reachable vertices.
     *
     * @param source the vertex where to start
     * @return a map containing all the possible reachable vertices from the source and the minimum path to reach them
     * @throws NullPointerException     if one of the parameter is null (except the consumer)
     * @throws IllegalArgumentException if the vertex is not in the graph
     */
    public final Map<V, List<Edge<V>>> distance(V source) throws NullPointerException, IllegalArgumentException {
        checkVert(source);
        Dijkstra<V> dijkstra = new Dijkstra<>();
        dijkstra.visit(this, source, null);
        return dijkstra.getLastDistance();
    }

    /**
     * Check if the object passed is not null.
     * If it's null then throws eventual exception
     *
     * @param objects the objects to test
     */
    protected final void check(Object... objects) {
        for (Object obj : objects)
            if (obj == null)
                throw new NullPointerException(PARAM_NULL);
    }

    /**
     * Check if the vertex passed is not null and if exist in the graph.
     * If not then throws eventual exception
     *
     * @param vertices the vertices to test
     */
    @SafeVarargs
    protected final void checkVert(V... vertices) {
        check((Object[]) vertices);
        for (V vert : vertices)
            try {
                if (!contains(vert))
                    throw new IllegalArgumentException(VERTEX_NOT_CONTAINED);
            } catch (ClassCastException ignore) {
            }
    }
}
