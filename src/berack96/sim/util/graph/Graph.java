package berack96.sim.util.graph;

import berack96.sim.util.graph.visit.VisitInfo;
import berack96.sim.util.graph.visit.VisitStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An interface for the graphs.<br>
 * This interface is used for the graphs with Directed edges.<br>
 * A directed edge between V1 and V2 is an edge that has V1 as source and V2 as destination.<br>
 *
 * @param <V> The Object that represent a vertex
 * @param <W> The Object that represent the edge (more specifically the weight of the edge)
 * @author Berack96
 */
public interface Graph<V, W extends Number> extends Iterable<V> {

    String NOT_DAG = "The graph is not a DAG";
    String NOT_CONNECTED = "The source vertex doesn't have a path that reach the destination";
    String PARAM_NULL = "The parameter must not be null";
    String VERTEX_NOT_CONTAINED = "The vertex must be contained in the graph";

    /**
     * Tells if the graph has some cycle.<br>
     * A cycle is detected if visiting the graph G starting from V1 (that is any of the vertex of G),
     * the visit can return to V1 in any point.
     *
     * @return true if has cycle, false otherwise
     */
    boolean isCyclic();

    /**
     * Tells if the graph has the property of DAG (Directed Acyclic Graph).<br>
     * A graph is a DAG only if absent of any cycle. ( see {@link #isCyclic()} )
     *
     * @return true if is a DAG, false otherwise
     */
    boolean isDAG();

    /**
     * Get an instance of the vertex linked with this graph.<br>
     * For more info see {@link Vertex}
     *
     * @param vertex the vertex
     * @return a vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Vertex<V> getVertex(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Add the vertex to the graph. If it's already in the graph it will be replaced.<br>
     * Of course the vertex added will have no edge to any other vertex nor form any other vertex.
     *
     * @param vertex the vertex to add
     * @throws NullPointerException if the vertex is null
     */
    void addVertex(V vertex) throws NullPointerException;

    /**
     * Add the specified vertex to the graph only if the graph doesn't contains it.<br>
     * The graph contains a vertex only if the method {@link #contains(V)} returns true.
     *
     * @param vertex the vertex to add
     * @return true if the vertex is added, false if the graph contains the vertex and therefore the new one is not added
     * @throws NullPointerException if the vertex is null
     */
    boolean addVertexIfAbsent(V vertex) throws NullPointerException;

    /**
     * Add all the vertices contained in the collection to the graph.<br>
     * If a vertex is contained in the collection and in the graph is ignored and it will not be replaced.<br>
     * Null vertices will be ignored and they will not be added to the graph.
     *
     * @param vertices a collection of the vertices to add
     * @throws NullPointerException if the set is null
     */
    void addAllVertices(Collection<V> vertices) throws NullPointerException;

    /**
     * Remove the selected vertex from the graph.<br>
     * After this method's call the vertex will be no longer present in the graph, and nether all his edges.
     *
     * @param vertex the vertex to remove
     * @throws NullPointerException if the vertex is null
     */
    void removeVertex(V vertex) throws IllegalArgumentException;

    /**
     * Remove all the vertex contained in the graph.<br>
     * After this method's call the graph will be empty; no vertices nor edges.
     */
    void removeAllVertex();

    /**
     * Check if the vertex passed is contained in the graph or not.<br>
     * The vertex V1 is contained in the graph G, if and only if:<br>
     * exist V2 in G such that V2.equals(V1)
     *
     * @param vertex the vertex to check
     * @return true if the vertex is contained, false otherwise
     * @throws NullPointerException if the vertex is null
     */
    boolean contains(V vertex) throws NullPointerException;

    /**
     * Add to the specified vertex the mark passed.<br>
     * A vertex can have multiple marker.
     *
     * @param vertex the vertex to mark
     * @param mark   the mark to add
     * @throws NullPointerException     if one of the param is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    void mark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove the selected mark from the vertex.<br>
     *
     * @param vertex the vertex where remove the mark
     * @param mark   the mark to remove
     * @throws NullPointerException     if a param is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    void unMark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException;

    /**
     * Unmark the vertex selected.<br>
     * After this call the vertex will not have any marked object to himself.
     *
     * @param vertex the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    void unMark(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the marker of this vertex.<br>
     * If the vertex doesn't have any mark, then it will return an empty set.<br>
     * Note: this set is linked to the marked vertex, so any changes to the set returned are reflected to the graph.
     *
     * @param vertex the vertex
     * @return all the mark to the vertex or an empty collection if none
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Collection<Object> getMarks(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove the selected mark from all the vertices
     *
     * @param mark the mark to remove
     * @throws NullPointerException if the mark is null
     */
    void unMarkAll(Object mark) throws NullPointerException;

