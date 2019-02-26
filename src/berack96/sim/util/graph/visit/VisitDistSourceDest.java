package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Edge;
import berack96.sim.util.graph.Graph;

import java.util.List;

/**
 * Interface that is helpful for implements visit that needs to retrieve the distance between a vertex to all the others
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public interface VisitDistSourceDest<V, W extends Number> extends VisitStrategy<V, W> {

    /**
     * Get the distance from the source to the destination<br>
     * The list contains the minimum path from the vertex marked as source to the destination vertex
     *
     * @param graph       the graph were to find the min path
     * @param source      the source vertex
     * @param destination the destination vertex
     * @return the distance
     * @throws NullPointerException     if one of the vertex is null
     * @throws IllegalArgumentException if one of the vertex is not contained in the graph
     */
    List<Edge<V, W>> distance(Graph<V, W> graph, V source, V destination) throws NullPointerException, IllegalArgumentException;
}
