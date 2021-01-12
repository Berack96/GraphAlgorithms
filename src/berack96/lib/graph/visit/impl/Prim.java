package berack96.lib.graph.visit.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphUndirected;
import berack96.lib.graph.visit.VisitMST;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Class that implement the algorithm discovered by Prim for the minimum spanning forest
 * for a given {@link GraphUndirected}
 *
 * @param <V> The vertex of the graph
 */
public class Prim<V> implements VisitMST<V> {

    private Collection<Edge<V>> mst;

    @Override
    public Collection<Edge<V>> getMST() {
        return mst;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException {
        mst = new LinkedList<>();
        Collection<V> vertices = graph.vertices();

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
