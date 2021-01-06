package berack96.lib.graph.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.Vertex;
import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.Depth;
import berack96.lib.graph.visit.impl.Dijkstra;
import berack96.lib.graph.visit.impl.Tarjan;
import berack96.lib.graph.visit.impl.VisitInfo;

import java.util.*;
import java.util.function.Consumer;

/**
 * An abstract class used for a basic implementation of a graph.<br>
 * It implements the visits, the markers and some other stupid to implement methods.<br>
 * It might not be super efficient but it works and you can always overwrite its methods for better performance
 *
 * @param <V> the vertex
 * @param <W> the weight
 * @author Berack96
 */
public abstract class AGraph<V, W extends Number> implements Graph<V, W> {

    /**
     * Map that contains the marker as key and a set of all the vertices that has it as the value.<br>
     * This map is build like this for performance in creating the marker for multiple vertices.<br>
     * If you flip the parameters (object and set) then has more performance over the single vertex.
     */
    private final Map<Object, Set<V>> markers = new HashMap<>();

    /**
     * Need this variable for not calculating each time the SCC or the cyclic part if the graph doesn't change
     */
    private Tarjan<V, W> tarjan = null;

    /**
     * Need this variable for not calculating each time the distance from a vertex to all his destinations if the graph doesn't change
     */
    final private Map<V, Dijkstra<V, W>> dijkstra = new HashMap<>();
    
    /**
     * Get a new instance of this Graph.
     * 
     * @return A new instance of the graph
     */
    protected abstract Graph<V, W> getNewInstance();

    /**
     * Add a vertex to the graph
     * 
     * @param vertex the vertex to add
     */
    protected abstract void addVertex(V vertex);

    /**
     * Check if a vertex is in the graph
     * 
     * @param vertex the vertex to check
     * @return true if is contained, false otherwise
     */
    protected abstract boolean containsVertex(V vertex);

    /**
     * Remove a vertex from the graph
     * 
     * @param vertex the vertex to remove
     */
    protected abstract void removeVertex(V vertex);
    
    /**
     * Remove all vertices from the graph
     */
    protected abstract void removeAllVertices();
    
    /**
     * Check if the edge is in the graph
     * @param vertex1 the source vertex
     * @param vertex2 the destination vertex
     * @return true if the edge is in the graph, false otherwise
     */
	protected abstract boolean containsEdgeImpl(V vertex1, V vertex2);
	
	/**
	 * Add a new edge to the graph.<br>
	 * If the edge already exist then replace the weight and returns the old one.
	 * 
     * @param vertex1 the source vertex
     * @param vertex2 the destination vertex
	 * @param weight the weight of the new edge
     * @return the old weight, null otherwise
	 */
    protected abstract W addEdgeImpl(V vertex1, V vertex2, W weight);

    /**
     * Get the weight of the edge
     * 
     * @param vertex1 the source vertex
     * @param vertex2 the destination vertex
     * @return the weight of the edge
     */
	protected abstract W getWeightImpl(V vertex1, V vertex2);

	/**
	 * Retrieves all the edges that goes out of a vertex.<br>
	 * (where the vertex is the source)
	 *   
     * @param vertex the source vertex
	 * @return a collection of edges
	 */
	protected abstract Collection<Edge<V, W>> getEdgesOutImpl(V vertex);

	/**
	 * Retrieves all the edges that goes in of a vertex.<br>
	 * (where the vertex is the destination)
	 *   
     * @param vertex the destination vertex
	 * @return a collection of edges
	 */
	protected abstract Collection<Edge<V, W>> getEdgesInImpl(V vertex);

	/**
	 * Remove the edge from the graph
     * @param vertex1 the source vertex
     * @param vertex2 the destination vertex
	 */
	protected abstract void removeEdgeImpl(V vertex1, V vertex2);

	/**
	 * Removes all the edges that goes out of a vertex.<br>
	 * (where the vertex is the source)
     * @param vertex the source vertex
	 */
	protected abstract void removeAllOutEdgeImpl(V vertex);

	/**
	 * Removes all the edges that goes in of a vertex.<br>
	 * (where the vertex is the destination)
	 * 
     * @param vertex the destination vertex
	 */
	protected abstract void removeAllInEdgeImpl(V vertex);
	
    @Override
    public boolean isCyclic() {
        return stronglyConnectedComponents().size() != size();
    }

    @Override
    public boolean isDAG() {
        return !isCyclic();
    }

    @Override
    public Vertex<V> get(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        return new Vertex<>(this, vertex);
    }

    @Override
	public boolean contains(V vertex) throws NullPointerException {
		checkNull(vertex);
		return containsVertex(vertex);
	}

	@Override
	public void add(V vertex) throws NullPointerException {
		checkNull(vertex);
		if(containsVertex(vertex))
			remove(vertex);
		addVertex(vertex);
        graphChanged();
	}
    
    @Override
    public boolean addIfAbsent(V vertex) throws NullPointerException {
        if(contains(vertex))
            return false;
        add(vertex);
        return true;
    }
    
