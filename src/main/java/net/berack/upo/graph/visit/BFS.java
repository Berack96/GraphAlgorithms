package net.berack.upo.graph.visit;

import java.util.LinkedList;
import java.util.function.Consumer;

import net.berack.upo.Graph;
import net.berack.upo.graph.VisitStrategy;

/**
 * Breadth-first search<br>
 * The algorithm starts at the root node and explores all of the neighbor nodes at the present depth prior to moving on to the nodes at the next depth level.
 *
 * @param <V> the vertex of the graph
 * @author Berack96
 */
public class BFS<V> implements VisitStrategy<V> {

    private int maxDepth = -1;

    public BFS<V> setMaxDepth(int depth) {
        this.maxDepth = depth;
        return this;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        VisitInfo<V> info = new VisitInfo<>(source);
        final LinkedList<V> toVisitChildren = new LinkedList<>();

        toVisitChildren.push(source);
        if (visit != null)
            visit.accept(source);
        info.setVisited(source);

        while (!toVisitChildren.isEmpty()) {
            V current = toVisitChildren.removeFirst();
            if (maxDepth > -1 && info.getDepth(current) >= maxDepth)
                break;

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
