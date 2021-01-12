package berack96.lib.graph.visit.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphUndirected;
import berack96.lib.graph.struct.QuickFind;
import berack96.lib.graph.struct.UnionFind;
import berack96.lib.graph.visit.VisitMST;

import java.util.*;
import java.util.function.Consumer;

/**
 * Class that implement the algorithm discovered by Kruskal for the minimum spanning forest
 * for a given {@link GraphUndirected}
 *
 * @param <V> The vertex of the graph
 */
public class Kruskal<V> implements VisitMST<V> {
    private Collection<Edge<V>> mst;

    @Override
    public Collection<Edge<V>> getMST() {
        return mst;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        UnionFind<V> sets = new QuickFind<>();
        sets.makeSetAll(graph.vertices());

        List<Edge<V>> edges = new ArrayList<>(graph.edges());
        Collections.sort(edges);

        mst = new HashSet<>(graph.size(), 1);
        Iterator<Edge<V>> iter = edges.iterator();
        while (iter.hasNext() && sets.size() > 1) {
            Edge<V> edge = iter.next();
            if (sets.union(edge.getSource(), edge.getDestination()))
                mst.add(edge);
        }
        return null;
    }
}
