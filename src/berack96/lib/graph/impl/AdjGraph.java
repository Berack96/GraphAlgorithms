package berack96.lib.graph.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.Vertex;
import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.VisitInfo;

public class AdjGraph<V, W extends Number> implements Graph<V, W> {

	@Override
	public Iterator<V> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCyclic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDAG() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vertex<V> getVertex(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addVertex(V vertex) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addVertexIfAbsent(V vertex) throws NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAllVertices(Collection<V> vertices) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeVertex(V vertex) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllVertex() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(V vertex) throws NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unMark(V vertex, Object mark) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unMark(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<V> getMarkedWith(Object mark) throws NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getMarks(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unMarkAll(Object mark) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unMarkAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public W addEdge(V vertex1, V vertex2, W weight) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public W addEdge(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public W addEdgeAndVertices(V vertex1, V vertex2, W weight) throws NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public W addEdgeAndVertices(Edge<V, W> edge) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAllEdges(Collection<Edge<V, W>> edges) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public W getWeight(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeEdge(V vertex1, V vertex2) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllInEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllOutEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllEdge(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllEdge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsEdge(V vertex1, V vertex2) throws NullPointerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<V> vertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Edge<V, W>> edges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Edge<V, W>> edgesOf(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Edge<V, W>> getEdgesIn(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Edge<V, W>> getEdgesOut(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> getChildren(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> getAncestors(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int degreeIn(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int degreeOut(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int degree(V vertex) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numberOfVertices() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numberOfEdges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public VisitInfo<V> visit(V source, VisitStrategy<V, W> strategy, Consumer<V> visit)
			throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<V, W> transpose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<V> topologicalSort() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<V>> stronglyConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<V, W> subGraph(V source, int depth) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<V, W> subGraph(Object... marker) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Edge<V, W>> distance(V source, V destination)
			throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<V, List<Edge<V, W>>> distance(V source) throws NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

}
