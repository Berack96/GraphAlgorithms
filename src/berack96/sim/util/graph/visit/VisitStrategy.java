package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.Hashtable;
import java.util.Map;
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
     * @throws NullPointerException          if one of the arguments is null (only the consumers can be null)
     * @throws IllegalArgumentException      if the source vertex is not in the graph
     * @throws UnsupportedOperationException in the case that the visit algorithm cannot be applied to the graph
     */
    void visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException;

    /**
     * The class used for getting the info of the visit.<br>
     * It could be used with the algorithm of the visit for set some useful data.
     *
     * @param <V> the vertex of the visit
     * @author Berack96
     */
    class VisitInfo<V> {
        private final Map<V, Long> discovered;
        private final Map<V, Long> visited;
        private final Map<V, V> parent;
        private final V source;
        private long time;

        /**
         * Need a source for initialize the basic values
         *
         * @param source the source of the visit
         * @throws NullPointerException if the source is null
         */
        public VisitInfo(V source) {
            if (source == null)
                throw new NullPointerException();

            discovered = new Hashtable<>();
            visited = new Hashtable<>();
            parent = new Hashtable<>();

            this.time = 0;
            this.source = source;
            setDiscovered(source);
        }

        /**
         * The time of the vertex when it is discovered in the visit.<br>
         * For "discovered" i mean when the node is first found by the visit algorithm. It may depends form {@link VisitStrategy}<br>
         * The time starts at 0 and for each vertex discovered it is increased by one. If a vertex is visited it also increase the time<br>
         *
         * @param vertex the vertex needed
         * @return the time of it's discovery
         * @throws IllegalArgumentException if the vertex is not discovered
         * @throws NullPointerException     if the vertex is null
         */
        public long getTimeDiscover(V vertex) throws IllegalArgumentException, NullPointerException {
            Long time = discovered.get(vertex);
            if (time == null)
                throw new IllegalArgumentException();
            return time;
        }

        /**
         * The time when the vertex is visited by the algorithm<br>
         * For "visited" i mean when the node is finally visited by the visit algorithm. It may depends form {@link VisitStrategy}<br>
         * The time starts at 0 and for each vertex discovered or visited is increased by one<br>
         *
         * @param vertex the vertex needed
         * @return the time of it's visit
         * @throws IllegalArgumentException if the vertex is not visited
         * @throws NullPointerException     if the vertex is null
         */
        public long getTimeVisit(V vertex) throws IllegalArgumentException, NullPointerException {
            Long time = visited.get(vertex);
            if (time == null)
                throw new IllegalArgumentException();
            return time;
        }

        /**
         * Tells if a vertex is discovered or not
         *
         * @param vertex the vertex chosen
         * @return true if is discovered
         */
        public boolean isDiscovered(V vertex) throws NullPointerException {
            try {
                return discovered.containsKey(vertex);
            } catch (NullPointerException e) {
                return false;
            }
        }

        /**
         * Tells if the vertex is visited or not
         *
         * @param vertex the vertex chosen
         * @return true if is visited
         */
        public boolean isVisited(V vertex) throws NullPointerException {
            try {
                return visited.containsKey(vertex);
            } catch (NullPointerException e) {
                return false;
            }
        }

        /**
         * Set a vertex as "visited". After this call the vertex is set as discovered (if not already) and visited.<br>
         * Next this call it will be possible to get the time of visit of that vertex<br>
         * Does nothing if the vertex is already been visited.
         *
         * @param vertex the vertex that has been visited
         */
        public synchronized void setVisited(V vertex) {
            setDiscovered(vertex);
            if (!visited.containsKey(vertex))
                visited.put(vertex, time++);
        }

        /**
         * Set a vertex as "discovered". After this call the vertex is set as discovered and it will be possible to get the time of it's discovery<br>
         * Does nothing if the vertex is already been discovered.
         *
         * @param vertex the vertex that has been discovered
         */
        public synchronized void setDiscovered(V vertex) {
            if (!discovered.containsKey(vertex))
                discovered.put(vertex, time++);
        }

        /**
         * Set the parent of a particular vertex<br>
         * The parent of a vertex is the one that has discovered it<br>
         * If the target vertex is not already discovered, then {@link #setDiscovered(Object)} is called<br>
         *
         * @param parent the vertex that is the parent
         * @param child  the vertex discovered
         * @throws IllegalArgumentException if the parent is not already discovered
         */
        public synchronized void setParent(V parent, V child) throws IllegalArgumentException {
            if (!isDiscovered(parent))
                throw new IllegalArgumentException(parent.toString());

            setDiscovered(child);
            this.parent.putIfAbsent(child, parent);
        }

        /**
         * Get the source of the visit.
         *
         * @return the source vertex where it's started the visit
         */
        public V getSource() {
            return source;
        }

        /**
         * Get the parent of a particular vertex.<br>
         * The parent of a vertex is the one that has discovered it<br>
         * If the vertex has no parent (it has not been set by the visit algorithm or it's the source) then null is returned.
         *
         * @param vertex the child vertex
         * @return the parent of the child
         * @throws IllegalArgumentException if the vertex has not been discovered yet
         */
        public V getParentOf(V vertex) throws IllegalArgumentException {
            if (isDiscovered(vertex))
                return parent.get(vertex);

            throw new IllegalArgumentException();
        }
    }
}
