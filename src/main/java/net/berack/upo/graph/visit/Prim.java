package net.berack.upo.graph.visit;

import java.util.Set;
import java.util.function.Consumer;

import net.berack.upo.Graph;
import net.berack.upo.GraphUndirected;
import net.berack.upo.graph.Edge;
import net.berack.upo.graph.VisitMST;

/**
 * Class that implement the algorithm discovered by Prim for the minimum spanning forest
 * for a given {@link GraphUndirected}
 *
 * @param <V> The vertex of the graph
 */
public class Prim<V> implements VisitMST<V> {

    private Set<Edge<V>> mst;

    @Override
    public Set<Edge<V>> getMST() {
        return mst;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        mst = Graph.getDefaultSet();
        Set<V> vertices = graph.vertices();

        if (source == null)
            source = vertices.iterator().next();
        VisitInfo<V> info = new VisitInfo<>(source);
        V current = source;

        do {
            if (current == null)
                current = vertices.iterator().next();

            Edge<V> min = null;
            for (Edge<V> edge : graph.edgesOf(current))
                if (vertices.contains(edge.getDestination()))
                    min = (min == null || edge.getWeight() < min.getWeight() ? edge : min);

            info.setParent(source, current);
            info.setVisited(current);
            if (visit != null)
                visit.accept(current);

            if (min == null)
                current = null;
            else {
                vertices.remove(current);
                source = min.getSource();
                current = min.getDestination();
                mst.add(min);
            }
        } while (vertices.size() != 0);

        return info;
    }
}
