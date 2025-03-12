package net.berack.upo;

import java.util.Set;

import net.berack.upo.graph.Edge;
import net.berack.upo.graph.VisitMST;
import net.berack.upo.graph.visit.Prim;

/**
 * This is a more specific interface for an implementation of a Directed Graph.<br>
 * A Directed Graph is a Graph where an arc or edge can be traversed in only one way.<br>
 * A directed edge between V1 and V2 is an edge that has V1 as source and V2 as destination.<br>
 *
 * @param <V> The Object that represent a vertex
 * @author Berack96
 */
public abstract class GraphUndirected<V> extends Graph<V> {

    /**
     * The connected components of an arbitrary undirected graph form a partition into subgraphs that are themselves connected.
     *
     * @return a Set containing the strongly connected components
     */
    public Set<Set<V>> connectedComponents() {
        return null;
    }

    /**
     * minimum spanning forest or minimum spamming tree of the graph
     *
     * @return A Set of edges representing the M.S.F.
     */
    public Set<Edge<V>> minimumSpanningForest() {
        VisitMST<V> visit = new Prim<>();
        visit.visit(this, iterator().next(), null);
        return visit.getMST();
    }

    @Override
    public Set<Edge<V>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        Set<Edge<V>> edges = getDefaultSet();
        getChildren(vertex).forEach(v -> edges.add(new Edge<>(vertex, v, getWeight(vertex, v))));
        return edges;
    }
}
