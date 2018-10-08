package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Depth-first search<br>
 * The algorithm starts at the root node and explores as far as possible along each branch before backtracking.
 *
 * @param <V> the vertex of the graph
 * @param <W> the weight of the graph
 */
public class DFS<V, W extends Number> implements VisitStrategy<V, W> {

    private VisitInfo<V> lastVisit = null;

    /**
     * Retrieve the info of the last visit
     *
     * @return an info of the visit
     */
    public VisitInfo<V> getLastVisit() {
        return lastVisit;
    }

    @Override
    public void visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        lastVisit = new VisitInfo<>(source);
        final Stack<V> toVisit = new Stack<>();

        toVisit.push(source);

        while (!toVisit.isEmpty()) {
            V current = toVisit.peek();
            boolean hasChildToVisit = false;
            Iterator<V> iter = graph.getChildren(current).iterator();

            while (iter.hasNext() && !hasChildToVisit) {
                V child = iter.next();
                if (!lastVisit.isDiscovered(child)) {
                    hasChildToVisit = true;
                    toVisit.push(child);
                    lastVisit.setParent(current, child);
                }
            }

            if (!hasChildToVisit) {
                toVisit.pop();
                lastVisit.setVisited(current);
                if (visit != null)
                    visit.accept(current);
            }
        }
    }
}
