package net.berack.upo.graph;

import java.util.Set;

/**
 * Interface that is helpful for implements visit that needs to retrieve the SCC
 *
 * @param <V> the vertex
 * @author Berack96
 */
public interface VisitSCC<V> extends VisitStrategy<V> {

    /**
     * Return the latest calculated strongly connected components of the graph.
     *
     * @return the latest SCC
     * @throws NullPointerException if there is no last calculated SCC
     */
    Set<Set<V>> getSCC();
}
