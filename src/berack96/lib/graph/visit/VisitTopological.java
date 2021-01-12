package berack96.lib.graph.visit;

import java.util.List;

/**
 * Interface that is helpful for implements visit that needs to retrieve the topological sort
 *
 * @param <V> the vertex
 * @author Berack96
 */
public interface VisitTopological<V> extends VisitStrategy<V> {

    /**
     * Return the latest calculated Topological sort of the graph.<br>
     * If the latest visited graph is not a DAG, it will return null.
     *
     * @return the topological order of the DAG
     * @throws NullPointerException if there is no last calculated topological sort
     */
    List<V> getTopologicalSort();
}