    @Override
    public void addAll(Collection<V> vertices) throws NullPointerException {
        checkNull(vertices);
        vertices.forEach(this::addIfAbsent);
    }

	@Override
	public void remove(V vertex) throws NullPointerException, IllegalArgumentException {
		unMark(vertex);
		removeVertex(vertex);
        graphChanged();
	}

	@Override
	public void removeAll() {
		unMarkAll();
		removeAllVertices();
        graphChanged();
	}

	@Override
	public boolean containsEdge(V vertex1, V vertex2) throws NullPointerException {
		checkNull(vertex1);
		checkNull(vertex2);
		return containsEdgeImpl(vertex1, vertex2);
	}

	@Override
	public W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex1);
		checkNullAndExist(vertex2);
        graphChanged();
		return addEdgeImpl(vertex1, vertex2, weight);
	}

	@Override
    public W addEdge(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
        checkNull(edge);
        return addEdge(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    @Override
    public W addEdgeAndVertices(V vertex1, V vertex2, W weight) throws NullPointerException {
        addIfAbsent(vertex1);
        addIfAbsent(vertex2);
        return addEdge(vertex1, vertex2, weight);
    }

    @Override
    public W addEdgeAndVertices(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
        return addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight());
    }

    @Override
    public void addAllEdges(Collection<Edge<V, W>> edges) throws NullPointerException {
        edges.forEach((edge) -> addEdgeAndVertices(edge.getSource(), edge.getDestination(), edge.getWeight()));
    }
    
    @Override
	public W getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);
		return getWeightImpl(vertex1, vertex2);
	}

	@Override
	public Collection<Edge<V, W>> getEdgesOut(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		return getEdgesOutImpl(vertex);
	}

	@Override
	public Collection<Edge<V, W>> getEdgesIn(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		return getEdgesInImpl(vertex);
	}
	
	@Override
	public void removeEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex1);
        checkNullAndExist(vertex2);
        removeEdgeImpl(vertex1, vertex2);
		graphChanged();
	}

	@Override
	public void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		removeAllOutEdgeImpl(vertex);
		graphChanged();
	}

	@Override
	public void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		removeAllInEdgeImpl(vertex);
		graphChanged();
	}
	
    @Override
    public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        removeVertex(vertex);
        addVertex(vertex);
    }

	@Override
	public void removeAllEdge() {
		Collection<V> vert = vertices();
		removeAllVertices();
		addAll(vert);
		graphChanged();
	}
	
    @Override
	public Collection<V> vertices() {
    	Set<V> set = new HashSet<>();
    	this.forEach(set::add);
		return set;
	}

	@Override
	public Collection<Edge<V, W>> edges() {
		Set<Edge<V,W>> set = new HashSet<>();
		this.forEach( v -> set.addAll(this.getEdgesOut(v)));
		return set;
	}

	@Override
	public int size() {
		return vertices().size();
	}

	@Override
	public int numberOfEdges() {
		return edges().size();
	}

    @Override
    public int degree(V vertex) throws NullPointerException, IllegalArgumentException {
        return degreeIn(vertex) + degreeOut(vertex);
    }

	@Override
	public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
		return getAncestors(vertex).size();
	}

	@Override
	public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
		return getChildrens(vertex).size();
	}

    @Override
    public Collection<Object> marks() {
    	Collection<Object> ret = new HashSet<>();
    	markers.forEach((m, v) -> {
    		if(v.size() > 0)
    			ret.add(m);
    	});
    	
    	return ret;
    }

    @Override
    public void mark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);

        Set<V> set = markers.computeIfAbsent(mark, (v) -> new HashSet<>());
        set.add(vertex);
    }

    @Override
    public void unMark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        checkNull(mark);
        markers.get(mark).remove(vertex);
    }

    @Override
    public void unMark(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        markers.forEach( (mark, set) -> set.remove(vertex) );
    }

	@Override
	public Collection<V> getMarkedWith(Object mark) throws NullPointerException {
		checkNull(mark);
		return markers.computeIfAbsent(mark, (v) -> new HashSet<>());
	}

    @Override
    public Collection<Object> getMarks(V vertex) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(vertex);
        
        Collection<Object> marks = new HashSet<>();
        markers.forEach( (mark, set) -> {
        	if (set.contains(vertex))
        		marks.add(mark);
        });
        
        return marks;
    }

    @Override
    public void unMarkAll(Object mark) {
        checkNull(mark);
        markers.remove(mark);
    }

    @Override
    public void unMarkAll() {
        markers.clear();
    }

	@Override
	public Collection<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		
		Collection<Edge<V,W>> coll = getEdgesIn(vertex);
		coll.addAll(getEdgesOut(vertex));
		return coll;
	}
	
	@Override
	public Collection<V> getChildrens(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		
		Set<V> set = new HashSet<>();
		getEdgesOut(vertex).forEach(e -> set.add(e.getDestination()));
		return set;
	}

	@Override
	public Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
		checkNullAndExist(vertex);
		
		Set<V> set = new HashSet<>();
		getEdgesIn(vertex).forEach(e -> set.add(e.getSource()));
		return set;
	}
	
    @Override
    public VisitInfo<V> visit(V source, VisitStrategy<V, W> strategy, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        return strategy.visit(this, source, visit);
    }

    @Override
    public Graph<V, W> transpose() {
        Graph<V, W> graph = getNewInstance();
        graph.addAll(vertices());
        for(Edge<V, W> edge : edges())
        	graph.addEdge(edge.getDestination(), edge.getSource(), edge.getWeight());
        
        return graph;
    }
    
    @Override
    public List<V> topologicalSort() throws UnsupportedOperationException {
        if (!isDAG())
            throw new UnsupportedOperationException(NOT_DAG);
        return getTarjan().getTopologicalSort();
    }

    @Override
    public Collection<Collection<V>> stronglyConnectedComponents() {
        return getTarjan().getSCC();
    }

    @Override
    public Graph<V, W> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException {
    	Graph<V, W> sub = getNewInstance();
        
    	Set<V> vertices = new HashSet<>();
        new Depth<V, W>(Math.max(depth, 0)).visit(this, source, vertices::add);

        sub.addAll(vertices);
        for (V vertex : vertices)
            getEdgesOut(vertex).forEach((edge) -> {
            	if(sub.contains(edge.getSource()) && sub.contains(edge.getDestination()))
                    sub.addEdge(edge);
            });

        return sub;
    }

    @Override
    public Graph<V, W> subGraph(final Object...marker) {
        final Graph<V, W> sub = getNewInstance();
        final Set<V> allVertices = new HashSet<>();
        final Set<Object> allMarkers = new HashSet<>();
        final boolean isEmpty = (marker == null || marker.length == 0);

        if (!isEmpty)
            Collections.addAll(allMarkers, marker);

        markers.forEach( (mark, set) -> {
        	if (isEmpty || allMarkers.contains(mark))
        		allVertices.addAll(set);
        });

        if (isEmpty) {
            Collection<V> toAdd = vertices();
            toAdd.removeAll(allVertices);
            allVertices.clear();
            allVertices.addAll(toAdd);
        }

        sub.addAll(allVertices);
        for (V vertex : sub.vertices())
            this.edgesOf(vertex).forEach( (edge) -> {
            	if(sub.contains(edge.getSource()) && sub.contains(edge.getDestination()))
                    sub.addEdge(edge);
            });

        return sub;
    }
    
    @Override
    public List<Edge<V, W>> distance(V source, V destination) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
        checkNullAndExist(source);
        checkNullAndExist(destination);

        Dijkstra<V, W> dijkstra = getDijkstra(source);	/* Cached */
        List<Edge<V, W>> path = dijkstra.getLastDistance().get(destination);
        if (path == null)
            throw new UnsupportedOperationException(NOT_CONNECTED);
        return new ArrayList<>(path);
    }

    @Override
    public Map<V, List<Edge<V, W>>> distance(V source) throws NullPointerException, IllegalArgumentException {
        checkNullAndExist(source);
        return new HashMap<>(getDijkstra(source).getLastDistance());	/* Cached */
    }
    
    /**
     * Simple function that reset all the caching variables if the graph changed
     */
    private void graphChanged() {
        tarjan = null;
        dijkstra.clear();
    }

    /**
     * Test if the object passed is null.
     * If it is throw an exception.
     * @param object the object to test
     */
    private void checkNull(Object object) {
        if (object == null)
            throw new NullPointerException(PARAM_NULL);
    }

    /**
     * Check if the vertex passed is null and if exist in the graph.
     * If not then throws eventual exception
     * @param vertex the vertex to test
     */
    private void checkNullAndExist(V vertex) {
        checkNull(vertex);
        if (!contains(vertex))
            throw new IllegalArgumentException(VERTEX_NOT_CONTAINED);
    }

    /**
     * Simple function that return the result of the Dijkstra visit, with the starting point as source.<br>
     * It also cache it, so multiple call will return always the same value unless the graph has changed.
     * @param source the source of the visit
     * @return the complete visit
     */
    private Dijkstra<V, W> getDijkstra(V source) {
        if (dijkstra.get(source) == null) {
            Dijkstra<V, W> newDijkstra = new Dijkstra<>();
            newDijkstra.visit(this, source, null);
            dijkstra.put(source, newDijkstra);
        }

        return dijkstra.get(source);
    }

    /**
     * Simple function that return the result of the Tarjan visit.<br>
     * It also cache it, so multiple call will return always the same value unless the graph has changed.
     * @return the tarjan visit
     */
    private Tarjan<V, W> getTarjan() {
        if (tarjan == null) {
            tarjan = new Tarjan<>();
            tarjan.visit(this, null, null);
        }

        return tarjan;
    }
}
