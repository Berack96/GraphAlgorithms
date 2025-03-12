package net.berack.upo;

import java.util.List;
import java.util.Set;

import net.berack.upo.graph.Edge;
import net.berack.upo.graph.VisitSCC;
import net.berack.upo.graph.VisitTopological;
import net.berack.upo.graph.visit.Tarjan;

/**
 * This is a more specific interface for an implementation of a Undirected Graph.<br>
 * An Undirected Graph is a Graph where an arc or edge can be traversed in both ways.<br>
 * For example an arc between A and B can be traversed even from B to A.<br>
 *
 * @param <V> The Object that represent a vertex
 * @author Berack96
 */
public abstract class GraphDirected<V> extends Graph<V> {
    String NOT_DAG = "The graph is not a DAG";

    /**
     * Tells if the graph has some cycle.<br>
     * A cycle is detected if visiting the graph G starting from V1 (that is any of the vertex of G),
     * the visit can return to V1 in any point.
     *
     * @return true if has cycle, false otherwise
     */
    public final boolean isCyclic() {
        return stronglyConnectedComponents().size() != size();
    }

    /**
     * Tells if the graph has the property of DAG (Directed Acyclic Graph).<br>
     * A graph is a DAG only if absent of any cycle. ( see {@link #isCyclic()} )
     *
     * @return true if is a DAG, false otherwise
     */
    public final boolean isDAG() {
        return !isCyclic();
    }

    /**
     * Remove all the edges that goes in the vertex.<br>
     * After this method's call it will be no longer possible travel to this vertex.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        for (V ancestor : getAncestors(vertex))
            removeEdge(ancestor, vertex);
    }

    /**
     * Remove all the edges that start from this vertex.<br>
     * After this method's call it will be no longer possible travel to any vertex from this one.
     *
     * @param vertex a vertex of the graph
     * @throws NullPointerException     if one of the parameters is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    public void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        for (V child : getChildren(vertex))
            removeEdge(vertex, child);
    }

    /**
     * Retrieve all the edges of a particular vertex.<br>
     * Note: the edges that are returned are the one that have this vertex as destination and another as source.<br>
     * Note2: depending on the implementation, modifying the returned Set<br>
     * could affect the graph behavior and the changes could be reflected to the graph.
     *
     * @param vertex a vertex of the graph
     * @return a Set of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public Set<Edge<V>> getEdgesIn(V vertex) throws NullPointerException, IllegalArgumentException {
        Set<V> ancestors = getAncestors(vertex);
        Set<Edge<V>> edgesIn = getDefaultSet();

        for (V ancestor : ancestors) {
            int weight = getWeight(ancestor, vertex);
            if (weight != NO_EDGE)
                edgesIn.add(new Edge<>(ancestor, vertex, weight));
        }
        return edgesIn;
    }

    /**
     * Retrieve all the edges that goes OUT of a particular vertex.<br>
     * Note: the edges that are returned are the one that have this vertex as source and another one as destination.<br>
     * Note2: depending on the implementation, modifying the returned Set<br>
     * could affect the graph behavior and the changes could be reflected to the graph.
     *
     * @param vertex a vertex of the graph
     * @return a Set of edges
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public Set<Edge<V>> getEdgesOut(V vertex) throws NullPointerException, IllegalArgumentException {
        Set<V> children = getChildren(vertex);
        Set<Edge<V>> edgesOut = getDefaultSet();

        for (V child : children) {
            int weight = getWeight(vertex, child);
            if (weight != NO_EDGE)
                edgesOut.add(new Edge<>(vertex, child, weight));
        }
        return edgesOut;
    }

    /**
     * Tells the degree of all the edges that goes to this vertex.<br>
     * Basically, it'll count how many edge towards himself it have.
     *
     * @param vertex a vertex of the graph
     * @return the in degree of the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
        return getAncestors(vertex).size();
    }

    /**
     * Tells the degree of all the edges that goes form this vertex to others.<br>
     * Basically, it'll count how many edge towards any other vertex it have.
     *
     * @param vertex a vertex of the graph
     * @return the out degree of the vertex
     * @throws NullPointerException     if the vertex is null
     * @throws IllegalArgumentException if the vertex is not contained in the graph
     */
    public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
        return getChildren(vertex).size();
    }

    /**
     * This method will create a new Graph that is the transposed version of the original.<br>
     * At the end of this method the new graph will have all the edges inverted in orientation.<br>
     * Example: if the graph G contains (V1, V2, V3) as vertex, and (V1-&gt;V2, V3-&gt;V2) as edges,
     * the transpose graph G' will contain (V1, V2, V3) as vertex, and (V2-&gt;V1, V2-&gt;V3) as edges.
     *
     * @return a transposed graph of this instance
     */
    public final GraphDirected<V> transpose() {
        GraphDirected<V> transposed = (GraphDirected<V>) getNewInstance();
        transposed.addAll(vertices());

        for (V vertex : transposed)
            for (V child : getChildren(vertex))
                transposed.addEdge(child, vertex, getWeight(vertex, child));
        return transposed;
    }

    /**
     * If the current graph is a DAG, it returns a topological sort of this graph.<br>
     * A topological ordering of a graph is a linear ordering of its vertices such that for
     * every directed edge (V1, V2) from vertex V1 to vertex V2, V2 comes before V1 in the ordering.
     *
     * @return a list containing the topological order of the vertices
     * @throws UnsupportedOperationException if the graph is not a DAG (see {@link #isDAG()})
     */
    public final List<V> topologicalSort() throws UnsupportedOperationException {
        VisitTopological<V> visit = new Tarjan<>();
        visit.visit(this, null, null);

        if (visit.getTopologicalSort() == null)
            throw new UnsupportedOperationException(NOT_DAG);
        return visit.getTopologicalSort();
    }

    /**
     * The strongly connected components or disconnected components of an arbitrary directed graph
     * form a partition into subgraphs that are themselves strongly connected.
     *
     * @return a Set containing the strongly connected components
     */
    public final Set<Set<V>> stronglyConnectedComponents() {
        VisitSCC<V> visit = new Tarjan<>();
        visit.visit(this, null, null);
        return visit.getSCC();
    }

    @Override
    public Set<Edge<V>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
        Set<Edge<V>> edges = getEdgesIn(vertex);
        edges.addAll(getEdgesOut(vertex));
        return edges;
    }

    @Override
    public Set<Edge<V>> edges() {
        Set<Edge<V>> set = getDefaultSet();
        forEach(v -> set.addAll(getEdgesIn(v)));
        return set;
    }

    @Override
    public final int degree(V vertex) throws NullPointerException, IllegalArgumentException {
        return degreeIn(vertex) + degreeOut(vertex);
    }
}
