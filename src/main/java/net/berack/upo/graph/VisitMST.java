package net.berack.upo.graph;

import java.util.Set;

/**
 * @param <V>
 */
public interface VisitMST<V> extends VisitStrategy<V> {

    /**
     * Return the latest calculated MST.<br>
     * https://en.wikipedia.org/wiki/Minimum_spanning_tree
     *
     * @return the latest MST
     * @throws NullPointerException if there is no last calculated MST
     */
    Set<Edge<V>> getMST();
}
