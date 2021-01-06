package berack96.lib.graph.visit.impl;

import berack96.lib.graph.Graph;
import berack96.lib.graph.visit.VisitStrategy;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Depth-first search<br>
 * The algorithm starts at the root node and explores as far as possible along each branch before backtracking.
 *
 * @param <V> the vertex of the graph
 * @param <W> the weight of the graph
 * @author Berack96
 */
public class DFS<V, W extends Number> implements VisitStrategy<V, W> {

    @Override
    public VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        VisitInfo<V> info = new VisitInfo<>(source);
        final Stack<V> toVisit = new Stack<>();

        toVisit.push(source);

        while (!toVisit.isEmpty()) {
            V current = toVisit.peek();
            boolean hasChildToVisit = false;
            Iterator<V> iter = graph.getChildrens(current).iterator();

            while (iter.hasNext() && !hasChildToVisit) {
                V child = iter.next();
                if (!info.isDiscovered(child)) {
                    hasChildToVisit = true;
                    toVisit.push(child);
                    info.setParent(current, child);
                }
            }

            if (!hasChildToVisit) {
                toVisit.pop();
                info.setVisited(current);
                if (visit != null)
                    visit.accept(current);
            }
        }
        return info;
    }
}
