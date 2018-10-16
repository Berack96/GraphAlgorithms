package berack96.sim.util.graph.visit;

import java.util.Collection;

/**
 * Interface that is helpful for implements visit that needs to retrieve the SCC
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public interface VisitSCC<V, W extends Number> extends VisitStrategy<V, W> {

    /**
     * Return the latest calculated strongly connected components of the graph.
     *
     * @return the latest SCC
     * @throws NullPointerException if there is no last calculated SCC
     */
    Collection<Collection<V>> getSCC();
}
