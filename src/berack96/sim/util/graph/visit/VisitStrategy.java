package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.function.Consumer;

/**
 * This class is used for define some strategy for the visit of a graph.
 *
 * @param <V> The Object that represent a vertex
 * @param <W> The Object that represent the edge (more specifically the weight of the edge)
 * @author Berack96
 */
public interface VisitStrategy<V, W extends Number> {

    /**
     * With this the graph will be visited accordingly to the strategy of the visit.<br>
     * Some strategy can accept a source vertex null, because they visit all the graph anyway.<br>
     * If you want to stop the visit of the graph, you just have to throw any exception in the visit function, but be sure to catch it
     *
     * @param graph  the graph to visit
     * @param source the source of the visit
     * @param visit  the function to apply at each vertex when they are visited
     * @return an info of the view
     * @throws NullPointerException          if one of the arguments is null (only the consumers can be null)
     * @throws IllegalArgumentException      if the source vertex is not in the graph
     * @throws UnsupportedOperationException in the case that the visit algorithm cannot be applied to the graph
     */
    VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException;
}
