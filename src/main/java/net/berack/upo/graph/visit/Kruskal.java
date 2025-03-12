package net.berack.upo.graph.visit;

import java.util.*;
import java.util.function.Consumer;

import net.berack.upo.Graph;
import net.berack.upo.GraphUndirected;
import net.berack.upo.graph.Edge;
import net.berack.upo.graph.VisitMST;
import net.berack.upo.graph.visit.struct.QuickFind;
import net.berack.upo.graph.visit.struct.UnionFind;

/**
 * Class that implement the algorithm discovered by Kruskal for the minimum spanning forest
 * for a given {@link GraphUndirected}
 *
 * @param <V> The vertex of the graph
 */
public class Kruskal<V> implements VisitMST<V> {
    private Set<Edge<V>> mst;

    @Override
    public Set<Edge<V>> getMST() {
        return mst;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        UnionFind<V> sets = new QuickFind<>();
        sets.makeSetAll(graph.vertices());

        List<Edge<V>> edges = new ArrayList<>(graph.edges());
        Collections.sort(edges);

        mst = Graph.getDefaultSet();
        Iterator<Edge<V>> iter = edges.iterator();
        while (iter.hasNext() && sets.size() > 1) {
            Edge<V> edge = iter.next();
            if (sets.union(edge.getSource(), edge.getDestination()))
                mst.add(edge);
        }
        return null;
    }
}