    /**
     * Remove all the marker to all the vertex.<br>
     * After this call the {@link #getMarks(Object)} applied to any vertex will return an empty set
     */
    void unMarkAll();

    /**
     * Add an edge between the two vertex.<br>
     * The edge will be created from the vertex V1 and the vertex V2<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @param weight  the weight of the edge
     * @return null or the previous weight of the edge if there was already one
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException;

    /**
     * Add an edge between the two vertex.<br>
     * The edge will be created from the vertex source of the edge and the vertex destination of it<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param edge the edge to add
     * @return null or the previous weight of the edge if there was already one
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    W addEdge(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException;

    /**
     * This particular function add an edge to the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * The edge will be created from the vertex V1 and the vertex V2<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @param weight  the weight of the edge
     * @return null or the previous weight of the edge if there was already one
     * @throws NullPointerException if one of the parameter is null
     */
    W addEdgeAndVertices(V vertex1, V vertex2, W weight) throws NullPointerException;

    /**
     * This particular function add an edge to the graph.<br>
     * If one of the two, or both vertices of the edge aren't contained in the graph, then the vertices will be added.<br>
     * The edge will be created from the vertex source of the edge and the vertex destination of it<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param edge the edge to add
     * @return null or the previous weight of the edge if there was already one
     * @throws NullPointerException if one of the parameter is null
     */
    W addEdgeAndVertices(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException;

    /**
     * Add all the edges of the collection to the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * Any null edges will be ignored.<br>
     * This method will overwrite any existing edge between the two vertex.
     *
     * @param edges the edges to add
     * @throws NullPointerException if the set is null
     */
    void addAllEdges(Collection<Edge<V, W>> edges) throws NullPointerException;

    /**
     * Get the weight of the selected edge.<br>
     * If the edge doesn't exist, then null is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @return the weight previously set, or null if the edge doesn't exist
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    W getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove the edge between the two vertex.<br>
     * If the edge doesn't exist, then this call does nothing.<br>
     * After this method's call it will be no longer possible to travel from V1 to V2, nether from V2 to V1.
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    void removeEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all the edges that goes in the vertex.<br>
     * After this method's call it will be no longer possible travel to this vertex.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all the edges that start from this vertex.<br>
     * After this method's call it will be no longer possible travel to any vertex from this one.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all edges form a particular vertex of the graph.<br>
     * After this method's call the selected vertex will have 0 edges.<br>
     * It will be no longer possible to reach this vertex from any other vertex, and vice versa.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all the edges of the graph.<br>
     * After this method's call the graph will have only vertices, and no edge.
     */
    void removeAllEdge();

    /**
     * Check if the edge between the two vertex passed is contained in the graph or not.<br>
     * An edge between V1 and V2 is contained in the graph if and only if i can travel from V1 to V2.<br>
     * If one of the two vertices is not contained in the graph, then even the edge isn't
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @return true if the edge is contained, false otherwise
     * @throws NullPointerException if one of the parameters is null
     */
    boolean containsEdge(V vertex1, V vertex2) throws NullPointerException;

    /**
     * Get all the vertices in the graph.<br>
     * If the graph doesn't contains vertices, it'll return an empty collection.<br>
     * Note that this collection is completely different the object used for the vertices, so any modification to this collection will not change the graph.
     *
     * @return an array that include all the vertices
     */
    Collection<V> vertices();

    /**
     * Get all the edges in the graph.<br>
     * If the graph doesn't contains edges, it'll return an empty collection.<br>
     * Note that this collection is completely different than the object used for the edges, so any modification to this collection will not change the graph.
     *
     * @return a collection that include all the edges
     */
    Collection<Edge<V, W>> edges();

    /**
     * Retrieve all the edges of a particular vertex.<br>
     * Note: the edges that are returned are the one that goes IN this vertex AND the edges that goes OUT of it.
     *
     * @param vertex a vertex of the graph
     * @return a collection of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Collection<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Retrieve all the edges of a particular vertex.<br>
     * Note: the edges that are returned are the one that have this vertex as destination and another as source.
     *
     * @param vertex a vertex of the graph
     * @return a collection of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Collection<Edge<V, W>> getEdgesIn(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Retrieve all the edges that goes OUT of a particular vertex.<br>
     * Note: the edges that are returned are the one that have this vertex as source and another one as destination.
     *
     * @param vertex a vertex of the graph
     * @return a collection of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Collection<Edge<V, W>> getEdgesOut(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices that are children of the vertex passed as parameter.<br>
     * The vertices V(0-N) that are 'children' of a vertex V1, are all the vertices that have an edge
     * where V1 is the source of that edge.
     *
     * @param vertex the source vertex
     * @return an array of vertices
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Collection<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices that have the vertex passed as their child.<br>
     * Basically is the opposite of {@link #getChildren(Object)}
     *
     * @param vertex a vertex of the graph
     * @return an array of ancestors of the vertex
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Tells the degree of all the edges that goes to this vertex.<br>
     * Basically, it'll count how many edge towards himself it have.
     *
     * @param vertex a vertex of the graph
     * @return the in degree of the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Tells the degree of all the edges that goes form this vertex to others.<br>
     * Basically, it'll count how many edge towards any other vertex it have.
     *
     * @param vertex a vertex of the graph
     * @return the out degree of the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException;

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
    int degree(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Tells how many vertices are in the graph.
     *
     * @return the number of vertices
     */
    int numberOfVertices();

    /**
     * Tells how many edges are in the graph.
     *
     * @return the number of edges
     */
    int numberOfEdges();

    /**
     * Visit the graph accordingly to the strategy that is passed.<br>
     * This method visit the graph from the source to all the vertex that are reachable form the source.<br>
     * Some strategy can accept a source vertex null, because they visit all the graph anyway.
     *
     * @param source   the source vertex of the visit
     * @param strategy the algorithm for visiting the graph
     * @param visit    the function to apply at each vertex
     * @throws NullPointerException     if one of the parameter is null (except the consumer)
     * @throws IllegalArgumentException if the vertex is not in the graph
     */
    VisitInfo<V> visit(V source, VisitStrategy<V, W> strategy, Consumer<V> visit) throws NullPointerException, IllegalArgumentException;

    /**
     * This method will create a new Graph that is the transposed version of the original.<br>
     * At the end of this method the new graph will have all the edges inverted in orientation.<br>
     * Example: if the graph G contains (V1, V2, V3) as vertex, and (V1->V2, V3->V2) as edges, the transpose graph G' will contain (V1, V2, V3) as vertex, and (V2->V1, V2->V3) as edges.
     *
     * @return a transposed graph of this instance
     */
    Graph<V, W> transpose();

    /**
     * If the current graph is a DAG, it returns a topological sort of this graph.<br>
     * A topological ordering of a graph is a linear ordering of its vertices such that for every directed edge (V1, V2) from vertex V1 to vertex V2, V2 comes before V1 in the ordering.
     *
     * @return an array containing the topological order of the vertices
     * @throws UnsupportedOperationException if the graph is not a DAG (see {@link #isDAG()})
     */
    List<V> topologicalSort() throws UnsupportedOperationException;

    /**
     * The strongly connected components or diconnected components of an arbitrary directed graph form a partition into subgraphs that are themselves strongly connected.
     *
     * @return a collection containing the strongly connected components
     */
    Collection<Collection<V>> stronglyConnectedComponents();

    /**
     * Get a sub-graph of the current one based on the maximum depth that is given.<br>
     * If the depth is 1 then only the source and it's children will be in the sub-graph.<br>
     * If the depth is 2 then only the source, it's children and it's children of it's children will be in the sub-graph.<br>
     * And so on.<br>
     * Of course the sub-graph will contain the edges that link the vertices, but only the one selected.
     *
     * @param source the source vertex
     * @param depth  the maximum depth (must be a positive number, if >=0 a graph containing only the source is returned)
     * @return a sub-graph of the original
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained
     */
    Graph<V, W> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException;

    /**
     * Get a sub-graph of the current one with only the vertex marked with the selected marker.<br>
     * Each vertex will have all his edges, but only the ones with the destination marked with the same marker.<br>
     * If the marker is null then the returning graph will have all the vertices that are not marked by any marker.<br>
     * If the graph doesn't contain any vertex with that marker then an empty graph is returned.
     *
     * @param marker the marker
     * @return a sub-graph of the current graph
     */
    Graph<V, W> subGraph(Object marker);

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
    List<Edge<V, W>> distance(V source, V destination) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException;

    /**
     * Get the minimum path from the source vertex to all the possible reachable vertices.
     *
     * @param source the vertex where to start
     * @return a map containing all the possible reachable vertices from the source and the minimum path to reach them
     * @throws NullPointerException     if one of the parameter is null (except the consumer)
     * @throws IllegalArgumentException if the vertex is not in the graph
     */
    Map<V, List<Edge<V, W>>> distance(V source) throws NullPointerException, IllegalArgumentException;

    // TODO maybe -> STATIC saveOnFile(orString) INSTANCE loadFromFile(orString), but need JSON parser
    // TODO maybe, but i don't think so... STATIC DISTANCE V* -> V*
}
