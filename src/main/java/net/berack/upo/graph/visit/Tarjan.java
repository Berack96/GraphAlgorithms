package net.berack.upo.graph.visit;

import java.util.*;
import java.util.function.Consumer;

import net.berack.upo.Graph;
import net.berack.upo.graph.VisitSCC;
import net.berack.upo.graph.VisitTopological;

/**
 * Class that implements the Tarjan algorithm and uses it for getting the SCC and the topological sort
 *
 * @param <V> vertex
 * @author Berack96
 */
public class Tarjan<V> implements VisitSCC<V>, VisitTopological<V> {

    private Set<Set<V>> SCC = null;
    private List<V> topologicalSort = null;

    private Map<V, Integer> indices = null;
    private Map<V, Integer> lowLink = null;
    private Stack<V> stack = null;
    private VisitInfo<V> info = null;

    @Override
    public Set<Set<V>> getSCC() {
        return SCC;
    }

    @Override
    public List<V> getTopologicalSort() {
        return topologicalSort;
    }

    /**
     * This particular visit strategy use only the graph and the visit, so the source param is not needed.
     *
     * @param graph  the graph to visit
     * @param source not needed
     * @param visit  the function to apply at each vertex when they are visited
     * @throws NullPointerException     if the graph is null
     * @throws IllegalArgumentException doesn't throw this
     */
    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        SCC = Graph.getDefaultSet();
        topologicalSort = new ArrayList<>(graph.size());
        info = null;

        indices = Graph.getDefaultMap();
        lowLink = Graph.getDefaultMap();
        stack = new Stack<>();
        int index = 0;

        for (V vertex : graph) {
            if (info == null)
                info = new VisitInfo<>(vertex);
            if (!indices.containsKey(vertex))
                strongConnect(graph, vertex, index, visit);
        }

        topologicalSort = (graph.size() == SCC.size()) ? topologicalSort : null;
        return info;
    }

    private void strongConnect(Graph<V> graph, V vertex, Integer index, Consumer<V> visit) {
        // Set the depth index for v to the smallest unused index
        indices.put(vertex, index);
        lowLink.put(vertex, index);
        index++;
        stack.push(vertex);
        info.setDiscovered(vertex);

        // Consider successors of v
        for (V child : graph.getChildren(vertex)) {
            if (!indices.containsKey(child)) {
                info.setParent(vertex, child);
                strongConnect(graph, child, index, visit);
                lowLink.put(vertex, Math.min(lowLink.get(vertex), lowLink.get(child)));
            } else if (stack.contains(child)) {
                // Successor w is in stack S and hence in the current SCC
                // If w is not on stack, then (v, w) is a cross-edge in the DFS tree and must be ignored
                // Note: The next line may look odd - but is correct.
                // It says w.index not w.lowlink; that is deliberate and from the original paper
                lowLink.put(vertex, Math.min(lowLink.get(vertex), indices.get(child)));
            }
        }

        // If v is a root node, pop the stack and generate an SCC
        if (lowLink.get(vertex).equals(indices.get(vertex))) {
            Set<V> newComponent = Graph.getDefaultSet();
            V temp;
            do {
                temp = stack.pop();
                topologicalSort.add(0, temp);
                newComponent.add(temp);

                info.setVisited(temp);
                if (visit != null)
                    visit.accept(temp);

            } while (!temp.equals(vertex));

            SCC.add(newComponent);
        }
    }
}
