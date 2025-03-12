package net.berack.upo.graph;

import java.util.List;

import net.berack.upo.Graph;

/**
 * Interface that is helpful for implements visit that needs to retrieve the distance between a vertex to all the others
 *
 * @param <V> the vertex
 * @author Berack96
 */
public interface VisitDistSourceDest<V> extends VisitStrategy<V> {

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
    List<Edge<V>> distance(Graph<V> graph, V source, V destination) throws NullPointerException, IllegalArgumentException;
}
