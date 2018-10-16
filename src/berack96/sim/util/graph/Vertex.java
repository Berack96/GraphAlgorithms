package berack96.sim.util.graph;

import berack96.sim.util.graph.visit.VisitInfo;
import berack96.sim.util.graph.visit.VisitStrategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Class used for represent a vertex of the graph.<br>
 * The vertex contained is linked with the graph, so if any changes are made to
 * it, then they will be reflected here.
 *
 * @param <V> the vertex
 * @author Berack96
 */
@SuppressWarnings("unchecked")
public class Vertex<V> {

    public static final String REMOVED = "The vertex is no longer in the graph";

    /**
     * The vertex associated
     */
    private final V vertex;
    /**
     * The graph associated
     */
    private final Graph<V, Number> graph;

    /**
     * Get a Vertex linked with the graph
     *
     * @param graph  the graph of the vertex
     * @param vertex the vertex
     * @throws NullPointerException if one of the param is null
     */
    public Vertex(Graph<V, ?> graph, V vertex) throws NullPointerException {
        if (graph == null || vertex == null)
            throw new NullPointerException();
        this.graph = (Graph<V, Number>) graph;
        this.vertex = vertex;
    }

    /**
     * Get the vertex
     *
     * @return the vertex
     */
    public V getValue() {
        return vertex;
    }

    /**
     * Mark the vertex with the associated string
     *
     * @param mark the marker
     * @throws NullPointerException          if the marker is null
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public void mark(Object mark) throws NullPointerException, UnsupportedOperationException {
        throwIfNotContained();
        graph.mark(vertex, mark);
    }

    /**
     * Remove the specified mark from this vertex
     *
     * @param mark the marker
     * @throws NullPointerException          if the mark is null
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public void unMark(Object mark) throws UnsupportedOperationException {
        throwIfNotContained();
        graph.unMark(vertex, mark);
    }

    /**
     * Remove all the marker from the vertex
     *
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public void unMark() throws UnsupportedOperationException {
        throwIfNotContained();
        graph.unMark(vertex);
    }

    /**
     * Get all the marks that are associated with this vertex
     *
     * @return a set of marks
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<Object> getMarks() throws UnsupportedOperationException {
        throwIfNotContained();
        return graph.getMarks(vertex);
    }

    /**
     * Get all the vertex children of the current vertex
     *
     * @return all the children
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<V> getChildren() throws UnsupportedOperationException {
        throwIfNotContained();
        return graph.getChildren(vertex);
    }

    /**
     * Get all the children of this vertex like {@link #getChildren()}, but as {@link Vertex}.<br>
     * In this way they are linked to the graph as this one.<br>
     * * This method allocate a new object for each vertex, so it is more heavy.
     *
     * @return a collection of vertices that are children of the current one
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<Vertex<V>> getChildrenAsVertex() throws UnsupportedOperationException {
        Collection<V> children = getChildren();
        Collection<Vertex<V>> toReturn = new HashSet<>();
        for (V vertex : children)
            toReturn.add(new Vertex<>(graph, vertex));

        return toReturn;
    }

    /**
     * Get all the vertex ancestor of this vertex.<br>
     * The ancestors are all the vertices that have as destination this vertex.
     *
     * @return a collection of vertices
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<V> getAncestors() throws UnsupportedOperationException {
        throwIfNotContained();
        return graph.getAncestors(vertex);
    }

    /**
     * Get all the ancestors of this vertex like {@link #getAncestors()}, but as {@link Vertex}.<br>
     * In this way they are linked to the graph as this one.<br>
     * This method allocate a new object for each vertex, so it is more heavy.
     *
     * @return a collection of vertices that are children of the current one
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<Vertex<V>> getAncestorsAsVertex() throws UnsupportedOperationException {
        Collection<V> ancestors = getAncestors();
        Collection<Vertex<V>> toReturn = new HashSet<>();
        for (V vertex : ancestors)
            toReturn.add(new Vertex<>(graph, vertex));

        return toReturn;
    }

    /**
     * Get all the edge that goes OUT of this vertex
     *
     * @return a collection of edges with source this one
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<Edge<V, Number>> getEdgesOut() throws UnsupportedOperationException {
        throwIfNotContained();
        return graph.getEdgesOut(vertex);
    }

    /**
     * Get all the edge that goes INTO this vertex
     *
     * @return a collection of edges with destination this one
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public Collection<Edge<V, Number>> getEdgesIn() throws UnsupportedOperationException {
        throwIfNotContained();
        return graph.getEdgesIn(vertex);
    }

    /**
     * Add a child to this vertex.<br>
     * The added child must be in the graph or it will return an exception.
     *
     * @param child  the destination vertex of this edge
     * @param weight the weight of the edge
     * @throws NullPointerException          if the param is null
     * @throws IllegalArgumentException      if the child vertex is not contained in the graph
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public void addChild(V child, Number weight) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        throwIfNotContained();
        graph.addEdge(vertex, child, weight);
    }

    /**
     * Removes a child of this vertex.
     * If the vertex passed as param is not a child, then this call does nothing.
     *
     * @param child the child of the current vertex
     * @throws NullPointerException          if the param is null
     * @throws IllegalArgumentException      if the child vertex is not contained in the graph
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public void removeChild(V child) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        throwIfNotContained();
        graph.removeEdge(vertex, child);
    }

    /**
     * This call tell if the current vertex is still contained in the graph linked.<br>
     * While this function return false all the other methods will throw an exception.
     *
     * @return true if it is, false otherwise
     */
    public boolean isStillContained() {
        return graph.contains(vertex);
    }

    /**
     * Add the vertex to the graph only if it's not already in the graph.
     */
    public void addIfAbsent() {
        graph.addVertexIfAbsent(vertex);
    }

    /**
     * Remove the vertex from the graph.<br>
     * After this call all the other methods will throw an exception
     */
    public void remove() {
        if (graph.contains(vertex))
            graph.removeVertex(vertex);
    }

    /**
     * Visit the graph from this current vertex with the strategy assigned
     *
     * @param strategy the strategy of the visit
     * @param visit    the function to apply at each vertex (can be null)
     * @return an info of the visit if supported by the strategy
     * @throws NullPointerException          if the strategy is null
     * @throws UnsupportedOperationException if the vertex is not in the graph anymore
     */
    public VisitInfo<V> visit(final VisitStrategy strategy, final Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        throwIfNotContained();
        return graph.visit(vertex, (VisitStrategy<V, Number>) strategy, visit);
    }

    @Override
    public String toString() {
        return vertex.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return obj instanceof Vertex && (this.vertex.equals(obj) || this.vertex.equals(((Vertex) obj).vertex));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Used for throwing the UnsupportedOperationException if the vertex is not contained anymore
     *
     * @throws UnsupportedOperationException if IllegalArgumentException is thrown by the runnable
     */
    private void throwIfNotContained() throws UnsupportedOperationException {
        if (!graph.contains(vertex))
            throw new UnsupportedOperationException(REMOVED);
    }
}
