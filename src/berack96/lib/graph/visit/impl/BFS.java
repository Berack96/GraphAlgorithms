package berack96.lib.graph.visit.impl;

import java.util.LinkedList;
import java.util.function.Consumer;

import berack96.lib.graph.Graph;
import berack96.lib.graph.visit.VisitStrategy;

/**
 * Breadth-first search<br>
 * The algorithm starts at the root node and explores all of the neighbor nodes at the present depth prior to moving on to the nodes at the next depth level.
 *
 * @param <V> the vertex of the graph
 * @param <W> the weight of the graph
 * @author Berack96
 */
public class BFS<V, W extends Number> implements VisitStrategy<V, W> {

    @Override
    public VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        VisitInfo<V> info = new VisitInfo<>(source);
        final LinkedList<V> toVisitChildren = new LinkedList<>();

        toVisitChildren.push(source);
        if (visit != null)
            visit.accept(source);
        info.setVisited(source);

        while (!toVisitChildren.isEmpty()) {
            V current = toVisitChildren.removeFirst();

            for (V child : graph.getChildren(current))
                if (!info.isDiscovered(child)) {
                    toVisitChildren.addLast(child);

                    info.setVisited(child);
                    info.setParent(current, child);
                    if (visit != null)
                        visit.accept(child);
                }
        }
        return info;
    }
}
