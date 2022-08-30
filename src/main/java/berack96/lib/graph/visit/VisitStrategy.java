package berack96.lib.graph.visit;

import berack96.lib.graph.Graph;
import berack96.lib.graph.GraphDirected;
import berack96.lib.graph.visit.impl.VisitInfo;

import java.util.function.Consumer;

/**
 * This class is used for define some strategy for the visit of a graph.
 *
 * @param <V> The Object that represent a vertex
 * @author Berack96
 */
public interface VisitStrategy<V> {

    /**
     * With this the graph will be visited accordingly to the strategy of the visit.<br>
     * Some strategy can accept a source vertex null, because they visit all the graph anyway.<br>
     * If you want to stop the visit of the graph, you just have to throw any exception in the visit function, but be sure to catch it
     *
     * @param graph  the graph to visit
     * @param source the vertex where the visit starts
     * @param visit  the function to apply at each vertex when they are visited
     * @return an info of the view
     * @throws NullPointerException          if the graph is null
     * @throws UnsupportedOperationException in the case that the visit algorithm cannot be applied to the graph
     */
    VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, UnsupportedOperationException;

    /**
     * Method used for checking if the graph is Directed.<br>
     * It's useful when the algorithm can only be applied to Directed graph.
     *
     * @param graph the instance of the graph to check
     * @return the instance of the graph casted to a {@link GraphDirected}
     * @throws UnsupportedOperationException in the case it's not a directed graph
     */
    default GraphDirected<V> checkDirected(Graph<V> graph) {
        if (graph instanceof GraphDirected)
            return (GraphDirected<V>) graph;
        throw new UnsupportedOperationException();
    }
}
