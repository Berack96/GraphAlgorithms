package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Breadth-first search<br>
 * The algorithm starts at the root node and explores all of the neighbor nodes at the present depth prior to moving on to the nodes at the next depth level.
 *
 * @param <V> the vertex of the graph
 * @param <W> the weight of the graph
 */
public class BFS<V, W extends Number> implements VisitStrategy<V, W> {

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
        final LinkedList<V> toVisitChildren = new LinkedList<>();

        toVisitChildren.push(source);
        if (visit != null)
            visit.accept(source);
        lastVisit.setVisited(source);

        while (!toVisitChildren.isEmpty()) {
            V current = toVisitChildren.removeFirst();

            for (V child : graph.getChildren(current))
                if (!lastVisit.isDiscovered(child)) {
                    toVisitChildren.addLast(child);

                    lastVisit.setVisited(child);
                    lastVisit.setParent(current, child);
                    if (visit != null)
                        visit.accept(child);
                }
        }
    }
}
