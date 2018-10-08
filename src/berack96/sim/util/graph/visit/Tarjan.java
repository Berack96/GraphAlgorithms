package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.*;
import java.util.function.Consumer;

public class Tarjan<V, W extends Number> implements VisitStrategy<V, W> {

    private Set<Set<V>> SCC = null;
    private List<V> topologicalSort = null;

    private Map<V, Integer> indices = null;
    private Map<V, Integer> lowLink = null;
    private Stack<V> stack = null;

    /**
     * Return the latest calculated strongly connected components of the graph.
     *
     * @return the latest SCC
     */
    public Set<Set<V>> getSCC() {
        return SCC;
    }

    /**
     * Return the latest calculated Topological sort of the graph.<br>
     * If the latest visited graph is not a DAG, it will return null.
     *
     * @return the topological order of the DAG
     */
    public List<V> getTopologicalSort() {
        return topologicalSort;
    }

    /**
     * This particular visit strategy use only the graph, so the other parameters are useless.
     *
     * @param graph  the graph to visit
     * @param source the source of the visit
     * @param visit  the function to apply at each vertex when they are visited
     * @throws NullPointerException     if the graph is null
     * @throws IllegalArgumentException doesn't throw this
     */
    @Override
    public void visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        SCC = new HashSet<>();
        topologicalSort = new LinkedList<>();

        indices = new HashMap<>();
        lowLink = new HashMap<>();
        stack = new Stack<>();
        Integer index = 0;

        for (V vertex : graph)
            if (!indices.containsKey(vertex))
                strongConnect(graph, vertex, index);

        topologicalSort = (graph.numberOfVertices() == SCC.size()) ? new ArrayList<>(topologicalSort) : null;
    }

    private void strongConnect(Graph<V, W> graph, V vertex, Integer index) {
        // Set the depth index for v to the smallest unused index
        indices.put(vertex, index);
        lowLink.put(vertex, index);
        index++;
        stack.push(vertex);

        // Consider successors of v
        for (V child : graph.getChildren(vertex)) {
            if (!indices.containsKey(child)) {
                strongConnect(graph, child, index);
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
            } while (!temp.equals(vertex));

            SCC.add(newComponent);
        }
    }
}
