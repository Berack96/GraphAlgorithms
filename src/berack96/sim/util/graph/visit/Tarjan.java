package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.*;
import java.util.function.Consumer;

/**
 * Class that implements the Tarjan algorithm and uses it for getting the SCC and the topological sort
 *
 * @param <V> vertex
 * @param <W> weight
 * @author Berack96
 */
public class Tarjan<V, W extends Number> implements VisitSCC<V, W>, VisitTopological<V, W> {

    private Collection<Collection<V>> SCC = null;
    private List<V> topologicalSort = null;

    private Map<V, Integer> indices = null;
    private Map<V, Integer> lowLink = null;
    private Stack<V> stack = null;
    private VisitInfo<V> info = null;

    @Override
    public Collection<Collection<V>> getSCC() {
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
    public VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        SCC = new HashSet<>();
        topologicalSort = new LinkedList<>();
        info = null;

        indices = new HashMap<>();
        lowLink = new HashMap<>();
        stack = new Stack<>();
        Integer index = 0;

        for (V vertex : graph) {
            if (info == null)
                info = new VisitInfo<>(vertex);
            if (!indices.containsKey(vertex))
                strongConnect(graph, vertex, index, visit);
        }

        topologicalSort = (graph.numberOfVertices() == SCC.size()) ? new ArrayList<>(topologicalSort) : null;
        return info;
    }

    private void strongConnect(Graph<V, W> graph, V vertex, Integer index, Consumer<V> visit) {
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
            Set<V> newComponent = new HashSet<>();
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
