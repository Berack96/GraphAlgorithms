package berack96.lib.graph.visit.impl;

import java.util.*;
import java.util.function.Consumer;

import berack96.lib.graph.visit.VisitStrategy;

/**
 * The class used for getting the info of the visit.<br>
 * It could be used with the algorithm of the visit for set some useful data.
 *
 * @param <V> the vertex of the visit
 * @author Berack96
 */
public class VisitInfo<V> {
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
    synchronized void setVisited(V vertex) {
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
    synchronized void setDiscovered(V vertex) {
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
    synchronized void setParent(V parent, V child) throws IllegalArgumentException {
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

    /**
     * Get all the visited vertices so far.
     *
     * @return the visited vertices
     */
    public Set<V> getVisited() {
        return visited.keySet();
    }

    /**
     * Get all the discovered vertices so far.
     *
     * @return the discovered vertices
     */
    public Set<V> getDiscovered() {
        return discovered.keySet();
    }

    /**
     * Iterate through all the vertices that are discovered.<br>
     * The vertices will be ordered by the time of their discover.
     *
     * @param consumer the function to apply to each
     */
    public void forEachDiscovered(Consumer<VertexInfo> consumer) {
        Set<V> vertices = getDiscovered();
        Queue<VertexInfo> queue = new PriorityQueue<>();

        vertices.forEach((vertex) -> queue.offer(new VertexInfo(vertex, getParentOf(vertex), getTimeDiscover(vertex), isVisited(vertex) ? getTimeVisit(vertex) : -1, false)));

        while (!queue.isEmpty())
            consumer.accept(queue.poll());
    }

    /**
     * Iterate through all the vertices that are visited.<br>
     * The vertices will be ordered by the time of their visit.
     *
     * @param consumer the function to apply to each
     */
    public void forEachVisited(Consumer<VertexInfo> consumer) {
        Set<V> vertices = getVisited();
        Queue<VertexInfo> queue = new PriorityQueue<>();

        vertices.forEach((vertex) -> queue.offer(new VertexInfo(vertex, getParentOf(vertex), getTimeDiscover(vertex), getTimeVisit(vertex), true)));

        while (!queue.isEmpty())
            consumer.accept(queue.poll());
    }

    /**
     * Iterate through all the vertices discovered and visited with the correct timeline.<br>
     * The vertices will be visited in the order that they are discovered and visited, so a vertex can appear two times (one for the discovery, anc the other for the visit)
     *
     * @param consumer the function to apply at each vertex
     */
    public void forEach(Consumer<VertexInfo> consumer) {
        Set<V> discovered = getDiscovered();
        Set<V> visited = getVisited();
        Queue<VertexInfo> queue = new PriorityQueue<>();

        discovered.forEach((vertex) -> queue.offer(new VertexInfo(vertex, getParentOf(vertex), getTimeDiscover(vertex), getTimeVisit(vertex), false)));
        visited.forEach((vertex) -> queue.offer(new VertexInfo(vertex, getParentOf(vertex), getTimeDiscover(vertex), getTimeVisit(vertex), true)));

        while (!queue.isEmpty())
            consumer.accept(queue.remove());
    }

    /**
     * Class used mainly for storing the data of the visit
     */
    public class VertexInfo implements Comparable<VertexInfo> {
        public final V vertex;
        public final V parent;
        public final long timeDiscovered;
        public final long timeVisited;
        private final boolean compareVisited;

        private VertexInfo(V vertex, V parent, long timeDiscovered, long timeVisited, boolean compareVisited) {
            this.vertex = vertex;
            this.parent = parent;
            this.timeDiscovered = timeDiscovered;
            this.timeVisited = timeVisited;
            this.compareVisited = compareVisited;
        }

        @Override
        public int compareTo(VertexInfo other) {
            long compareThis = compareVisited ? timeVisited : timeDiscovered;
            long compareOther = other.compareVisited ? other.timeVisited : other.timeDiscovered;

            return (int) (compareThis - compareOther);
        }

        @Override
        public String toString() {
            return String.format("%s -> %s [D:%3d, V:%3d]", parent, vertex, timeDiscovered, timeVisited);
        }
    }
}
