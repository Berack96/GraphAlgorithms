package berack96.lib.graph.visit.impl;

import berack96.lib.graph.visit.VisitStrategy;

import java.util.*;
import java.util.function.Consumer;

/**
 * The class used for getting the info of the visit.<br>
 * It could be used with the algorithm of the visit for set some useful data.
 *
 * @param <V> the vertex of the visit
 * @author Berack96
 */
public class VisitInfo<V> {
    private static final int NOT_SET = -1;
    
    private final Map<V, VertexInfo> vertices;
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

        this.vertices = new Hashtable<>();
        this.time = 0;
        this.source = source;
        setDiscovered(source);
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
     * @throws IllegalArgumentException if the vertex has not been discovered yet or is null
     */
    public V getParentOf(V vertex) throws IllegalArgumentException {
        VertexInfo info = vertices.get(vertex);
        if (!isDiscovered(vertex))
        	throw new IllegalArgumentException();
        return info.parent;
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
    	VertexInfo info = vertices.get(vertex);
    	long time = (info == null) ? NOT_SET : info.timeDiscovered;
    	
    	if(time == NOT_SET)
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
    	VertexInfo info = vertices.get(vertex);
    	long time = (info == null) ? NOT_SET : info.timeVisited;
    	
    	if(time == NOT_SET)
    		throw new IllegalArgumentException();
        return time;
    }

    /**
     * The depth of the vertex when it was first discovered. 
     *
     * @param vertex the vertex needed
     * @return the depth of it's discovery
     * @throws IllegalArgumentException if the vertex is not discovered
     * @throws NullPointerException     if the vertex is null
     */
    public long getDepth(V vertex) throws IllegalArgumentException, NullPointerException {
    	VertexInfo info = vertices.get(vertex);
    	long depth = (info == null) ? NOT_SET : info.depth;
    	
    	if(depth == NOT_SET)
    		throw new IllegalArgumentException();
        return depth;
    }

    /**
     * Tells if a vertex is discovered or not
     *
     * @param vertex the vertex chosen
     * @return true if is discovered
     */
    public boolean isDiscovered(V vertex) throws NullPointerException {
        try {
            return vertices.get(vertex).timeDiscovered != NOT_SET;
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
        	return vertices.get(vertex).timeVisited != NOT_SET;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Set a vertex as "visited". After this call the vertex is set as discovered (if not already) and visited.<br>
     * Next this call it will be possible to get the time of visit of that vertex<br>
     * Does nothing if the vertex has already been visited.
     *
     * @param vertex the vertex that has been visited
     */
    synchronized void setVisited(V vertex) {
        setDiscovered(vertex);
        VertexInfo info = vertices.get(vertex);
        if(info.timeVisited != NOT_SET)
        	return;
        
        info.timeVisited = time;
        time++;
    }

    /**
     * Set a vertex as "discovered". After this call the vertex is set as discovered and it will be possible to get the time of it's discovery<br>
     * Does nothing if the vertex has already been discovered.
     *
     * @param vertex the vertex that has been discovered
     */
    synchronized void setDiscovered(V vertex) {
		VertexInfo info = vertices.computeIfAbsent(vertex, (v) -> new VertexInfo(vertex));
    	if(info.timeDiscovered != NOT_SET)
        	return;
        
        info.timeDiscovered = time;
        info.depth = 0;
        time++;
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
        VertexInfo info = vertices.get(child);
		info.parent = parent;
		info.depth = vertices.get(parent).depth + 1;
    }

    /**
     * Get all the visited vertices so far.
     *
     * @return the visited vertices
     */
    public Set<V> getVisited() {
    	Set<V> visited = new HashSet<>(vertices.size());
    	vertices.forEach((vert, info) -> {
    		if(info.timeVisited != NOT_SET)
    			visited.add(vert);
    	});
        return visited;
    }

    /**
     * Get all the discovered vertices so far.
     *
     * @return the discovered vertices
     */
    public Set<V> getDiscovered() {
    	Set<V> discovered = new HashSet<>(vertices.size());
    	vertices.forEach((vert, info) -> {
    		if(info.timeDiscovered != NOT_SET)
    			discovered.add(vert);
    	});
        return discovered;
    }

    /**
     * Iterate through all the vertices that are discovered.<br>
     * The vertices will be ordered by the time of their discover.
     *
     * @param consumer the function to apply to each
     */
    public void forEachDiscovered(Consumer<VertexInfo> consumer) {
        Queue<VertexInfo> queue = new PriorityQueue<>();
        vertices.forEach((v, info) -> { 
        	if(info.timeDiscovered != NOT_SET)
        		queue.offer(new VertexInfo(info, false));
        });

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
        Queue<VertexInfo> queue = new PriorityQueue<>();
        vertices.forEach((v, info) -> { 
        	if(info.timeVisited != NOT_SET)
        		queue.offer(new VertexInfo(info, true));
        });

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
        Queue<VertexInfo> queue = new PriorityQueue<>();
        vertices.forEach((v, info) -> { 
        	if(info.timeDiscovered != NOT_SET)
        		queue.offer(new VertexInfo(info, false));
        	if(info.timeVisited != NOT_SET)
        		queue.offer(new VertexInfo(info, true));
        });
        
        while (!queue.isEmpty())
            consumer.accept(queue.remove());
    }
    
    /**
     * Class used mainly for storing the data of the visit
     */
    public class VertexInfo implements Comparable<VertexInfo> {
		public V vertex;
        public V parent;
        public long timeDiscovered;
        public long timeVisited;
        public long depth;
        private final boolean compareVisited;

        private VertexInfo(V vertex) {
			this.vertex = vertex;
            this.timeDiscovered = NOT_SET;
            this.timeVisited = NOT_SET;
            this.depth = NOT_SET;
            this.compareVisited = false;
        }
        
        private VertexInfo(VertexInfo info, boolean compare) {
            this.vertex = info.vertex;
            this.parent = info.parent;
            this.timeDiscovered = info.timeDiscovered;
            this.timeVisited = info.timeVisited;
            this.depth = info.depth;
            this.compareVisited = compare;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            try {
            	return obj instanceof VisitInfo.VertexInfo && obj.toString().equals(toString());
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public int compareTo(VertexInfo other) {
            long compareThis = compareVisited ? timeVisited : timeDiscovered;
            long compareOther = other.compareVisited ? other.timeVisited : other.timeDiscovered;
            return (int) (compareThis - compareOther);
        }

        @Override
        public String toString() {
            return String.format("%s -> %s (%3d) [D:%3d, V:%3d]", parent, vertex, depth, timeDiscovered, timeVisited);
        }
    }
}
