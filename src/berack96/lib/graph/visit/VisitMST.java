package berack96.lib.graph.visit;

import berack96.lib.graph.Edge;

import java.util.Collection;

/**
 * @param <V>
 */
public interface VisitMST<V> extends VisitStrategy<V> {

    /**
     * Return the latest calculated MST.
     *
     * @return the latest MST
     * @throws NullPointerException if there is no last calculated MST
     */
    Collection<Edge<V>> getMST();
}
