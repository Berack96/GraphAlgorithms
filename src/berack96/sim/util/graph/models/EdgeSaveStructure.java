package berack96.sim.util.graph.models;

/**
 * Support class used for saving a Graph in a file.
 * 
 * @author Berack96
 *
 */
public class EdgeSaveStructure {
	public EdgeSaveStructure() {}
	protected EdgeSaveStructure(String s, String d, String w) {
		this.src = s;
		this.dest = d;
		this.weight = w;
	}
	
	public String src;
	public String dest;
	public String weight;
}