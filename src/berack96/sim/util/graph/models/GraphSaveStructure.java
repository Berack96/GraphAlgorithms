package berack96.sim.util.graph.models;

import berack96.sim.util.graph.Edge;
import berack96.sim.util.graph.Graph;

/**
 * Support class used for saving a Graph in a file.
 * 
 * @author Berack96
 *
 */
public class GraphSaveStructure {
	public GraphSaveStructure() {}
	public GraphSaveStructure(Graph<?, ?> graph, String other) {
		this.vertices = new String[graph.numberOfVertices()];
		int i = 0;
		for(Object o: graph.vertices()) {
			this.vertices[i] = Graph.GSON.toJson(o);
			i++;
		}
		
		this.edges = new EdgeSaveStructure[graph.numberOfEdges()];
		i = 0;
		for (Edge<?, ?> edge : graph.edges()) {
			this.edges[i] = new EdgeSaveStructure(
					Graph.GSON.toJson(edge.getSource()),
					Graph.GSON.toJson(edge.getDestination()),
					Graph.GSON.toJson(edge.getWeight())
				);
			i++;
		}
		
		
		this.other = other;
	}
	
	public String[] vertices;
	public EdgeSaveStructure[] edges;
	//public MarkSaveStructure[] marks;
	public String other;
}