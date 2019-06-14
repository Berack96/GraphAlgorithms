package berack96.sim.util.graph.models;

/**
 * Support class used for saving a Graph in a file.
 * 
 * @author Berack96
 *
 */
public class MarkSaveStructure {
	public MarkSaveStructure() {}
	protected MarkSaveStructure(String v, Object m) {
		this.vert = v;
		this.mark = m;
	}
	
	public String vert;
	public Object mark;
}
