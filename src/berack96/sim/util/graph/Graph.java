package berack96.sim.util.graph;

import berack96.sim.util.graph.visit.VisitStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * Add all the vertices contained in the set to the graph.<br>
     * If a vertex is contained in the set and in the graph is ignored and it will not be replaced.<br>
     * Null vertices will be ignored and they will not be added to the graph.
     *
     * @param vertices a set containing the vertices
     * @throws NullPointerException if the set is null
     */
    void addAllVertices(Set<V> vertices) throws NullPointerException;

    /**
     * Remove the selected vertex from the graph.<br>
     * After this method's call the vertex will be no longer present in the graph, and nether all his edges.
     *
     * @param vertex the vertex to remove
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    void removeVertex(V vertex) throws NullPointerException, IllegalArgumentException;

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
     * Add an edge between the two vertex.<br>
     * The edge will be created from the vertex V1 and the vertex V2<br>
     * This method will overwrite any existing edge between the two vertex.<br>
     * If there was a previous edge then it is returned
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @param weight  the weight of the edge
     * @return null or the previous value of the edge if there was already one
     * @throws NullPointerException     if one of the parameter is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException;

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
     * @return null or the previous value of the edge if there was already one
     * @throws NullPointerException if one of the parameter is null
     */
    W addEdgeAndVertices(V vertex1, V vertex2, W weight) throws NullPointerException;

    /**
     * Add all the edges of the set in the graph.<br>
     * If one of the two, or both vertices aren't contained in the graph, then the vertices will be added.<br>
     * Any null edges will be ignored.<br>
     * This method will overwrite any existing edge between the two vertex.
     *
     * @param edges the edges to add
     * @throws NullPointerException if the set is null
     */
    void addAllEdges(Set<Edge<V, W>> edges) throws NullPointerException;

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
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Remove all the edges of the graph.<br>
     * After this method's call the graph will have only vertices, and no edge.
     */
    void removeAllEdge();

    /**
     * Check if the edge between the two vertex passed is contained in the graph or not.<br>
     * An edge between V1 and V2 is contained in the graph if and only if i can travel from V1 to V2.
     *
     * @param vertex1 a vertex of the graph
     * @param vertex2 a vertex of the graph
     * @return true if the edge is contained, false otherwise
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    boolean containsEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices in the graph.<br>
     * If the graph doesn't contains vertices, it'll return an empty set.<br>
     * Note that this set is completely different than the set used for the vertices, so any modification of this set will not change the graph.
     *
     * @return a set that include all the vertices
     */
    Set<V> vertices();

    /**
     * Get all the edges in the graph.<br>
     * If the graph doesn't contains edges, it'll return an empty set.<br>
     * Note that this set is completely different than the set used for the edges, so any modification of this set will not change the graph.
     *
     * @return a set that include all the edges
     */
    Set<Edge<V, W>> edges();

    /**
     * Retrieve all the edges from a particular vertex.<br>
     * Note: the edges that is returned are the edges that goes IN this vertex AND the edges that goes OUT of it.
     *
     * @param vertex a vertex of the graph
     * @return a set of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Set<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException;

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
    Set<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * This method will get all the child of the vertex selected.<br>
     * The map created will be a {@link java.util.LinkedHashMap LinkedHashMap}<br>
     * The order in which the vertex are iterated in the map will be from the vertex with the lowest weight to the one with the highest.
     *
     * @param vertex a vertex of the graph
     * @return a map of all the child and their respective weight
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    Map<V, W> getChildrenAndWeight(V vertex) throws NullPointerException, IllegalArgumentException;

    /**
     * Get all the vertices that have the vertex passed as their child.<br>
     * Basically is the opposite of {@link #getChildren(Object)}
     *
     * @param vertex a vertex of the graph
     * @return a set of ancestors of the vertex
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    Set<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException;

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
    void visit(V source, VisitStrategy<V, W> strategy, Consumer<V> visit) throws NullPointerException, IllegalArgumentException;

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
     * @return a set containing the strongly connected components
     */
    Set<Set<V>> stronglyConnectedComponents();

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
     * @throws IllegalArgumentException if the vertex is null
     */
    Graph<V, W> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException;

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

    /**
     * Class used for retrieving the edges of the graph.
     *
     * @param <V> the vertices
     * @param <W> the weight of the edge
     */
    class Edge<V, W extends Number> {
        private final V source;
        private final V destination;
        private final W weight;

        /**
         * Create an final version of this object
         *
         * @param source      the source of the edge
         * @param destination the destination of the edge
         * @param weight      the weight od the edge
         */
        public Edge(V source, V destination, W weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        /**
         * The vertex where the edge goes
         *
         * @return the vertex
         */
        public V getDestination() {
            return destination;
        }

        /**
         * The vertex where the edge starts from
         *
         * @return the vertex
         */
        public V getSource() {
            return source;
        }

        /**
         * The weight of the edge
         *
         * @return the weight
         */
        public W getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "[" + source + " -> " + destination + ", " + weight + "]";
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            try {
                return obj.getClass().equals(getClass()) && obj.toString().equals(toString());
            } catch (Exception e) {
                return false;
            }
        }
    }
}
