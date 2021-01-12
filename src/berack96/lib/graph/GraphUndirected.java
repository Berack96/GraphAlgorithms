package berack96.lib.graph;

import berack96.lib.graph.visit.VisitMST;
import berack96.lib.graph.visit.impl.Prim;

import java.util.Collection;
import java.util.LinkedList;

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
     * @return a collection containing the strongly connected components
     */
    public Collection<Collection<V>> connectedComponents() {
        return null;
    }

    /**
     * minimum spanning forest or minimum spamming tree of the graph
     *
     * @return A collection of edges representing the M.S.F.
     */
    public Collection<Edge<V>> minimumSpanningForest() {
        VisitMST<V> visit = new Prim<>();
        visit.visit(this, iterator().next(), null);
        return visit.getMST();
    }

    @Override
    public Collection<Edge<V>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
        checkVert(vertex);
        Collection<Edge<V>> edges = new LinkedList<>();
        getChildren(vertex).forEach(v -> edges.add(new Edge<>(vertex, v, getWeight(vertex, v))));
        return edges;
    }
}
