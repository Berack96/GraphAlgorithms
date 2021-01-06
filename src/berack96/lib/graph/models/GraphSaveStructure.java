package berack96.lib.graph.models;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Support class used for saving a Graph in a file.
 * 
 * @author Berack96
 *
 */
public class GraphSaveStructure<V, W extends Number> {

	final public Gson gson = new Gson();
	public String[] vertices;
	public EdgeSaveStructure[] edges;
	//public MarkSaveStructure[] marks;

	/*
	 * Load the graph saved in this class in an instance of a graph passed.
	 * Before loading the graph, it is emptied.
	 */
	public final void load(Graph<V, W> graph, String fileName, Class<V> classV, Class<W> classW) throws FileNotFoundException {
		Gson gson = new GsonBuilder().registerTypeAdapter(this.getClass(), new Creator(this)).create();
		JsonReader reader = new JsonReader(new FileReader(fileName));
		gson.fromJson(reader, this.getClass());
		loadGraph(graph, classV, classW);
	}

	/**
	 * This method can be used by sub-classes for saving other stuff from the graph
	 *
	 * @param graph the graph to load with
	 * @param classV the class used for the Vertex
	 * @param classW the class used for the Weight
	 * @throws NullPointerException if the graph is null
	 * @throws JsonSyntaxException if the file is malformed or corrupted
	 */
	protected void loadGraph(Graph<V, W> graph, Class<V> classV, Class<W> classW) throws NullPointerException, JsonSyntaxException {
		graph.removeAll();
		for(String str : vertices)
			graph.add(gson.fromJson(str, classV));

		for (EdgeSaveStructure edge : edges)
			graph.addEdge(
					gson.fromJson(edge.src, classV),
					gson.fromJson(edge.dest, classV),
					gson.fromJson(edge.weight, classW)
			);
	}
	/**
	 * Save the Graph passed as input to a file inserted as parameter.<br>
	 * The resulting file is a Json string representing all the graph.<br>
	 * If the directory for getting through the file do not exist,<br>
	 * then it is created.<br>
	 * Marks are not included.<br>
	 * The additional parameter is used if you want to save other as well as the graph.
	 *
	 * @param graph the graph to save
	 * @param file the name of the file
	 * @throws IOException for various reason that appear in the message, but the most common is that the file is not found.
	 */
    public final void save(Graph<V,W> graph, String file) throws IOException {
    	saveGraph(graph);
		int slash = file.lastIndexOf("\\");
    	if(slash == -1)
    		slash = file.lastIndexOf("/");
    	if(slash != -1) {
    		String dir = file.substring(0, slash);
    		File fDir = new File(dir);
			//noinspection ResultOfMethodCallIgnored
			fDir.mkdirs();
    	}

    	FileWriter writer = new FileWriter(file);
        gson.toJson(this, writer);
    	writer.close();
    }

	/**
	 * This method can be used by sub-classes for saving other stuff from the graph
	 * @param graph the graph to save
	 */
	protected void saveGraph(Graph<V,W> graph) {
		this.vertices = new String[graph.size()];
		int i = 0;
		for(Object o: graph.vertices())
			this.vertices[i++] = gson.toJson(o);

		this.edges = new EdgeSaveStructure[graph.numberOfEdges()];
		i = 0;
		for (Edge<?, ?> edge : graph.edges())
			this.edges[i++] = new EdgeSaveStructure(
					gson.toJson(edge.getSource()),
					gson.toJson(edge.getDestination()),
					gson.toJson(edge.getWeight())
			);
	}

	private class Creator implements InstanceCreator<GraphSaveStructure<V,W>> {
		private final GraphSaveStructure<V,W> save;
		public Creator(GraphSaveStructure<V,W> save) { this.save = save; }
		public GraphSaveStructure<V,W> createInstance(Type type) { return save; }
	}
}