package berack96.lib.graph.visit;

import java.util.List;
import java.util.Map;

import berack96.lib.graph.Edge;

/**
 * Interface that is helpful for implements visit that needs to retrieve the distance between a vertex to all the others
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public interface VisitDistance<V, W extends Number> extends VisitStrategy<V, W> {

    /**
     * Get the last calculated distance to all the possible destinations<br>
     * The map contains all the possible vertices that are reachable from the source set in the visit<br>
     * If there is no path between the destination and the source, then null is returned as accordingly to the map interface<br>
     * If the visit is not already been done, then the map is null.
     *
     * @return the last distance
     * @throws NullPointerException if the visit is not already been done
     */
    Map<V, List<Edge<V, W>>> getLastDistance() throws NullPointerException;

    /**
     * Get the last source vertex of the visit for calculating the destinations.<br>
     * Returns null if the visit is not already been done
     *
     * @return the last vertex
     */
    V getLastSource();
}
